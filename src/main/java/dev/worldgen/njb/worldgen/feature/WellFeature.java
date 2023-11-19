package dev.worldgen.njb.worldgen.feature;

import com.mojang.serialization.Codec;
import dev.worldgen.njb.NotJustBiomes;
import dev.worldgen.njb.worldgen.feature.config.WellConfig;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class WellFeature extends Feature<WellConfig> {


    public WellFeature(Codec<WellConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<WellConfig> context) {
        StructureWorldAccess world = context.getWorld();
        WellConfig config = context.getConfig();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();
        BlockPos pos;

        int x;
        int y;
        int z;
        for(x = -2; x <= 2; ++x) {
            for(z = -2; z <= 2; ++z) {
                for(y = -1; y <= 3; ++y) {
                    pos = origin.add(x, y, z);
                    boolean edge = Math.abs(x) == 2 || Math.abs(z) == 2;
                    boolean middle = Math.abs(x) == 1 && Math.abs(z) == 1;
                    boolean center = x == 0 && z == 0;
                    boolean aligned = x == 0 || z == 0;

                    if (y > 0 && edge) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                        continue;
                    }
                    if (edge) {
                        world.setBlockState(pos, (aligned && y == 0) ? config.slabState().get(random, pos) : config.standardState().get(random, pos), 2);
                        continue;
                    }
                    if (middle && y != 3) {
                        world.setBlockState(pos, config.standardState().get(random, pos), 2);
                        continue;
                    }
                    if (y == 3) {
                        world.setBlockState(pos, center ? config.standardState().get(random, pos) : config.slabState().get(random, pos), 2);
                        continue;
                    }
                    if (y == -1) {
                        world.setBlockState(pos, config.fluidState().get(random, pos), 2);
                        if (config.placeSuspiciousBlock() && random.nextBoolean()) {
                            BlockPos downPos = pos.down();
                            world.setBlockState(downPos, config.suspiciousState(), 2);
                            world.getBlockEntity(downPos, BlockEntityType.BRUSHABLE_BLOCK).ifPresent((block) -> {
                                block.setLootTable(config.suspiciousLootTable(), downPos.asLong());
                            });
                        } else {
                            world.setBlockState(pos.down(), config.groundState().get(random, pos), 2);
                        }
                        continue;
                    }
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                }
                world.setBlockState(origin.add(x, -3, z), config.standardState().get(random, origin.add(x, -3, z)), 2);
            }
        }
        var selectedEntityType = config.entity().getRandom(random);
        if (selectedEntityType.isPresent() && config.entityChance() > 0.0F && random.nextFloat() < config.entityChance()) {
            Entity wellEntity = selectedEntityType.get().value().create(world.toServerWorld());
            assert wellEntity != null;
            float offset = random.nextBoolean() ? -3.5F : 3.5F;
            boolean xAxis = random.nextBoolean();
            wellEntity.refreshPositionAndAngles(origin.getX()+(xAxis ? offset : 0), origin.getY(), origin.getZ()+(xAxis ? 0 : offset), random.nextBetween(-180, 180), 0);
            world.spawnEntity(wellEntity);
        }
        return true;
    }
}
