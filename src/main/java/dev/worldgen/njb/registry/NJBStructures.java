package dev.worldgen.njb.registry;

import dev.worldgen.njb.worldgen.structure.AlternateMansionStructure;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.structure.StructureType;

public class NJBStructures {

    public static final Registry<StructureType<?>> registry = Registries.STRUCTURE_TYPE;
    public static final StructureType<AlternateMansionStructure> ALTERNATE_MANSION = RegistryUtils.register(
            registry, "mansion", () -> AlternateMansionStructure.CODEC
    );
    public static void init() {}
}
