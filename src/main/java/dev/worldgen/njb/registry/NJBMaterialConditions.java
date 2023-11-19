package dev.worldgen.njb.registry;

import com.mojang.serialization.Codec;
import dev.worldgen.njb.worldgen.surface.BiomeEntryMaterialCondition;
import dev.worldgen.njb.worldgen.surface.OreVeinMaterialCondition;
import dev.worldgen.njb.worldgen.surface.RandomMaterialCondition;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

import static dev.worldgen.njb.registry.RegistryUtils.register;

public class NJBMaterialConditions {
    public static final Registry<Codec<? extends MaterialRules.MaterialCondition>> registry = Registries.MATERIAL_CONDITION;

    public static final Codec<OreVeinMaterialCondition> ORE_VEIN = register(
        registry, "ore_vein", OreVeinMaterialCondition.CODEC.codec()
    );
    public static final Codec<RandomMaterialCondition> RANDOM = register(
        registry, "random", RandomMaterialCondition.CODEC.codec()
    );
    public static final Codec<BiomeEntryMaterialCondition> BIOME = register(
        registry, "biome", BiomeEntryMaterialCondition.CODEC.codec()
    );

    public static void init() {}
}
