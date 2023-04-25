package apollo.njb;

import apollo.njb.config.ConfigHandler;
import apollo.njb.registry.*;
import apollo.njb.worldgen.NJBBiomeModifications;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotJustBiomes implements ModInitializer {
	public static final String MOD_ID = "njb";
	public static final Logger LOGGER = LoggerFactory.getLogger("njb");

	@Override
	public void onInitialize() {
		ConfigHandler.loadOrCreateDefaultConfig();

		NJBPlacementModifiers.init();
		NJBConfiguredFeatures.init();
		NJBTrunkPlacers.init();
		NJBTreeDecorators.init();
		NJBStructures.init();
		NJBStructurePieces.init();


		NJBBiomeModifications.placeFeaturesInWorld();
	}
}