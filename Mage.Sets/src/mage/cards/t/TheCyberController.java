package mage.cards.t;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.BecomePermanentFacedownEffect;
import mage.abilities.effects.common.continuous.BoostControlledEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.constants.*;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.players.Player;
import mage.util.CardUtil;

import java.util.UUID;

/**
 * @author Susucr
 */
public final class TheCyberController extends CardImpl {

    public TheCyberController(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT, CardType.CREATURE}, "{X}{U}{U}{B}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.CYBERMAN);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // Other artifact creatures you control get +1/+1.
        this.addAbility(new SimpleStaticAbility(new BoostControlledEffect(
                1, 1, Duration.WhileOnBattlefield,
                StaticFilters.FILTER_PERMANENTS_ARTIFACT_CREATURE, true
        )));

        // When The Cyber-Controller enters the battlefield, each opponent mills X cards. Put all creature cards milled this way onto the battlefield face down under your control. They're 2/2 Cyberman artifact creatures.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new TheCyberControllerEffect()));
    }

    private TheCyberController(final TheCyberController card) {
        super(card);
    }

    @Override
    public TheCyberController copy() {
        return new TheCyberController(this);
    }
}

class TheCyberControllerEffect extends OneShotEffect {

    private static final BecomePermanentFacedownEffect.PermanentApplier applier = (permanent, game, source) -> {
        permanent.addCardType(game, CardType.ARTIFACT, CardType.CREATURE);
        permanent.addSubType(game, SubType.CYBERMAN);
        permanent.getPower().setModifiedBaseValue(2);
        permanent.getToughness().setModifiedBaseValue(2);
    };

    TheCyberControllerEffect() {
        super(Outcome.PutCreatureInPlay);
        staticText = "each opponent mills X cards. Put all creature cards milled this way " +
                "onto the battlefield face down under your control. They're 2/2 Cyberman artifact creatures";
    }

    private TheCyberControllerEffect(final TheCyberControllerEffect effect) {
        super(effect);
    }

    @Override
    public TheCyberControllerEffect copy() {
        return new TheCyberControllerEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }
        int xValue = CardUtil.getSourceCostsTag(game, source, "X", 0);
        if (xValue <= 0) {
            return true;
        }
        Cards cards = new CardsImpl();
        for (UUID opponentId : game.getOpponents(source.getControllerId())) {
            Player opponent = game.getPlayer(opponentId);
            if (opponent != null) {
                cards.addAll(opponent.millCards(xValue, source, game));
            }
        }
        Cards creatureCards = new CardsImpl();
        for (UUID cardId : cards) {
            Card card = game.getCard(cardId);
            if (card != null && card.isCreature(game)) {
                creatureCards.add(card);
            }
        }
        if (!creatureCards.isEmpty()) {
            game.addEffect(new BecomePermanentFacedownEffect(applier, creatureCards, game), source);
            controller.moveCards(
                    creatureCards.getCards(game), Zone.BATTLEFIELD, source, game, false,
                    true, false, null
            );
        }
        return true;
    }
}
