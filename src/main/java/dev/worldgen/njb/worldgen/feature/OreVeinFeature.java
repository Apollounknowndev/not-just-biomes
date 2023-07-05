package dev.worldgen.njb.worldgen.feature;
import com.mojang.serialization.Codec;
import dev.worldgen.njb.worldgen.feature.config.OreVeinConfig;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.List;

public class OreVeinFeature extends Feature<OreVeinConfig> {
    public OreVeinFeature(Codec<OreVeinConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<OreVeinConfig> context) {
        BlockPos originPos = context.getOrigin();
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        OreVeinConfig config = context.getConfig();
        int radius = config.radius.get(random);
        DoublePerlinNoiseSampler noiseSamplerA = DoublePerlinNoiseSampler.create(new ChunkRandom(new CheckedRandom(config.noiseProvider.seed)), config.noiseProvider.noiseParameters);
        DoublePerlinNoiseSampler noiseSamplerB = DoublePerlinNoiseSampler.create(new ChunkRandom(new CheckedRandom(config.noiseProvider.seed+1)), config.noiseProvider.noiseParameters);
        for (BlockPos pos : BlockPos.iterate(originPos.add(-radius, -radius, -radius), originPos.add(radius, radius, radius))) {
            double i = Math.abs(noiseSamplerA.sample((double)pos.getX() * config.noiseProvider.xz_scale, (double)pos.getY() * config.noiseProvider.y_scale, (double)pos.getZ() * config.noiseProvider.xz_scale));
            double j = Math.abs(noiseSamplerB.sample((double)pos.getX() * config.noiseProvider.xz_scale, (double)pos.getY() * config.noiseProvider.y_scale, (double)pos.getZ() * config.noiseProvider.xz_scale));
            double k = pos.getSquaredDistance(originPos)/(radius*radius);
            double l = Math.max(i, j)+getAdditive(config.points, k);
            if (l < 0.05 && world.getBlockState(pos).isIn(config.replaceable)) {
                world.setBlockState(pos, config.state.get(random, pos), Block.NOTIFY_LISTENERS);
            }
        }
        return true;
    }

    private double getAdditive(List<OreVeinConfig.OreVeinAdditivePoints> points, double value) {
        if (value <= points.get(0).percentDistanceFromMaxRadius()) {
            return points.get(0).islandAdditive();
        } else if (value >= points.get(points.size()-1).percentDistanceFromMaxRadius()) {
            return points.get(points.size()-1).islandAdditive();
        } else {
            for (int i = 0; i <= points.size()-1; i++) {
                if (points.get(i).percentDistanceFromMaxRadius() <= value) continue;
                OreVeinConfig.OreVeinAdditivePoints j = points.get(i-1);
                OreVeinConfig.OreVeinAdditivePoints k = points.get(i);
                double slope = (k.islandAdditive()-j.islandAdditive())/(k.percentDistanceFromMaxRadius()- j.percentDistanceFromMaxRadius());
                return j.islandAdditive() + slope * (value - j.percentDistanceFromMaxRadius());
            }
        }
        throw new IllegalStateException("Could not find the noise additive for the voidborn:island feature");
    }
}