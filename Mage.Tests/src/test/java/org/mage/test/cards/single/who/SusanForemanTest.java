package org.mage.test.cards.single.who;

import mage.abilities.keyword.DoctorsCompanionAbility;
import mage.abilities.keyword.PlainswalkAbility;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author Susucr
 */
public class SusanForemanTest extends CardTestPlayerBase {

    @Test
    public void test_ManaAbility() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Susan Foreman");
        addCard(Zone.HAND, playerA, "Llanowar Elves");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Llanowar Elves");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Llanowar Elves", 1);
        assertTappedCount("Susan Foreman", true, 1);
    }

    @Test
    public void test_Plainswalk_DefendingPlayerControlsPlains() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Susan Foreman");
        addCard(Zone.BATTLEFIELD, playerB, "Plains");
        addCard(Zone.BATTLEFIELD, playerB, "Silvercoat Lion");

        attack(1, playerA, "Susan Foreman");
        block(1, playerB, "Silvercoat Lion", "Susan Foreman");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertLife(playerB, 20 - 2);
    }

    @Test
    public void test_Plainswalk_DefendingPlayerControlsNoPlains() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Susan Foreman");
        addCard(Zone.BATTLEFIELD, playerB, "Mountain");
        addCard(Zone.BATTLEFIELD, playerB, "Silvercoat Lion");

        attack(1, playerA, "Susan Foreman");
        block(1, playerB, "Silvercoat Lion", "Susan Foreman");

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        // Lion blocked Susan Foreman: Susan (2/3) survives, Lion (2/2) dies, no combat damage to playerB
        assertPermanentCount(playerA, "Susan Foreman", 1);
        assertGraveyardCount(playerB, "Silvercoat Lion", 1);
        assertLife(playerB, 20);
    }

    @Test
    public void test_HasDoctorsCompanionAndPlainswalk() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Susan Foreman");

        setStopAt(1, PhaseStep.PRECOMBAT_MAIN);
        execute();

        assertAbility(playerA, "Susan Foreman", DoctorsCompanionAbility.getInstance(), true);
        assertAbility(playerA, "Susan Foreman", new PlainswalkAbility(), true);
    }
}
