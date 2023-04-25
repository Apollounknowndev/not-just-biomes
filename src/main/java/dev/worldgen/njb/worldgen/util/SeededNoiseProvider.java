package dev.worldgen.njb.worldgen.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;


public class SeededNoiseProvider {
    public static final Codec<SeededNoiseProvider> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            Codec.LONG.fieldOf("seed").forGetter((seededNoiseProvider) -> {
                return seededNoiseProvider.seed;
            }), DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter((seededNoiseProvider) -> {
                return seededNoiseProvider.noiseParameters;
            }), Codec.FLOAT.fieldOf("xz_scale").forGetter((seededNoiseProvider) -> {
                return seededNoiseProvider.xz_scale;
            }), Codec.FLOAT.fieldOf("y_scale").orElse(0.0F).forGetter((seededNoiseProvider) -> {
                return seededNoiseProvider.y_scale;
            })
        ).apply(instance, SeededNoiseProvider::new);
    });
    public final DoublePerlinNoiseSampler.NoiseParameters noiseParameters;
    public final long seed;
    public final float xz_scale;
    public final float y_scale;

    private SeededNoiseProvider(long seed, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float xz_scale, float y_scale) {
        this.seed = seed;
        this.noiseParameters = noiseParameters;
        this.xz_scale = xz_scale;
        this.y_scale = y_scale;
    }
}
