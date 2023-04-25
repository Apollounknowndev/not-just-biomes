package apollo.njb.registry;

import apollo.njb.worldgen.structure.AlternateMansionStructure;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.structure.StructureType;
import static apollo.njb.registry.RegistryUtils.register;

public class NJBStructures {

    public static final Registry<StructureType<?>> registry = Registries.STRUCTURE_TYPE;
    public static final StructureType<AlternateMansionStructure> ALTERNATE_MANSION = register(
            registry, "mansion", () -> AlternateMansionStructure.CODEC
    );
    public static void init() {}
}
