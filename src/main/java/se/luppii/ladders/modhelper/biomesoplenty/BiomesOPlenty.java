/**
 * 
 */
package se.luppii.ladders.modhelper.biomesoplenty;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import se.luppii.ladders.LLadders;
import se.luppii.ladders.lib.Config;
import se.luppii.ladders.lib.References;
import se.luppii.ladders.modhelper.IExtension;

/**
 * @author Aiquen
 *
 */
public class BiomesOPlenty implements IExtension {


	/**
	 * @see se.luppii.ladders.modhelper.IExtension#load()
	 */
	@Override
	public void load() {
		if (Config.biomesOPlentyRecipe.getBoolean(true)) {
			FMLLog.info("Configured to use Biomes O' Plenty recipe.");
			// Try to use Ivy as recipes for ladders
			try {
				// Vine Ladder
				FMLLog.info("[" + References.MOD_NAME + "] Biomes O' Plenty found, " + References.MOD_HELP_BIOMES_NAME + " is loading.");
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LLadders.blockVineLadder, 4, 0), true, new Object[] { "R R", "PPP", "R R", 'P',
						"plankWood", 'R', biomesoplenty.api.content.BOPCBlocks.ivy }));
				FMLLog.info("[" + References.MOD_HELP_ROPES_NAME + "] Loading complete.");
			}
			catch (Exception err) {
				FMLLog.warning("[" + References.MOD_HELP_ROPES_NAME + "] Ropes+ present, but unable to load recipe!");
				FMLLog.warning(err.toString());
			}
		}

	}

}
