package se.luppii.ladders.modhelper.ropesplus;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import se.luppii.ladders.LLadders;
import se.luppii.ladders.lib.Config;
import se.luppii.ladders.lib.References;
import se.luppii.ladders.modhelper.IExtension;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;

public class RopesPlus implements IExtension {

	@Override
	public void load() {

		if (Config.ropesPlusRecipe.getBoolean(true)) {
			// Try to use ropes as recipes for ladders
			try {
				// Rope Ladder
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LLadders.blockRopeLadder, 4, 0), true, new Object[] { "R R", "PPP", "R R", 'P',
						"plankWood", 'R', atomicstryker.ropesplus.common.RopesPlusCore.instance.blockRope }));
			}
			catch (Exception err) {
				FMLLog.warning("[" + References.MOD_HELP_ROPES_NAME + "] Ropes+ present, but unable to load recipe!");
				FMLLog.warning(err.toString());
			}
		}
	}
}
