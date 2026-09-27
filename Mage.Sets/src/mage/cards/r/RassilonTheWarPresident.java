package mage.cards.r;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.ConspireAbility;
import mage.abilities.triggers.BeginningOfUpkeepTriggeredAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.stack.Spell;
import mage.game.stack.StackObject;
import mage.players.Player;
import mage.util.CardUtil;

/**
 * @author Susucr
 */
public final class RassilonTheWarPresident extends CardImpl {

    public RassilonTheWarPresident(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{U}{B}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.TIME_LORD);
        this.subtype.add(SubType.NOBLE);
        this.power = new MageInt(3);
        this.toughness = new MageInt(4);

        // At the beginning of your upkeep, you lose 2 life and exile the top card of your library. You may play that card for as long as it remains exiled.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(new RassilonTheWarPresidentEffect()));

        // Each noncreature spell you cast from exile has conspire.
        this.addAbility(new SimpleStaticAbility(new RassilonTheWarPresidentConspireEffect()));
    }

    private RassilonTheWarPresident(final RassilonTheWarPresident card) {
        super(card);
    }

    @Override
    public RassilonTheWarPresident copy() {
        return new RassilonTheWarPresident(this);
    }
}

class RassilonTheWarPresidentEffect extends OneShotEffect {

    RassilonTheWarPresidentEffect() {
        super(Outcome.DrawCard);
        staticText = "you lose 2 life and exile the top card of your library. "
                + "You may play that card for as long as it remains exiled";
    }

    private RassilonTheWarPresidentEffect(final RassilonTheWarPresidentEffect effect) {
        super(effect);
    }

    @Override
    public RassilonTheWarPresidentEffect copy() {
        return new RassilonTheWarPresidentEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player == null) {
            return false;
        }
        player.loseLife(2, game, source, false);
        Card card = player.getLibrary().getFromTop(game);
        if (card == null) {
            return true;
        }
        if (player.moveCardsToExile(
                card, source, game, true,
                CardUtil.getExileZoneId(game, source), CardUtil.getSourceName(game, source)
        )) {
            CardUtil.makeCardPlayable(game, source, card, false, Duration.Custom, false, player.getId(), null);
        }
        return true;
    }
}

class RassilonTheWarPresidentConspireEffect extends ContinuousEffectImpl {

    private final ConspireAbility conspireAbility;

    public RassilonTheWarPresidentConspireEffect() {
        super(Duration.WhileOnBattlefield, Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, Outcome.AddAbility);
        staticText = "Each noncreature spell you cast from exile has conspire. <i>(As you cast that spell, you may tap two "
                + "untapped creatures you control that share a color with it. When you do, copy it and you may choose new targets for the copy.)</i>";
        this.conspireAbility = new ConspireAbility(ConspireAbility.ConspireTargets.MORE);
    }

    private RassilonTheWarPresidentConspireEffect(final RassilonTheWarPresidentConspireEffect effect) {
        super(effect);
        this.conspireAbility = effect.conspireAbility;
    }

    @Override
    public RassilonTheWarPresidentConspireEffect copy() {
        return new RassilonTheWarPresidentConspireEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        for (StackObject stackObject : game.getStack()) {
            if (!(stackObject instanceof Spell) || !((Spell) stackObject).wasCast()
                    || !stackObject.isControlledBy(source.getControllerId())) {
                continue;
            }
            Spell spell = (Spell) stackObject;
            if (spell.wasCastFrom(Zone.EXILED) && StaticFilters.FILTER_SPELL_NON_CREATURE.match(stackObject, game)) {
                game.getState().addOtherAbility(spell.getCard(), conspireAbility.setAddedById(source.getSourceId()));
            }
        }
        return true;
    }
}
