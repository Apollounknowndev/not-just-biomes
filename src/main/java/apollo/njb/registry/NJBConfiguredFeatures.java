package apollo.njb.registry;

import apollo.njb.worldgen.feature.ConfigBasedSelectorFeature;
import apollo.njb.worldgen.feature.DungeonFeature;
import apollo.njb.worldgen.feature.FallenLogFeature;
import apollo.njb.worldgen.feature.RockFeature;
import apollo.njb.worldgen.feature.config.ConfigBasedSelectorConfig;
import apollo.njb.worldgen.feature.config.DungeonConfig;
import apollo.njb.worldgen.feature.config.FallenLogConfig;
import apollo.njb.worldgen.feature.config.RockConfig;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import static apollo.njb.registry.RegistryUtils.register;

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
    public static void init() {}
}
