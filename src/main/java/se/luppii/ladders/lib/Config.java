package se.luppii.ladders.lib;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Config {

	// General
	public static Property ropeLadderLeftClick;

	public static Property sturdyLadderLeftClick;

	public static Property vineLadderLeftClick;

	public static Property debugMode;
	
	// Ladder lenghts
	public static Property vineLadderLength;
	
	public static Property ropeLadderLength;
	
	public static Property sturdyLadderLength;
	
	// Updater
	public static Property checkForUpdates;

	// Ropes+ config
	public static Property ropesPlusRecipe;

	public static Property removeVanillaRopeRecipe;

	// Thaumcraft config
	public static Property useThaumcraft;

	// LadderDispenser config
	public static Property canClimbOnDispenser;

	// Biomes O' Plenty
	public static Property biomesOPlentyRecipe;

	public static Property removeVanillaVineRecipe;

	public static void loadConfig(FMLPreInitializationEvent e) {

		Configuration config = new Configuration(e.getSuggestedConfigurationFile());
		try {
			config.load();
			// Are we debugging?
			debugMode = config.get(Configuration.CATEGORY_GENERAL, "Luppis Ladders Debug Mode", false);
			// Block config.
			ropeLadderLeftClick = config.get(Configuration.CATEGORY_GENERAL, "Rope Ladder extend on left click", true);
			sturdyLadderLeftClick = config.get(Configuration.CATEGORY_GENERAL, "Sturdy Ladder extend on left click", true);
			vineLadderLeftClick = config.get(Configuration.CATEGORY_GENERAL, "Vine Ladder extend on left click", true);
			// Update Checker
			checkForUpdates = config.get("updater", "Check for updates", true);
			// Ladder lengths, 0 or less is infinite
			vineLadderLength = config.get("Lengtha", "vineLadderLength", 16);
			ropeLadderLength = config.get("Lengths", "ropeLadderLength", 32);
			sturdyLadderLength = config.get("Lengths", "sturdyLadderLength", 32);
			// Ropes+ config
			ropesPlusRecipe = config.get("modcompat", "Use Ropes+ recipe for Rope Ladder (if mod is loaded)", true);
			removeVanillaRopeRecipe = config.get("modcompat", "Remove vanilla recipe for Rope Ladder", false);
			// Thaumcraft config
			useThaumcraft = config.get("modcompat", "Use thaumcraft aspects", true);
			// LadderDispenser config
			canClimbOnDispenser = config.get("ladderdispenser", "Can climb on Ladder Dispenser", true);
			// Biomes O' Plenty
			biomesOPlentyRecipe = config.get("modcompat", "Use Biomes O' Plenty recipe for Vine Ladder (if mod is loaded)", true);
			removeVanillaVineRecipe = config.get("modcompat", "Remove vanilla recipe for Vine Ladder", false);
			config.save();
		}
		catch (Exception err) {
			err.printStackTrace();
		}
	}
}
