package eu.mikroskeem.worldeditcui;

import com.mumfrey.worldeditcui.WorldEditCUI;
import com.mumfrey.worldeditcui.config.FlagOption;
import com.mumfrey.worldeditcui.event.listeners.CUIListenerChannel;
import com.mumfrey.worldeditcui.event.listeners.CUIListenerWorldRender;
import eu.mikroskeem.worldeditcui.mixins.MinecraftClientAccess;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
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

    private static final String KEYBIND_CATEGORY_WECUI = "key.categories.worldeditcui";
    private final KeyBinding keyBindToggleUI = key("toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN);
    private final KeyBinding keyBindClearSel = key("clear", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN);
    private final KeyBinding keyBindChunkBorder = key("chunk", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN);

    private WorldEditCUI controller;
    private CUIListenerWorldRender worldRenderListener;
    private CUIListenerChannel channelListener;

    private World lastWorld;
    private ClientPlayerEntity lastPlayer;

    private boolean visible = true;
    private int delayedHelo = 0;
    private static RenderMode activeRenderMode;

    /**
     * Register a key binding
     *
     * @param name id, will be used as a localization key under {@code key.worldeditcui.<name>}
     * @param type type
     * @param code default value
     * @return new, registered keybinding in the mod category
     */
    private static KeyBinding key(final String name, final InputUtil.Type type, final int code) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + WorldEditCUI.MOD_ID + '.' + name, type, code, KEYBIND_CATEGORY_WECUI));
    }

    public static RenderMode getRenderMode() {
        return activeRenderMode;
    }

    /* package */ static void setRenderMode(final RenderMode mode) {
        activeRenderMode = mode;
    }

    @Override
    public void onInitialize() {
        instance = this;

        // Hook into game
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ClientLifecycleEvents.CLIENT_STARTED.register(this::onGameInitDone);
        ClientSidePacketRegistry.INSTANCE.register(CHANNEL_WECUI, this::onPluginMessage);
    }

    private void onTick(MinecraftClient mc) {
        boolean inGame = mc.player != null;
        boolean clock = ((MinecraftClientAccess) mc).getRenderTickCounter().tickDelta > 0;

        if (inGame && mc.currentScreen == null) {
            while (this.keyBindToggleUI.wasPressed()) {
                if (isPressed(mc, GLFW.GLFW_KEY_LEFT_SHIFT) || isPressed(mc, GLFW.GLFW_KEY_RIGHT_SHIFT)) {
                    FlagOption.ALWAYS_ON_TOP.value(!FlagOption.ALWAYS_ON_TOP.value());
                } else {
                    this.visible = !this.visible;
                }
            }

            while (this.keyBindClearSel.wasPressed()) {
                if (mc.player != null) {
                    mc.player.sendChatMessage("//sel");
                }

                if (FlagOption.CLEAR_ALL_ON_KEY.value()) {
                    controller.clearRegions();
                }
            }

            while (this.keyBindChunkBorder.wasPressed()) {
                controller.toggleChunkBorders();
            }
        }

        if (inGame && clock && controller != null) {
            if (activeRenderMode != RenderMode.FREX_POST_RENDER) {
                activeRenderMode = FlagOption.ALWAYS_ON_TOP.value() ? RenderMode.ALWAYS_ON_TOP : RenderMode.STANDARD;
            }

            if (mc.world != this.lastWorld || mc.player != this.lastPlayer) {
                this.lastWorld = mc.world;
                this.lastPlayer = mc.player;

                controller.getDebugger().debug("World change detected, sending new handshake");
                controller.clear();
                this.helo();
                this.delayedHelo = FabricModWorldEditCUI.DELAYED_HELO_TICKS;
                if (mc.player != null && FlagOption.PROMISCUOUS.value()) {
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
                String stringPayload = data.toString(0, data.readableBytes(), StandardCharsets.UTF_8);
                ctx.getTaskQueue().execute(() -> channelListener.onMessage(stringPayload));
            } else {
                getController().getDebugger().debug("Warning, invalid (zero length) payload received from server");
            }
        } catch (Exception ex) {
            getController().getDebugger().info("Error decoding payload from server", ex);
        }
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
        if (this.visible && activeRenderMode != RenderMode.ALWAYS_ON_TOP) {
            worldRenderListener.onRender(partialTicks);
        }
    }

    public void onPostRender(float partialTicks) {
        // TODO: implement this?
        if (this.visible && activeRenderMode == RenderMode.ALWAYS_ON_TOP) {
            worldRenderListener.onRender(partialTicks);
        }
    }

    private void helo() {
        String message = "v|" + WorldEditCUI.PROTOCOL_VERSION;
        ByteBuf buffer = Unpooled.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8));
        ClientSidePacketRegistry.INSTANCE.sendToServer(CHANNEL_WECUI, new PacketByteBuf(buffer));
    }

    private boolean isPressed(MinecraftClient client, int keycode) {
        return InputUtil.isKeyPressed(client.getWindow().getHandle(), keycode);
    }

    public WorldEditCUI getController()
    {
        return this.controller;
    }

    public static FabricModWorldEditCUI getInstance() {
        return instance;
    }
}
