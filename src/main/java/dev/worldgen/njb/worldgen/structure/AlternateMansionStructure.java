package dev.worldgen.njb.worldgen.structure;

import dev.worldgen.njb.config.ConfigHandler;
import dev.worldgen.njb.structure.AlternateMansionGenerator;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.*;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import java.util.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AlternateMansionStructure extends StructureFeature<AlternateMansionStructureConfig> {

    public AlternateMansionStructure(Codec<AlternateMansionStructureConfig> configCodec) {
        super(configCodec, AlternateMansionStructure::addPieces, AlternateMansionStructure::postPlace);
    }

    private static Optional<StructurePiecesGenerator<AlternateMansionStructureConfig>> addPieces(StructureGeneratorFactory.Context<AlternateMansionStructureConfig> context) {
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
        chunkRandom.setCarverSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        BlockRotation blockRotation = BlockRotation.random(chunkRandom);
        int i = 5;
        int j = 5;
        if (blockRotation == BlockRotation.CLOCKWISE_90) {
            i = -5;
        } else if (blockRotation == BlockRotation.CLOCKWISE_180) {
            i = -5;
            j = -5;
        } else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
            j = -5;
        }

        int k = context.chunkPos().getOffsetX(7);
        int l = context.chunkPos().getOffsetZ(7);
        int[] is = context.getCornerHeights(k, i, l, j);
        int m = Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
        if (m < 60) {
            return Optional.empty();
        } else if (!context.validBiome().test(context.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(k), BiomeCoords.fromBlock(is[0]), BiomeCoords.fromBlock(l)))) {
            return Optional.empty();
        } else {
            BlockPos blockPos = new BlockPos(context.chunkPos().getCenterX(), m + 1, context.chunkPos().getCenterZ());
            return Optional.of((collector, contextx) -> {
                if (ConfigHandler.getConfigValue("mansion")) {
                    List<AlternateMansionGenerator.Piece> list = Lists.newLinkedList();
                    AlternateMansionGenerator.addPieces(contextx.structureManager(), blockPos, blockRotation, list, chunkRandom, contextx.config().roomTemplates);
                    Objects.requireNonNull(collector);
                    list.forEach(collector::addPiece);
                } else {
                    List<WoodlandMansionGenerator.Piece> list = Lists.newLinkedList();
                    WoodlandMansionGenerator.addPieces(contextx.structureManager(), blockPos, blockRotation, list, chunkRandom);
                    Objects.requireNonNull(collector);
                    list.forEach(collector::addPiece);
                }
            });
        }
    }

    public static void postPlace(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, StructurePiecesList pieces) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockBox blockBox = pieces.getBoundingBox();
        boolean moduleEnabled = ConfigHandler.getConfigValue("mansion");
        int i = world.getBottomY();
        int j = blockBox.getMinY();

        for(int k = chunkBox.getMinX(); k <= chunkBox.getMaxX(); ++k) {
            for(int l = chunkBox.getMinZ(); l <= chunkBox.getMaxZ(); ++l) {
                mutable.set(k, j, l);
                if (!world.isAir(mutable) && blockBox.contains(mutable) && pieces.contains(mutable)) {
                    for(int m = j - 1; m > i; --m) {
                        mutable.setY(m);
                        if (!world.isAir(mutable) && !world.getBlockState(mutable).getMaterial().isLiquid()) {
                            break;
                        }
                        // TODO: Use the foundationProvider
                        BlockState secondaryBlockState = moduleEnabled ?  Blocks.MOSSY_COBBLESTONE.getDefaultState() : Blocks.COBBLESTONE.getDefaultState();
                        world.setBlockState(mutable, random.nextInt(5) == 1 ? secondaryBlockState : Blocks.COBBLESTONE.getDefaultState(), 2);
                    }
                }
            }
        }
    }


}
