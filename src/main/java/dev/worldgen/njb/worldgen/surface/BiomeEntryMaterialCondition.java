package dev.worldgen.njb.worldgen.surface;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

public record BiomeEntryMaterialCondition(RegistryEntryList<Biome> biomes) implements MaterialRules.MaterialCondition {

    public static final CodecHolder<BiomeEntryMaterialCondition> CODEC = CodecHolder.of(
        RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.entryList(RegistryKeys.BIOME).fieldOf("biomes").forGetter(BiomeEntryMaterialCondition::biomes)
        ).apply(instance, BiomeEntryMaterialCondition::new))
    );

    @Override
    public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
        return CODEC;
    }

    @Override
    public MaterialRules.BooleanSupplier apply(final MaterialRules.MaterialRuleContext materialRuleContext) {
        class BiomeEntryPredicate extends MaterialRules.FullLazyAbstractPredicate {
            BiomeEntryPredicate() {
                super(materialRuleContext);
            }

            protected boolean test() {
                return BiomeEntryMaterialCondition.this.biomes.contains(this.context.biomeSupplier.get());
            }
        }

        return new BiomeEntryPredicate();
    }
}
