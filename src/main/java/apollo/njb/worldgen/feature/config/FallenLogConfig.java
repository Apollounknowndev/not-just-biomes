package apollo.njb.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class FallenLogConfig implements FeatureConfig {
    public static final Codec<FallenLogConfig> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("log_provider").forGetter((fallenLog) -> {
                return fallenLog.logProvider;
            }),
            BlockStateProvider.TYPE_CODEC.fieldOf("above_log_provider").forGetter((fallenLog) -> {
                return fallenLog.aboveLogProvider;
            }),
            Codec.floatRange(0, 1).fieldOf("above_log_placement_chance").forGetter((fallenLog) -> {
                return fallenLog.aboveLogPlacementChance;
            }),
            IntProvider.createValidatingCodec(2, 10).fieldOf("log_length").orElse(ConstantIntProvider.create(4)).forGetter((fallenLog) -> {
                return fallenLog.logLength;
            })
        ).apply(instance, FallenLogConfig::new);
    });
    public final BlockStateProvider logProvider;
    public final BlockStateProvider aboveLogProvider;
    public final Float aboveLogPlacementChance;
    public final IntProvider logLength;

    private FallenLogConfig(BlockStateProvider logProvider, BlockStateProvider aboveLogProvider, Float aboveLogPlacementChance, IntProvider logLength) {
        this.logProvider = logProvider;
        this.aboveLogProvider = aboveLogProvider;
        this.aboveLogPlacementChance = aboveLogPlacementChance;
        this.logLength = logLength;
    }
}
