package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author Susucr
 */
public class ThePandoricaTest extends CardTestPlayerBase {

    /**
     * The Pandorica {2}{W}
     * Legendary Artifact
     * You may choose not to untap The Pandorica during your untap step.
     * {1}{W}, {T}: Untap another target nonland permanent, then it phases out. It can't phase in for as long as The Pandorica remains tapped. When The Pandorica becomes untapped or leaves the battlefield, that permanent phases in. Activate only as a sorcery.
     */
    private static final String pandorica = "The Pandorica";

    @Test
    public void test_PhaseOutAndUntapPhasesIn() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        addCard(Zone.BATTLEFIELD, playerA, pandorica);
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears", 1, true); // tapped

        // Turn 1: Player A activates ability targeting Player B's Bears
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{1}{W}, {T}:", "Grizzly Bears");

        // Turn 2: Bears is phased out
        checkPermanentCount("Bears phased out", 2, PhaseStep.PRECOMBAT_MAIN, playerB, "Grizzly Bears", 0);

        // Turn 3: Player A untaps The Pandorica (default is to untap unless chosen not to)
        // Delayed trigger triggers when Pandorica untaps, phasing Bears back in
        setChoice(playerA, true); // untap Pandorica

        setStopAt(3, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerB, "Grizzly Bears", 1);
        assertTapped("Grizzly Bears", false);
    }

    @Test
    public void test_KeepTappedDuringUntapStep() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        addCard(Zone.BATTLEFIELD, playerA, pandorica);
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears", 1, true);

        // Turn 1: Activate ability targeting Bears
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{1}{W}, {T}:", "Grizzly Bears");

        // Turn 3: Player A chooses not to untap The Pandorica
        setChoice(playerA, false); // do not untap

        // Turn 3 postcombat: Bears still phased out
        checkPermanentCount("Bears still phased out turn 3", 3, PhaseStep.POSTCOMBAT_MAIN, playerB, "Grizzly Bears", 0);

        // Turn 5: Player A chooses to untap The Pandorica
        setChoice(playerA, true); // untap

        setStopAt(5, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerB, "Grizzly Bears", 1);
        assertTapped(pandorica, false);
    }

    @Test
    public void test_PhasesInWhenLeavesBattlefield() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        addCard(Zone.BATTLEFIELD, playerA, pandorica);
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears", 1, true);
        addCard(Zone.BATTLEFIELD, playerB, "Plains", 2);
        addCard(Zone.HAND, playerB, "Disenchant");

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{1}{W}, {T}:", "Grizzly Bears");

        // Turn 2: Player B destroys The Pandorica
        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerB, "Disenchant", pandorica);

        setStopAt(2, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertGraveyardCount(playerA, pandorica, 1);
        assertPermanentCount(playerB, "Grizzly Bears", 1);
    }

    @Test
    public void test_LeavesBattlefieldBeforeResolveDoesNotPhaseOut() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        addCard(Zone.BATTLEFIELD, playerA, pandorica);
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears", 1, true);
        addCard(Zone.BATTLEFIELD, playerB, "Plains", 2);
        addCard(Zone.HAND, playerB, "Disenchant");

        // Player A activates Pandorica targeting Bears
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{1}{W}, {T}:", "Grizzly Bears");

        // In response, Player B destroys Pandorica
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerB, "Disenchant", pandorica);

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        // Pandorica destroyed
        assertGraveyardCount(playerA, pandorica, 1);
        // Bears was untapped by the ability, but did NOT phase out because Pandorica was gone!
        assertPermanentCount(playerB, "Grizzly Bears", 1);
        assertTapped("Grizzly Bears", false);
    }
}
