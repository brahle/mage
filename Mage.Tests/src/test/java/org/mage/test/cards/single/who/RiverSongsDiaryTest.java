package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author Susucr
 */
public class RiverSongsDiaryTest extends CardTestPlayerBase {

    @Test
    public void test_Imprint_ExilesResolvingSpellFromHand() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "River Song's Diary");
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        addCard(Zone.HAND, playerA, "Lightning Bolt");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertLife(playerB, 20 - 3);
        assertGraveyardCount(playerA, "Lightning Bolt", 0);
        assertExileCount(playerA, "Lightning Bolt", 1);
    }

    @Test
    public void test_Imprint_DoesNotExileCreatureSpell() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "River Song's Diary");
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 2);
        addCard(Zone.HAND, playerA, "Grizzly Bears");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Grizzly Bears");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertExileCount(playerA, 0);
    }

    @Test
    public void test_UpkeepTrigger_LessThan4Cards_DoesNotTrigger() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "River Song's Diary");
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 3);
        addCard(Zone.HAND, playerA, "Lightning Bolt", 3);

        // Turn 1: cast 3 Lightning Bolts
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);

        // Turn 3 upkeep: only 3 cards exiled, condition not met (requires 4 or more)
        setStopAt(3, PhaseStep.PRECOMBAT_MAIN);
        execute();

        assertLife(playerB, 20 - 9);
        assertExileCount(playerA, "Lightning Bolt", 3);
    }

    @Test
    public void test_UpkeepTrigger_FourOrMoreCards_CastsRandomSpellForFree() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "River Song's Diary");
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 4);
        addCard(Zone.HAND, playerA, "Lightning Bolt", 4);

        // Turn 1: cast 4 Lightning Bolts to exile 4 cards with the Diary
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);

        // Turn 3 upkeep: Diary triggers, choosing a card at random
        // Cast without paying its mana cost?
        setChoice(playerA, "Yes");
        addTarget(playerA, playerB); // Target for free Lightning Bolt

        setStopAt(3, PhaseStep.PRECOMBAT_MAIN);
        execute();

        // 4 bolts on Turn 1 (12 damage) + 1 free bolt on Turn 3 (3 damage) = 15 damage
        assertLife(playerB, 20 - 15);
    }

    @Test
    public void test_UpkeepTrigger_DiaryRemovedWithUpkeepOnStack_StillCasts() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "River Song's Diary");
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 4);
        addCard(Zone.HAND, playerA, "Lightning Bolt", 4);
        addCard(Zone.BATTLEFIELD, playerB, "Plains", 2);
        addCard(Zone.HAND, playerB, "Disenchant");

        for (int i = 0; i < 4; i++) {
            castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        }
        castSpell(3, PhaseStep.UPKEEP, playerB, "Disenchant", "River Song's Diary");
        setChoice(playerA, true);
        addTarget(playerA, playerB);

        setStopAt(3, PhaseStep.PRECOMBAT_MAIN);
        execute();

        assertGraveyardCount(playerA, "River Song's Diary", 1);
        assertLife(playerB, 20 - 15);
    }
}
