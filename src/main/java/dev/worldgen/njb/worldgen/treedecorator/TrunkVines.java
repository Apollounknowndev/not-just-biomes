package dev.worldgen.njb.worldgen.treedecorator;

import dev.worldgen.njb.registry.NJBTreeDecorators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
public class TrunkVines extends TreeDecorator {
    public static final Codec<TrunkVines> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((treeDecorator) -> {
                return treeDecorator.probability;
            })
        ).apply(instance, TrunkVines::new);
    });
    private final float probability;

    public TrunkVines(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return NJBTreeDecorators.TRUNK_VINES;
    }

    @Override
    public void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions) {
        if (this.probability > 0.0F && random.nextFloat() < this.probability) {
            logPositions.forEach((pos) -> {
                BlockPos blockPos;
                blockPos = pos.west();
                if (world.testBlockState(blockPos, BlockStatePredicate.forBlock(Blocks.AIR))) {
                    replacer.accept(blockPos, Blocks.VINE.getDefaultState().with(VineBlock.EAST, true));
                }
                blockPos = pos.east();
                if (world.testBlockState(blockPos, BlockStatePredicate.forBlock(Blocks.AIR))) {
                    replacer.accept(blockPos, Blocks.VINE.getDefaultState().with(VineBlock.WEST, true));
                }
                blockPos = pos.north();
                if (world.testBlockState(blockPos, BlockStatePredicate.forBlock(Blocks.AIR))) {
                    replacer.accept(blockPos, Blocks.VINE.getDefaultState().with(VineBlock.SOUTH, true));
                }
                blockPos = pos.south();
                if (world.testBlockState(blockPos, BlockStatePredicate.forBlock(Blocks.AIR))) {
                    replacer.accept(blockPos, Blocks.VINE.getDefaultState().with(VineBlock.NORTH, true));
                }
            });
        }
    }
}

