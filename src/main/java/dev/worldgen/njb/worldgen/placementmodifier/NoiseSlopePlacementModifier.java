package dev.worldgen.njb.worldgen.placementmodifier;

import dev.worldgen.njb.config.ConfigHandler;
import dev.worldgen.njb.registry.NJBPlacementModifiers;
import dev.worldgen.njb.worldgen.util.SeededNoiseProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.placementmodifier.AbstractCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
public class NoiseSlopePlacementModifier extends AbstractCountPlacementModifier {
    public static final Codec<NoiseSlopePlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            SeededNoiseProvider.CODEC.fieldOf("noise_provider").forGetter((noiseSlope) -> {
                return noiseSlope.noiseProvider;
            }), Codec.DOUBLE.fieldOf("noise_threshold").forGetter((noiseSlope) -> {
                return noiseSlope.noiseThreshold;
            }), Codec.INT.fieldOf("slope").forGetter((noiseSlope) -> {
                return noiseSlope.noiseCountMultiplier;
            }), Codec.INT.fieldOf("count_offset").orElse(0).forGetter((noiseSlope) -> {
                return noiseSlope.noiseCountMultiplier;
            }), Codec.BOOL.fieldOf("discard_check_if_module_disabled").orElse(false).forGetter((noiseSlope) -> {
                return noiseSlope.discardCheckIfModuleDisabled;
            }), Codec.STRING.fieldOf("module").orElse("").forGetter(noiseSlope -> {
                return noiseSlope.module;
            }), IntProvider.VALUE_CODEC.fieldOf("module_disabled_count").orElse(ConstantIntProvider.ZERO).forGetter((noiseSlope) -> {
                return noiseSlope.moduleDisabledCount;
            })
        ).apply(instance, NoiseSlopePlacementModifier::new);
    });
    private final DoublePerlinNoiseSampler noiseSampler;
    private final SeededNoiseProvider noiseProvider;
    private final double noiseThreshold;
    private final int noiseCountMultiplier;
    private final int countOffset;
    private final boolean discardCheckIfModuleDisabled;
    private final String module;
    private final IntProvider moduleDisabledCount;

    private NoiseSlopePlacementModifier(SeededNoiseProvider noiseProvider, double noiseThreshold, int noiseCountMultiplier, int countOffset, boolean discardCheckIfModuleDisabled, String module, IntProvider moduleDisabledCount) {
        this.noiseSampler = DoublePerlinNoiseSampler.create(new ChunkRandom(new CheckedRandom(noiseProvider.seed)), noiseProvider.noiseParameters);
        this.noiseProvider = noiseProvider;
        this.noiseThreshold = noiseThreshold;
        this.noiseCountMultiplier = noiseCountMultiplier;
        this.countOffset = countOffset;
        this.discardCheckIfModuleDisabled = discardCheckIfModuleDisabled;
        this.module = module;
        this.moduleDisabledCount = moduleDisabledCount;
    }

    protected int getCount(Random random, BlockPos pos) {
        if (discardCheckIfModuleDisabled && !ConfigHandler.getConfigValue(module)) {return moduleDisabledCount.get(random);}
        double d = this.noiseSampler.sample((double)pos.getX() * noiseProvider.xz_scale, (double)pos.getY() * noiseProvider.y_scale, (double)pos.getZ() * noiseProvider.xz_scale);
        return d < this.noiseThreshold ? this.countOffset : this.countOffset+(int)Math.ceil((d - this.noiseThreshold) * (double)this.noiseCountMultiplier);
    }

    public PlacementModifierType<?> getType() {
        return NJBPlacementModifiers.NOISE_SLOPE;
    }
}
