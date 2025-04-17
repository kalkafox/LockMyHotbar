package dev.kalkafox.lockmyhotbar.client.mixin;

import dev.kalkafox.lockmyhotbar.client.LockMyHotbarClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Function;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {


    @Shadow @Nullable protected Slot hoveredSlot;

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void onDrop(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {

        int shifted = slotId - 36;

        if (type.equals(ClickType.THROW) && shifted >= 0 && shifted < 9 && LockMyHotbarClient.isSlotLocked(shifted)) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderSlotHighlightFront", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void darkenContainerHighlight(GuiGraphics instance, Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation sprite, int x, int y, int width, int height) {
        var selected = this.hoveredSlot;

        if (selected == null) return;

        var shifted = selected.index - 36;

        instance.blitSprite(renderTypeGetter, sprite, x, y, width, height);

        if (shifted >= 0 && shifted < 9 && LockMyHotbarClient.isSlotLocked(shifted)) {

            instance.fill(x + 4, y + 4, x + width - 4, y + height - 4, 0x88000000);
        }


    }


}
