package dev.worldgen.njb.registry;

import dev.worldgen.njb.worldgen.placementmodifier.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import static dev.worldgen.njb.registry.RegistryUtils.register;

public class NJBPlacementModifiers {

    public static final Registry<PlacementModifierType<?>> registry = Registries.PLACEMENT_MODIFIER_TYPE;
    public static final PlacementModifierType<ConfigPlacementModifier> CONFIG = register(
        registry, "config", () -> ConfigPlacementModifier.MODIFIER_CODEC
    );
    public static final PlacementModifierType<HeightFilterPlacementModifier> HEIGHT_FILTER = register(
        registry, "height_filter", () -> HeightFilterPlacementModifier.MODIFIER_CODEC
    );
    public static final PlacementModifierType<ModuleBasedCountPlacementModifier> MODULE_BASED_COUNT = register(
        registry, "module_based_count", () -> ModuleBasedCountPlacementModifier.MODIFIER_CODEC
    );
    public static final PlacementModifierType<NoiseSlopePlacementModifier> NOISE_SLOPE = register(
        registry, "noise_slope", () -> NoiseSlopePlacementModifier.MODIFIER_CODEC
    );
    public static final PlacementModifierType<NoiseThresholdPlacementModifier> NOISE_THRESHOLD = register(
        registry, "noise_threshold", () -> NoiseThresholdPlacementModifier.MODIFIER_CODEC
    );

    public static final PlacementModifierType<SteepCornersPlacementModifier> STEEP_CORNERS = register(
        registry, "steep_corners", () -> SteepCornersPlacementModifier.MODIFIER_CODEC
    );
    public static void init() {}
}
