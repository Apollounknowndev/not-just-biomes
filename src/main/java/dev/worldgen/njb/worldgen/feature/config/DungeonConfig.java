package dev.worldgen.njb.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class DungeonConfig implements FeatureConfig {
    public static final Codec<DungeonConfig> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("air_state").forGetter((dungeon) -> {
                return dungeon.airState;
            }),
            BlockStateProvider.TYPE_CODEC.fieldOf("floor_state").forGetter((dungeon) -> {
                return dungeon.floorState;
            }),
            BlockStateProvider.TYPE_CODEC.fieldOf("wall_state").forGetter((dungeon) -> {
                return dungeon.wallState;
            }),
            Codec.BOOL.fieldOf("bandlands_override").orElse(false).forGetter((dungeon) -> {
                return dungeon.bandlandsOverride;
            }),
            Codec.BOOL.fieldOf("mountain_override").orElse(false).forGetter((dungeon) -> {
                return dungeon.mountainOverride;
            }),
            IntProvider.createValidatingCodec(2, 12).fieldOf("x_radius").forGetter((dungeon) -> {
                return dungeon.xRadius;
            }),
            IntProvider.createValidatingCodec(2, 12).fieldOf("z_radius").forGetter((dungeon) -> {
                return dungeon.zRadius;
            }),
            IntProvider.createValidatingCodec(2, 12).fieldOf("height").forGetter((dungeon) -> {
                return dungeon.height;
            }),
            IntProvider.createValidatingCodec(0, 8).fieldOf("max_chest_placement_attempts").forGetter((dungeon) -> {
                return dungeon.maxChestPlacementAttempts;
            }),
            Codec.intRange(0, 64).fieldOf("minimum_air_openings").forGetter((dungeon) -> {
                return dungeon.minAirOpenings;
            }),
            Codec.intRange(0, 64).fieldOf("maximum_air_openings").forGetter((dungeon) -> {
                return dungeon.maxAirOpenings;
            }),
            PlacedFeature.REGISTRY_CODEC.fieldOf("ceiling_feature").forGetter((dungeon) -> {
                return dungeon.ceilingFeature;
            }),
            IntProvider.createValidatingCodec(0, 16).fieldOf("ceiling_feature_placements").forGetter((dungeon) -> {
                return dungeon.ceilingFeaturePlacements;
            }),
            RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).fieldOf("spawner_mobs").forGetter((dungeon) -> {
                return dungeon.spawnerMobs;
            })
        ).apply(instance, DungeonConfig::new);
    });
    public final BlockStateProvider airState;
    public final BlockStateProvider floorState;
    public final BlockStateProvider wallState;
    public final Boolean bandlandsOverride;
    public final Boolean mountainOverride;
    public final IntProvider xRadius;
    public final IntProvider zRadius;
    public final IntProvider height;
    public final IntProvider maxChestPlacementAttempts;
    public final Integer minAirOpenings;
    public final Integer maxAirOpenings;
    public final RegistryEntry<PlacedFeature> ceilingFeature;
    public final IntProvider ceilingFeaturePlacements;
    public final RegistryEntryList<EntityType<?>> spawnerMobs;

    private DungeonConfig(BlockStateProvider airState, BlockStateProvider floorState, BlockStateProvider wallState, Boolean bandlandsOverride, Boolean mountainOverride, IntProvider xRadius, IntProvider zRadius, IntProvider height, IntProvider maxChestPlacementAttempts, int minAirOpenings, int maxAirOpenings, RegistryEntry<PlacedFeature> ceilingFeature, IntProvider ceilingFeaturePlacements, RegistryEntryList<EntityType<?>> spawnerMobs) {
        this.airState = airState;
        this.floorState = floorState;
        this.wallState = wallState;
        this.bandlandsOverride = bandlandsOverride;
        this.mountainOverride = mountainOverride;
        this.xRadius = xRadius;
        this.zRadius = zRadius;
        this.height = height;
        this.maxChestPlacementAttempts = maxChestPlacementAttempts;
        this.minAirOpenings = minAirOpenings;
        this.maxAirOpenings = maxAirOpenings;
        this.ceilingFeature = ceilingFeature;
        this.ceilingFeaturePlacements = ceilingFeaturePlacements;
        this.spawnerMobs = spawnerMobs;
    }
}
