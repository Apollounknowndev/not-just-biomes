package dev.worldgen.njb.registry;

import dev.worldgen.njb.worldgen.feature.ConfigBasedSelectorFeature;
import dev.worldgen.njb.worldgen.feature.DungeonFeature;
import dev.worldgen.njb.worldgen.feature.FallenLogFeature;
import dev.worldgen.njb.worldgen.feature.RockFeature;
import dev.worldgen.njb.worldgen.feature.config.ConfigBasedSelectorConfig;
import dev.worldgen.njb.worldgen.feature.config.DungeonConfig;
import dev.worldgen.njb.worldgen.feature.config.FallenLogConfig;
import dev.worldgen.njb.worldgen.feature.config.RockConfig;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import static dev.worldgen.njb.registry.RegistryUtils.register;

public class NJBConfiguredFeatures {

    public static final Registry<Feature<?>> registry = Registry.FEATURE;
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
    public static void init() {}
}
