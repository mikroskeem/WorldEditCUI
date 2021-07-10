package eu.mikroskeem.worldeditcui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mumfrey.worldeditcui.WorldEditCUI;
import com.mumfrey.worldeditcui.config.CUIConfiguration;
import com.mumfrey.worldeditcui.event.listeners.CUIListenerChannel;
import com.mumfrey.worldeditcui.event.listeners.CUIListenerWorldRender;
import eu.mikroskeem.worldeditcui.mixins.MinecraftClientAccess;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.nio.charset.StandardCharsets;

/**
 * Fabric mod entrypoint
 *
 * @author Mark Vainomaa
 */
public final class FabricModWorldEditCUI implements ModInitializer {
    private static final int DELAYED_HELO_TICKS = 10;

    public static final String MOD_ID = "worldeditcui";
    private static FabricModWorldEditCUI instance;

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

    /**
     * Register a key binding
     *
     * @param name id, will be used as a localization key under {@code key.worldeditcui.<name>}
     * @param type type
     * @param code default value
     * @return new, registered keybinding in the mod category
     */
    private static KeyBinding key(final String name, final InputUtil.Type type, final int code) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + MOD_ID + '.' + name, type, code, KEYBIND_CATEGORY_WECUI));
    }

    @Override
    @SuppressWarnings("deprecation") // RenderSystem/immediate mode GL use
    public void onInitialize() {
        if (Boolean.getBoolean("wecui.debug.mixinaudit")) {
            MixinEnvironment.getCurrentEnvironment().audit();
        }

        instance = this;

        // Set up event listeners
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ClientLifecycleEvents.CLIENT_STARTED.register(this::onGameInitDone);
        CUINetworking.subscribeToCuiPacket(this::onPluginMessage);
        ClientPlayConnectionEvents.JOIN.register(this::onJoinGame);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(ctx -> {
            if (ctx.advancedTranslucency()) {
                try {
                    RenderSystem.getModelViewStack().push();
                    RenderSystem.getModelViewStack().method_34425(ctx.matrixStack().peek().getModel());
                    RenderSystem.applyModelViewMatrix();
                    ctx.worldRenderer().getTranslucentFramebuffer().beginWrite(false);
                    this.onPostRenderEntities(ctx);
                } finally {
                    MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
                    RenderSystem.getModelViewStack().pop();
                }
            }
        });
        WorldRenderEvents.LAST.register(ctx -> {
            if (!ctx.advancedTranslucency()) {
                OptifineHooks.doOptifineAwareRender(ctx, this::onPostRenderEntities);
            }
        });
    }

    private void onTick(MinecraftClient mc) {
        CUIConfiguration config = controller.getConfiguration();
        boolean inGame = mc.player != null;
        boolean clock = ((MinecraftClientAccess) mc).getRenderTickCounter().tickDelta > 0;

        if (inGame && mc.currentScreen == null) {
            while (this.keyBindToggleUI.wasPressed()) {
                this.visible = !this.visible;
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
            if (mc.world != this.lastWorld || mc.player != this.lastPlayer) {
                this.lastWorld = mc.world;
                this.lastPlayer = mc.player;

                controller.getDebugger().debug("World change detected, sending new handshake");
                controller.clear();
                this.helo(mc.getNetworkHandler());
                this.delayedHelo = FabricModWorldEditCUI.DELAYED_HELO_TICKS;
                if (mc.player != null && config.isPromiscuous()) {
                    mc.player.sendChatMessage("/we cui"); //Tricks WE to send the current selection
                }
            }

            if (this.delayedHelo > 0) {
                this.delayedHelo--;
                if (this.delayedHelo == 0) {
                    this.helo(mc.getNetworkHandler());
                }
            }
        }
    }

    private void onPluginMessage(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf data, PacketSender sender) {
        try {
            int readableBytes = data.readableBytes();
            if (readableBytes > 0) {
                String stringPayload = data.toString(0, data.readableBytes(), StandardCharsets.UTF_8);
                client.execute(() -> channelListener.onMessage(stringPayload));
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

    public void onJoinGame(final ClientPlayNetworkHandler handler, final PacketSender sender, final MinecraftClient client) {
        this.visible = true;
        controller.getDebugger().debug("Joined game, sending initial handshake");
        this.helo(handler);
    }

    public void onPostRenderEntities(final WorldRenderContext ctx) {
        if (this.visible) {
            this.worldRenderListener.onRender(ctx.matrixStack(), ctx.tickDelta());
        }
    }

    private void helo(final ClientPlayNetworkHandler handler) {
        String message = "v|" + WorldEditCUI.PROTOCOL_VERSION;
        ByteBuf buffer = Unpooled.copiedBuffer(message, StandardCharsets.UTF_8);
        CUINetworking.send(handler, new PacketByteBuf(buffer));
    }

    public WorldEditCUI getController()
    {
        return this.controller;
    }

    public static FabricModWorldEditCUI getInstance() {
        return instance;
    }
}
