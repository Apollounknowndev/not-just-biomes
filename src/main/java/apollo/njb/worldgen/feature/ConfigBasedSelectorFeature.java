package apollo.njb.worldgen.feature;

import apollo.njb.config.ConfigHandler;
import apollo.njb.worldgen.feature.config.ConfigBasedSelectorConfig;
import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ConfigBasedSelectorFeature extends Feature<ConfigBasedSelectorConfig> {
    public ConfigBasedSelectorFeature(Codec<ConfigBasedSelectorConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<ConfigBasedSelectorConfig> context) {
        Random random = context.getRandom();
        ConfigBasedSelectorConfig config = context.getConfig();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        ChunkGenerator chunkGenerator = context.getGenerator();
        BlockPos blockPos = context.getOrigin();
        return (ConfigHandler.getConfigValue(config.module) ? config.moduleEnabledFeature : config.moduleDisabledFeature).value().generateUnregistered(structureWorldAccess, chunkGenerator, random, blockPos);
    }
}
