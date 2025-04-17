package dev.kalkafox.lockmyhotbar.client.mixin;

import dev.kalkafox.lockmyhotbar.client.LockMyHotbarClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Unique float hotbarSelectionPulseTicks = 0f;

    @Shadow @Nullable protected abstract Player getCameraPlayer();

    @Shadow @Final private static ResourceLocation HOTBAR_SELECTION_SPRITE;

    @Shadow @Final private Minecraft minecraft;

    @Redirect(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    private void darkenLockedHotbar(GuiGraphics instance, Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation sprite, int x, int y, int width, int height) {
        instance.blitSprite(renderTypeGetter, HOTBAR_SELECTION_SPRITE, x, y, width, height);
        var selected = this.getCameraPlayer().getInventory().selected;
        if (LockMyHotbarClient.lockedSlots[selected]) {
            float deltaTicks = this.minecraft.getDeltaTracker().getRealtimeDeltaTicks();
            hotbarSelectionPulseTicks = (hotbarSelectionPulseTicks + deltaTicks) % 20f;
            int tint = getTint();
            instance.blitSprite(renderTypeGetter, HOTBAR_SELECTION_SPRITE, x, y, width, height, tint);
        }
    }

    @Unique
    private int getTint() {
        float phase     = hotbarSelectionPulseTicks / 20f;                // 0→1 each second
        float alphaFrac = (float)(Math.sin(phase * Math.PI * 2) * 0.5 + 0.5);
        int alpha = (int)((1f - alphaFrac) * 255f);
        return (alpha << 24) | (0x80 << 16) | (0x80 << 8) | 0x80;
    }

}
