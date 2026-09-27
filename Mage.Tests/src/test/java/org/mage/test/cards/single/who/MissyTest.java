package org.mage.test.cards.single.who;

import mage.constants.CardType;
import mage.constants.EmptyNames;
import mage.constants.PhaseStep;
import mage.constants.Planes;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.game.permanent.Permanent;
import org.junit.Assert;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link mage.cards.m.Missy Missy}
 * {3}{U}{B}{R}
 * Legendary Creature — Time Lord Rogue
 * 4/5
 * Whenever another nonartifact creature dies, return it to the battlefield under your control face down. It's a 2/2 Cyberman artifact creature.
 * At the beginning of your end step, each opponent faces a villainous choice — They sacrifice an artifact, or chaos ensues.
 *
 * @author Susucr
 */
public class MissyTest extends CardTestPlayerBase {

    @Test
    public void testNonartifactCreatureDiesReturnsFaceDown() {
        addCard(Zone.BATTLEFIELD, playerA, "Missy");
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        addCard(Zone.HAND, playerA, "Lightning Bolt");

        addCard(Zone.BATTLEFIELD, playerB, "Grizzly Bears");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", "Grizzly Bears");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerB, "Grizzly Bears", 0);
        assertPermanentCount(playerA, EmptyNames.FACE_DOWN_CREATURE.getTestCommand(), 1);

        List<Permanent> cybermen = currentGame.getBattlefield().getAllActivePermanents(playerA.getId())
                .stream()
                .filter(p -> p.isFaceDown(currentGame))
                .collect(Collectors.toList());

        Assert.assertEquals("Should have 1 face-down permanent", 1, cybermen.size());
        Permanent cyberman = cybermen.get(0);
        Assert.assertTrue("Must be face down", cyberman.isFaceDown(currentGame));
        Assert.assertTrue("Must be artifact", cyberman.getCardType(currentGame).contains(CardType.ARTIFACT));
        Assert.assertTrue("Must be creature", cyberman.getCardType(currentGame).contains(CardType.CREATURE));
        Assert.assertTrue("Must have Cyberman subtype", cyberman.hasSubtype(SubType.CYBERMAN, currentGame));
        Assert.assertFalse("Must NOT have Bear subtype", cyberman.hasSubtype(SubType.BEAR, currentGame));
        Assert.assertTrue("Must be colorless", cyberman.getColor(currentGame).isColorless());
        Assert.assertEquals("Power should be 2", 2, cyberman.getPower().getValue());
        Assert.assertEquals("Toughness should be 2", 2, cyberman.getToughness().getValue());
    }

    @Test
    public void testArtifactCreatureDiesDoesNotTrigger() {
        addCard(Zone.BATTLEFIELD, playerA, "Missy");
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        addCard(Zone.HAND, playerA, "Lightning Bolt");

        addCard(Zone.BATTLEFIELD, playerB, "Bronze Sable"); // 2/1 Artifact Creature

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", "Bronze Sable");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, EmptyNames.FACE_DOWN_CREATURE.getTestCommand(), 0);
        assertGraveyardCount(playerB, "Bronze Sable", 1);
    }

    @Test
    public void testVillainousChoiceSacrificeArtifact() {
        addCard(Zone.BATTLEFIELD, playerA, "Missy");
        addCard(Zone.BATTLEFIELD, playerB, "Sol Ring");

        setStrictChooseMode(true);

        // At end step, Player B faces villainous choice: choose "They sacrifice an artifact" (first choice = true)
        setChoice(playerB, true);
        setChoice(playerB, "Sol Ring");

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertPermanentCount(playerB, "Sol Ring", 0);
        assertGraveyardCount(playerB, "Sol Ring", 1);
    }

    @Test
    public void testVillainousChoiceChaosEnsues() {
        addCard(Zone.BATTLEFIELD, playerA, "Missy");
        addCard(Zone.BATTLEFIELD, playerB, "Sol Ring");

        setStrictChooseMode(true);

        // At end step, Player B faces villainous choice: choose "chaos ensues" (second choice = false)
        setChoice(playerB, false);

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertPermanentCount(playerB, "Sol Ring", 1);
    }

    @Test
    public void testVillainousChoiceChaosEnsuesWithPlane() {
        addPlane(playerA, Planes.PLANE_HEDRON_FIELDS_OF_AGADEEM);
        addCard(Zone.BATTLEFIELD, playerA, "Missy");
        setChoice(playerB, false);

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertPermanentCount(playerA, "Eldrazi Token", 1);
    }
}
