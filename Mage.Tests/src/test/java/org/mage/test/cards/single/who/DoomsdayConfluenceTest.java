package org.mage.test.cards.single.who;

import mage.abilities.keyword.MenaceAbility;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * {@link mage.cards.d.DoomsdayConfluence Doomsday Confluence}
 * {4}{B}{B}
 * Sorcery
 * Choose three. You may choose the same mode more than once.
 * • Target opponent loses 3 life and you draw a card.
 * • Create a 3/3 black Dalek artifact creature token with menace.
 * • Return target creature card from your graveyard to your hand.
 *
 * @author Susucr
 */
public class DoomsdayConfluenceTest extends CardTestPlayerBase {

    @Test
    public void testThreeDifferentModes() {
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 6);
        addCard(Zone.HAND, playerA, "Doomsday Confluence");
        addCard(Zone.GRAVEYARD, playerA, "Grizzly Bears");

        setStrictChooseMode(true);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Doomsday Confluence");
        setModeChoice(playerA, "1");
        setModeChoice(playerA, "2");
        setModeChoice(playerA, "3");
        addTarget(playerA, playerB); // Target opponent for mode 1
        addTarget(playerA, "Grizzly Bears"); // Target creature in graveyard for mode 3

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertLife(playerB, 17);
        assertHandCount(playerA, "Grizzly Bears", 1);
        assertPermanentCount(playerA, "Dalek Token", 1);
        assertAbility(playerA, "Dalek Token", new MenaceAbility(false), true);
        assertPowerToughness(playerA, "Dalek Token", 3, 3);
        assertGraveyardCount(playerA, "Doomsday Confluence", 1);
    }

    @Test
    public void testModeOneThreeTimes() {
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 6);
        addCard(Zone.HAND, playerA, "Doomsday Confluence");

        setStrictChooseMode(true);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Doomsday Confluence");
        setModeChoice(playerA, "1");
        setModeChoice(playerA, "1");
        setModeChoice(playerA, "1");
        addTarget(playerA, playerB);
        addTarget(playerA, playerB);
        addTarget(playerA, playerB);

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertLife(playerB, 11);
        // Player draws 3 cards, cast 1 -> 3 cards in hand
        assertHandCount(playerA, 3);
    }

    @Test
    public void testModeTwoThreeTimes() {
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 6);
        addCard(Zone.HAND, playerA, "Doomsday Confluence");

        setStrictChooseMode(true);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Doomsday Confluence");
        setModeChoice(playerA, "2");
        setModeChoice(playerA, "2");
        setModeChoice(playerA, "2");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "Dalek Token", 3);
    }
}
