package com.dogalafetmod.event;

import com.dogalafetmod.DogalAfetMod;
import com.dogalafetmod.network.PacketSyncTimer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "dogalafetmod")
public class DisasterManager {

    private static int ticksElapsed = 0;
    private static final int DISASTER_INTERVAL_TICKS = 2400; // 2 dakika = 120 saniye * 20 tick
    private static final Random random = new Random();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        ticksElapsed++;
        int remainingSeconds = (DISASTER_INTERVAL_TICKS - ticksElapsed) / 20;

        if (ticksElapsed % 20 == 0) {
            DogalAfetMod.CHANNEL.send(PacketDistributor.ALL.noArg(), new PacketSyncTimer(remainingSeconds));
        }

        if (ticksElapsed >= DISASTER_INTERVAL_TICKS) {
            ticksElapsed = 0;
            if (event.getServer() != null) {
                triggerRandomDisaster(event.getServer().overworld());
            }
        }
    }

    private static void triggerRandomDisaster(ServerLevel world) {
        int disasterType = random.nextInt(5);

        for (ServerPlayer player : world.players()) {
            BlockPos pos = player.blockPosition();

            switch (disasterType) {
                case 0: // YILDIRIM FIRTINASI
                    player.sendSystemMessage(Component.literal("§c[AFET] Şiddetli Yıldırım Fırtınası Başladı!"));
                    for (int i = 0; i < 6; i++) {
                        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);
                        if (lightning != null) {
                            lightning.moveTo(pos.getX() + random.nextInt(16) - 8, pos.getY(), pos.getZ() + random.nextInt(16) - 8);
                            world.addFreshEntity(lightning);
                        }
                    }
                    break;

                case 1: // METEOR DÜŞMESİ
                    player.sendSystemMessage(Component.literal("§c[AFET] Gökten Meteorlar Düşüyor!"));
                    for (int i = 0; i < 3; i++) {
                        BlockPos targetPos = pos.offset(random.nextInt(12) - 6, 0, random.nextInt(12) - 6);
                        world.explode(null, targetPos.getX(), targetPos.getY(), targetPos.getZ(), 4.5F, ServerLevel.ExplosionInteraction.TNT);
                    }
                    break;

                case 2: // ANİ SU BASKINI (TSUNAMİ)
                    player.sendSystemMessage(Component.literal("§c[AFET] Ani Tsunami ve Su Baskını!"));
                    for (int x = -3; x <= 3; x++) {
                        for (int z = -3; z <= 3; z++) {
                            BlockPos waterPos = pos.offset(x, 0, z);
                            if (world.isEmptyBlock(waterPos)) {
                                world.setBlockAndUpdate(waterPos, Blocks.WATER.defaultBlockState());
                            }
                        }
                    }
                    break;

                case 3: // YANARDAĞ / LAV PATLAMASI
                    player.sendSystemMessage(Component.literal("§c[AFET] Magma Yüzeye Çıktı!"));
                    for (int i = 0; i < 2; i++) {
                        BlockPos lavaPos = pos.offset(random.nextInt(6) - 3, 0, random.nextInt(6) - 3);
                        world.setBlockAndUpdate(lavaPos, Blocks.LAVA.defaultBlockState());
                    }
                    break;

                case 4: // DEPREM
                    player.sendSystemMessage(Component.literal("§c[AFET] Şiddetli Deprem! Zemin Çöküyor!"));
                    for (int x = -2; x <= 2; x++) {
                        for (int z = -2; z <= 2; z++) {
                            BlockPos breakPos = pos.offset(x, -1, z);
                            world.destroyBlock(breakPos, false);
                        }
                    }
                    break;
            }
        }
    }
}
