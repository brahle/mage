package org.mage.test.cards.single.who;

import mage.abilities.keyword.MenaceAbility;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * {@link mage.cards.d.DoomsdayConfluence Doomsday Confluence}
 * {X}{X}{B}
 * Sorcery
 * Choose X. You may choose the same mode more than once.
 * • Each player sacrifices a nonartifact creature of their choice.
 * • Create a 3/3 black Dalek artifact creature token with menace.
 * • Each opponent discards a card.
 *
 * @author Susucr
 */
public class DoomsdayConfluenceTest extends CardTestPlayerBase {

    @Test
    public void testThreeDifferentModes() {
        // X = 3 -> cost {3}{3}{B} = {6}{B}
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 7);
        addCard(Zone.HAND, playerA, "Doomsday Confluence");
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears"); // nonartifact creature
        addCard(Zone.BATTLEFIELD, playerA, "Ornithopter"); // artifact creature (should not be sacrificed)

        addCard(Zone.BATTLEFIELD, playerB, "Silvercoat Lion"); // nonartifact creature
        addCard(Zone.HAND, playerB, "Lightning Bolt"); // to discard

        setStrictChooseMode(true);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Doomsday Confluence");
        setChoice(playerA, "X=3");
        setModeChoice(playerA, "1");
        setModeChoice(playerA, "2");
        setModeChoice(playerA, "3");
        setChoice(playerA, "Grizzly Bears"); // player A sacrifices nonartifact creature
        setChoice(playerB, "Silvercoat Lion"); // player B sacrifices nonartifact creature
        addTarget(playerB, "Lightning Bolt"); // player B discards card

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertGraveyardCount(playerA, "Grizzly Bears", 1);
        assertPermanentCount(playerA, "Ornithopter", 1);
        assertPermanentCount(playerA, "Dalek Token", 1);
        assertAbility(playerA, "Dalek Token", new MenaceAbility(false), true);
        assertPowerToughness(playerA, "Dalek Token", 3, 3);

        assertGraveyardCount(playerB, "Silvercoat Lion", 1);
        assertGraveyardCount(playerB, "Lightning Bolt", 1);
        assertGraveyardCount(playerA, "Doomsday Confluence", 1);
    }

    @Test
    public void testModeTwoTwice() {
        // X = 2 -> cost {2}{2}{B} = {4}{B} = 5 mana
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 5);
        addCard(Zone.HAND, playerA, "Doomsday Confluence");

        setStrictChooseMode(true);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Doomsday Confluence");
        setChoice(playerA, "X=2");
        setModeChoice(playerA, "2");
        setModeChoice(playerA, "2");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "Dalek Token", 2);
        assertGraveyardCount(playerA, "Doomsday Confluence", 1);
    }

    @Test
    public void testXZero() {
        // X = 0 -> cost {0}{0}{B} = {B}
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 1);
        addCard(Zone.HAND, playerA, "Doomsday Confluence");

        setStrictChooseMode(true);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Doomsday Confluence");
        setChoice(playerA, "X=0");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertGraveyardCount(playerA, "Doomsday Confluence", 1);
    }
}
