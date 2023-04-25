package dev.worldgen.njb.worldgen.placementmodifier;

import dev.worldgen.njb.config.ConfigHandler;
import dev.worldgen.njb.registry.NJBPlacementModifiers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.AbstractConditionalPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class ConfigPlacementModifier extends AbstractConditionalPlacementModifier {
    public static final Codec<ConfigPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            Codec.STRING.fieldOf("module").forGetter(config -> {
                return config.module;
            }),
            Codec.BOOL.fieldOf("inverted").forGetter(config -> {
                return config.inverted;
            })
        ).apply(instance, ConfigPlacementModifier::new);
    });
    private final String module;
    private final Boolean inverted;

    public ConfigPlacementModifier(String key, Boolean inverted) {
        this.module = key;
        this.inverted = inverted;
    }
    @Override
    protected boolean shouldPlace(FeaturePlacementContext context, Random random, BlockPos pos) {
        return this.inverted != ConfigHandler.getConfigValue(module);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return NJBPlacementModifiers.CONFIG;
    }
}
