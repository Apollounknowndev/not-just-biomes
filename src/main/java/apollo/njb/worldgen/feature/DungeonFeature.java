package apollo.njb.worldgen.feature;

import apollo.njb.NotJustBiomes;
import apollo.njb.worldgen.feature.config.DungeonConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.function.Predicate;

public class DungeonFeature extends Feature<DungeonConfig> {
    public DungeonFeature(Codec<DungeonConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DungeonConfig> context) {
        Predicate<BlockState> predicate = Feature.notInBlockTagPredicate(BlockTags.FEATURES_CANNOT_REPLACE);
        StructureWorldAccess world = context.getWorld();
        DungeonConfig config = context.getConfig();
        BlockPos blockPos = context.getOrigin();
        Random random = context.getRandom();
        BlockPos blockPos2;
        int x;
        int y;
        int z;
        int length = config.xRadius.get(random);
        int width = config.zRadius.get(random);
        int height = config.height.get(random)-2;
        int r = 0;
        int s = 0;
        for (x = -length; x <= length; x++) {
            for (y = -1; y <= height; y++) {
                for (z = -width; z <= width; z++) {
                    blockPos2 = blockPos.add(x, y, z);
                    Material material = world.getBlockState(blockPos2).getMaterial();
                    boolean bl = material.isSolid();
                    if (y == -1 && !bl) {
                        return false;
                    }if (y == height && !bl) {
                        s++;
                    }
                    if (Math.abs(x) != length && Math.abs(z) != width || y != 0 || !world.isAir(blockPos2) || !world.isAir(blockPos2.up())) continue;
                    r++;
                }
            }
        }
        if (r < config.minAirOpenings || r > config.maxAirOpenings || s > 4) {
            return false;
        }
        for (x = -length; x <= length; x++) {
            for (y = -1; y <= height; y++) {
                for (z = -width; z <= width; z++) {
                    blockPos2 = blockPos.add(x, y, z);
                    BlockState blockState = world.getBlockState(blockPos2);
                    if (config.mountainOverride) {
                        boolean bl = ((Math.abs(x) == length-1 && Math.abs(z) == width-1) && y != -1);
                        boolean bl2 = ((Math.abs(x) == length-1 || Math.abs(z) == width-1) && !(Math.abs(x) == length || Math.abs(z) == width) && y == height && !bl);
                        if (bl || bl2) {
                            Direction.Axis axis = bl ? Direction.Axis.Y : (Math.abs(x) == length - 1 ? Direction.Axis.Z : Direction.Axis.X);
                            if (!blockState.getMaterial().isSolid() || blockState.isOf(Blocks.CHEST)) continue;
                            this.setBlockStateIf(world, blockPos2, Blocks.SPRUCE_LOG.getDefaultState().with(PillarBlock.AXIS, axis), predicate);
                            continue;
                        }
                    }
                    if (Math.abs(x) == length || y == -1 || Math.abs(z) == width) {
                        if (blockPos2.getY() >= world.getBottomY() && !world.getBlockState(blockPos2.down()).getMaterial().isSolid()) {
                            world.setBlockState(blockPos2, config.airState.get(random, blockPos2), Block.NOTIFY_LISTENERS);
                            continue;
                        }
                        if (!blockState.getMaterial().isSolid() || blockState.isOf(Blocks.CHEST)) continue;
                        if (config.bandlandsOverride) {
                            if (blockState.isIn(BlockTags.TERRACOTTA)) continue;
                            this.setBlockStateIf(world, blockPos2, Blocks.TERRACOTTA.getDefaultState(), predicate);
                            continue;
                        }
                        if (y == -1) {
                            this.setBlockStateIf(world, blockPos2, config.floorState.get(random, blockPos2), predicate);
                            continue;
                        }
                        this.setBlockStateIf(world, blockPos2, config.wallState.get(random, blockPos2), predicate);
                        continue;
                    }
                    if (blockState.isOf(Blocks.CHEST) || blockState.isOf(Blocks.SPAWNER)) continue;
                    this.setBlockStateIf(world, blockPos2, config.airState.get(random, blockPos2), predicate);
                }
            }
        }
        block6: for (x = 0; x < config.maxChestPlacementAttempts.get(random); ++x) {
            for (y = 0; y < 3; ++y) {
                int u = blockPos.getX() + random.nextBetween(-length+1,length-1);
                int v = blockPos.getY();
                int w = blockPos.getZ() + random.nextBetween(-width+1,width-1);
                BlockPos blockPos3 = new BlockPos(u, v, w);
                if (!world.isAir(blockPos3)) continue;
                int aa = 0;
                for (Direction direction : Direction.Type.HORIZONTAL) {
                    if (!world.getBlockState(blockPos3.offset(direction)).getMaterial().isSolid()) continue;
                    ++aa;
                }
                if (aa != 1) continue;
                this.setBlockStateIf(world, blockPos3, StructurePiece.orientateChest(world, blockPos3, Blocks.CHEST.getDefaultState()), predicate);
                LootableContainerBlockEntity.setLootTable(world, random, blockPos3, LootTables.SIMPLE_DUNGEON_CHEST);
                continue block6;
            }
        }
        this.setBlockStateIf(world, blockPos, Blocks.SPAWNER.getDefaultState(), predicate);
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof MobSpawnerBlockEntity mobSpawnerBlockEntity) {
            mobSpawnerBlockEntity.setEntityType(config.spawnerMobs.getRandom(random).get().value(), random);
        } else {
            NotJustBiomes.LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
        for (int i = 0; i < config.ceilingFeaturePlacements.get(random); i++) {
            int u = blockPos.getX() + random.nextBetween(-length+1,length-1);
            int v = blockPos.getY() + height;
            int w = blockPos.getZ() + random.nextBetween(-width+1,width-1);
            BlockPos blockPos3 = new BlockPos(u, v, w);
            config.ceilingFeature.value().generateUnregistered(world, context.getGenerator(), random, blockPos3);
        }
        return true;
    }
}
