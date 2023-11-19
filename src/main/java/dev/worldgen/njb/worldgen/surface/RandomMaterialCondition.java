package dev.worldgen.njb.worldgen.surface;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

public record RandomMaterialCondition(Identifier seed, double chance) implements MaterialRules.MaterialCondition {

    public static final CodecHolder<RandomMaterialCondition> CODEC = CodecHolder.of(
        RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("seed").forGetter(RandomMaterialCondition::seed),
            Codec.DOUBLE.fieldOf("chance").forGetter(RandomMaterialCondition::chance)
        ).apply(instance, RandomMaterialCondition::new))
    );

    @Override
    public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
        return CODEC;
    }

    @Override
    public MaterialRules.BooleanSupplier apply(final MaterialRules.MaterialRuleContext materialRuleContext) {
        class RandomPredicate extends MaterialRules.FullLazyAbstractPredicate {
            RandomPredicate() {
                super(materialRuleContext);
            }

            protected boolean test() {
                NoiseConfig noiseConfig = context.noiseConfig;
                DensityFunction.UnblendedNoisePos noisePos = new DensityFunction.UnblendedNoisePos(context.blockX, context.blockY, context.blockZ);
                Random random = noiseConfig.getOrCreateRandomDeriver(RandomMaterialCondition.this.seed).split(noisePos.blockX(), noisePos.blockY(), noisePos.blockZ());
                return (RandomMaterialCondition.this.chance == 1 || random.nextFloat() < RandomMaterialCondition.this.chance);
            }
        }

        return new RandomPredicate();
    }
}
