package dev.worldgen.njb.worldgen.placementmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.njb.registry.NJBPlacementModifiers;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.AbstractConditionalPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SteepCornersPlacementModifier extends AbstractConditionalPlacementModifier {
    public static final Codec<SteepCornersPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            Codec.intRange(1, 24).fieldOf("radius").forGetter(steep -> {
                return steep.radius;
            }),
            Codecs.POSITIVE_INT.fieldOf("threshold").forGetter(steep -> {
                return steep.threshold;
            }),
            Heightmap.Type.CODEC.fieldOf("heightmap").forGetter((steep) -> {
                return steep.heightmap;
            })
        ).apply(instance, SteepCornersPlacementModifier::new);
    });
    private final Integer radius;
    private final Integer threshold;
    private final Heightmap.Type heightmap;

    public SteepCornersPlacementModifier(Integer radius, Integer threshold, Heightmap.Type heightmap) {
        this.radius = radius;
        this.threshold = threshold;
        this.heightmap = heightmap;
    }
    @Override
    protected boolean shouldPlace(FeaturePlacementContext context, Random random, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getZ();
        List<Integer> heightmapSamples = new ArrayList<>(List.of(
            context.getTopY(this.heightmap, i-radius, j-radius),
            context.getTopY(this.heightmap, i+radius, j-radius),
            context.getTopY(this.heightmap, i-radius, j+radius),
            context.getTopY(this.heightmap, i+radius, j+radius)
        ));
        Collections.sort(heightmapSamples);

        return heightmapSamples.get(3) - heightmapSamples.get(0) <= this.threshold;
    }

    @Override
    public PlacementModifierType<?> getType() {
        return NJBPlacementModifiers.STEEP_CORNERS;
    }
}
