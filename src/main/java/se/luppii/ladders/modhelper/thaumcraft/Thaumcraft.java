/**
 * 
 */
package se.luppii.ladders.modhelper.thaumcraft;

import net.minecraft.item.ItemStack;
import se.luppii.ladders.LLadders;
import se.luppii.ladders.lib.Config;
import se.luppii.ladders.modhelper.IExtension;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import cpw.mods.fml.common.Optional;

/**
 * @author aiquen
 *
 */
public class Thaumcraft implements IExtension {

	@Override
	@Optional.Method(modid = "Thaumcraft")
	public void load() {

		if (Config.useThaumcraft.getBoolean(true)) {
			// Facing directions
			int[] direction = { 0, 1, 2, 3 };
			// Sturdy ladders
			thaumcraft.api.ThaumcraftApi.registerObjectTag(new ItemStack(LLadders.blockSturdyLadder), direction,
					(new AspectList()).add(Aspect.TOOL, 1).add(Aspect.METAL, 1));
			// Rope ladders
			thaumcraft.api.ThaumcraftApi.registerObjectTag(new ItemStack(LLadders.blockRopeLadder), direction,
					new AspectList().add(Aspect.TOOL, 1).add(Aspect.TREE, 2));
			//Vine ladders
			thaumcraft.api.ThaumcraftApi.registerObjectTag(new ItemStack(LLadders.blockVineLadder), direction,
					new AspectList().add(Aspect.TOOL, 1).add(Aspect.PLANT, 2));
			//Ladder dispenser
			thaumcraft.api.ThaumcraftApi.registerObjectTag(new ItemStack(LLadders.blockLadderDispenser), direction, new AspectList().add(Aspect.MECHANISM, 2)
					.add(Aspect.MOTION, 1).add(Aspect.METAL, 4).add(Aspect.ENERGY, 1));
			//Bride builder
			thaumcraft.api.ThaumcraftApi.registerObjectTag(new ItemStack(LLadders.blockBridgeBuilder), new int[] { 0, 1, 2, 3, 4, 5 },
					new AspectList().add(Aspect.MECHANISM, 2).add(Aspect.MOTION, 1).add(Aspect.METAL, 4).add(Aspect.ENERGY, 1));
		}
	}
}
