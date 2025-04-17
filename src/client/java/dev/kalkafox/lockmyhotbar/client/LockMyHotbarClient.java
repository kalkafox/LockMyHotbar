package dev.kalkafox.lockmyhotbar.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.kalkafox.lockmyhotbar.client.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class LockMyHotbarClient implements ClientModInitializer {

    public static KeyMapping keyBinding;


    public static final Config CONFIG = Config.createAndLoad();

    public static boolean[] lockedSlots = CONFIG.lockedSlots();


    @Override
    public void onInitializeClient() {

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.lockmyhotbar.lock", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_KP_7, "category.lockmyhotbar"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (keyBinding.consumeClick()) {

                int selectedSlot = client.player.getInventory().selected;

                lockedSlots[selectedSlot] = !lockedSlots[selectedSlot];

                String status = lockedSlots[selectedSlot] ? "locked" : "unlocked";
                client.player.displayClientMessage(
                        Component.literal("Slot " + selectedSlot + " is now " + status + "."),
                        true
                );
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPacketListener, minecraft) -> {
            CONFIG.save();
        });

    }

    public static boolean isSlotLocked(int slot) {
        return LockMyHotbarClient.lockedSlots[slot];
    }
}
