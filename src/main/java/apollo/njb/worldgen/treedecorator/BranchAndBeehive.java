package apollo.njb.worldgen.treedecorator;

import apollo.njb.registry.NJBTreeDecorators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class BranchAndBeehive extends TreeDecorator {
    public static final Codec<BranchAndBeehive> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            BlockState.CODEC.fieldOf("branch_provider").forGetter((treeDecorator) -> {
                return treeDecorator.branchProvider;
            }),
            Codec.floatRange(0.0F, 1.0F).fieldOf("branch_probability").forGetter((treeDecorator) -> {
                return treeDecorator.branchProbability;
            }),
            Codec.floatRange(0.0F, 1.0F).fieldOf("beehive_probability").forGetter((treeDecorator) -> {
                return treeDecorator.beehiveProbability;
            })
        ).apply(instance, BranchAndBeehive::new);
    });
    private final BlockState branchProvider;
    private final float branchProbability;
    private final float beehiveProbability;

    public BranchAndBeehive(BlockState branchProvider, float branchProbability, float beehiveProbability) {
        this.branchProvider = branchProvider;
        this.branchProbability = branchProbability;
        this.beehiveProbability = beehiveProbability;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return NJBTreeDecorators.BRANCH_AND_BEEHIVE;
    }

    @Override
    public void generate(TreeDecorator.Generator generator) {
        Random random = generator.getRandom();
        if (random.nextFloat() < this.branchProbability) {
            ObjectArrayList<BlockPos> logPositions = generator.getLogPositions();
            logPositions.remove(logPositions.size()-1);
            for(int i = 0; i < 3; i++) {
                if (logPositions.size() <= 1) break;
                logPositions.remove(logPositions.size()-1);
                logPositions.remove(0);
            }
            if (logPositions.size() == 0) return;
            Direction branchDirection = Direction.fromHorizontal(random.nextInt(4));
            BlockPos pos = logPositions.get(random.nextInt(logPositions.size())).offset(branchDirection);
            if(generator.isAir(pos)) {
                Direction.Axis axis = branchDirection.getAxis();
                generator.replace(pos, branchProvider.with(PillarBlock.AXIS, axis));
                if(random.nextFloat() < this.beehiveProbability){
                    generator.replace(pos.down(), Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, branchDirection));
                    generator.getWorld().getBlockEntity(pos.down(), BlockEntityType.BEEHIVE).ifPresent(blockEntity -> {
                        int i = 2 + random.nextInt(2);
                        for (int j = 0; j < i; ++j) {
                            NbtCompound nbtCompound = new NbtCompound();
                            nbtCompound.putString("id", Registries.ENTITY_TYPE.getId(EntityType.BEE).toString());
                            blockEntity.addBee(nbtCompound, random.nextInt(599), false);
                        }
                    });
                }
            }
        }
    }
}

