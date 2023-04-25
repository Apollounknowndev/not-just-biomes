package apollo.njb.worldgen.structure;

import apollo.njb.config.ConfigHandler;
import apollo.njb.structure.AlternateMansionGenerator;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AlternateMansionStructure extends Structure {
    public static final Codec<AlternateMansionStructure> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            Structure.configCodecBuilder(instance),
            MansionTemplates.ROOM_CODEC.fieldOf("room_templates").forGetter((mansion) -> {
                return mansion.roomTemplates;
            }),
            BlockStateProvider.TYPE_CODEC.fieldOf("foundation_provider").forGetter((mansion) -> {
                return mansion.foundationProvider;
            })
        ).apply(instance, AlternateMansionStructure::new);
    });
    public final MansionTemplates roomTemplates;

    public final BlockStateProvider foundationProvider;

    public AlternateMansionStructure(Structure.Config config, MansionTemplates roomTemplates, BlockStateProvider foundationProvider) {
        super(config);
        this.roomTemplates = roomTemplates;
        this.foundationProvider = foundationProvider;
    }

    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        BlockRotation blockRotation = BlockRotation.random(context.random());
        BlockPos blockPos = this.getShiftedPos(context, blockRotation);
        return blockPos.getY() < 60 ? Optional.empty() : Optional.of(new Structure.StructurePosition(blockPos, (collector) -> {
            this.addPieces(collector, context, blockPos, blockRotation);
        }));
    }

    private void addPieces(StructurePiecesCollector collector, Structure.Context context, BlockPos pos, BlockRotation rotation) {
        if (ConfigHandler.getConfigValue("mansion")) {
            List<AlternateMansionGenerator.Piece> list = Lists.newLinkedList();
            AlternateMansionGenerator.addPieces(context.structureTemplateManager(), pos, rotation, list, context.random(), this.roomTemplates);
            Objects.requireNonNull(collector);
            list.forEach(collector::addPiece);
        } else {
            List<WoodlandMansionGenerator.Piece> list = Lists.newLinkedList();
            WoodlandMansionGenerator.addPieces(context.structureTemplateManager(), pos, rotation, list, context.random());
            Objects.requireNonNull(collector);
            list.forEach(collector::addPiece);
        }
    }

    public void postPlace(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox box, ChunkPos chunkPos, StructurePiecesList pieces) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockBox blockBox = pieces.getBoundingBox();
        boolean moduleEnabled = ConfigHandler.getConfigValue("mansion");
        int i = world.getBottomY();
        int j = blockBox.getMinY();

        for(int k = box.getMinX(); k <= box.getMaxX(); ++k) {
            for(int l = box.getMinZ(); l <= box.getMaxZ(); ++l) {
                mutable.set(k, j, l);
                if (!world.isAir(mutable) && blockBox.contains(mutable) && pieces.contains(mutable)) {
                    for(int m = j - 1; m > i; --m) {
                        mutable.setY(m);
                        if (!world.isAir(mutable) && !world.getBlockState(mutable).getMaterial().isLiquid()) {
                            break;
                        }

                        world.setBlockState(mutable, moduleEnabled ? foundationProvider.get(random, new BlockPos(k, m, l)) : Blocks.COBBLESTONE.getDefaultState(), 2);
                    }
                }
            }
        }
    }

    public StructureType<?> getType() {
        return StructureType.WOODLAND_MANSION;
    }
}
