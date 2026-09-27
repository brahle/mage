package mage.cards.t;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.DealsDamageSourceTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.combat.CantBeBlockedByMoreThanOneSourceEffect;
import mage.abilities.effects.common.combat.MustBeBlockedByAtLeastOneSourceEffect;
import mage.abilities.keyword.ForetellAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.game.Game;
import mage.game.permanent.Permanent;

/**
 * @author Skiwkr, Susucr
 */
public final class TheForetoldSoldier extends CardImpl {

    public TheForetoldSoldier(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{G}{G}");

        this.subtype.add(SubType.ALIEN);
        this.subtype.add(SubType.ZOMBIE);
        this.subtype.add(SubType.SOLDIER);
        this.power = new MageInt(6);
        this.toughness = new MageInt(6);

        // The Foretold Soldier must be blocked if able.
        this.addAbility(new SimpleStaticAbility(new MustBeBlockedByAtLeastOneSourceEffect(Duration.WhileOnBattlefield)));

        // The Foretold Soldier can't be blocked by more than one creature.
        this.addAbility(new SimpleStaticAbility(new CantBeBlockedByMoreThanOneSourceEffect()));

        // Whenever The Foretold Soldier deals damage, exile it face down. It becomes foretold.
        this.addAbility(new DealsDamageSourceTriggeredAbility(new TheForetoldSoldierEffect()));

        // Foretell {1}{G}
        this.addAbility(new ForetellAbility(this, "{1}{G}"));
    }

    private TheForetoldSoldier(final TheForetoldSoldier card) {
        super(card);
    }

    @Override
    public TheForetoldSoldier copy() {
        return new TheForetoldSoldier(this);
    }
}

class TheForetoldSoldierEffect extends OneShotEffect {

    TheForetoldSoldierEffect() {
        super(Outcome.Benefit);
        staticText = "exile it face down. It becomes foretold";
    }

    private TheForetoldSoldierEffect(final TheForetoldSoldierEffect effect) {
        super(effect);
    }

    @Override
    public TheForetoldSoldierEffect copy() {
        return new TheForetoldSoldierEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = source.getSourcePermanentIfItStillExists(game);
        if (permanent == null) {
            return false;
        }
        return ForetellAbility.doExileBecomesForetold(permanent, game, source, "{1}{G}");
    }
}
