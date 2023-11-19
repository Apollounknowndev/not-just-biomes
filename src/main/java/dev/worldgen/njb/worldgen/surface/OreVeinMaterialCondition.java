package dev.worldgen.njb.worldgen.surface;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

public record OreVeinMaterialCondition(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> toggle, RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ridgedA, RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ridgedB, double threshold, int minY, int maxY, boolean usePositiveToggle) implements MaterialRules.MaterialCondition {

    public static final CodecHolder<OreVeinMaterialCondition> CODEC = CodecHolder.of(RecordCodecBuilder.create(instance -> instance.group(
        RegistryKey.createCodec(RegistryKeys.NOISE_PARAMETERS).fieldOf("toggle").forGetter(OreVeinMaterialCondition::toggle),
        RegistryKey.createCodec(RegistryKeys.NOISE_PARAMETERS).fieldOf("ridged_a").forGetter(OreVeinMaterialCondition::ridgedA),
        RegistryKey.createCodec(RegistryKeys.NOISE_PARAMETERS).fieldOf("ridged_b").forGetter(OreVeinMaterialCondition::ridgedB),
        Codec.DOUBLE.fieldOf("threshold").forGetter(OreVeinMaterialCondition::threshold),
        Codec.INT.fieldOf("min_y").forGetter(OreVeinMaterialCondition::minY),
        Codec.INT.fieldOf("max_y").forGetter(OreVeinMaterialCondition::maxY),
        Codec.BOOL.fieldOf("use_positive_toggle").forGetter(OreVeinMaterialCondition::usePositiveToggle)
    ).apply(instance, OreVeinMaterialCondition::new)));

    @Override
    public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
        return CODEC;
    }

    @Override
    public MaterialRules.BooleanSupplier apply(final MaterialRules.MaterialRuleContext materialRuleContext) {
        class OreVeinPredicate extends MaterialRules.FullLazyAbstractPredicate {
            OreVeinPredicate() {
                super(materialRuleContext);
            }

            protected boolean test() {
                NoiseConfig noiseConfig = context.noiseConfig;
                DoublePerlinNoiseSampler toggleNoise = context.noiseConfig.getOrCreateSampler(OreVeinMaterialCondition.this.toggle);
                DoublePerlinNoiseSampler ridgedANoise = context.noiseConfig.getOrCreateSampler(OreVeinMaterialCondition.this.ridgedA);
                DoublePerlinNoiseSampler ridgedBNoise = context.noiseConfig.getOrCreateSampler(OreVeinMaterialCondition.this.ridgedB);
                RandomSplitter randomDeriver = noiseConfig.getOreRandomDeriver();
                DensityFunction.UnblendedNoisePos noisePos = new DensityFunction.UnblendedNoisePos(context.blockX, context.blockY, context.blockZ);

                int i = noisePos.blockY();
                int j = OreVeinMaterialCondition.this.maxY - i;
                int k = i - OreVeinMaterialCondition.this.minY;
                if (k < 0 || j < 0) return false;


                double d = toggleNoise.sample(noisePos.blockX() * 1.5, noisePos.blockY() * 1.5, noisePos.blockZ() * 1.5);
                if (d < 0 == OreVeinMaterialCondition.this.usePositiveToggle) {
                    return false;
                }
                double e = Math.abs(d);
                int l = Math.min(j, k);
                double f = MathHelper.clampedMap(l, 0.0, 20.0, -0.2, 0.0);
                if (e + f < OreVeinMaterialCondition.this.threshold) {
                    return false;
                } else {
                    Random random = randomDeriver.split(noisePos.blockX(), i, noisePos.blockZ());
                    if (random.nextFloat() > 0.875F) {
                        return false;
                    } else {
                        double a = Math.abs(ridgedANoise.sample(noisePos.blockX() * 4, noisePos.blockY() * 4, noisePos.blockZ() * 4));
                        double b = Math.abs(ridgedBNoise.sample(noisePos.blockX() * 4, noisePos.blockY() * 4, noisePos.blockZ() * 4));
                        return !(Math.max(a, b)-0.08 >= 0);
                    }
                }
            }

        }

        return new OreVeinPredicate();
    }
}
