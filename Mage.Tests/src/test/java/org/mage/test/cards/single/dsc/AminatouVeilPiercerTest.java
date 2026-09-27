package org.mage.test.cards.single.dsc;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.counters.CounterType;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author Susucr
 */
public class AminatouVeilPiercerTest extends CardTestPlayerBase {

    @Test
    public void test_SurveilOnUpkeep() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Aminatou, Veil Piercer");
        addCard(Zone.LIBRARY, playerA, "Plains");
        addCard(Zone.LIBRARY, playerA, "Island");
        skipInitShuffling();

        // At beginning of upkeep, Surveil 2: put Plains into graveyard
        addTarget(playerA, "Plains");

        setStopAt(1, PhaseStep.DRAW);
        execute();

        assertPermanentCount(playerA, "Aminatou, Veil Piercer", 1);
        assertGraveyardCount(playerA, "Plains", 1);
    }

    @Test
    public void test_SurveilIntoMiracleDrawStep() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Aminatou, Veil Piercer");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        // On Turn 3, PlayerA has upkeep then draw step.
        // Library (from top to bottom):
        // Forest, Forest (surveilled away on T1 upkeep)
        // Swamp (surveilled away on T3 upkeep)
        // Sphere of Safety ({4}{W} -> {W}, drawn on T3 draw step)
        addCard(Zone.LIBRARY, playerA, "Sphere of Safety");
        addCard(Zone.LIBRARY, playerA, "Swamp");
        addCard(Zone.LIBRARY, playerA, "Forest");
        addCard(Zone.LIBRARY, playerA, "Forest");
        skipInitShuffling();

        // Turn 1 upkeep surveil: mill the 2 Forests
        addTarget(playerA, "Forest^Forest");

        // Turn 3 upkeep surveil: mill the Swamp, leaving Sphere of Safety on top
        addTarget(playerA, "Swamp");

        // Turn 3 draw step: draws Sphere of Safety
        setChoice(playerA, "Yes"); // Reveal to use Miracle?
        setChoice(playerA, "Yes"); // Cast for miracle cost?

        setStopAt(3, PhaseStep.PRECOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "Sphere of Safety", 1);
        assertTappedCount("Plains", true, 1);
        assertHandCount(playerA, "Sphere of Safety", 0);
    }

    @Test
    public void test_MiracleEnchantmentWithDrawSpell() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Aminatou, Veil Piercer");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 1);
        addCard(Zone.HAND, playerA, "Reach Through Mists");
        // Sphere of Safety costs {4}{W} -> reduced by {4} is {W}
        addCard(Zone.LIBRARY, playerA, "Sphere of Safety");
        addCard(Zone.LIBRARY, playerA, "Forest");
        addCard(Zone.LIBRARY, playerA, "Forest");
        skipInitShuffling();

        // Turn 1 upkeep surveil: mill the 2 Forests
        addTarget(playerA, "Forest^Forest");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Reach Through Mists");
        setChoice(playerA, "Yes"); // Reveal to use Miracle?
        setChoice(playerA, "Yes"); // Cast for miracle cost?

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Sphere of Safety", 1);
        assertTappedCount("Plains", true, 1);
        assertTappedCount("Island", true, 1);
        assertHandCount(playerA, "Sphere of Safety", 0);
    }

    @Test
    public void test_MiracleEnchantmentCostLessThan4Generic() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Aminatou, Veil Piercer");
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 1);
        addCard(Zone.HAND, playerA, "Reach Through Mists");
        // Bitterblossom costs {1}{B} -> reduced by {4} generic mana is {B}
        addCard(Zone.LIBRARY, playerA, "Bitterblossom");
        addCard(Zone.LIBRARY, playerA, "Forest");
        addCard(Zone.LIBRARY, playerA, "Forest");
        skipInitShuffling();

        // Turn 1 upkeep surveil: mill the 2 Forests
        addTarget(playerA, "Forest^Forest");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Reach Through Mists");
        setChoice(playerA, "Yes"); // Reveal to use Miracle?
        setChoice(playerA, "Yes"); // Cast for miracle cost?

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Bitterblossom", 1);
        assertTappedCount("Swamp", true, 1);
        assertTappedCount("Island", true, 1);
        assertHandCount(playerA, "Bitterblossom", 0);
    }

    @Test
    public void test_NonEnchantmentDoesNotMiracle() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Aminatou, Veil Piercer");
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 1);
        addCard(Zone.HAND, playerA, "Reach Through Mists");
        addCard(Zone.LIBRARY, playerA, "Grizzly Bears");
        addCard(Zone.LIBRARY, playerA, "Swamp");
        addCard(Zone.LIBRARY, playerA, "Swamp");
        skipInitShuffling();

        // Turn 1 upkeep surveil: mill the 2 Swamps
        addTarget(playerA, "Swamp^Swamp");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Reach Through Mists");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Grizzly Bears", 0);
        assertHandCount(playerA, "Grizzly Bears", 1);
        assertTappedCount("Forest", true, 0);
        assertTappedCount("Island", true, 1);
    }

    @Test
    public void test_SecondCardDrawnDoesNotMiracle() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Aminatou, Veil Piercer");
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);
        addCard(Zone.HAND, playerA, "Reach Through Mists", 2);
        addCard(Zone.LIBRARY, playerA, "Bitterblossom");
        addCard(Zone.LIBRARY, playerA, "Plains");
        addCard(Zone.LIBRARY, playerA, "Mountain");
        addCard(Zone.LIBRARY, playerA, "Mountain");
        skipInitShuffling();

        // Turn 1 upkeep surveil: mill the 2 Mountains
        addTarget(playerA, "Mountain^Mountain");

        // First draw: cast Reach Through Mists to draw Plains (first card drawn this turn)
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Reach Through Mists");

        // Second draw: cast Reach Through Mists to draw Bitterblossom (second card drawn this turn)
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Reach Through Mists");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        // Bitterblossom was second card drawn, so miracle shouldn't trigger
        assertPermanentCount(playerA, "Bitterblossom", 0);
        assertHandCount(playerA, "Bitterblossom", 1);
        assertTappedCount("Swamp", true, 0);
        assertTappedCount("Island", true, 2);
    }

    @Test
    public void test_RoomCardMiracle() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Aminatou, Veil Piercer");
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 1);
        addCard(Zone.HAND, playerA, "Reach Through Mists");
        // Dollmaker's Shop {1}{W} // Porcelain Gallery {4}{W}{W}
        // Choose Porcelain Gallery: {4}{W}{W} - {4} = {W}{W}
        addCard(Zone.LIBRARY, playerA, "Dollmaker's Shop // Porcelain Gallery");
        addCard(Zone.LIBRARY, playerA, "Forest");
        addCard(Zone.LIBRARY, playerA, "Forest");
        skipInitShuffling();

        // Turn 1 upkeep surveil: mill the 2 Forests
        addTarget(playerA, "Forest^Forest");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Reach Through Mists");
        setChoice(playerA, "Yes"); // Reveal to use Miracle?
        setChoice(playerA, "Yes"); // Cast for miracle cost?
        setChoice(playerA, "Porcelain Gallery"); // choose half to cast

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Porcelain Gallery", 1);
        assertTappedCount("Plains", true, 2);
        assertTappedCount("Island", true, 1);
        assertHandCount(playerA, 0);
    }

    @Test
    public void test_MiracleVariableX() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Aminatou, Veil Piercer");
        addCard(Zone.BATTLEFIELD, playerA, "Island");
        addCard(Zone.BATTLEFIELD, playerA, "Forest", 5);
        addCard(Zone.HAND, playerA, "Reach Through Mists");
        addCard(Zone.LIBRARY, playerA, "Nyxborn Hydra");
        addCard(Zone.LIBRARY, playerA, "Swamp", 2);
        skipInitShuffling();

        addTarget(playerA, "Swamp^Swamp");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Reach Through Mists");
        setChoice(playerA, true);
        setChoice(playerA, true);
        setChoice(playerA, "X=4");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertCounterCount(playerA, "Nyxborn Hydra", CounterType.P1P1, 4);
        assertTappedCount("Forest", true, 1);
    }

    @Test
    public void test_MiracleVariableX_SphereOfResistanceTax() {
        setStrictChooseMode(true);
        addCard(Zone.BATTLEFIELD, playerA, "Aminatou, Veil Piercer");
        addCard(Zone.BATTLEFIELD, playerA, "Sphere of Resistance");
        addCard(Zone.BATTLEFIELD, playerA, "Tropical Island", 5);
        addCard(Zone.HAND, playerA, "Reach Through Mists");
        addCard(Zone.LIBRARY, playerA, "Nyxborn Hydra");
        addCard(Zone.LIBRARY, playerA, "Swamp", 2);
        skipInitShuffling();

        addTarget(playerA, "Swamp^Swamp");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Reach Through Mists");
        setChoice(playerA, true);
        setChoice(playerA, true);
        setChoice(playerA, "X=1");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertCounterCount(playerA, "Nyxborn Hydra", CounterType.P1P1, 1);
        assertTappedCount("Tropical Island", true, 4);
    }
}
