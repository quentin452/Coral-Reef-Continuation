package nandonalt.mods.coralmod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

final class ReefGen implements IReefGen {

	private final Block coralBlock;
	private final int numberOfBlocks;
	private final boolean spikyEnabled;

	private boolean generated = false;

	ReefGen(Block coralBlock, int numberOfBlocks, boolean spikyEnabled) {
		this.coralBlock = coralBlock;
		this.numberOfBlocks = numberOfBlocks;
		this.spikyEnabled = spikyEnabled;
	}

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        final float f = random.nextFloat() * (float) Math.PI;
        final double d1 = x + 8 + MathHelper.sin(f) * numberOfBlocks / 8.0f;
        final double d2 = x + 8 - MathHelper.sin(f) * numberOfBlocks / 8.0f;
        final double d3 = z + 8 + MathHelper.cos(f) * numberOfBlocks / 8.0f;
        final double d4 = z + 8 - MathHelper.cos(f) * numberOfBlocks / 8.0f;
        final double d5 = y + random.nextInt(3) + 2;
        final double d6 = y + random.nextInt(3) + 2;

        double d11Divided = numberOfBlocks / 16.0;

        for (int i = 0; i <= numberOfBlocks; i++) {
            double d7 = d1 + (d2 - d1) * i / numberOfBlocks;
            double d8 = d5 + (d6 - d5) * i / numberOfBlocks;
            double d9 = d3 + (d4 - d3) * i / numberOfBlocks;

            double sinValue = MathHelper.sin(i * (float) Math.PI / numberOfBlocks) + 1.0f;
            double d10 = random.nextDouble() * d11Divided;
            double d11 = sinValue * d10 + 1.0;
            double d12 = sinValue * d10 + 1.0;

            double d11DividedBy2 = d11 / 2.0;
            double d12DividedBy2 = d12 / 2.0;

            int startX = (int) (d7 - d11DividedBy2);
            int endX = (int) (d7 + d11DividedBy2);
            int startY = (int) (d8 - d12DividedBy2);
            int endY = (int) (d8 + d12DividedBy2);
            int startZ = (int) (d9 - d11DividedBy2);
            int endZ = (int) (d9 + d11DividedBy2);

            for (int j = startX; j <= endX; j++) {
                for (int k = startY; k <= endY; k++) {
                    for (int m = startZ; m <= endZ; m++) {
                        double d13 = (j + 0.5 - d7) / d11DividedBy2;
                        double d14 = (k + 0.5 - d8) / d12DividedBy2;
                        double d15 = (m + 0.5 - d9) / d11DividedBy2;
                        double distance = d13 * d13 + d14 * d14 + d15 * d15;

                        if (distance < 1.0D) {
                            Block block = world.getBlock(j, k, m);
                            Material material = block.getMaterial();

                            boolean isValidBlock = (material == Material.sand || material == Material.ground);
                            boolean isWaterBlock = Util.checkWater(world.getBlock(j, k + 1, m)) &&
                                Util.checkWater(world.getBlock(j, k + 7, m));

                            if (isValidBlock && isWaterBlock) {
                                Util.setCoralBlock(world, j, k, m, coralBlock, 0, 2);
                                Util.setCoralBlock(world, j + 1, k, m, coralBlock, 0, 2);
                                Util.setCoralBlock(world, j, k, m + 1, coralBlock, 0, 2);
                                Util.setCoralBlock(world, j + 1, k, m + 1, coralBlock, 0, 2);
                                generated = true;

                                for (int pass = 0; pass <= 3; pass++) {
                                    genCoral(world, random, j, k, m, pass);
                                }
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

    private void genCoral(World world, Random random, int x, int y, int z, int pass) {
        int xOffset = (pass == 1 || pass == 3) ? 1 : 0;
        int zOffset = (pass == 2 || pass == 3) ? 1 : 0;
        int posX = x + xOffset;
        int posZ = z + zOffset;

        if (Util.checkWater(world.getBlock(posX, y + 1, posZ)) && random.nextInt(2) == 0) {
            generateCoral(world, random, posX, y, posZ);
        }

        if (spikyEnabled && random.nextInt(30) == 0 && Util.checkWater(world.getBlock(posX, y + 1, posZ))) {
            generateSpikyCoral(world, random, posX, y, posZ);
        }
    }

    private void generateCoral(World world, Random random, int posX, int y, int posZ) {
        int rand = random.nextInt(3);
        Util.setCoralBlock(world, posX, y + 1, posZ, CoralMod.coral1, rand, 2);

        generateAdditionalCoral(world, random, posX, y, posZ, rand);
    }

    private void generateAdditionalCoral(World world, Random random, int posX, int y, int posZ, int rand) {
        if (random.nextInt(20) == 0) {
            Util.setCoralBlock(world, posX, y + 1, posZ, CoralMod.coral5, 5, 2);
        }

        if (random.nextInt(5) == 0) {
            Util.setCoralBlock(world, posX, y + 1, posZ, CoralMod.coral4, 3, 2);
        }

        if (random.nextInt(2) == 0 && rand == 1) {
            Util.setCoralBlock(world, posX, y + 2, posZ, CoralMod.coral1, 1, 2);
            generateStackedCoral(world, random, posX, y, posZ);
        }
    }

    private void generateStackedCoral(World world, Random random, int posX, int y, int posZ) {
        if (random.nextInt(4) == 0) {
            Util.setCoralBlock(world, posX, y + 3, posZ, CoralMod.coral1, 1, 2);
            if (random.nextInt(8) == 0) {
                Util.setCoralBlock(world, posX, y + 4, posZ, CoralMod.coral1, 1, 2);
                if (random.nextInt(8) == 0) {
                    Util.setCoralBlock(world, posX, y + 5, posZ, CoralMod.coral1, 1, 2);
                }
            }
        }
    }

    private void generateSpikyCoral(World world, Random random, int posX, int y, int posZ) {
        Util.setCoralBlock(world, posX, y + 1, posZ, CoralMod.coral1, 4, 2);
        if (random.nextInt(2) == 0) {
            Util.setCoralBlock(world, posX, y + 2, posZ, CoralMod.coral1, 4, 2);
            if (random.nextInt(4) == 0) {
                Util.setCoralBlock(world, posX, y + 3, posZ, CoralMod.coral1, 4, 2);
            }
        }
    }
}
