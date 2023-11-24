package nandonalt.mods.coralmod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class CoralGenerator {

    private boolean isCoralGenerationEnabled() {
        return CoralMod.settingsManager.getBooleanValue("settings", "coralgen");
    }

    private boolean shouldGenerateInAllDimensions() {
        return CoralMod.settingsManager.getBooleanValue("settings", "alldimensions");
    }

    private boolean isOceanOnlyEnabled() {
        return CoralMod.settingsManager.getBooleanValue("settings", "oceanonly");
    }

    private List<String> getBiomesList() {
        String biomes = CoralMod.settingsManager.getValue("generation", "biomes");
        String[] biomesArray = Util.safeSplit(biomes, ",");
        return Arrays.asList(biomesArray);
    }

    @SubscribeEvent
    public void populateChunk(PopulateChunkEvent.Post evt) {
        // Check coral generation is enabled
        if (!isCoralGenerationEnabled()) return;

        // Check dimension
        if (evt.world.getWorldInfo().getVanillaDimension() != 0 && !shouldGenerateInAllDimensions()) return;

        // Convert to non-chunk positions
        final int posX = evt.chunkX << 4;
        final int posZ = evt.chunkZ << 4;

        // Check biome
        if (isOceanOnlyEnabled()) {
            BiomeGenBase biome = getBiomeGenAt(evt.world, posX, posZ);
            if (biome.biomeName.endsWith("River") || biome.biomeName.startsWith("River")
                || !BiomeDictionary.isBiomeOfType(biome, Type.WATER)) {
                return;
            }
        } else {
            List<String> biomesList = getBiomesList();
            if (!biomesList.isEmpty()) {
                int biomeID = getBiomeGenAt(evt.world, posX, posZ).biomeID;
                if (!biomesList.contains(String.valueOf(biomeID))) return;
            }
        }

        generate(evt.rand, posX, posZ, evt.world);
    }

	private BiomeGenBase getBiomeGenAt(World world, int posX, int posZ) {
		return world.getWorldChunkManager().getBiomeGenAt(posX, posZ);
	}

	/**
	 * Generate coral reef
	 */
    static boolean generate(Random random, int posX, int posZ, World world) {
        final int size = CoralMod.settingsManager.getIntValue("settings", "avgsize");
        final int[] minValues = {15, 35, 45};
        final int[] maxValues = {20, 35, 45};

        final int min1 = minValues[Math.min(size, minValues.length - 1)];
        final int min2 = size == 0 ? 10 : 25;
        final int max1 = maxValues[Math.min(size, maxValues.length - 1)];
        final int max2 = size == 0 ? 20 : 35;

        final int baseHeight = CoralMod.settingsManager.getIntValue("generation", "baseheight");
        final int iterationFactor = CoralMod.settingsManager.getIntValue("generation", "iterationfactor");
        final int radius = CoralMod.settingsManager.getIntValue("generation", "radius");
        final int tmp = CoralMod.settingsManager.getIntValue("generation", "heightoffset");
        final int heightOffset = Math.max(tmp, 4);
        final int maxHeight = Math.min(baseHeight + heightOffset, world.getHeight());
        final int iterations = (heightOffset / 16) * iterationFactor;

        IReefGen reefGen;
        int genNum = 0; // number of 'reefs' generated

        // Generation pass
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < iterations; i++) {
                final int x = posX + random.nextInt(radius);
                final int y = baseHeight + random.nextInt(maxHeight - baseHeight);
                final int z = posZ + random.nextInt(radius);
                final int numberReef = random.nextInt((j == 0 ? max1 : max2) - (j == 0 ? min1 : min2) + 1) + (j == 0 ? min1 : min2);
                reefGen = (j == 0) ? new ReefGen(CoralMod.coral2, numberReef, CoralMod.settingsManager.getBooleanValue("settings", "spikyenabled")) : new ReefGen2(CoralMod.coral3, numberReef);
                reefGen.generate(world, random, x, y, z);
                genNum++;
            }
        }

        // whether any reefs actually generated
        return genNum > 0;
    }
}
