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
 * {@link mage.cards.c.Cybership Cybership}
 * {6}
 * Artifact — Vehicle
 * Flying
 * Whenever Cybership deals combat damage to a player, put the top two cards of that player's library onto the battlefield face down under your control. They're 2/2 Cyberman artifact creatures.
 * Crew 4
 *
 * @author Susucr
 */
public class CybershipTest extends CardTestPlayerBase {

    @Test
    public void testCombatDamageTrigger() {
        addCard(Zone.BATTLEFIELD, playerA, "Cybership");
        addCard(Zone.BATTLEFIELD, playerA, "Pillarfield Ox"); // 2/4
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears"); // 2/2 -> total power 4 to crew

        // Crew Cybership
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Crew 4");
        setChoice(playerA, "Pillarfield Ox^Grizzly Bears");

        // Attack with Cybership
        attack(1, playerA, "Cybership", playerB);

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertLife(playerB, 20 - 8);

        // Player A should control 2 face-down Cybermen
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
            Assert.assertTrue("Must be colorless", cyberman.getColor(currentGame).isColorless());
            Assert.assertEquals("Power should be 2", 2, cyberman.getPower().getValue());
            Assert.assertEquals("Toughness should be 2", 2, cyberman.getToughness().getValue());
        }
    }
}
