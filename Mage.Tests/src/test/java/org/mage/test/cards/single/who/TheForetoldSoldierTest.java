package org.mage.test.cards.single.who;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author Skiwkr, Susucr
 */
public class TheForetoldSoldierTest extends CardTestPlayerBase {

    /**
     * The Foretold Soldier {2}{G}{G}
     * Creature — Alien Zombie Soldier 6/6
     * The Foretold Soldier must be blocked if able.
     * The Foretold Soldier can't be blocked by more than one creature.
     * Whenever The Foretold Soldier deals damage, exile it face down. It becomes foretold.
     * Foretell {1}{G}
     */
    private static final String soldier = "The Foretold Soldier";

    @Test
    public void test_CombatDamageToPlayerExilesFaceDownAndCastLater() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Forest", 2);
        addCard(Zone.BATTLEFIELD, playerA, soldier);

        attack(1, playerA, soldier, playerB);

        // Turn 1 postcombat: cannot cast turn it was foretold
        checkPlayableAbility("Can't cast turn it was foretold", 1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Foretell {1}{G}", false);

        // Turn 3 precombat: cast it for foretell cost {1}{G}
        activateAbility(3, PhaseStep.PRECOMBAT_MAIN, playerA, "Foretell {1}{G}");

        setStopAt(3, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertLife(playerB, 20 - 6);
        assertPermanentCount(playerA, soldier, 1);
        assertExileCount(playerA, soldier, 0);
    }

    @Test
    public void test_CombatDamageToCreatureSurvivesExiles() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, soldier);
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");

        attack(1, playerA, soldier, playerB);
        block(1, playerB, "Grizzly Bears", soldier);

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertLife(playerB, 20);
        assertGraveyardCount(playerB, "Grizzly Bears", 1);
        assertExileCount(playerA, soldier, 1);
        assertPermanentCount(playerA, soldier, 0);
    }

    @Test
    public void test_CombatDamageLethalDoesNotExile() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, soldier);
        addCard(Zone.BATTLEFIELD, playerB, "Colossal Dreadmaw"); // 6/6

        attack(1, playerA, soldier, playerB);
        block(1, playerB, "Colossal Dreadmaw", soldier);

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertLife(playerB, 20);
        assertGraveyardCount(playerB, "Colossal Dreadmaw", 1);
        assertGraveyardCount(playerA, soldier, 1);
        assertExileCount(playerA, soldier, 0);
    }

    @Test
    public void test_MustBeBlockedAndCantBeBlockedByMoreThanOne() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, soldier);
        addCard(Zone.BATTLEFIELD, playerB, "Memnite");
        addCard(Zone.BATTLEFIELD, playerB, "Ornithopter");

        attack(1, playerA, soldier, playerB);
        block(1, playerB, "Memnite", soldier);

        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertGraveyardCount(playerB, "Memnite", 1);
        assertPermanentCount(playerB, "Ornithopter", 1);
        assertExileCount(playerA, soldier, 1);
    }

    @Test
    public void test_ForetellFromHand() {
        setStrictChooseMode(true);

        addCard(Zone.BATTLEFIELD, playerA, "Forest", 4);
        addCard(Zone.HAND, playerA, soldier);

        // Turn 1: pay {2} to foretell from hand
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Fore");

        // Turn 3: cast from exile for {1}{G}
        activateAbility(3, PhaseStep.PRECOMBAT_MAIN, playerA, "Foretell {1}{G}");

        setStopAt(3, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, soldier, 1);
        assertExileCount(playerA, soldier, 0);
    }
}
