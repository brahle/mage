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
 * {@link mage.cards.t.TheCyberController The Cyber-Controller}
 * {X}{U}{U}{B}
 * Legendary Artifact Creature — Cyberman
 * 3/3
 * Other artifact creatures you control get +1/+1.
 * When The Cyber-Controller enters the battlefield, each opponent mills X cards. Put all creature cards milled this way onto the battlefield face down under your control. They're 2/2 Cyberman artifact creatures.
 *
 * @author Susucr
 */
public class TheCyberControllerTest extends CardTestPlayerBase {

    @Test
    public void testMillAndReturnCreaturesFaceDown() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 4);
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 2);
        addCard(Zone.HAND, playerA, "The Cyber-Controller");

        // Player B library: top 3 cards are Grizzly Bears, Lightning Bolt, Silvercoat Lion
        // Order on top: index 0 is top
        skipInitShuffling();
        removeAllCardsFromLibrary(playerB);
        addCard(Zone.LIBRARY, playerB, "Forest");
        addCard(Zone.LIBRARY, playerB, "Silvercoat Lion"); // creature
        addCard(Zone.LIBRARY, playerB, "Lightning Bolt");  // noncreature
        addCard(Zone.LIBRARY, playerB, "Grizzly Bears");   // creature (on top)

        // Cast with X=3 (total cost {3}{U}{U}{B} = 6 mana)
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "The Cyber-Controller");
        setChoice(playerA, "X=3");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "The Cyber-Controller", 1);
        assertPowerToughness(playerA, "The Cyber-Controller", 3, 3);

        // Player A should control 2 face-down Cybermen
        assertPermanentCount(playerA, EmptyNames.FACE_DOWN_CREATURE.getTestCommand(), 2);

        // Noncreature went to graveyard
        assertGraveyardCount(playerB, "Lightning Bolt", 1);
        assertGraveyardCount(playerB, "Grizzly Bears", 0);
        assertGraveyardCount(playerB, "Silvercoat Lion", 0);

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
            // 2/2 base + 1/+1 from The Cyber-Controller = 3/3
            Assert.assertEquals("Power should be 3 (boosted by +1/+1)", 3, cyberman.getPower().getValue());
            Assert.assertEquals("Toughness should be 3 (boosted by +1/+1)", 3, cyberman.getToughness().getValue());
        }
    }

    @Test
    public void testBoostOtherArtifactCreatures() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Swamp", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Bronze Sable"); // 2/1 artifact creature
        addCard(Zone.HAND, playerA, "The Cyber-Controller");

        // Cast with X=0
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "The Cyber-Controller");
        setChoice(playerA, "X=0");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "The Cyber-Controller", 1);
        assertPowerToughness(playerA, "The Cyber-Controller", 3, 3);
        // Bronze Sable was 2/1, now 3/2
        assertPowerToughness(playerA, "Bronze Sable", 3, 2);
    }
}
