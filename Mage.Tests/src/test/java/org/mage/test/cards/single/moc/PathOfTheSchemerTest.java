package org.mage.test.cards.single.moc;

import mage.constants.CardType;
import mage.constants.PhaseStep;
import mage.constants.Planes;
import mage.constants.Zone;
import mage.counters.CounterType;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author TheElk801
 */
public class PathOfTheSchemerTest extends CardTestPlayerBase {

    @Test
    public void test_PathOfTheSchemer_NoPlane_VotePlaneswalk() {
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 5);
        addCard(Zone.HAND, playerA, "Path of the Schemer");
        addCard(Zone.GRAVEYARD, playerB, "Grizzly Bears");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Schemer");
        setChoice(playerA, "Grizzly Bears");
        setChoice(playerA, "Yes"); // vote planeswalk
        setChoice(playerB, "Yes"); // vote planeswalk

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertType("Grizzly Bears", CardType.ARTIFACT, true);
        assertType("Grizzly Bears", CardType.CREATURE, true);
        assertGraveyardCount(playerA, "Path of the Schemer", 1);
    }

    @Test
    public void test_PathOfTheSchemer_NoPlane_VoteChaos() {
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 5);
        addCard(Zone.HAND, playerA, "Path of the Schemer");
        addCard(Zone.GRAVEYARD, playerA, "Grizzly Bears");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Schemer");
        setChoice(playerA, "Grizzly Bears");
        setChoice(playerA, "No"); // vote chaos
        setChoice(playerB, "No"); // vote chaos

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertType("Grizzly Bears", CardType.ARTIFACT, true);
        assertType("Grizzly Bears", CardType.CREATURE, true);
        assertGraveyardCount(playerA, "Path of the Schemer", 1);
    }

    @Test
    public void test_PathOfTheSchemer_WithPlane_ChaosEnsues() {
        // Hedron Fields of Agadeem: Whenever you roll {CHAOS}, create a 7/7 colorless Eldrazi creature token with annihilator 1.
        addPlane(playerA, Planes.PLANE_HEDRON_FIELDS_OF_AGADEEM);
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 5);
        addCard(Zone.HAND, playerA, "Path of the Schemer");
        addCard(Zone.GRAVEYARD, playerA, "Grizzly Bears");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Schemer");
        setChoice(playerA, "Grizzly Bears");
        setChoice(playerA, "No"); // vote chaos
        setChoice(playerB, "No"); // vote chaos

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertPermanentCount(playerA, "Eldrazi Token", 1);
        assertGraveyardCount(playerA, "Path of the Schemer", 1);
    }

    @Test
    public void test_PathOfTheSchemer_WithPlane_VotePlaneswalk() {
        addPlane(playerA, Planes.PLANE_HEDRON_FIELDS_OF_AGADEEM);
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 5);
        addCard(Zone.HAND, playerA, "Path of the Schemer");
        addCard(Zone.GRAVEYARD, playerA, "Grizzly Bears");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Schemer");
        setChoice(playerA, "Grizzly Bears");
        setChoice(playerA, "Yes"); // vote planeswalk
        setChoice(playerB, "Yes"); // vote planeswalk

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertPermanentCount(playerA, "Eldrazi Token", 0);
        assertHandCount(playerA, 0);
    }

    @Test
    public void test_PathChaosUsesPlaneSource() {
        addPlane(playerA, Planes.PLANE_FEEDING_GROUNDS);
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 5);
        addCard(Zone.BATTLEFIELD, playerA, "Mirran Crusader");
        addCard(Zone.GRAVEYARD, playerA, "Grizzly Bears");
        addCard(Zone.HAND, playerA, "Path of the Schemer");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Path of the Schemer");
        setChoice(playerA, "Grizzly Bears");
        setChoice(playerA, false);
        setChoice(playerB, false);
        addTarget(playerA, "Mirran Crusader");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertCounterCount(playerA, "Mirran Crusader", CounterType.P1P1, 3);
    }
}
