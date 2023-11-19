package dev.worldgen.njb.worldgen.trunk;

import dev.worldgen.njb.registry.NJBTrunkPlacers;
import dev.worldgen.njb.worldgen.SeededNoiseProvider;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;
public class NoiseBasedStraight extends TrunkPlacer {
    public static final Codec<NoiseBasedStraight> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            Codec.intRange(0,32).fieldOf("minimum_height").forGetter((trunkPlacer) -> {
                return trunkPlacer.minimumHeight;
            }), Codec.intRange(0,32).fieldOf("maximum_height").forGetter((trunkPlacer) -> {
                return trunkPlacer.maximumHeight;
            }), Codec.intRange(0,24).fieldOf("height_random").forGetter((trunkPlacer) -> {
                return trunkPlacer.randomHeight;
            }), SeededNoiseProvider.CODEC.fieldOf("noise_provider").forGetter((trunkPlacer) -> {
                return trunkPlacer.noiseProvider;
            }), TrunkType.CODEC.fieldOf("trunk_type").orElse(TrunkType.DEFAULT).forGetter((trunkPlacer) -> {
                return trunkPlacer.trunkType;
            })).apply(instance, NoiseBasedStraight::new);
    });

    private final SeededNoiseProvider noiseProvider;
    private final DoublePerlinNoiseSampler noiseSampler;
    private final int minimumHeight;
    private final int maximumHeight;
    private final int randomHeight;
    private final TrunkType trunkType;

    public NoiseBasedStraight(int minimumHeight, int maximumHeight, int randomHeight, SeededNoiseProvider noiseProvider, TrunkType trunkType) {
        super(minimumHeight, maximumHeight, randomHeight);
        this.minimumHeight = minimumHeight;
        this.maximumHeight = maximumHeight;
        this.randomHeight = randomHeight;
        this.trunkType = trunkType;
        this.noiseProvider = noiseProvider;
        this.noiseSampler = DoublePerlinNoiseSampler.create(new ChunkRandom(new CheckedRandom(noiseProvider.seed())), noiseProvider.noiseParameters());

    }

    protected TrunkPlacerType<?> getType() {
        return NJBTrunkPlacers.NOISE_BASED_STRAIGHT;
    }

    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
        setToDirt(world, replacer, random, startPos.down(), config);

        double i = this.noiseSampler.sample((double)startPos.getX() * noiseProvider.xzScale(), (double)startPos.getY() * noiseProvider.yScale(), (double)startPos.getZ() * noiseProvider.xzScale());
        int trunkHeight = Math.toIntExact(Math.min(Math.max(Math.round(((float)(this.maximumHeight-this.minimumHeight)/2)*i+((float)(this.minimumHeight+this.maximumHeight)/2)),this.minimumHeight),this.maximumHeight))+ random.nextInt(this.randomHeight+1);

        if (this.trunkType == TrunkType.DEFAULT) {
            for (int k = 0; k < trunkHeight; ++k) {
                this.getAndSetState(world, replacer, random, startPos.up(k), config);
            }
            return ImmutableList.of(new FoliagePlacer.TreeNode(startPos.up(trunkHeight), 0, false));
        } else {
            // TODO: What is this abomination of code???
            int halfHeight = (int)Math.ceil((double)trunkHeight/2);
            for (int k = 0; k < halfHeight+1; ++k) {
                this.getAndSetState(world, replacer, random, startPos.up(k), config);
            }
            boolean r = random.nextBoolean();
            boolean hasDoubleBranches = random.nextBoolean();
            int offset = random.nextBoolean() ? 1 : -1;
            int offset2 = random.nextBoolean() ? 1 : -1;
            Axis axis = random.nextBoolean() ? Axis.X : Axis.Z;
            Axis oppositeAxis = axis == Axis.X ? Axis.Z : Axis.X;
            if (r) {
                for (int k = halfHeight; k < trunkHeight; ++k) {
                    this.getAndSetState(world, replacer, random, startPos.up(k).offset(axis, offset),
                            config);
                }
                if (hasDoubleBranches) {
                    for (int k = halfHeight; k < trunkHeight; ++k) {
                        this.getAndSetState(world, replacer, random, startPos.up(k).offset(axis,
                                offset * -1), config);
                    }
                    return ImmutableList.of(
                            new FoliagePlacer.TreeNode(startPos.up(trunkHeight-1).offset(axis, offset), 0,
                                    false),
                            new FoliagePlacer.TreeNode(startPos.up(trunkHeight-1).offset(axis, offset * -1), 0, false)
                    );
                } else {
                    return ImmutableList.of(new FoliagePlacer.TreeNode(startPos.up(trunkHeight-1).offset(axis, offset), 0, false));
                }
            } else {
                for (int k = halfHeight; k < trunkHeight; ++k) {
                    this.getAndSetState(world, replacer, random, startPos.up(k).offset(axis, offset).offset(oppositeAxis, offset2), config);
                }
                if (hasDoubleBranches) {
                    for (int k = halfHeight; k < trunkHeight; ++k) {
                        this.getAndSetState(world, replacer, random, startPos.up(k).offset(axis, offset * -1).offset(oppositeAxis, offset2 * -1), config);
                    }
                    return ImmutableList.of(
                            new FoliagePlacer.TreeNode(startPos.up(trunkHeight-1).offset(axis, offset).offset(oppositeAxis, offset2), 0, false),
                            new FoliagePlacer.TreeNode(startPos.up(trunkHeight-1).offset(axis, offset * -1).offset(oppositeAxis, offset2 * -1), 0, false)
                    );
                } else {
                    return ImmutableList.of(new FoliagePlacer.TreeNode(startPos.up(trunkHeight-1).offset(axis, offset).offset(oppositeAxis, offset2), 0, false));
                }
            }
        }
    }

    public static enum TrunkType implements StringIdentifiable {
        TECTONIC_OAK("tectonic_oak"),
        DEFAULT("default");

        public static final Codec<TrunkType> CODEC = StringIdentifiable.createCodec(TrunkType::values);
        private final String name;

        private TrunkType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}
