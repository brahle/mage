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
    public void test_CastAndEquip_ChangesNameAndAddsCreatureType() {
        setStrictChooseMode(true);
        addCard(Zone.HAND, playerA, "Psychic Paper");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 4);

        // Cast Psychic Paper ({2})
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Psychic Paper");
        setChoice(playerA, "Grizzly Bears"); // Choose card name
        setChoice(playerA, "Bear");          // Choose creature type
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);

        // Equip to Silvercoat Lion ({2})
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Equip {2}", "Silvercoat Lion");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertPermanentCount(playerA, "Silvercoat Lion", 0);
        assertSubtype("Grizzly Bears", SubType.CAT);
        assertSubtype("Grizzly Bears", SubType.BEAR);
    }

    @Test
    public void test_Unattached_DoesNotChangeNameOrType() {
        setStrictChooseMode(true);
        addCard(Zone.HAND, playerA, "Psychic Paper");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);

        // Cast Psychic Paper ({2}), do not equip
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Psychic Paper");
        setChoice(playerA, "Grizzly Bears");
        setChoice(playerA, "Bear");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Silvercoat Lion", 1);
        assertSubtype("Silvercoat Lion", SubType.CAT);
        assertPermanentCount(playerA, "Psychic Paper", 1);
    }

    @Test
    public void test_Reequip_MovesNameToNewCreature() {
        setStrictChooseMode(true);
        addCard(Zone.HAND, playerA, "Psychic Paper");
        addCard(Zone.BATTLEFIELD, playerA, "Silvercoat Lion");
        addCard(Zone.BATTLEFIELD, playerA, "Memnite");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 6);

        // Cast Psychic Paper
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Psychic Paper");
        setChoice(playerA, "Grizzly Bears");
        setChoice(playerA, "Bear");
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);

        // Equip to Silvercoat Lion
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Equip {2}", "Silvercoat Lion");
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);

        // Re-equip to Memnite
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Equip {2}", "Memnite");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        // Silvercoat Lion is no longer equipped, reverts to normal
        assertPermanentCount(playerA, "Silvercoat Lion", 1);
        assertSubtype("Silvercoat Lion", SubType.CAT);

        // Memnite is now equipped, becomes Grizzly Bears + Bear
        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertPermanentCount(playerA, "Memnite", 0);
        assertSubtype("Grizzly Bears", SubType.CONSTRUCT);
        assertSubtype("Grizzly Bears", SubType.BEAR);
    }
}
