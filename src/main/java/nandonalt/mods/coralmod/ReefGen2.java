package nandonalt.mods.coralmod;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

final class ReefGen2 implements IReefGen {

	private final Block coralBlock;
	private final int numberOfBlocks;

	private boolean generated = false;

	ReefGen2(Block coralBlock, int numberOfBlocks) {
		this.coralBlock = coralBlock;
		this.numberOfBlocks = numberOfBlocks;
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		final float f = random.nextFloat() * (float)Math.PI;
		final double d1 = x + 8 + MathHelper.sin(f) * numberOfBlocks / 8.0f;
		final double d2 = x + 8 - MathHelper.sin(f) * numberOfBlocks / 8.0f;
		final double d3 = z + 8 + MathHelper.cos(f) * numberOfBlocks / 8.0f;
		final double d4 = z + 8 - MathHelper.cos(f) * numberOfBlocks / 8.0f;
        final double d5 = (double) y + random.nextInt(3) + 2;
		final double d6 = (double) y + random.nextInt(3) + 2;
        for (int i = 0; i <= numberOfBlocks; i++) {
            // Reduce the complexity of nested loops by extracting common values
            final double d7 = d1 + (d2 - d1) * i / numberOfBlocks;
            final double d8 = d5 + (d6 - d5) * i / numberOfBlocks;
            final double d9 = d3 + (d4 - d3) * i / numberOfBlocks;
            final double d10 = random.nextDouble() * numberOfBlocks / 16.0;
            final double d11 = (MathHelper.sin(i * (float)Math.PI / numberOfBlocks) + 1.0f) * d10 + 1.0;
            final double d12 = (MathHelper.sin(i * (float)Math.PI / numberOfBlocks) + 1.0f) * d10 + 1.0;
            // Store the results of function calls to improve performance
            final boolean waterCheck1 = Util.checkWater(world.getBlock(x, y + 1, z));
            final boolean waterCheck2 = Util.checkWater(world.getBlock(x, y + 2, z));
            final boolean waterCheck3 = Util.checkWater(world.getBlock(x, y + 3, z));
            final boolean waterCheck4 = Util.checkWater(world.getBlock(x, y + 4, z));
            for (int j = (int) (d7 - d11 / 2.0); j <= (int) (d7 + d11 / 2.0); j++) {
                for (int k = (int) (d8 - d12 / 2.0); k <= (int) (d8 + d12 / 2.0); k++) {
                    for (int m = (int) (d9 - d11 / 2.0); m <= (int) (d9 + d11 / 2.0); m++) {
                        final double d13 = (j + 0.5D - d7) / (d11 / 2.0);
                        final double d14 = (k + 0.5D - d8) / (d12 / 2.0);
                        final double d15 = (m + 0.5D - d9) / (d11 / 2.0);
                        final double distanceSquared = d13 * d13 + d14 * d14 + d15 * d15;
                        if (distanceSquared < 1.0) {
                            final Block block = world.getBlock(j, k, m);
                            // Store coral value to avoid redundant checks
                            final boolean isCoral2Or3 = (block == CoralMod.coral2 || block == CoralMod.coral3);
                            if (isCoral2Or3 && waterCheck1 && waterCheck2 && waterCheck3 && waterCheck4) {
                                if (!CoralMod.settingsManager.getBooleanValue("generation", "dryseabeds") && block == CoralMod.coral3) {
                                    continue;
                                }
                                // Store positions to avoid code repetition
                                final int[][] positions = {
                                    {0, 0, 0}, {1, 0, 0}, {0, 0, 1}, {1, 0, 1},
                                    {0, random.nextInt(2), 0}, {1, random.nextInt(2), 0},
                                    {0, random.nextInt(2), 1}, {1, random.nextInt(2), 1}
                                };
                                for (int[] pos : positions) {
                                    Util.setCoralBlock(world, j + pos[0], k + pos[1], m + pos[2], coralBlock, 0, 2);
                                }
                                generated = true;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

	@Override
	public boolean isGenerated() {
		return generated;
	}

}
