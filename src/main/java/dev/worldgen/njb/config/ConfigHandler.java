package dev.worldgen.njb.config;

import com.mojang.serialization.JsonOps;
import dev.worldgen.njb.NotJustBiomes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class ConfigHandler {
    private static final Path FILE_PATH = FabricLoader.getInstance().getConfigDir().resolve("not-just-biomes.json");

    public static final ConfigCodec DEFAULT_CONFIG = new ConfigCodec(
        Map.of(
            "birch_forest", true,
            "cherry_grove", true,
            "dungeon", true,
            "forest", true,
            "ore_vein", true,
            "swamp", true,
            "taiga", true,
            "well", true
        ),
        Map.of(
            "tectonic_trees", false
        )
    );

    private static ConfigCodec CONFIG;
    public static void load() {
        if (!Files.isRegularFile(FILE_PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            NotJustBiomes.LOGGER.info("Config file for Not Just Biomes not found, creating file with default values...");
            try(BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
                writer.write(gson.toJson(ConfigCodec.CODEC.encodeStart(JsonOps.INSTANCE, DEFAULT_CONFIG).result().get()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            JsonElement json = JsonParser.parseReader(reader);
            Optional<ConfigCodec> result = ConfigCodec.CODEC.parse(JsonOps.INSTANCE, json).result();
            if (result.isEmpty()) {
                result = Optional.of(DEFAULT_CONFIG);
            }
            CONFIG = result.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        writeToFile(CONFIG);
    }

    public static ConfigCodec getConfig() {
        return CONFIG;
    }

    public static Boolean isModuleEnabled(String key) {
        return CONFIG.isModuleEnabled(key);
    }

    public static void writeToFile(ConfigCodec input) {
        try(BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(ConfigCodec.CODEC.encodeStart(JsonOps.INSTANCE, input).result().get()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
