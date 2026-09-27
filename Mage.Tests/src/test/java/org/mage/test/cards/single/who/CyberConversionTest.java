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
 * {@link mage.cards.c.CyberConversion Cyber Conversion}
 * {U}{U}
 * Instant
 * Turn target creature face down. It's a 2/2 Cyberman artifact creature.
 *
 * @author Susucr
 */
public class CyberConversionTest extends CardTestPlayerBase {

    @Test
    public void testTurnCreatureFaceDown() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);
        addCard(Zone.HAND, playerA, "Cyber Conversion");

        addCard(Zone.BATTLEFIELD, playerB, "Serra Angel"); // 4/4 Flying, Vigilance

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Cyber Conversion", "Serra Angel");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerB, EmptyNames.FACE_DOWN_CREATURE.getTestCommand(), 1);
        assertPermanentCount(playerB, "Serra Angel", 0);

        List<Permanent> cybermen = currentGame.getBattlefield().getAllActivePermanents(playerB.getId())
                .stream()
                .filter(p -> p.isFaceDown(currentGame))
                .collect(Collectors.toList());

        Assert.assertEquals("Should have 1 face-down permanent", 1, cybermen.size());
        Permanent cyberman = cybermen.get(0);
        Assert.assertTrue("Must be face down", cyberman.isFaceDown(currentGame));
        Assert.assertTrue("Must be artifact", cyberman.getCardType(currentGame).contains(CardType.ARTIFACT));
        Assert.assertTrue("Must be creature", cyberman.getCardType(currentGame).contains(CardType.CREATURE));
        Assert.assertTrue("Must have Cyberman subtype", cyberman.hasSubtype(SubType.CYBERMAN, currentGame));
        Assert.assertFalse("Must NOT have Angel subtype", cyberman.hasSubtype(SubType.ANGEL, currentGame));
        Assert.assertTrue("Must be colorless", cyberman.getColor(currentGame).isColorless());
        Assert.assertEquals("Power should be 2", 2, cyberman.getPower().getValue());
        Assert.assertEquals("Toughness should be 2", 2, cyberman.getToughness().getValue());
        Assert.assertTrue("Should have no abilities (no flying/vigilance)", cyberman.getAbilities(currentGame).isEmpty());
    }

    @Test
    public void testTurnTokenFaceDown() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);
        addCard(Zone.HAND, playerA, "Cyber Conversion");

        addCard(Zone.BATTLEFIELD, playerB, "Mountain", 2);
        addCard(Zone.HAND, playerB, "Dragon Fodder"); // Creates two 1/1 red Goblin tokens

        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerB, "Dragon Fodder");
        waitStackResolved(2, PhaseStep.PRECOMBAT_MAIN);

        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerA, "Cyber Conversion", "Goblin Token");

        setStrictChooseMode(true);
        setStopAt(2, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerB, EmptyNames.FACE_DOWN_CREATURE.getTestCommand(), 1);
        assertPermanentCount(playerB, "Goblin Token", 1);

        Permanent cyberman = currentGame.getBattlefield().getAllActivePermanents(playerB.getId())
                .stream()
                .filter(p -> p.isFaceDown(currentGame))
                .findFirst()
                .orElse(null);

        Assert.assertNotNull("Must find face-down permanent", cyberman);
        Assert.assertTrue("Must be artifact", cyberman.getCardType(currentGame).contains(CardType.ARTIFACT));
        Assert.assertTrue("Must be creature", cyberman.getCardType(currentGame).contains(CardType.CREATURE));
        Assert.assertTrue("Must have Cyberman subtype", cyberman.hasSubtype(SubType.CYBERMAN, currentGame));
        Assert.assertFalse("Must NOT have Goblin subtype", cyberman.hasSubtype(SubType.GOBLIN, currentGame));
        Assert.assertEquals("Power should be 2", 2, cyberman.getPower().getValue());
        Assert.assertEquals("Toughness should be 2", 2, cyberman.getToughness().getValue());
    }

    @Test
    public void testTurnFaceUpWithBreakOpen() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 2);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 2);
        addCard(Zone.HAND, playerA, "Cyber Conversion");
        addCard(Zone.HAND, playerA, "Break Open"); // {1}{R} Turn target face-down creature face up.

        addCard(Zone.BATTLEFIELD, playerB, "Serra Angel");

        // Turn it face down
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Cyber Conversion", "Serra Angel");
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);

        // Turn it face up
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Break Open", EmptyNames.FACE_DOWN_CREATURE.getTestCommand());

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerB, EmptyNames.FACE_DOWN_CREATURE.getTestCommand(), 0);
        assertPermanentCount(playerB, "Serra Angel", 1);
        assertPowerToughness(playerB, "Serra Angel", 4, 4);
    }
}
