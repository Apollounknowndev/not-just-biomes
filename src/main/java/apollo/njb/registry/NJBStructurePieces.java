package apollo.njb.registry;

import apollo.njb.structure.AlternateMansionGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.StructurePieceType;
import static apollo.njb.registry.RegistryUtils.register;

public class NJBStructurePieces {

    public static final Registry<StructurePieceType> registry = Registries.STRUCTURE_PIECE;
    public static final StructurePieceType ALTERNATE_MANSION_PIECE = register(
        registry, "alt_mansion", AlternateMansionGenerator.Piece::new
    );

    public static void init() {}
}
