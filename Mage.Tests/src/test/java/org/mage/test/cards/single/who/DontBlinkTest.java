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
     * Don't Blink {1}{U}
     * Instant
     * Until end of turn, if one or more creatures would enter from exile or after being cast from exile, their owners shuffle them into their libraries instead.
     * Cycling {2} ({2}, Discard this card: Draw a card.)
     */
    private static final String dontBlink = "Don't Blink";

    @Test
    public void test_BlinkEffect_ShuffledIntoLibrary() {
        // Player A: Cloudshift (Exile target creature you control, then return that card to the battlefield under its owner's control.)
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");
        addCard(Zone.HAND, playerA, dontBlink);
        addCard(Zone.HAND, playerA, "Cloudshift");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, dontBlink);
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Cloudshift", "Silvercoat Lion");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "Silvercoat Lion", 0);
        assertExileCount(playerA, "Silvercoat Lion", 0);
        assertLibraryCount(playerA, "Silvercoat Lion", 1);
        assertGraveyardCount(playerA, dontBlink, 1);
        assertGraveyardCount(playerA, "Cloudshift", 1);
    }

    @Test
    public void test_CastFromExile_ShuffledIntoLibrary() {
        // Light Up the Stage: Exile the top two cards of your library. Until the end of your next turn, you may play those cards.
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 2);
        addCard(Zone.HAND, playerA, dontBlink);
        addCard(Zone.HAND, playerA, "Light Up the Stage");
        addCard(Zone.LIBRARY, playerA, "Silvercoat Lion");
        addCard(Zone.LIBRARY, playerA, "Grizzly Bears");

        skipInitShuffling();

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Light Up the Stage");
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, dontBlink);
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);

        // Cast Grizzly Bears from exile
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Grizzly Bears");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "Grizzly Bears", 0);
        assertExileCount(playerA, "Grizzly Bears", 0);
        assertLibraryCount(playerA, "Grizzly Bears", 1);
        assertExileCount(playerA, "Silvercoat Lion", 1);
    }

    @Test
    public void test_NormalCastFromHand_NotAffected() {
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 4);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);
        addCard(Zone.HAND, playerA, dontBlink);
        addCard(Zone.HAND, playerA, "Silvercoat Lion");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, dontBlink);
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Silvercoat Lion");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "Silvercoat Lion", 1);
    }

    @Test
    public void test_Cycling() {
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        addCard(Zone.HAND, playerA, dontBlink);
        addCard(Zone.LIBRARY, playerA, "Grizzly Bears");

        skipInitShuffling();

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Cycling {2}");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertHandCount(playerA, "Grizzly Bears", 1);
        assertGraveyardCount(playerA, dontBlink, 1);
    }
}
