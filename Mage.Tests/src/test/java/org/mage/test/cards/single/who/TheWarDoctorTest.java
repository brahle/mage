package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.counters.CounterType;
import org.junit.Test;
import org.mage.test.player.TestPlayer;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author Susucr
 */
public class TheWarDoctorTest extends CardTestPlayerBase {

    private static final String warDoctor = "The War Doctor";

    @Test
    public void test_ExileCardSingleAndBatch() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Plains", 1);
        addCard(Zone.BATTLEFIELD, playerA, warDoctor); // 3/5
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");
        addCard(Zone.HAND, playerA, "Swords to Plowshares");

        // Swords to Plowshares exiles Grizzly Bears (single card exile)
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Swords to Plowshares", "Grizzly Bears");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertCounterCount(playerA, warDoctor, CounterType.TIME, 1);
        assertExileCount(playerB, "Grizzly Bears", 1);
    }

    @Test
    public void test_ExileBatch() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, warDoctor);
        addCard(Zone.GRAVEYARD, playerB, "Grizzly Bears");
        addCard(Zone.GRAVEYARD, playerB, "Centaur Courser");
        addCard(Zone.GRAVEYARD, playerB, "Craw Wurm");
        addCard(Zone.HAND, playerA, "Bojuka Bog");

        // Play Bojuka Bog targeting playerB to exile all 3 cards from their graveyard simultaneously
        playLand(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Bojuka Bog");
        addTarget(playerA, playerB);

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        // 3 cards exiled in a single batch -> only 1 time counter
        assertCounterCount(playerA, warDoctor, CounterType.TIME, 1);
        assertExileCount(playerB, 3);
    }

    @Test
    public void test_ExileTokenDoesNotTrigger() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Plains", 3);
        addCard(Zone.BATTLEFIELD, playerA, warDoctor);
        addCard(Zone.HAND, playerA, "Raise the Alarm");
        addCard(Zone.HAND, playerA, "Swords to Plowshares");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Raise the Alarm");

        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Swords to Plowshares", "Soldier Token");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        // Token exiled does not trigger The War Doctor
        assertCounterCount(playerA, warDoctor, CounterType.TIME, 0);
    }

    @Test
    public void test_CascadeSequential() {
        skipInitShuffling();
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Taiga", 4);
        addCard(Zone.BATTLEFIELD, playerA, warDoctor);
        addCard(Zone.HAND, playerA, "Bloodbraid Elf"); // {2}{R}{G}, Cascade

        // Top of library:
        // Forest (MV 0, land)
        // Mountain (MV 0, land)
        // Plains (MV 0, land)
        // Grizzly Bears (MV 2, nonland MV < 4)
        addCard(Zone.LIBRARY, playerA, "Grizzly Bears");
        addCard(Zone.LIBRARY, playerA, "Plains");
        addCard(Zone.LIBRARY, playerA, "Mountain");
        addCard(Zone.LIBRARY, playerA, "Forest");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Bloodbraid Elf");
        setChoice(playerA, true); // cast Grizzly Bears with cascade
        // 4 triggers are put on the stack, choose order for first 3
        setChoice(playerA, "Whenever one or more other permanents phase out", 3);

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        // 4 cards exiled sequentially one by one during cascade -> 4 time counters!
        assertCounterCount(playerA, warDoctor, CounterType.TIME, 4);
        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertPermanentCount(playerA, "Bloodbraid Elf", 1);
    }

    @Test
    public void test_PhasingOut() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Plains", 4);
        addCard(Zone.BATTLEFIELD, playerA, warDoctor);
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears");
        addCard(Zone.BATTLEFIELD, playerA, "Centaur Courser");
        addCard(Zone.HAND, playerA, "Clever Concealment");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Clever Concealment");
        addTarget(playerA, "Grizzly Bears^Centaur Courser");
        addTarget(playerA, TestPlayer.TARGET_SKIP);

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        // 2 creatures phased out simultaneously in a batch -> 1 time counter!
        assertCounterCount(playerA, warDoctor, CounterType.TIME, 1);
    }

    @Test
    public void test_AttackDamageAndExileOnDeath() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, warDoctor);
        addCounters(1, PhaseStep.UPKEEP, playerA, warDoctor, CounterType.TIME, 3);
        addCard(Zone.BATTLEFIELD, playerB, "Hill Giant"); // 3/3

        attack(1, playerA, warDoctor, playerB);
        addTarget(playerA, "Hill Giant"); // War Doctor trigger deals 3 damage to Hill Giant

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        // Hill Giant took 3 damage and died, should be in exile
        assertExileCount(playerB, "Hill Giant", 1);
        assertGraveyardCount(playerB, "Hill Giant", 0);
    }

    @Test
    public void test_AttackDamageSurvivesAndDiesLater() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        addCard(Zone.BATTLEFIELD, playerA, warDoctor);
        addCounters(1, PhaseStep.UPKEEP, playerA, warDoctor, CounterType.TIME, 2);
        addCard(Zone.BATTLEFIELD, playerB, "Hill Giant"); // 3/3
        addCard(Zone.HAND, playerA, "Lightning Bolt");

        attack(1, playerA, warDoctor, playerB);
        addTarget(playerA, "Hill Giant"); // Deals 2 damage, Hill Giant survives with 2 damage marked

        // In postcombat main, finish it off with Lightning Bolt
        castSpell(1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Lightning Bolt", "Hill Giant");

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        // Hill Giant was dealt damage by The War Doctor's ability this turn and died -> exiled!
        assertExileCount(playerB, "Hill Giant", 1);
        assertGraveyardCount(playerB, "Hill Giant", 0);
    }

    @Test
    public void test_AttackDamageToPlayer() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, warDoctor);
        addCounters(1, PhaseStep.UPKEEP, playerA, warDoctor, CounterType.TIME, 4);

        attack(1, playerA, warDoctor, playerB);
        addTarget(playerA, playerB); // Trigger deals 4 damage to playerB

        // playerB does not block, takes 3 combat damage from 3/5 War Doctor too
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        // 20 - 4 (trigger) - 3 (combat) = 13
        assertLife(playerB, 13);
    }
}
