package apollo.njb.worldgen.placementmodifier;

import apollo.njb.config.ConfigHandler;
import apollo.njb.registry.NJBPlacementModifiers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.placementmodifier.AbstractCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class ModuleBasedCountPlacementModifier extends AbstractCountPlacementModifier {
    public static final Codec<ModuleBasedCountPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            Codec.STRING.fieldOf("module").orElse("").forGetter(noiseSlope -> {
                return noiseSlope.module;
            }),
            IntProvider.VALUE_CODEC.fieldOf("module_enabled_count").orElse(ConstantIntProvider.ZERO).forGetter((noiseSlope) -> {
                return noiseSlope.moduleEnabledCount;
            }),
            IntProvider.VALUE_CODEC.fieldOf("module_disabled_count").orElse(ConstantIntProvider.ZERO).forGetter((noiseSlope) -> {
                return noiseSlope.moduleDisabledCount;
            })
        ).apply(instance, ModuleBasedCountPlacementModifier::new);
    });
    private final String module;
    private final IntProvider moduleEnabledCount;
    private final IntProvider moduleDisabledCount;

    private ModuleBasedCountPlacementModifier(String key, IntProvider moduleEnabledCount, IntProvider moduleDisabledCount) {
        this.module = key;
        this.moduleDisabledCount = moduleDisabledCount;
        this.moduleEnabledCount = moduleEnabledCount;
    }

    protected int getCount(Random random, BlockPos pos) {
        return ConfigHandler.getConfigValue(module) ? this.moduleEnabledCount.get(random) : this.moduleDisabledCount.get(random);
    }

    public PlacementModifierType<?> getType() {
        return NJBPlacementModifiers.MODULE_BASED_COUNT;
    }
}
