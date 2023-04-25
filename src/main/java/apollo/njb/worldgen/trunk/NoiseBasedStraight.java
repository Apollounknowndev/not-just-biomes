package apollo.njb.worldgen.trunk;

import apollo.njb.registry.NJBTrunkPlacers;
import apollo.njb.worldgen.util.SeededNoiseProvider;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
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
            })).apply(instance, NoiseBasedStraight::new);
    });

    private final SeededNoiseProvider noiseProvider;
    private final DoublePerlinNoiseSampler noiseSampler;
    private final int minimumHeight;
    private final int maximumHeight;
    private final int randomHeight;

    public NoiseBasedStraight(int minimumHeight, int maximumHeight, int randomHeight, SeededNoiseProvider noiseProvider) {
        super(minimumHeight, maximumHeight, randomHeight);
        this.minimumHeight = minimumHeight;
        this.maximumHeight = maximumHeight;
        this.randomHeight = randomHeight;
        this.noiseProvider = noiseProvider;
        this.noiseSampler = DoublePerlinNoiseSampler.create(new ChunkRandom(new CheckedRandom(noiseProvider.seed)), noiseProvider.noiseParameters);

    }

    protected TrunkPlacerType<?> getType() {
        return NJBTrunkPlacers.NOISE_BASED_STRAIGHT;
    }

    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
        setToDirt(world, replacer, random, startPos.down(), config);

        double i = this.noiseSampler.sample((double)startPos.getX() * noiseProvider.xz_scale, (double)startPos.getY() * noiseProvider.y_scale, (double)startPos.getZ() * noiseProvider.xz_scale);
        int j = Math.toIntExact(Math.min(Math.max(Math.round(((float)(this.maximumHeight-this.minimumHeight)/2)*i+((float)(this.minimumHeight+this.maximumHeight)/2)),this.minimumHeight),this.maximumHeight))+ random.nextInt(this.randomHeight+1);

        for(int k = 0; k < j; ++k) {
            this.getAndSetState(world, replacer, random, startPos.up(k), config);
        }

        return ImmutableList.of(new FoliagePlacer.TreeNode(startPos.up(j), 0, false));
    }
}
