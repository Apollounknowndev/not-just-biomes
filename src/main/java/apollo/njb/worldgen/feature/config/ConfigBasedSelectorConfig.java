package apollo.njb.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ConfigBasedSelectorConfig implements FeatureConfig {
    public static final Codec<ConfigBasedSelectorConfig> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
            Codec.STRING.fieldOf("module").forGetter(selector -> {
                return selector.module;
            }),
            PlacedFeature.REGISTRY_CODEC.fieldOf("module_enabled_feature").forGetter(selector -> {
                return selector.moduleEnabledFeature;
            }),
            PlacedFeature.REGISTRY_CODEC.fieldOf("module_disabled_feature").forGetter(selector -> {
                return selector.moduleDisabledFeature;
            })
        ).apply(instance, ConfigBasedSelectorConfig::new);
    });
    public final String module;
    public final RegistryEntry<PlacedFeature> moduleEnabledFeature;
    public final RegistryEntry<PlacedFeature> moduleDisabledFeature;

    private ConfigBasedSelectorConfig(String module, RegistryEntry<PlacedFeature> moduleEnabledFeature, RegistryEntry<PlacedFeature> moduleDisabledFeature) {
        this.module = module;
        this.moduleEnabledFeature = moduleEnabledFeature;
        this.moduleDisabledFeature = moduleDisabledFeature;
    }
}

