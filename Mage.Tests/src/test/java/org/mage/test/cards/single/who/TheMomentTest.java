package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.counters.CounterType;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author Susucr
 */
public class TheMomentTest extends CardTestPlayerBase {

    /**
     * The Moment {2}
     * Legendary Artifact
     * At the beginning of your upkeep, put a time counter on The Moment.
     * {2}, {T}: Untap target creature you control. It phases out until The Moment leaves the battlefield.
     * {3}, {T}: Destroy each nonland permanent with mana value less than or equal to the number of time counters on The Moment. Then sacrifice The Moment. Activate only as a sorcery.
     */
    private static final String moment = "The Moment";

    @Test
    public void test_UpkeepCounter() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        addCard(Zone.HAND, playerA, moment);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, moment);

        // Turn 3: playerA upkeep trigger adds time counter
        setStopAt(3, PhaseStep.PRECOMBAT_MAIN);
        execute();

        assertCounterCount(playerA, moment, CounterType.TIME, 1);
    }

    @Test
    public void test_PhaseOutAndPhaseInOnLeaveBattlefield() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        addCard(Zone.BATTLEFIELD, playerA, moment);
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears", 1, true); // tapped
        addCard(Zone.BATTLEFIELD, playerB, "Plains", 2);
        addCard(Zone.HAND, playerB, "Disenchant");

        // Turn 1: Player A activates ability to untap Bears and phase out
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{2}, {T}:", "Grizzly Bears");

        // Turn 2: Player B's turn; Bears remains phased out
        checkPermanentCount("Bears phased out", 2, PhaseStep.PRECOMBAT_MAIN, playerA, "Grizzly Bears", 0);

        // Turn 2: Player B destroys The Moment with Disenchant
        castSpell(2, PhaseStep.POSTCOMBAT_MAIN, playerB, "Disenchant", moment);

        setStopAt(2, PhaseStep.END_TURN);
        execute();

        assertGraveyardCount(playerA, moment, 1);
        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertTapped("Grizzly Bears", false);
    }

    @Test
    public void test_DestroyAndSacrificeWithFewerThanTwoCounters() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Plains", 3);
        addCard(Zone.BATTLEFIELD, playerA, moment);
        addCounters(1, PhaseStep.PRECOMBAT_MAIN, playerA, moment, CounterType.TIME, 1);

        addCard(Zone.BATTLEFIELD, playerB, "Elite Vanguard"); // CMC 1
        addCard(Zone.BATTLEFIELD, playerB, "Centaur Courser"); // CMC 3

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{3}, {T}:");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertGraveyardCount(playerB, "Elite Vanguard", 1);
        assertPermanentCount(playerB, "Centaur Courser", 1);
        assertGraveyardCount(playerA, moment, 1);
    }

    @Test
    public void test_DestroyWithTwoCountersDestroysSelfAndPhasedIn() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Plains", 5);
        addCard(Zone.BATTLEFIELD, playerA, moment);
        addCard(Zone.BATTLEFIELD, playerA, "Centaur Courser"); // CMC 3
        addCounters(1, PhaseStep.PRECOMBAT_MAIN, playerA, moment, CounterType.TIME, 2);

        // Phase out Centaur Courser with {2}, {T}
        // Wait, to do both, untap The Moment first or use separate turn:
        // Turn 1: phase out Centaur Courser
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{2}, {T}:", "Centaur Courser");

        // Turn 3: The Moment untaps on turn 3, counters now 3 (added 1 on turn 3 upkeep)
        activateAbility(3, PhaseStep.PRECOMBAT_MAIN, playerA, "{3}, {T}:");

        setStopAt(3, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        // The Moment destroyed itself; Centaur Courser (CMC 3 <= 3) was phased out so it couldn't be destroyed!
        // Then as The Moment leaves the battlefield, Centaur Courser phases in!
        assertGraveyardCount(playerA, moment, 1);
        assertPermanentCount(playerA, "Centaur Courser", 1);
    }
}
