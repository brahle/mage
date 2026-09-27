package org.mage.test.cards.single.moc;

import mage.constants.PhaseStep;
import mage.constants.Planes;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author TheElk801
 */
public class PathOfTheGhosthunterTest extends CardTestPlayerBase {

    @Test
    public void test_PathOfTheGhosthunter_NoPlane_VotePlaneswalk() {
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 5);
        addCard(Zone.HAND, playerA, "Path of the Ghosthunter");

        setChoice(playerA, "X=3");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Ghosthunter");
        setChoice(playerA, "Yes"); // vote planeswalk
        setChoice(playerB, "Yes"); // vote planeswalk

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Spirit Token", 3);
        assertGraveyardCount(playerA, "Path of the Ghosthunter", 1);
    }

    @Test
    public void test_PathOfTheGhosthunter_NoPlane_VoteChaos() {
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 4);
        addCard(Zone.HAND, playerA, "Path of the Ghosthunter");

        setChoice(playerA, "X=2");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Ghosthunter");
        setChoice(playerA, "No"); // vote chaos
        setChoice(playerB, "No"); // vote chaos

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Spirit Token", 2);
        assertGraveyardCount(playerA, "Path of the Ghosthunter", 1);
    }

    @Test
    public void test_PathOfTheGhosthunter_WithPlane_ChaosEnsues() {
        // Hedron Fields of Agadeem: Whenever you roll {CHAOS}, create a 7/7 colorless Eldrazi creature token with annihilator 1.
        addPlane(playerA, Planes.PLANE_HEDRON_FIELDS_OF_AGADEEM);
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 4);
        addCard(Zone.HAND, playerA, "Path of the Ghosthunter");

        setChoice(playerA, "X=2");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Ghosthunter");
        setChoice(playerA, "No"); // vote chaos
        setChoice(playerB, "No"); // vote chaos

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Spirit Token", 2);
        assertPermanentCount(playerA, "Eldrazi Token", 1);
        assertGraveyardCount(playerA, "Path of the Ghosthunter", 1);
    }

    @Test
    public void test_PathOfTheGhosthunter_WithPlane_VotePlaneswalk() {
        addPlane(playerA, Planes.PLANE_HEDRON_FIELDS_OF_AGADEEM);
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 4);
        addCard(Zone.HAND, playerA, "Path of the Ghosthunter");

        setChoice(playerA, "X=2");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Ghosthunter");
        setChoice(playerA, "Yes"); // vote planeswalk
        setChoice(playerB, "Yes"); // vote planeswalk

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Spirit Token", 2);
        assertPermanentCount(playerA, "Eldrazi Token", 0);
        assertHandCount(playerA, 0);
    }
}
