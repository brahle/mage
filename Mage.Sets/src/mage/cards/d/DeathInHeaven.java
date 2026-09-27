package mage.cards.d;

import mage.abilities.Ability;
import mage.abilities.common.SagaAbility;
import mage.abilities.effects.Effects;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ExileGraveyardAllTargetPlayerEffect;
import mage.abilities.effects.common.MillCardsTargetEffect;
import mage.abilities.effects.common.continuous.BecomePermanentFacedownEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.constants.*;
import mage.game.ExileZone;
import mage.game.Game;
import mage.players.Player;
import mage.target.TargetPlayer;
import mage.util.CardUtil;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class DeathInHeaven extends CardImpl {

    public DeathInHeaven(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{3}{B}");

        this.subtype.add(SubType.SAGA);

        // (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
        SagaAbility sagaAbility = new SagaAbility(this);

        // I,II - Target player mills two cards, then exiles their graveyard.
        sagaAbility.addChapterEffect(
                this, SagaChapter.CHAPTER_I, SagaChapter.CHAPTER_II,
                new Effects(
                        new MillCardsTargetEffect(2),
                        new ExileGraveyardAllTargetPlayerEffect(true)
                                .setText(", then exiles their graveyard")
                ), new TargetPlayer()
        );

        // III - Put all creature cards exiled with Death in Heaven onto the battlefield face down under your control. They're 2/2 Cyberman artifact creatures.
        sagaAbility.addChapterEffect(this, SagaChapter.CHAPTER_III, new DeathInHeavenEffect());

        this.addAbility(sagaAbility);
    }

    private DeathInHeaven(final DeathInHeaven card) {
        super(card);
    }

    @Override
    public DeathInHeaven copy() {
        return new DeathInHeaven(this);
    }
}

class DeathInHeavenEffect extends OneShotEffect {

    private static final BecomePermanentFacedownEffect.PermanentApplier applier
            = (permanent, game, source) -> {
        permanent.addCardType(game, CardType.ARTIFACT, CardType.CREATURE);
        permanent.addSubType(game, SubType.CYBERMAN);
        permanent.getPower().setModifiedBaseValue(2);
        permanent.getToughness().setModifiedBaseValue(2);
    };

    DeathInHeavenEffect() {
        super(Outcome.Benefit);
        staticText = "put all creature cards exiled with {this} onto the battlefield " +
                "face down under your control. They're 2/2 Cyberman artifact creatures";
    }

    private DeathInHeavenEffect(final DeathInHeavenEffect effect) {
        super(effect);
    }

    @Override
    public DeathInHeavenEffect copy() {
        return new DeathInHeavenEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player == null) {
            return false;
        }
        ExileZone exileZone = game.getExile().getExileZone(CardUtil.getExileZoneId(game, source));
        if (exileZone == null || exileZone.isEmpty()) {
            return false;
        }
        Cards cards = new CardsImpl();
        for (UUID cardId : exileZone) {
            Card card = game.getCard(cardId);
            if (card != null && card.isCreature(game)) {
                cards.add(card);
            }
        }
        if (cards.isEmpty()) {
            return false;
        }
        game.addEffect(new BecomePermanentFacedownEffect(applier, cards, game), source);
        player.moveCards(
                cards.getCards(game), Zone.BATTLEFIELD, source, game, false,
                true, false, null
        );
        return true;
    }
}
