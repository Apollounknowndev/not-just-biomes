package dev.worldgen.njb.worldgen.feature;

import dev.worldgen.njb.worldgen.feature.config.RockConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.ForestRockFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RockFeature extends Feature<RockConfig> {
    public RockFeature(Codec<RockConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<RockConfig> context) {
        BlockState blockState;
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        RockConfig config = context.getConfig();
        while (blockPos.getY() > world.getBottomY() + 3 && (world.isAir(blockPos.down()) || !ForestRockFeature.isSoil(blockState = world.getBlockState(blockPos.down())) && !ForestRockFeature.isStone(blockState))) {
            blockPos = blockPos.down();
        }
        if (blockPos.getY() <= world.getBottomY() + 3) {
            return false;
        }
        for (int i = 0; i < config.rockPlacements.get(random); ++i) {
            for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-2, -2, -2), blockPos.add(2, 2, 2))) {
                double j = 2.25+((double)(random.nextInt(5)- 2)/5);
                if (blockPos2.getSquaredDistance(blockPos) <= j && (world.getBlockState(blockPos2) == Blocks.GRASS_BLOCK.getDefaultState() || world.getBlockState(blockPos2).getMaterial().isReplaceable())) {
                    world.setBlockState(blockPos2, config.state.getBlockState(random, blockPos), Block.NOTIFY_LISTENERS);
                    if (blockPos2.up().getSquaredDistance(blockPos) > j && world.getBlockState(blockPos2.up()).getMaterial().isReplaceable() && config.aboveRockState.isPresent() && random.nextDouble() < config.aboveRockPlacementChance) {
                        world.setBlockState(blockPos2.up(), config.aboveRockState.get().getBlockState(random, blockPos), Block.NOTIFY_LISTENERS);
                    }
                }
            }
            blockPos = blockPos.add(-1 + random.nextInt(3), -random.nextInt(2), -1 + random.nextInt(3));
        }
        return true;
    }
}
