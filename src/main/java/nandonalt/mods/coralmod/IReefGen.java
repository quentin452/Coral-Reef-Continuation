package nandonalt.mods.coralmod;

import net.minecraft.world.World;

import java.util.Random;

interface IReefGen {

	boolean generate(World world, Random random, int x, int y, int z);
	boolean isGenerated();

}