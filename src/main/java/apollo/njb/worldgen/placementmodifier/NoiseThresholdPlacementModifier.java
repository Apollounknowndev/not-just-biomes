package apollo.njb.worldgen.placementmodifier;

import apollo.njb.registry.NJBPlacementModifiers;
import apollo.njb.worldgen.util.SeededNoiseProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.placementmodifier.AbstractCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
public class NoiseThresholdPlacementModifier extends AbstractCountPlacementModifier {
    public static final Codec<NoiseThresholdPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            SeededNoiseProvider.CODEC.fieldOf("noise_provider").forGetter((noiseThreshold) -> {
                return noiseThreshold.noiseProvider;
            }), Codec.DOUBLE.fieldOf("lower_threshold").orElse(-64.0).forGetter((noiseThreshold) -> {
                return noiseThreshold.lowerThreshold;
            }), Codec.DOUBLE.fieldOf("upper_threshold").orElse(64.0).forGetter((noiseThreshold) -> {
                return noiseThreshold.upperThreshold;
            }), Codec.INT.fieldOf("inside_bounds").forGetter((noiseThreshold) -> {
                return noiseThreshold.insideBounds;
            }), Codec.INT.fieldOf("outside_bounds").forGetter((noiseThreshold) -> {
                return noiseThreshold.outsideBounds;
            })
        ).apply(instance, NoiseThresholdPlacementModifier::new);
    });
    private final double lowerThreshold;
    private final double upperThreshold;
    private final int insideBounds;
    private final int outsideBounds;
    private final DoublePerlinNoiseSampler noiseSampler;
    private final SeededNoiseProvider noiseProvider;

    private NoiseThresholdPlacementModifier(SeededNoiseProvider noiseProvider, double lowerThreshold, double upperThreshold, int insideBounds, int outsideBounds) {
        this.noiseProvider = noiseProvider;
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
        this.insideBounds = insideBounds;
        this.outsideBounds = outsideBounds;
        this.noiseSampler = DoublePerlinNoiseSampler.create(new ChunkRandom(new CheckedRandom(noiseProvider.seed)), noiseProvider.noiseParameters);
    }

    protected int getCount(Random random, BlockPos pos) {
        double d = this.noiseSampler.sample((double)pos.getX() * noiseProvider.xz_scale, (double)pos.getY() * noiseProvider.y_scale, (double)pos.getZ() * noiseProvider.xz_scale);
        return d < this.upperThreshold && d > this.lowerThreshold ? this.insideBounds : this.outsideBounds;
    }

    public PlacementModifierType<?> getType() {
        return NJBPlacementModifiers.NOISE_THRESHOLD;
    }
}
