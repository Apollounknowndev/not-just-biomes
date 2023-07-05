package dev.worldgen.njb.worldgen;

import dev.worldgen.njb.NotJustBiomes;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.Arrays;
import java.util.List;

public class NJBBiomeModifications {
    private static final TagKey<Biome> OAK_FOREST = TagKey.of(RegistryKeys.BIOME, new Identifier(NotJustBiomes.MOD_ID, "oak_forest"));
    private static final TagKey<Biome> CHERRY = TagKey.of(RegistryKeys.BIOME, new Identifier(NotJustBiomes.MOD_ID, "cherry"));
    private static final List<String> birchFeatures = Arrays.asList("fallen_log","block_patch", "rock");
    private static final List<String> forestFeatures = Arrays.asList("fallen_log","block_patch", "rock");
    private static final List<String> taigaFeatures = Arrays.asList("fallen_log","block_patch", "rock");
    private static final List<String> cherryFeatures = Arrays.asList("azalea_bush","bamboo_cherry", "flowers_cherry", "rock_cherry", "block_patch");
    private static final List<String> swampFeatures = Arrays.asList("mud_pools", "blue_orchids", "disk_mud");
    private static final RegistryKey<PlacedFeature> DESERT_DUNGEON = getRegistryKeyWithPath("dungeon/desert");
    public static final RegistryKey<PlacedFeature> JUNGLE_DUNGEON = getRegistryKeyWithPath("dungeon/jungle");
    public static final RegistryKey<PlacedFeature> BADLANDS_DUNGEON = getRegistryKeyWithPath("dungeon/badlands");
    public static final RegistryKey<PlacedFeature> MOUNTAIN_DUNGEON = getRegistryKeyWithPath("dungeon/mountain");
    public static final RegistryKey<PlacedFeature> FROZEN_WELL = getRegistryKeyWithModuleAndPath("well/", "frozen");
    public static final RegistryKey<PlacedFeature> BADLANDS_WELL = getRegistryKeyWithModuleAndPath("well/", "badlands");
    public static final RegistryKey<PlacedFeature> EMERALD_ORE_VEIN = getRegistryKeyWithModuleAndPath("ore_vein/", "emerald");
    public static final RegistryKey<PlacedFeature> GOLD_ORE_VEIN = getRegistryKeyWithModuleAndPath("ore_vein/", "gold");
    public static final RegistryKey<PlacedFeature> NETHER_GOLD_ORE_VEIN = getRegistryKeyWithModuleAndPath("ore_vein/", "nether_gold");
    public static RegistryKey<PlacedFeature> getRegistryKeyWithPath(String path) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(NotJustBiomes.MOD_ID, path));
    }

    public static RegistryKey<PlacedFeature> getRegistryKeyWithModuleAndPath(String moduleFolder, String path) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(NotJustBiomes.MOD_ID, moduleFolder.concat(path)));
    }

    public static void placeModuleFeatures(List<String> features, TagKey<Biome> biomeTagKey, String moduleFolder) {
        for (String feature : features)
            BiomeModifications.addFeature(
                BiomeSelectors.tag(biomeTagKey),
                GenerationStep.Feature.VEGETAL_DECORATION,
                getRegistryKeyWithModuleAndPath(moduleFolder, feature)
            );
    }
    public static void placeFeaturesInWorld() {
        BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.DESERT), GenerationStep.Feature.UNDERGROUND_STRUCTURES, DESERT_DUNGEON);
        BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.JUNGLE), GenerationStep.Feature.UNDERGROUND_STRUCTURES, JUNGLE_DUNGEON);
        BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.BADLANDS), GenerationStep.Feature.UNDERGROUND_STRUCTURES, BADLANDS_DUNGEON);
        BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.MOUNTAIN), GenerationStep.Feature.UNDERGROUND_STRUCTURES, MOUNTAIN_DUNGEON);
        BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.SNOWY), GenerationStep.Feature.SURFACE_STRUCTURES, FROZEN_WELL);
        BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.BADLANDS), GenerationStep.Feature.SURFACE_STRUCTURES, BADLANDS_WELL);
        BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.MOUNTAIN), GenerationStep.Feature.UNDERGROUND_ORES, EMERALD_ORE_VEIN);
        BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.BADLANDS), GenerationStep.Feature.UNDERGROUND_ORES, GOLD_ORE_VEIN);
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(), GenerationStep.Feature.UNDERGROUND_ORES, NETHER_GOLD_ORE_VEIN);

        placeModuleFeatures(birchFeatures, ConventionalBiomeTags.BIRCH_FOREST, "birch_forest/");
        placeModuleFeatures(cherryFeatures, CHERRY, "cherry_grove/");
        placeModuleFeatures(forestFeatures, OAK_FOREST, "forest/");
        placeModuleFeatures(taigaFeatures, ConventionalBiomeTags.TAIGA, "taiga/");
        placeModuleFeatures(swampFeatures, ConventionalBiomeTags.SWAMP, "swamp/");
    }
}
