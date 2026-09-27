package mage.cards.a;

import mage.ApprovingObject;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.triggers.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.costs.CostAdjuster;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.costs.mana.ManaCost;
import mage.abilities.costs.mana.ManaCosts;
import mage.abilities.costs.mana.VariableManaCost;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.keyword.SurveilEffect;
import mage.abilities.keyword.MiracleAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.cards.SplitCard;
import mage.choices.Choice;
import mage.choices.ChoiceImpl;
import mage.constants.*;
import mage.filter.common.FilterEnchantmentCard;
import mage.game.Game;
import mage.players.Player;
import mage.util.CardUtil;
import mage.watchers.common.MiracleWatcher;

import java.util.UUID;

/**
 * @author Susucr
 */
public final class AminatouVeilPiercer extends CardImpl {

    public AminatouVeilPiercer(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{W}{U}{B}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.WIZARD);
        this.power = new MageInt(2);
        this.toughness = new MageInt(4);

        // At the beginning of your upkeep, surveil 2.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(new SurveilEffect(2)));

        // Each enchantment card in your hand has miracle. Its miracle cost is equal to its mana cost reduced by {4}.
        Ability ability = new SimpleStaticAbility(Zone.BATTLEFIELD, new AminatouVeilPiercerEffect());
        ability.addWatcher(new MiracleWatcher());
        this.addAbility(ability);
    }

    private AminatouVeilPiercer(final AminatouVeilPiercer card) {
        super(card);
    }

    @Override
    public AminatouVeilPiercer copy() {
        return new AminatouVeilPiercer(this);
    }
}

class AminatouVeilPiercerEffect extends ContinuousEffectImpl {

    private static final FilterEnchantmentCard filter = new FilterEnchantmentCard("enchantment card");

    public AminatouVeilPiercerEffect() {
        super(Duration.WhileOnBattlefield, Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, Outcome.AddAbility);
        this.staticText = "each enchantment card in your hand has miracle. " +
                "Its miracle cost is equal to its mana cost reduced by {4}";
    }

    protected AminatouVeilPiercerEffect(final AminatouVeilPiercerEffect effect) {
        super(effect);
    }

    @Override
    public AminatouVeilPiercerEffect copy() {
        return new AminatouVeilPiercerEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player == null) {
            return false;
        }
        for (Card card : player.getHand().getCards(filter, game)) {
            game.getState().addOtherAbility(card, new AminatouMiracleAbility());
        }
        return true;
    }
}

class AminatouMiracleAbility extends MiracleAbility {

    public AminatouMiracleAbility() {
        super(new AminatouMiracleEffect(), "Miracle—Its miracle cost is equal to its mana cost reduced by {4}.");
    }

    protected AminatouMiracleAbility(final AminatouMiracleAbility ability) {
        super(ability);
    }

    @Override
    public AminatouMiracleAbility copy() {
        return new AminatouMiracleAbility(this);
    }
}

class AminatouMiracleEffect extends OneShotEffect {

    public AminatouMiracleEffect() {
        super(Outcome.Benefit);
        this.staticText = "cast this card for its miracle cost";
    }

    protected AminatouMiracleEffect(final AminatouMiracleEffect effect) {
        super(effect);
    }

    @Override
    public AminatouMiracleEffect copy() {
        return new AminatouMiracleEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        Card card = game.getCard(getTargetPointer().getFirst(game, source));
        if (controller == null || card == null || !Zone.HAND.equals(game.getState().getZone(card.getId()))) {
            return false;
        }

        Card cardToCast = card;
        if (card instanceof SplitCard) {
            SplitCard splitCard = (SplitCard) card;
            Choice choice = new ChoiceImpl(true);
            choice.setMessage("Choose which half to cast with miracle");
            choice.getChoices().add(splitCard.getLeftHalfCard().getName());
            choice.getChoices().add(splitCard.getRightHalfCard().getName());
            if (controller.choose(Outcome.Benefit, choice, game)) {
                if (splitCard.getRightHalfCard().getName().equals(choice.getChoice())) {
                    cardToCast = splitCard.getRightHalfCard();
                } else {
                    cardToCast = splitCard.getLeftHalfCard();
                }
            }
        }

        SpellAbility abilityToCast = cardToCast.getSpellAbility().copy();
        if (cardToCast.getManaCost().stream().anyMatch(VariableManaCost.class::isInstance)) {
            CostAdjuster existingAdjuster = abilityToCast.getCostAdjuster();
            abilityToCast.setCostAdjuster(new CostAdjuster() {
                @Override
                public void prepareX(Ability ability, Game game) {
                    if (existingAdjuster != null) {
                        existingAdjuster.prepareX(ability, game);
                    }
                }

                @Override
                public void prepareCost(Ability ability, Game game) {
                    if (existingAdjuster != null) {
                        existingAdjuster.prepareCost(ability, game);
                    }
                    CardUtil.reduceCost(ability, 4);
                }

                @Override
                public void increaseCost(Ability ability, Game game) {
                    if (existingAdjuster != null) {
                        existingAdjuster.increaseCost(ability, game);
                    }
                }

                @Override
                public void reduceCost(Ability ability, Game game) {
                    if (existingAdjuster != null) {
                        existingAdjuster.reduceCost(ability, game);
                    }
                }
            });
        } else {
            ManaCosts<ManaCost> reducedCost = CardUtil.reduceCost(cardToCast.getManaCost().copy(), 4);
            if (reducedCost.isEmpty()) {
                reducedCost.add(new GenericManaCost(0));
            }
            ManaCosts<ManaCost> costRef = abilityToCast.getManaCostsToPay();
            costRef.clear();
            costRef.add(reducedCost);
        }
        controller.cast(abilityToCast, game, false, new ApprovingObject(source, game));
        return true;
    }
}
