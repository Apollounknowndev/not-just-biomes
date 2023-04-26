package dev.worldgen.njb.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class AlternateMansionStructureConfig implements FeatureConfig {
    public static final Codec<AlternateMansionStructureConfig> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
            MansionTemplates.ROOM_CODEC.fieldOf("room_templates").forGetter((mansion) -> {
                return mansion.roomTemplates;
            })
        ).apply(instance, AlternateMansionStructureConfig::new);
    });
    public final MansionTemplates roomTemplates;
    public AlternateMansionStructureConfig(MansionTemplates roomTemplates) {
        this.roomTemplates = roomTemplates;
    }
}
