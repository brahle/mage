package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author Susucr
 */
public class RassilonTheWarPresidentTest extends CardTestPlayerBase {

    private static final String rassilon = "Rassilon, the War President";

    @Test
    public void testUpkeepTriggerAndPlayCardWithConspire() {
        setStrictChooseMode(true);

        // Player A starts
        addCard(Zone.BATTLEFIELD, playerA, rassilon);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Goblin Guide", 2);

        removeAllCardsFromLibrary(playerA);
        addCard(Zone.LIBRARY, playerA, "Lightning Bolt");

        // Turn 1 upkeep: Rassilon triggers
        // Player A loses 2 life, exiles Lightning Bolt from top of library
        // Turn 1 Precombat Main: Cast Lightning Bolt from exile with Conspire
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        setChoice(playerA, true); // Use conspire
        setChoice(playerA, "Goblin Guide^Goblin Guide"); // Tap two Goblin Guides
        setChoice(playerA, false); // Don't change target for copy

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertLife(playerA, 20 - 2);
        assertLife(playerB, 20 - 3 - 3);
        assertTapped("Goblin Guide", true);
        assertGraveyardCount(playerA, "Lightning Bolt", 1);
        assertExileCount(playerA, 0);
    }

    @Test
    public void testSpellCastFromHandDoesNotHaveConspire() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, rassilon);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Goblin Guide", 2);
        addCard(Zone.HAND, playerA, "Lightning Bolt");

        // Rassilon triggers on upkeep, let's have a Mountain on top of library
        removeAllCardsFromLibrary(playerA);
        addCard(Zone.LIBRARY, playerA, "Mountain");

        // Turn 1 Precombat Main: Cast Lightning Bolt from HAND (should NOT trigger conspire choice)
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertLife(playerA, 20 - 2);
        assertLife(playerB, 20 - 3);
        assertTapped("Goblin Guide", false);
        assertGraveyardCount(playerA, "Lightning Bolt", 1);
        assertExileCount(playerA, 1); // The Mountain from library
    }

    @Test
    public void testCreatureCastFromExileDoesNotHaveConspire() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, rassilon);
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Elvish Mystic", 2);

        removeAllCardsFromLibrary(playerA);
        addCard(Zone.LIBRARY, playerA, "Grizzly Bears");

        // Turn 1 upkeep: Rassilon triggers, exiles Grizzly Bears
        // Turn 1 Precombat Main: Cast Grizzly Bears from exile (creature spell, does NOT have conspire)
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Grizzly Bears");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertLife(playerA, 20 - 2);
        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertTapped("Elvish Mystic", false);
        assertExileCount(playerA, 0);
    }
}
