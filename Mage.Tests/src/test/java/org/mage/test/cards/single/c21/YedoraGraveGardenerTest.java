package org.mage.test.cards.single.c21;

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
public class YedoraGraveGardenerTest extends CardTestPlayerBase {

    /**
     * Yedora, Grave Gardener {4}{G}
     * Legendary Creature — Treefolk Druid 5/5
     * Whenever another nontoken creature you control dies, you may return it to the battlefield face down under its owner's control. It's a Forest land. (It has no other types or abilities.)
     */
    private static final String yedora = "Yedora, Grave Gardener";

    @Test
    public void test_YedoraReturnForest() {
        addCard(Zone.BATTLEFIELD, playerA, yedora);
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears");
        addCard(Zone.HAND, playerA, "Lightning Bolt");
        addCard(Zone.BATTLEFIELD, playerA, "Mountain");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", "Grizzly Bears");
        setChoice(playerA, true); // Use Yedora trigger

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, EmptyNames.FACE_DOWN_CREATURE.getTestCommand(), 1);

        List<Permanent> forests = currentGame.getBattlefield().getAllActivePermanents(playerA.getId())
                .stream()
                .filter(p -> p.isFaceDown(currentGame))
                .collect(Collectors.toList());

        Assert.assertEquals("Should have 1 face-down permanent", 1, forests.size());
        Permanent forest = forests.get(0);
        Assert.assertTrue("Must be face down", forest.isFaceDown(currentGame));
        Assert.assertTrue("Must be land", forest.isLand(currentGame));
        Assert.assertFalse("Must NOT be creature", forest.isCreature(currentGame));
        Assert.assertTrue("Must have Forest subtype", forest.hasSubtype(SubType.FOREST, currentGame));
        Assert.assertFalse("Must NOT have Bear subtype", forest.hasSubtype(SubType.BEAR, currentGame));
        Assert.assertTrue("Has green mana ability", forest.getAbilities(currentGame).stream().anyMatch(a -> a instanceof mage.abilities.mana.GreenManaAbility));
    }
}
