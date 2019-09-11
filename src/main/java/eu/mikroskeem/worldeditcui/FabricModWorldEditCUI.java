package eu.mikroskeem.worldeditcui;

import com.google.common.base.Charsets;
import com.mumfrey.worldeditcui.WorldEditCUI;
import com.mumfrey.worldeditcui.config.CUIConfiguration;
import com.mumfrey.worldeditcui.event.listeners.CUIListenerChannel;
import com.mumfrey.worldeditcui.event.listeners.CUIListenerWorldRender;
import eu.mikroskeem.worldeditcui.interfaces.IMinecraftClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;

/**
 * Fabric mod entrypoint
 *
 * @author Mark Vainomaa
 */
public final class FabricModWorldEditCUI implements ModInitializer {
    private static final int DELAYED_HELO_TICKS = 10;
    private static FabricModWorldEditCUI instance;
    public static final Identifier CHANNEL_WECUI = new Identifier("worldedit", "cui");

    private final Identifier keybindToggleUIId = new Identifier("worldeditcui", "toggle");
    private final Identifier keybindClearSelId = new Identifier("worldeditcui", "clear");
    private final Identifier keybindChunkBorderId = new Identifier("worldeditcui", "chunk");
    private final FabricKeyBinding keyBindToggleUI = FabricKeyBinding.Builder.create(keybindToggleUIId, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.categories.worldeditcui").build();
    private final FabricKeyBinding keyBindClearSel = FabricKeyBinding.Builder.create(keybindClearSelId, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.categories.worldeditcui").build();
    private final FabricKeyBinding keyBindChunkBorder = FabricKeyBinding.Builder.create(keybindChunkBorderId, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.categories.worldeditcui").build();

    private WorldEditCUI controller;
    private CUIListenerWorldRender worldRenderListener;
    private CUIListenerChannel channelListener;

    private World lastWorld;
    private ClientPlayerEntity lastPlayer;

    private boolean visible = true;
    private boolean alwaysOnTop = false;
    private int delayedHelo = 0;

    @Override
    public void onInitialize() {
        instance = this;

        // Register keybindings
        KeyBindingRegistry.INSTANCE.addCategory("key.categories.worldeditcui");
        KeyBindingRegistry.INSTANCE.register(keyBindToggleUI);
        KeyBindingRegistry.INSTANCE.register(keyBindClearSel);
        KeyBindingRegistry.INSTANCE.register(keyBindChunkBorder);

        // Hook into game
        ClientTickCallback.EVENT.register(this::onTick);
        ClientSidePacketRegistry.INSTANCE.register(CHANNEL_WECUI, this::onPluginMessage);
    }

    private void onTick(MinecraftClient mc) {
        CUIConfiguration config = controller.getConfiguration();
        boolean inGame = mc.player != null;
        boolean clock = ((IMinecraftClient) mc).getRenderTickCounter().ticksThisFrame > 0;

        if (inGame && mc.currentScreen == null) {
            while (this.keyBindToggleUI.wasPressed()) {
                if (isPressed(mc, GLFW.GLFW_KEY_LEFT_SHIFT) || isPressed(mc, GLFW.GLFW_KEY_RIGHT_SHIFT)) {
                    config.setAlwaysOnTop(!config.isAlwaysOnTop());
                } else {
                    this.visible = !this.visible;
                }
            }

            while (this.keyBindClearSel.wasPressed()) {
                if (mc.player != null) {
                    mc.player.sendChatMessage("//sel");
                }

                if (config.isClearAllOnKey()) {
                    controller.clearRegions();
                }
            }

            while (this.keyBindChunkBorder.wasPressed()) {
                controller.toggleChunkBorders();
            }
        }

        if (inGame && clock && controller != null) {
            this.alwaysOnTop = config.isAlwaysOnTop();

            if (mc.world != this.lastWorld || mc.player != this.lastPlayer) {
                this.lastWorld = mc.world;
                this.lastPlayer = mc.player;

                controller.getDebugger().debug("World change detected, sending new handshake");
                controller.clear();
                this.helo();
                this.delayedHelo = FabricModWorldEditCUI.DELAYED_HELO_TICKS;
                if (mc.player != null && config.isPromiscuous()) {
                    mc.player.sendChatMessage("/we cui"); //Tricks WE to send the current selection
                }
            }

            if (this.delayedHelo > 0) {
                this.delayedHelo--;
                if (this.delayedHelo == 0) {
                    this.helo();
                }
            }
        }
    }

    private void onPluginMessage(PacketContext ctx, PacketByteBuf data) {
        try {
            int readableBytes = data.readableBytes();
            if (readableBytes > 0) {
                byte[] payload = new byte[readableBytes];
                data.readBytes(payload);
                String stringPayload = new String(payload, StandardCharsets.UTF_8);
                ctx.getTaskQueue().executeSync(() -> channelListener.onMessage(stringPayload));
            } else {
                getController().getDebugger().debug("Warning, invalid (zero length) payload received from server");
            }
        } catch (Exception ignored) {}
    }

    public void onGameInitDone(MinecraftClient client) {
        this.controller = new WorldEditCUI();
        this.controller.initialise(client);
        this.worldRenderListener = new CUIListenerWorldRender(this.controller, client);
        this.channelListener = new CUIListenerChannel(this.controller);
    }

    public void onJoinGame() {
        this.visible = true;
        controller.getDebugger().debug("Joined game, sending initial handshake");
        this.helo();
    }

    public void onPostRenderEntities(float partialTicks) {
        if (this.visible && !this.alwaysOnTop) {
            GuiLighting.disable();
            worldRenderListener.onRender(partialTicks);
            GuiLighting.enable();
        }
    }

    public void onPostRender(float partialTicks) {
        if (this.visible && this.alwaysOnTop) {
            worldRenderListener.onRender(partialTicks);
        }
    }

    private void helo() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        String message = "v|" + WorldEditCUI.PROTOCOL_VERSION;
        buffer.writeBytes(message.getBytes(Charsets.UTF_8));
        ClientSidePacketRegistry.INSTANCE.sendToServer(CHANNEL_WECUI, buffer);
    }

    private boolean isPressed(MinecraftClient client, int keycode) {
        return InputUtil.isKeyPressed(client.window.getHandle(), keycode);
    }

    public WorldEditCUI getController()
    {
        return this.controller;
    }

    public static FabricModWorldEditCUI getInstance() {
        return instance;
    }
}