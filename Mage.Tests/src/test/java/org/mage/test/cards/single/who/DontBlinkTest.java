package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author Susucr
 */
public class DontBlinkTest extends CardTestPlayerBase {

    /**
     * Don't Blink {2}{U}
     * Instant
     * Exile all nonland permanents that entered the battlefield this turn.
     */
    private static final String dontBlink = "Don't Blink";

    @Test
    public void test_DontBlink_ExileNonlandEnteredThisTurn() {
        // Player A: lands from turn 1
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5);
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears"); // entered on previous turn
        addCard(Zone.HAND, playerA, dontBlink);
        addCard(Zone.HAND, playerA, "Silvercoat Lion"); // creature to cast this turn
        addCard(Zone.HAND, playerA, "Sol Ring"); // artifact to cast this turn
        addCard(Zone.HAND, playerA, "Plains"); // land to play this turn

        // Player B: creature from previous turn
        addCard(Zone.BATTLEFIELD, playerB, "Hill Giant"); // entered previous turn

        // Turn 3: Player A plays a land, casts a creature and an artifact, then casts Don't Blink
        playLand(3, PhaseStep.PRECOMBAT_MAIN, playerA, "Plains");
        castSpell(3, PhaseStep.PRECOMBAT_MAIN, playerA, "Silvercoat Lion");
        waitStackResolved(3, PhaseStep.PRECOMBAT_MAIN);
        castSpell(3, PhaseStep.PRECOMBAT_MAIN, playerA, "Sol Ring");

        castSpell(3, PhaseStep.POSTCOMBAT_MAIN, playerA, dontBlink);

        setStrictChooseMode(true);
        setStopAt(3, PhaseStep.END_TURN);
        execute();

        // Entered previous turns: NOT exiled
        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertPermanentCount(playerB, "Hill Giant", 1);

        // Land entered this turn: NOT exiled (nonland filter)
        assertPermanentCount(playerA, "Plains", 1);

        // Nonlands entered this turn: exiled
        assertPermanentCount(playerA, "Silvercoat Lion", 0);
        assertPermanentCount(playerA, "Sol Ring", 0);
        assertExileCount(playerA, "Silvercoat Lion", 1);
        assertExileCount(playerA, "Sol Ring", 1);

        // Don't Blink in graveyard
        assertGraveyardCount(playerA, dontBlink, 1);
    }

    @Test
    public void test_DontBlink_OpponentPermanentsEnteredThisTurn() {
        // In Turn 2, Player B casts a creature
        addCard(Zone.BATTLEFIELD, playerB, "Mountain", 2);
        addCard(Zone.HAND, playerB, "Goblin Raider");

        // Player A has Don't Blink and mana
        addCard(Zone.BATTLEFIELD, playerA, "Island", 3);
        addCard(Zone.HAND, playerA, dontBlink);

        // Turn 2: Player B casts Goblin Raider
        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerB, "Goblin Raider");

        // Player A casts Don't Blink in Player B's postcombat main
        castSpell(2, PhaseStep.POSTCOMBAT_MAIN, playerA, dontBlink);

        setStrictChooseMode(true);
        setStopAt(2, PhaseStep.END_TURN);
        execute();

        assertPermanentCount(playerB, "Goblin Raider", 0);
        assertExileCount(playerB, "Goblin Raider", 1);
        assertGraveyardCount(playerA, dontBlink, 1);
    }
}
