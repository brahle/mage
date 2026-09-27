package org.mage.test.cards.single.who;

import mage.constants.CardType;
import mage.constants.EmptyNames;
import mage.constants.PhaseStep;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.game.permanent.Permanent;
import org.junit.Assert;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Susucr
 */
public class DeathInHeavenTest extends CardTestPlayerBase {

    /**
     * Death in Heaven {3}{B}
     * Enchantment — Saga
     * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
     * I, II — Target player mills two cards, then exiles their graveyard.
     * III — Put all creature cards exiled with Death in Heaven onto the battlefield face down under your control. They're 2/2 Cyberman artifact creatures.
     */
    private static final String deathInHeaven = "Death in Heaven";

    @Test
    public void test_DeathInHeaven_FullCycle() {
        // Player A has mana, Death in Heaven, and Lightning Bolt
        addCard(Zone.HAND, playerA, deathInHeaven);
        addCard(Zone.HAND, playerA, "Lightning Bolt");
        addCard(Zone.BATTLEFIELD, playerA, "Badlands", 6);

        // Player B starts with cards in graveyard and a creature on the battlefield
        addCard(Zone.GRAVEYARD, playerB, "Serra Angel"); // creature card in GY initially
        addCard(Zone.GRAVEYARD, playerB, "Dark Ritual"); // noncreature card in GY initially
        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");

        // Turn 1 (Player A): Cast Death in Heaven
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, deathInHeaven);
        addTarget(playerA, playerB); // Chapter I targets Player B

        // In Turn 1 postcombat main: Player A destroys Grizzly Bears so it enters Player B's graveyard
        castSpell(1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Lightning Bolt", "Grizzly Bears");

        // Turn 3 (Player A): Chapter II triggers
        addTarget(playerA, playerB); // Chapter II targets Player B (exiling Grizzly Bears)

        // Turn 5 (Player A): Chapter III triggers
        // Put all creature cards exiled with Death in Heaven onto the battlefield face down under your control.
        // They're 2/2 Cyberman artifact creatures.

        setStrictChooseMode(true);
        setStopAt(5, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        // Check noncreature cards remain in exile
        assertExileCount(playerB, "Dark Ritual", 1);

        // Check creatures are not in exile anymore
        assertExileCount(playerB, "Serra Angel", 0);
        assertExileCount(playerB, "Grizzly Bears", 0);

        // Death in Heaven should be sacrificed after Chapter III
        assertGraveyardCount(playerA, deathInHeaven, 1);

        // Player A should control 2 face-down Cyberman creatures
        assertPermanentCount(playerA, EmptyNames.FACE_DOWN_CREATURE.getTestCommand(), 2);

        List<Permanent> cybermen = currentGame.getBattlefield().getAllActivePermanents(playerA.getId())
                .stream()
                .filter(p -> p.isFaceDown(currentGame))
                .collect(Collectors.toList());

        Assert.assertEquals("Should have 2 face-down permanents", 2, cybermen.size());
        for (Permanent cyberman : cybermen) {
            Assert.assertTrue("Must be face down", cyberman.isFaceDown(currentGame));
            Assert.assertTrue("Must be artifact", cyberman.getCardType(currentGame).contains(CardType.ARTIFACT));
            Assert.assertTrue("Must be creature", cyberman.getCardType(currentGame).contains(CardType.CREATURE));
            Assert.assertTrue("Must have Cyberman subtype", cyberman.hasSubtype(SubType.CYBERMAN, currentGame));
            Assert.assertFalse("Must NOT have Angel subtype", cyberman.hasSubtype(SubType.ANGEL, currentGame));
            Assert.assertFalse("Must NOT have Bear subtype", cyberman.hasSubtype(SubType.BEAR, currentGame));
            Assert.assertTrue("Must be colorless", cyberman.getColor(currentGame).isColorless());
            Assert.assertEquals("Power should be 2", 2, cyberman.getPower().getValue());
            Assert.assertEquals("Toughness should be 2", 2, cyberman.getToughness().getValue());
            Assert.assertTrue("Should have no abilities (such as Flying)", cyberman.getAbilities(currentGame).isEmpty());
        }
    }

    @Test
    public void test_DeathInHeaven_NoCreaturesExiled() {
        addCard(Zone.HAND, playerA, deathInHeaven);
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 4);

        // Player B only has noncreature cards in graveyard
        addCard(Zone.GRAVEYARD, playerB, "Dark Ritual");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, deathInHeaven);
        addTarget(playerA, playerB); // Chapter I targets Player B

        addTarget(playerA, playerB); // Chapter II targets Player B

        setStrictChooseMode(true);
        setStopAt(5, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertGraveyardCount(playerA, deathInHeaven, 1);
        assertPermanentCount(playerA, EmptyNames.FACE_DOWN_CREATURE.getTestCommand(), 0);
        assertExileCount(playerB, "Dark Ritual", 1);
    }
}
