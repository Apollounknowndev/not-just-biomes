package dev.worldgen.njb.registry;

import dev.worldgen.njb.worldgen.structure.AlternateMansionStructure;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.StructureType;

public class NJBStructures {

    public static final Registry<StructureType<?>> registry = Registry.STRUCTURE_TYPE;
    public static final StructureType<AlternateMansionStructure> ALTERNATE_MANSION = RegistryUtils.register(
            registry, "mansion", () -> AlternateMansionStructure.CODEC
    );
    public static void init() {}
}
