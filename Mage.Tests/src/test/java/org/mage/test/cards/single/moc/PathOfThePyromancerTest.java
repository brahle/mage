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
public class PathOfThePyromancerTest extends CardTestPlayerBase {

    @Test
    public void test_PathOfThePyromancer_NoPlane_VotePlaneswalk() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        addCard(Zone.HAND, playerA, "Path of the Pyromancer");
        addCard(Zone.HAND, playerA, "Shock", 2);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Pyromancer");
        setChoice(playerA, "Yes"); // vote planeswalk
        setChoice(playerB, "Yes"); // vote planeswalk

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertGraveyardCount(playerA, "Shock", 2);
        assertGraveyardCount(playerA, "Path of the Pyromancer", 1);
        assertHandCount(playerA, 3); // discarded 2, drew 2 + 1 = 3
    }

    @Test
    public void test_PathOfThePyromancer_SpendProducedMana() {
        removeAllCardsFromLibrary(playerA);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        addCard(Zone.HAND, playerA, "Path of the Pyromancer");
        addCard(Zone.HAND, playerA, "Grizzly Bears", 1);
        addCard(Zone.LIBRARY, playerA, "Shock", 2);

        // Cast Path of the Pyromancer (costs {4}{R}, taps all 5 Mountains)
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Pyromancer");
        setChoice(playerA, "No"); // vote chaos
        setChoice(playerB, "No"); // vote chaos
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN, 1);

        // Path of the Pyromancer discarded 1 card, drew 2 cards (including Shock), and added {R} to mana pool
        // Spend the produced {R} to cast the drawn Shock without having any untapped lands
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Shock", playerB);

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertLife(playerB, 20 - 2);
        assertGraveyardCount(playerA, "Grizzly Bears", 1);
        assertGraveyardCount(playerA, "Shock", 1);
        assertGraveyardCount(playerA, "Path of the Pyromancer", 1);
        assertHandCount(playerA, 1);
    }

    @Test
    public void test_PathOfThePyromancer_WithPlane_ChaosEnsues() {
        // Hedron Fields of Agadeem: Whenever you roll {CHAOS}, create a 7/7 colorless Eldrazi creature token with annihilator 1.
        addPlane(playerA, Planes.PLANE_HEDRON_FIELDS_OF_AGADEEM);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        addCard(Zone.HAND, playerA, "Path of the Pyromancer");
        addCard(Zone.HAND, playerA, "Shock", 1);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Pyromancer");
        setChoice(playerA, "No"); // vote chaos
        setChoice(playerB, "No"); // vote chaos

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Eldrazi Token", 1);
        assertHandCount(playerA, 2);
    }

    @Test
    public void test_PathOfThePyromancer_WithPlane_VotePlaneswalk() {
        addPlane(playerA, Planes.PLANE_HEDRON_FIELDS_OF_AGADEEM);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        addCard(Zone.HAND, playerA, "Path of the Pyromancer");
        addCard(Zone.HAND, playerA, "Shock", 1);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Pyromancer");
        setChoice(playerA, "Yes"); // vote planeswalk
        setChoice(playerB, "Yes"); // vote planeswalk

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Eldrazi Token", 0);
        assertHandCount(playerA, 2);
    }

    @Test
    public void test_PathChaosUsesPlaneSource() {
        addPlane(playerA, Planes.PLANE_FEEDING_GROUNDS);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        addCard(Zone.BATTLEFIELD, playerA, "Silver Knight");
        addCard(Zone.HAND, playerA, "Path of the Pyromancer");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Pyromancer");
        setChoice(playerA, false);
        setChoice(playerB, false);
        addTarget(playerA, "Silver Knight");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertCounterCount(playerA, "Silver Knight", CounterType.P1P1, 2);
    }
}
