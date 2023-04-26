package dev.worldgen.njb.registry;

import dev.worldgen.njb.NotJustBiomes;
import dev.worldgen.njb.mixin.StructureFeatureAccessor;
import dev.worldgen.njb.worldgen.structure.AlternateMansionStructure;
import dev.worldgen.njb.worldgen.structure.AlternateMansionStructureConfig;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;

public class NJBStructures {
    public static StructureFeature<?> MANSION = new AlternateMansionStructure(AlternateMansionStructureConfig.CODEC);

    public static void init() {
        StructureFeatureAccessor.callRegister("njb:mansion", MANSION, GenerationStep.Feature.SURFACE_STRUCTURES);
    }
}
