package dev.worldgen.njb;

import dev.worldgen.njb.config.ConfigHandler;
import dev.worldgen.njb.registry.*;
import dev.worldgen.njb.worldgen.surface.OreVeinMaterialCondition;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotJustBiomes implements ModInitializer {
	public static final String MOD_ID = "njb";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ConfigHandler.load();

		NJBConfiguredFeatures.init();
		NJBMaterialConditions.init();
		NJBModifierPredicates.init();
		NJBPlacementModifiers.init();
		NJBTreeDecorators.init();
		NJBTrunkPlacers.init();
	}
}