package apollo.njb.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.Optional;

public class RockConfig implements FeatureConfig {
    public static final Codec<RockConfig> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("state_provider").forGetter((rock) -> {
                return rock.state;
            }),
            BlockStateProvider.TYPE_CODEC.optionalFieldOf("above_rock_provider").forGetter((rock) -> {
                return rock.aboveRockState;
            }),
            Codec.doubleRange(0, 1).fieldOf("above_rock_placement_chance").orElse(0D).forGetter((rock) -> {
                return rock.aboveRockPlacementChance;
            }),
            IntProvider.createValidatingCodec(1, 10).fieldOf("rock_placements").orElse(ConstantIntProvider.create(3)).forGetter((rock) -> {
                return rock.rockPlacements;
            })
        ).apply(instance, RockConfig::new);
    });
    public final BlockStateProvider state;
    public final Optional<BlockStateProvider> aboveRockState;
    public final Double aboveRockPlacementChance;
    public final IntProvider rockPlacements;

    private RockConfig(BlockStateProvider state, Optional<BlockStateProvider> aboveRockState, Double aboveRockPlacementChance, IntProvider rockPlacements) {
        this.state = state;
        this.aboveRockState = aboveRockState;
        this.aboveRockPlacementChance = aboveRockPlacementChance;
        this.rockPlacements = rockPlacements;
    }
}
