package dev.worldgen.njb.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;


public record SeededNoiseProvider(DoublePerlinNoiseSampler.NoiseParameters noiseParameters, long seed, float xzScale, float yScale) {
    public static final Codec<SeededNoiseProvider> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("noise").forGetter(SeededNoiseProvider::noiseParameters),
        Codec.LONG.fieldOf("seed").forGetter(SeededNoiseProvider::seed),
        Codec.FLOAT.fieldOf("xz_scale").forGetter(SeededNoiseProvider::xzScale),
        Codec.FLOAT.fieldOf("y_scale").orElse(0.0F).forGetter(SeededNoiseProvider::yScale)
    ).apply(instance, SeededNoiseProvider::new));
}
