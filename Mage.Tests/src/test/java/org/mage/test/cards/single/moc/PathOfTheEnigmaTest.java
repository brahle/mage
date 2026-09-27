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
public class PathOfTheEnigmaTest extends CardTestPlayerBase {

    @Test
    public void test_PathOfTheEnigma_NoPlane_VotePlaneswalk() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5);
        addCard(Zone.HAND, playerA, "Path of the Enigma");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Enigma", playerA);
        setChoice(playerA, "Yes"); // vote planeswalk
        setChoice(playerB, "Yes"); // vote planeswalk

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertHandCount(playerA, 4);
        assertGraveyardCount(playerA, "Path of the Enigma", 1);
    }

    @Test
    public void test_PathOfTheEnigma_NoPlane_VoteChaos() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5);
        addCard(Zone.HAND, playerA, "Path of the Enigma");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Enigma", playerA);
        setChoice(playerA, "No"); // vote chaos
        setChoice(playerB, "No"); // vote chaos

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertHandCount(playerA, 4);
        assertGraveyardCount(playerA, "Path of the Enigma", 1);
    }

    @Test
    public void test_PathOfTheEnigma_WithPlane_ChaosEnsues() {
        // Hedron Fields of Agadeem: Whenever you roll {CHAOS}, create a 7/7 colorless Eldrazi creature token with annihilator 1.
        addPlane(playerA, Planes.PLANE_HEDRON_FIELDS_OF_AGADEEM);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5);
        addCard(Zone.HAND, playerA, "Path of the Enigma");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Enigma", playerA);
        setChoice(playerA, "No"); // vote chaos
        setChoice(playerB, "No"); // vote chaos

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertHandCount(playerA, 4);
        assertPermanentCount(playerA, "Eldrazi Token", 1);
        assertGraveyardCount(playerA, "Path of the Enigma", 1);
    }

    @Test
    public void test_PathOfTheEnigma_WithPlane_VotePlaneswalk() {
        addPlane(playerA, Planes.PLANE_HEDRON_FIELDS_OF_AGADEEM);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5);
        addCard(Zone.HAND, playerA, "Path of the Enigma");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Enigma", playerA);
        setChoice(playerA, "Yes"); // vote planeswalk
        setChoice(playerB, "Yes"); // vote planeswalk

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertHandCount(playerA, 4);
        assertPermanentCount(playerA, "Eldrazi Token", 0);
        assertGraveyardCount(playerA, "Path of the Enigma", 1);
    }

    @Test
    public void test_PathChaosUsesPlaneSource() {
        addPlane(playerA, Planes.PLANE_FEEDING_GROUNDS);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5);
        addCard(Zone.BATTLEFIELD, playerA, "Skylasher");
        addCard(Zone.HAND, playerA, "Path of the Enigma");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Enigma", playerA);
        setChoice(playerA, false);
        setChoice(playerB, false);
        addTarget(playerA, "Skylasher");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertCounterCount(playerA, "Skylasher", CounterType.P1P1, 2);
    }
}
