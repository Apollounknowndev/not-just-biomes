package dev.worldgen.njb.worldgen.feature.config;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.njb.worldgen.util.SeededNoiseProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;

public class OreVeinConfig implements FeatureConfig {
    public static final Codec<OreVeinConfig> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            SeededNoiseProvider.CODEC.fieldOf("noise_provider").forGetter((island) -> {
                return island.noiseProvider;
            }),
            OreVeinAdditivePoints.CODEC.listOf().fieldOf("points").forGetter((island) -> {
                return island.points;
            }),
            BlockStateProvider.TYPE_CODEC.fieldOf("state_provider").forGetter((island) -> {
                return island.state;
            }),
            IntProvider.createValidatingCodec(6, 24).fieldOf("max_radius").forGetter((island) -> {
                return island.radius;
            }),
            RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("replaceable").forGetter((island) -> {
                return island.replaceable;
            })
        ).apply(instance, OreVeinConfig::new);
    });
    public final SeededNoiseProvider noiseProvider;
    public final List<OreVeinAdditivePoints> points;
    public final BlockStateProvider state;
    public final IntProvider radius;
    public final RegistryEntryList<Block> replaceable;

    private OreVeinConfig(SeededNoiseProvider noiseProvider, List<OreVeinAdditivePoints> points, BlockStateProvider state, IntProvider radius, RegistryEntryList<Block> replaceable) {
        this.noiseProvider = noiseProvider;
        this.points = points;
        this.state = state;
        this.radius = radius;
        this.replaceable = replaceable;
    }

    public static record OreVeinAdditivePoints(Float percentDistanceFromMaxRadius, Float islandAdditive) {
        public static final Codec<OreVeinAdditivePoints> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(
                    Codec.FLOAT.fieldOf("percent_distance_from_max_radius").forGetter(OreVeinAdditivePoints::percentDistanceFromMaxRadius),
                    Codec.FLOAT.fieldOf("noise_additive").forGetter(OreVeinAdditivePoints::islandAdditive)
            ).apply(instance, OreVeinAdditivePoints::new);
        });

        public OreVeinAdditivePoints(Float percentDistanceFromMaxRadius, Float islandAdditive) {
            this.percentDistanceFromMaxRadius = percentDistanceFromMaxRadius;
            this.islandAdditive = islandAdditive;
        }

        public Float percentDistanceFromMaxRadius() {
            return this.percentDistanceFromMaxRadius;
        }

        public Float islandAdditive() {
            return this.islandAdditive;
        }
    }
}