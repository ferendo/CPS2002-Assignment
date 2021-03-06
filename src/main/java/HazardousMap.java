import exceptions.MapWasAlreadyInitialized;
import exceptions.SizeOfMapWasNotSet;

import java.util.Random;

/**
 * Generates a Map that contains 25-35% Water tiles.
 *
 * @author Dylan Frendo.
 */
public class HazardousMap extends Map {

    /**
     * The constructor to create a new instance. If there was an instance
     * beforehand, an exception will be thrown and the new instance is ignored.
     *
     * @throws MapWasAlreadyInitialized: There was an instance beforehand,
     * ignore the new instance.
     */
    protected HazardousMap() throws MapWasAlreadyInitialized {
        // Check if there is already an instance, if there is throw the error
        // Since multiple instances are not allowed.
        setInstance(this);
        size = 0;
    }

    @Override
    void generate() throws SizeOfMapWasNotSet {
        final int MAX_WATER_TILE = 35;
        final int MIN_WATER_TILE = 25;

        int x, y, waterTileAmount, counter = 0, totalAmountOfWaterTiles;
        Random rand;

        if (size == 0) {
            throw new SizeOfMapWasNotSet();
        }
        map = new TILE_TYPE[size][size];
        rand = new Random();

        // Fill the whole map with Grass Tile.
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                map[i][j] = TILE_TYPE.GRASS;
            }
        }
        // Generate random points, size is exclusive but 0 is inclusive.
        x = rand.nextInt(size);
        y = rand.nextInt(size);
        waterTileAmount = rand.nextInt((MAX_WATER_TILE - MIN_WATER_TILE) + 1) + MIN_WATER_TILE;

        // Generate random tile location.
        map[x][y] = TILE_TYPE.TREASURE;

        // There will around 25% to 35% water Tiles (rounded to the next Integer)
        totalAmountOfWaterTiles = (int) Math.ceil(((double) (size * size) / 100) * waterTileAmount);
        while (counter++ < totalAmountOfWaterTiles) {
            x = rand.nextInt(size);
            y = rand.nextInt(size);
            // If the location is already water or treasure, it does not count as a new water Tile.
            if (map[x][y] == TILE_TYPE.TREASURE || map[x][y] == TILE_TYPE.WATER) {
                counter--;
                continue;
            }
            map[x][y] = TILE_TYPE.WATER;
        }
    }
}
