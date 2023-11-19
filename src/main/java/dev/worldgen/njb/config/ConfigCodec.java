package dev.worldgen.njb.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.*;

public class ConfigCodec {
    public static final List<String> ALL_MODULES = List.of(
        "birch_forest",
        "cherry_grove",
        "dungeon",
        "forest",
        "ore_vein",
        "swamp",
        "taiga",
        "well",
        "tectonic_trees"
    );
    public static final List<String> STANDARD_MODULES = List.of(
        "birch_forest",
        "cherry_grove",
        "dungeon",
        "forest",
        "ore_vein",
        "swamp",
        "taiga",
        "well"
    );
    public static final List<String> SPECIAL_MODULES = List.of(
        "tectonic_trees"
    );
    public static final Codec<ConfigCodec> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        Codec.simpleMap(Codec.STRING, Codec.BOOL, Keyable.forStrings(STANDARD_MODULES::stream)).fieldOf("standard_modules").forGetter(ConfigCodec::getStandardModules),
        Codec.simpleMap(Codec.STRING, Codec.BOOL, Keyable.forStrings(SPECIAL_MODULES::stream)).fieldOf("special_modules").forGetter(ConfigCodec::getSpecialModules)
    ).apply(instance, ConfigCodec::new));
    private LinkedHashMap<String, Boolean> standardModules;
    private LinkedHashMap<String, Boolean> specialModules;
    public ConfigCodec(Map<String, Boolean> standardModules, Map<String, Boolean> specialModules) {
        this.standardModules = this.sortList(standardModules);
        for(String module : STANDARD_MODULES) {
            if(!this.standardModules.containsKey(module)) {
                this.standardModules.put(module, true);
            }
        }
        this.specialModules = this.sortList(specialModules);
        for(String module : SPECIAL_MODULES) {
            if(!this.specialModules.containsKey(module)) {
                this.specialModules.put(module, false);
            }
        }
    }

    private LinkedHashMap<String, Boolean> sortList(Map<String, Boolean> unsortedMap) {
        LinkedHashMap<String, Boolean> sortedMap = new LinkedHashMap<>();
        ArrayList<Map.Entry<String, Boolean>> unsortedList = new ArrayList<>(unsortedMap.entrySet());
        unsortedList.sort(Map.Entry.comparingByKey());
        for (Map.Entry<String, Boolean> entry : unsortedList) {
            if (ALL_MODULES.contains(entry.getKey())) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }
        }
        return sortedMap;
    }

    public Map<String, Boolean> getStandardModules() {
        return this.standardModules;
    }

    public Map<String, Boolean> getSpecialModules() {
        return this.specialModules;
    }


    public boolean isModuleEnabled(String module) {
        boolean value = false;
        if(STANDARD_MODULES.contains(module)) {
            value = this.standardModules.get(module);
        } else if (SPECIAL_MODULES.contains(module)) {
            value = this.specialModules.get(module);
        }
        return value;
    }

    public void flipModuleValue(String module) {
        if(STANDARD_MODULES.contains(module)) {
            this.standardModules.put(module, !this.standardModules.get(module));
            ConfigHandler.writeToFile(this);
        } else if (SPECIAL_MODULES.contains(module)) {
            this.specialModules.put(module, !this.specialModules.get(module));
            ConfigHandler.writeToFile(this);

        }
    }
}
