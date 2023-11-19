package dev.worldgen.njb.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record WellConfig(BlockStateProvider standardState, BlockStateProvider slabState, BlockStateProvider fluidState, BlockStateProvider groundState, BlockState suspiciousState, Boolean placeSuspiciousBlock, Identifier suspiciousLootTable, float entityChance, RegistryEntryList<EntityType<?>> entity) implements FeatureConfig {
    public static final Codec<WellConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        BlockStateProvider.TYPE_CODEC.fieldOf("standard_state").forGetter(WellConfig::standardState),
        BlockStateProvider.TYPE_CODEC.fieldOf("slab_state").forGetter(WellConfig::slabState),
        BlockStateProvider.TYPE_CODEC.fieldOf("fluid_state").forGetter(WellConfig::fluidState),
        BlockStateProvider.TYPE_CODEC.fieldOf("ground_state").forGetter(WellConfig::groundState),
        BlockState.CODEC.fieldOf("suspicious_state").orElse(Blocks.STONE.getDefaultState()).forGetter(WellConfig::suspiciousState),
        Codec.BOOL.fieldOf("place_suspicious_block").orElse(false).forGetter(WellConfig::placeSuspiciousBlock),
        Identifier.CODEC.fieldOf("suspicious_loot_table").orElse(new Identifier("archaeology/desert_well")).forGetter(WellConfig::suspiciousLootTable),
        Codec.floatRange(0.0F, 1.0F).fieldOf("entity_chance").orElse(0.0F).forGetter(WellConfig::entityChance),
        RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).fieldOf("entity").orElse(RegistryEntryList.of()).forGetter(WellConfig::entity)
    ).apply(instance, WellConfig::new));
}
