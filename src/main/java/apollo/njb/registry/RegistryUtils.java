package apollo.njb.registry;

import apollo.njb.NotJustBiomes;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RegistryUtils {
    public static <V, T extends V> T register(Registry<V> registry, String name, T entry) {
        return Registry.register(registry, new Identifier(NotJustBiomes.MOD_ID, name), entry);
    }
}
