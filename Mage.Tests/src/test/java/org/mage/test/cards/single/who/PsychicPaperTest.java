package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.SubType;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author Susucr
 */
public class PsychicPaperTest extends CardTestPlayerBase {

    @Test
    public void test_CastAndEquip_ChangesNameAndReplacesCreatureType() {
        setStrictChooseMode(true);
        addCard(Zone.HAND, playerA, "Psychic Paper");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion"); // Cat
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 4);

        // Cast Psychic Paper ({2}) - no choices made on cast/ETB
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Psychic Paper");
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);

        // Equip to Silvercoat Lion ({2}) - choices made as it becomes attached
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Equip {2}", "Silvercoat Lion");
        setChoice(playerA, "Grizzly Bears"); // Choose creature card name
        setChoice(playerA, "Bear");          // Choose creature type

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertPermanentCount(playerA, "Silvercoat Lion", 0);
        assertSubtype("Grizzly Bears", SubType.BEAR);
        assertNotSubtype("Grizzly Bears", SubType.CAT);
    }

    @Test
    public void test_Unattached_DoesNotChangeNameOrType() {
        setStrictChooseMode(true);
        addCard(Zone.HAND, playerA, "Psychic Paper");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);

        // Cast Psychic Paper ({2}), do not equip - no choices prompted
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Psychic Paper");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Silvercoat Lion", 1);
        assertSubtype("Silvercoat Lion", SubType.CAT);
        assertPermanentCount(playerA, "Psychic Paper", 1);
    }

    @Test
    public void test_Reequip_AllowsNewChoiceAndRevertsOldCreature() {
        setStrictChooseMode(true);
        addCard(Zone.HAND, playerA, "Psychic Paper");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerA, "Memnite");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 6);

        // Cast Psychic Paper
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Psychic Paper");
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);

        // Equip to Silvercoat Lion
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Equip {2}", "Silvercoat Lion");
        setChoice(playerA, "Grizzly Bears");
        setChoice(playerA, "Bear");
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);

        // Re-equip to Memnite - new choices as it becomes attached
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Equip {2}", "Memnite");
        setChoice(playerA, "Llanowar Elves");
        setChoice(playerA, "Elf");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        // Silvercoat Lion is no longer equipped, reverts to normal
        assertPermanentCount(playerA, "Silvercoat Lion", 1);
        assertSubtype("Silvercoat Lion", SubType.CAT);
        assertNotSubtype("Silvercoat Lion", SubType.BEAR);

        // Memnite is now equipped, becomes Llanowar Elves + Elf (Construct is replaced)
        assertPermanentCount(playerA, "Llanowar Elves", 1);
        assertPermanentCount(playerA, "Memnite", 0);
        assertSubtype("Llanowar Elves", SubType.ELF);
        assertNotSubtype("Llanowar Elves", SubType.CONSTRUCT);
    }

    @Test
    public void test_EquippedCreature_CantBeBlocked() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Psychic Paper");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);

        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");

        // Equip
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Equip {2}", "Silvercoat Lion");
        setChoice(playerA, "Grizzly Bears");
        setChoice(playerA, "Bear");

        attack(1, playerA, "Grizzly Bears", playerB);
        block(1, playerB, "Grizzly Bears", "Grizzly Bears"); // Opponent's Grizzly Bears cannot block attacker

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertLife(playerB, 20 - 2);
    }

    @Test
    public void test_EquippedCreature_HasWard() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Psychic Paper");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);

        addCard(Zone.BATTLEFIELD, playerB, "Mountain", 2);
        addCard(Zone.HAND, playerB, "Lightning Bolt");

        // Equip
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Equip {2}", "Silvercoat Lion");
        setChoice(playerA, "Grizzly Bears");
        setChoice(playerA, "Bear");

        // Player B targets Grizzly Bears (the equipped Lion) with Lightning Bolt and pays ward {1}
        castSpell(1, PhaseStep.POSTCOMBAT_MAIN, playerB, "Lightning Bolt", "Grizzly Bears");
        setChoice(playerB, true); // pay Ward {1}

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertPermanentCount(playerA, "Grizzly Bears", 0);
        assertGraveyardCount(playerA, "Silvercoat Lion", 1);
        assertGraveyardCount(playerB, "Lightning Bolt", 1);
        assertTappedCount("Mountain", true, 2); // 1 for Bolt, 1 for Ward
    }
}
