package mage.cards.c;

import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.BecomePermanentFacedownEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.common.TargetCreaturePermanent;

import java.util.UUID;

/**
 * @author Susucr
 */
public final class CyberConversion extends CardImpl {

    public CyberConversion(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{U}{U}");

        // Turn target creature face down. It's a 2/2 Cyberman artifact creature.
        this.getSpellAbility().addEffect(new CyberConversionEffect());
        this.getSpellAbility().addTarget(new TargetCreaturePermanent());
    }

    private CyberConversion(final CyberConversion card) {
        super(card);
    }

    @Override
    public CyberConversion copy() {
        return new CyberConversion(this);
    }
}

class CyberConversionEffect extends OneShotEffect {

    private static final BecomePermanentFacedownEffect.PermanentApplier applier = (permanent, game, source) -> {
        permanent.addCardType(game, CardType.ARTIFACT, CardType.CREATURE);
        permanent.addSubType(game, SubType.CYBERMAN);
        permanent.getPower().setModifiedBaseValue(2);
        permanent.getToughness().setModifiedBaseValue(2);
    };

    CyberConversionEffect() {
        super(Outcome.Detriment);
        staticText = "turn target creature face down. It's a 2/2 Cyberman artifact creature";
    }

    private CyberConversionEffect(final CyberConversionEffect effect) {
        super(effect);
    }

    @Override
    public CyberConversionEffect copy() {
        return new CyberConversionEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanent(getTargetPointer().getFirst(game, source));
        if (permanent == null) {
            return false;
        }
        permanent.turnFaceDown(source, game, source.getControllerId());
        game.addEffect(new BecomePermanentFacedownEffect(applier, permanent, game), source);
        return true;
    }
}
