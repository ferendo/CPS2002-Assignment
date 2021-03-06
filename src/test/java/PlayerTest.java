import exceptions.InitialPlayerPositionWasNotSet;
import exceptions.MapWasAlreadyInitialized;
import exceptions.PlayerDidNotHaveAnyPositionsYet;
import exceptions.PositionIsOutOfRange;
import org.junit.*;

import java.lang.reflect.Field;

import static org.junit.Assert.fail;

/**
 * @author Miguel Dingli
 */
public class PlayerTest {

    private Player player;
    private final int startX = 5, startY = 10, id = 1;

    @Before
    public void setUp() {
        player = new Player(id);
    }

    @After
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {
        // Clear Map instance
        Field instance = Map.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void getId_constructorValueMatchesGetterValue() {
        Assert.assertTrue(player.getID() == id);
    }

    @Test
    public void getTeam_constructorValueMatchesGetterValue() {

        final int teamID = 10;
        final Team team = new Team(teamID);
        player = new Player(id, team);
        Assert.assertTrue(player.getTeam().getID() == teamID);
    }

    @Test(expected = PlayerDidNotHaveAnyPositionsYet.class)
    public void resetInitialPosition_playerMustHaveAtLeastOnePosition() {

        player.resetInitialPosition();
        fail("Reset allowed for a player with no positions.");
    }

    @Test
    public void resetInitialPosition_initialPositionChangesAfterReset() {

        final Position firstStartPos = new Position(startX, startY);
        final Position secondStartPos = new Position(startX - 1, startY - 1);
        final Position arbitraryPos = new Position(startX - 2, startY - 2);

        // By the end of this part, visited list = [firstStartPos]
        setStartPosition();
        Assume.assumeTrue(player.getPosition().equals(firstStartPos));

        // By the end of this part, visited list = [firstStartPos, secondStartPos]
        player.setPosition(secondStartPos);
        Assume.assumeTrue(player.getPosition().equals(secondStartPos));

        // By the end of this part, visited list = [secondStartPos, firstStartPos]
        player.resetInitialPosition();

        // By the end of this part, visited list = [secondStartPos, firstStartPos, arbitraryPos]
        player.setPosition(arbitraryPos);
        Assume.assumeTrue(player.getPosition().equals(arbitraryPos));

        // Player should be returned to secondStartPos
        player.backToStartPosition();
        Assert.assertTrue(player.getPosition().equals(secondStartPos));
    }

    @Test
    public void getTeam_noTeamMeansThatGetterReturnsNull() {

        player = new Player(id);
        Assert.assertTrue(player.getTeam() == null);
    }

    @Test
    public void setPosition_nullArgument() {
        Assert.assertFalse(player.setPosition(null));
    }
    @Test
    public void addPosition_nullArgument() {
        Assert.assertFalse(player.addPosition(null));
    }

    @Test(expected = InitialPlayerPositionWasNotSet.class)
    public void getPosition_valueBeforeSettingInitialPositionIsNull() {
        player.getPosition();
    }

    @Test
    public void getPosition_setterValueMatchesGetterValue() {
        setStartPosition();
        Assert.assertTrue(player.getPosition().getX() == startX);
        Assert.assertTrue(player.getPosition().getY() == startY);
    }

    @Test
    public void wasVisited_startPositionShouldBeVisited() throws PositionIsOutOfRange, MapWasAlreadyInitialized {
        setStartPosition();
        int mapSize = 20;

        generateMap(mapSize);
        Assert.assertTrue(player.wasVisited(startX, startY));
    }

    @Test
    public void wasVisited_addedPositionShouldBeVisited() throws PositionIsOutOfRange, MapWasAlreadyInitialized {
        setStartPosition();
        int mapSize = 20;
        Position posToAdd = new Position(5, 10);

        generateMap(mapSize);
        Assert.assertTrue(player.wasVisited(posToAdd.getX(), posToAdd.getY()));
    }

    @Test
    public void wasVisited_nonStartPositionShouldNotBeVisited() throws PositionIsOutOfRange, MapWasAlreadyInitialized {
        setStartPosition();
        int mapSize = 20;

        generateMap(mapSize);
        Assert.assertFalse(player.wasVisited(startX + 1, startY + 1));
    }

    @Test(expected = PositionIsOutOfRange.class)
    public void wasVisited_negativeCoordinates() throws PositionIsOutOfRange, MapWasAlreadyInitialized {
        setStartPosition();
        int mapSize = 20;

        generateMap(mapSize);
        player.wasVisited(-1, -1);
    }

    @Test(expected = PositionIsOutOfRange.class)
    public void wasVisited_xPositionGreaterThanMapSize() throws PositionIsOutOfRange, MapWasAlreadyInitialized {
        setStartPosition();
        int mapSize = 20;

        generateMap(mapSize);
        player.wasVisited(mapSize + 1, 1);
    }

    @Test(expected = PositionIsOutOfRange.class)
    public void wasVisited_yPositionGreaterThanMapSize() throws PositionIsOutOfRange, MapWasAlreadyInitialized {
        setStartPosition();
        int mapSize = 20;

        generateMap(mapSize);
        player.wasVisited(1, mapSize + 1);
    }

    @Test
    public void backToStartPosition_positionAfterCallEqualToStartPosition() {
        setStartPosition();
        player.backToStartPosition();
        Assert.assertTrue(player.getPosition().equals(new Position(startX, startY)));
    }

    @Test
    public void backToStartPosition_positionAfterCallUnequalToNonStartPosition() {
        setStartPosition();
        Assume.assumeTrue(player.setPosition(new Position(startX + 1, startY + 1)));
        player.backToStartPosition();
        Assert.assertTrue(player.getPosition().equals(new Position(startX, startY)));
    }

    private void generateMap(final int mapSize) throws MapWasAlreadyInitialized {
        int numberOfPlayers = 3;

        // Map is needed to set the size of the map used by wasVisited
        Map map = new SafeMap();
        Assume.assumeTrue(map.setMapSize(mapSize, mapSize, numberOfPlayers));
    }

    private void setStartPosition() {
        Assume.assumeTrue(player.setPosition(new Position(startX, startY)));
    }
}