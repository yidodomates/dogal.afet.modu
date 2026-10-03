package com.dogalafetmod.client;

import com.dogalafetmod.DogalAfetMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DogalAfetMod.MODID, value = Dist.CLIENT)
public class DisasterHUD {
    public static int remainingSeconds = -1;

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (remainingSeconds >= 0) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && !mc.options.hideGui) {
                int width = mc.getWindow().getGuiScaledWidth();
                String text = "Afete Kalan Sure: " + remainingSeconds + " sn";
                int textWidth = mc.font.width(text);
                
                mc.font.drawShadow(event.PoseStack, text, (width - textWidth) / 2, 20, 0xFFFF5555);
            }
        }
    }
}
