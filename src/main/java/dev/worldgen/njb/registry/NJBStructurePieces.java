package dev.worldgen.njb.registry;

import dev.worldgen.njb.structure.AlternateMansionGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.structure.StructurePieceType;
import static dev.worldgen.njb.registry.RegistryUtils.register;

public class NJBStructurePieces {

    public static final Registry<StructurePieceType> registry = Registry.STRUCTURE_PIECE;
    public static final StructurePieceType ALTERNATE_MANSION_PIECE = register(
        registry, "alt_mansion", AlternateMansionGenerator.Piece::new
    );

    public static void init() {}
}
