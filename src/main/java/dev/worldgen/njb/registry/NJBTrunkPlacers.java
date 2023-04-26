package dev.worldgen.njb.registry;

import dev.worldgen.njb.worldgen.trunk.NoiseBasedStraight;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class NJBTrunkPlacers {
    public static final Registry<TrunkPlacerType<?>> registry = Registry.TRUNK_PLACER_TYPE;
    public static final TrunkPlacerType<NoiseBasedStraight> NOISE_BASED_STRAIGHT = RegistryUtils.register(
        registry, "noise_based_straight", new TrunkPlacerType<>(NoiseBasedStraight.CODEC)
    );

    public static void init() {}
}
