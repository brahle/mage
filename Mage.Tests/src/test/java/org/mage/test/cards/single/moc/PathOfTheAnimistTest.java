package org.mage.test.cards.single.moc;

import mage.constants.PhaseStep;
import mage.constants.Planes;
import mage.constants.Zone;
import mage.counters.CounterType;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author TheElk801
 */
public class PathOfTheAnimistTest extends CardTestPlayerBase {

    @Test
    public void test_PathOfTheAnimist_NoPlane_VotePlaneswalk() {
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 4);
        addCard(Zone.HAND, playerA, "Path of the Animist");
        addCard(Zone.LIBRARY, playerA, "Forest", 2);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Animist");
        addTarget(playerA, "Forest^Forest"); // search 2 basic forests
        setChoice(playerA, "Yes"); // vote planeswalk
        setChoice(playerB, "Yes"); // vote planeswalk

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Forest", 6);
        assertTappedCount("Forest", true, 6); // 4 tapped for mana, 2 entered tapped
        assertGraveyardCount(playerA, "Path of the Animist", 1);
    }

    @Test
    public void test_PathOfTheAnimist_NoPlane_VoteChaos() {
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 4);
        addCard(Zone.HAND, playerA, "Path of the Animist");
        addCard(Zone.LIBRARY, playerA, "Forest", 2);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Animist");
        addTarget(playerA, "Forest^Forest"); // search 2 basic forests
        setChoice(playerA, "No"); // vote chaos
        setChoice(playerB, "No"); // vote chaos

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Forest", 6);
        assertTappedCount("Forest", true, 6);
        assertGraveyardCount(playerA, "Path of the Animist", 1);
    }

    @Test
    public void test_PathOfTheAnimist_WithPlane_ChaosEnsues() {
        // Hedron Fields of Agadeem: Whenever you roll {CHAOS}, create a 7/7 colorless Eldrazi creature token with annihilator 1.
        addPlane(playerA, Planes.PLANE_HEDRON_FIELDS_OF_AGADEEM);
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 4);
        addCard(Zone.HAND, playerA, "Path of the Animist");
        addCard(Zone.LIBRARY, playerA, "Forest", 2);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Animist");
        addTarget(playerA, "Forest^Forest");
        setChoice(playerA, "No"); // vote chaos
        setChoice(playerB, "No"); // vote chaos

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Forest", 6);
        assertPermanentCount(playerA, "Eldrazi Token", 1);
        assertGraveyardCount(playerA, "Path of the Animist", 1);
    }

    @Test
    public void test_PathOfTheAnimist_WithPlane_VotePlaneswalk() {
        addPlane(playerA, Planes.PLANE_HEDRON_FIELDS_OF_AGADEEM);
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 4);
        addCard(Zone.HAND, playerA, "Path of the Animist");
        addCard(Zone.LIBRARY, playerA, "Forest", 2);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Animist");
        addTarget(playerA, "Forest^Forest");
        setChoice(playerA, "Yes"); // vote planeswalk
        setChoice(playerB, "Yes"); // vote planeswalk

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Forest", 6);
        assertPermanentCount(playerA, "Eldrazi Token", 0); // No chaos triggered
        assertGraveyardCount(playerA, "Path of the Animist", 1);
    }

    @Test
    public void test_PathChaosUsesPlaneSource() {
        addPlane(playerA, Planes.PLANE_FEEDING_GROUNDS);
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 4);
        addCard(Zone.BATTLEFIELD, playerA, "Mirran Crusader");
        addCard(Zone.HAND, playerA, "Path of the Animist");
        addCard(Zone.LIBRARY, playerA, "Forest", 2);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Animist");
        addTarget(playerA, "Forest^Forest");
        setChoice(playerA, false);
        setChoice(playerB, false);
        addTarget(playerA, "Mirran Crusader");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertCounterCount(playerA, "Mirran Crusader", CounterType.P1P1, 3);
    }
}
