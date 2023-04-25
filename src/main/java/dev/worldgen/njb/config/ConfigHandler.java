package dev.worldgen.njb.config;

import dev.worldgen.njb.NotJustBiomes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigHandler {
    private static final Path FILE_PATH = FabricLoader.getInstance().getConfigDir().resolve("not-just-biomes.json");

    public static final Map<String, Boolean> DEFAULT_CONFIG_VALUES = new LinkedHashMap<>(){
        {
            put("birch_forest", true);
            put("cherry_grove", true);
            put("dungeon", true);
            put("forest", true);
            put("mansion", true);
            put("swamp", true);
            put("taiga", true);
        }
    };

    static Map<String, Boolean> CONFIG_VALUES;
    public static void loadOrCreateDefaultConfig() {
        if (!Files.isRegularFile(FILE_PATH)) {
            NotJustBiomes.LOGGER.info("Config file for Not Just Biomes not found, creating file with default values...");
            CONFIG_VALUES = DEFAULT_CONFIG_VALUES;
            writeToFile(CONFIG_VALUES);
        } else {
            try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
                JsonElement json = JsonParser.parseReader(reader);
                JsonObject jsonObject = json.getAsJsonObject();
                CONFIG_VALUES = new HashMap<>();
                for (Map.Entry<String, JsonElement> configValues : jsonObject.entrySet()) {
                    if (DEFAULT_CONFIG_VALUES.containsKey(configValues.getKey())) {
                        String key = configValues.getKey();
                        Boolean value = configValues.getValue().getAsBoolean();
                        CONFIG_VALUES.put(key, value);
                    }
                }
                for (Map.Entry<String, Boolean> defaultConfigValues : DEFAULT_CONFIG_VALUES.entrySet()) {
                    CONFIG_VALUES.putIfAbsent(defaultConfigValues.getKey(), defaultConfigValues.getValue());
                }
                writeToFile(CONFIG_VALUES);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean getConfigValue(String key) {
        boolean value = false;
        if (CONFIG_VALUES.containsKey(key)) {
            value = CONFIG_VALUES.get(key);
        } else if (DEFAULT_CONFIG_VALUES.containsKey(key)) {
            value = DEFAULT_CONFIG_VALUES.get(key);
        }
        return value;
    }

    public static void writeToFile(Map<String, Boolean> input) {
        try(BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("enabled_modules", input);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(map));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void flipValue(String key) {
        CONFIG_VALUES.put(key, !CONFIG_VALUES.get(key));
        writeToFile(CONFIG_VALUES);
    }
}
