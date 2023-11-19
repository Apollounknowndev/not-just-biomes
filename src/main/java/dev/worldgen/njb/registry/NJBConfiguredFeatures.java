package dev.worldgen.njb.registry;

import dev.worldgen.njb.worldgen.feature.*;
import dev.worldgen.njb.worldgen.feature.config.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import static dev.worldgen.njb.registry.RegistryUtils.register;

public class NJBConfiguredFeatures {

    public static final Registry<Feature<?>> registry = Registries.FEATURE;
    public static final Feature<DungeonConfig> DUNGEON = register(
        registry, "dungeon", new DungeonFeature(DungeonConfig.CODEC)
    );
    public static final Feature<ConfigBasedSelectorConfig> CONFIG_BASED_SELECTOR = register(
        registry, "config_based_selector", new ConfigBasedSelectorFeature(ConfigBasedSelectorConfig.CODEC)
    );
    public static final Feature<RockConfig> ROCK = register(
        registry, "rock", new RockFeature(RockConfig.CODEC)
    );
    public static final Feature<FallenLogConfig> FALLEN_LOG = register(
        registry, "fallen_log", new FallenLogFeature(FallenLogConfig.CODEC)
    );
    public static final Feature<WellConfig> WELL = register(
        registry, "well", new WellFeature(WellConfig.CODEC)
    );

    public static void init() {}
}
