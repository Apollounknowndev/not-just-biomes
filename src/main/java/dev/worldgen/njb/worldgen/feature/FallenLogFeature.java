package dev.worldgen.njb.worldgen.feature;

import dev.worldgen.njb.worldgen.feature.config.FallenLogConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import java.util.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class FallenLogFeature extends Feature<FallenLogConfig> {
    public FallenLogFeature(Codec<FallenLogConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<FallenLogConfig> context) {
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        FallenLogConfig config = context.getConfig();
        Direction direction = Direction.fromHorizontal(random.nextInt(4));
        Direction.Axis axis = direction.getAxis();
        boolean canPlace = true;
        for (int i = 0; i < config.logLength.get(random); ++i) {
            BlockPos relativePos = blockPos.offset(direction, i);
            canPlace = world.getBlockState(relativePos).getMaterial().isReplaceable() && world.getBlockState(relativePos.offset(Direction.DOWN, 1)).isIn(BlockTags.DIRT);
        }
        if (canPlace) {
            for (int i = 0; i < config.logLength.get(random); ++i) {
                BlockPos relativePos = blockPos.offset(direction, i);
                BlockState logState = config.logProvider.getBlockState(random, relativePos).with(PillarBlock.AXIS, axis);
                BlockState aboveLogState = config.aboveLogProvider.getBlockState(random, relativePos.up());
                world.setBlockState(relativePos, logState, Block.NOTIFY_LISTENERS);
                if (random.nextFloat() < config.aboveLogPlacementChance) {
                    world.setBlockState(relativePos.up(), aboveLogState, Block.NOTIFY_LISTENERS);
                }
            }
            return true;
        }
        return false;
    }
}
