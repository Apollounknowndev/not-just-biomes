package dev.worldgen.njb.worldgen.treedecorator;

import dev.worldgen.njb.registry.NJBTreeDecorators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
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
    public void generate(Generator generator) {
        Random random = generator.getRandom();
        if (this.probability > 0.0F && random.nextFloat() < this.probability) {
            generator.getLogPositions().forEach((pos) -> {
                BlockPos blockPos;
                blockPos = pos.west();
                if (generator.isAir(blockPos)) {
                    generator.replaceWithVine(blockPos, VineBlock.EAST);
                }
                blockPos = pos.east();
                if (generator.isAir(blockPos)) {
                    generator.replaceWithVine(blockPos, VineBlock.WEST);
                }
                blockPos = pos.north();
                if (generator.isAir(blockPos)) {
                    generator.replaceWithVine(blockPos, VineBlock.SOUTH);
                }
                blockPos = pos.south();
                if (generator.isAir(blockPos)) {
                    generator.replaceWithVine(blockPos, VineBlock.NORTH);
                }
            });
        }
    }
}

