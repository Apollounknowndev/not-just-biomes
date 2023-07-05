package dev.worldgen.njb.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class WellConfig implements FeatureConfig {
    public static final Codec<WellConfig> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("standard_state").forGetter((well) -> {
                return well.standardState;
            }),
            BlockStateProvider.TYPE_CODEC.fieldOf("slab_state").forGetter((well) -> {
                return well.slabState;
            }),
            BlockStateProvider.TYPE_CODEC.fieldOf("fluid_state").forGetter((well) -> {
                return well.fluidState;
            }),
            BlockStateProvider.TYPE_CODEC.fieldOf("ground_state").forGetter((well) -> {
                return well.groundState;
            }),
            BlockState.CODEC.fieldOf("suspicious_state").orElse(Blocks.STONE.getDefaultState()).forGetter((well) -> {
                return well.suspiciousState;
            }),
            Codec.BOOL.fieldOf("place_suspicious_block").orElse(false).forGetter((well) -> {
                return well.placeSuspiciousBlock;
            }),
            Identifier.CODEC.fieldOf("suspicious_loot_table").orElse(new Identifier("archaeology/desert_well")).forGetter((well) -> {
                return well.suspiciousLootTable;
            })
        ).apply(instance, WellConfig::new);
    });
    public final BlockStateProvider standardState;
    public final BlockStateProvider slabState;
    public final BlockStateProvider fluidState;
    public final BlockStateProvider groundState;
    public final BlockState suspiciousState;
    public final Boolean placeSuspiciousBlock;
    public final Identifier suspiciousLootTable;


    private WellConfig(BlockStateProvider standardState, BlockStateProvider slabState, BlockStateProvider fluidState, BlockStateProvider groundState, BlockState suspiciousState, Boolean placeSuspiciousBlock, Identifier suspiciousLootTable) {
        this.standardState = standardState;
        this.slabState = slabState;
        this.fluidState = fluidState;
        this.groundState = groundState;
        this.suspiciousState = suspiciousState;
        this.placeSuspiciousBlock = placeSuspiciousBlock;
        this.suspiciousLootTable = suspiciousLootTable;
    }
}
