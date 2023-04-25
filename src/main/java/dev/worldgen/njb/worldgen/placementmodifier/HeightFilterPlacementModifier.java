package dev.worldgen.njb.worldgen.placementmodifier;

import dev.worldgen.njb.registry.NJBPlacementModifiers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.placementmodifier.AbstractConditionalPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class HeightFilterPlacementModifier extends AbstractConditionalPlacementModifier {
    public static final Codec<HeightFilterPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            HeightProvider.CODEC.fieldOf("min_inclusive").forGetter(height -> {
                return height.minHeight;
            }),
            HeightProvider.CODEC.fieldOf("max_inclusive").forGetter(height -> {
                return height.maxHeight;
            })
        ).apply(instance, HeightFilterPlacementModifier::new);
    });
    private final HeightProvider minHeight;
    private final HeightProvider maxHeight;

    public HeightFilterPlacementModifier(HeightProvider minHeight, HeightProvider maxHeight) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }
    @Override
    protected boolean shouldPlace(FeaturePlacementContext context, Random random, BlockPos pos) {
        return pos.getY() >= minHeight.get(random, context) && pos.getY() <= maxHeight.get(random, context);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return NJBPlacementModifiers.HEIGHT_FILTER;
    }
}
