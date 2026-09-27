package mage.cards.m;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.DiesCreatureTriggeredAbility;
import mage.abilities.triggers.BeginningOfEndStepTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.FaceVillainousChoiceOpponentsEffect;
import mage.abilities.effects.common.RollPlanarDieEffect;
import mage.abilities.effects.common.continuous.BecomePermanentFacedownEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.choices.FaceVillainousChoice;
import mage.choices.VillainousChoice;
import mage.constants.*;
import mage.filter.StaticFilters;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.AnotherPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetSacrifice;

import java.util.UUID;

/**
 * @author Susucr
 */
public final class Missy extends CardImpl {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("another nonartifact creature");

    static {
        filter.add(AnotherPredicate.instance);
        filter.add(Predicates.not(CardType.ARTIFACT.getPredicate()));
    }

    private static final FaceVillainousChoice choice = new FaceVillainousChoice(
            Outcome.Sacrifice, new MissyFirstChoice(), new MissySecondChoice()
    );

    public Missy(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{U}{B}{R}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.TIME_LORD);
        this.subtype.add(SubType.ROGUE);
        this.power = new MageInt(4);
        this.toughness = new MageInt(5);

        // Whenever another nonartifact creature dies, return it to the battlefield under your control face down. It's a 2/2 Cyberman artifact creature.
        this.addAbility(new DiesCreatureTriggeredAbility(new MissyReturnEffect(), false, filter, true));

        // At the beginning of your end step, each opponent faces a villainous choice — They sacrifice an artifact, or chaos ensues.
        this.addAbility(new BeginningOfEndStepTriggeredAbility(new FaceVillainousChoiceOpponentsEffect(choice)));
    }

    private Missy(final Missy card) {
        super(card);
    }

    @Override
    public Missy copy() {
        return new Missy(this);
    }
}

class MissyReturnEffect extends OneShotEffect {

    private static final BecomePermanentFacedownEffect.PermanentApplier applier = (permanent, game, source) -> {
        permanent.addCardType(game, CardType.ARTIFACT, CardType.CREATURE);
        permanent.addSubType(game, SubType.CYBERMAN);
        permanent.getPower().setModifiedBaseValue(2);
        permanent.getToughness().setModifiedBaseValue(2);
    };

    MissyReturnEffect() {
        super(Outcome.PutCreatureInPlay);
        staticText = "return it to the battlefield under your control face down. It's a 2/2 Cyberman artifact creature";
    }

    private MissyReturnEffect(final MissyReturnEffect effect) {
        super(effect);
    }

    @Override
    public MissyReturnEffect copy() {
        return new MissyReturnEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        Card card = game.getCard(getTargetPointer().getFirst(game, source));
        if (player == null || card == null || game.getState().getZone(card.getId()) != Zone.GRAVEYARD) {
            return false;
        }
        game.addEffect(new BecomePermanentFacedownEffect(applier, card, game), source);
        player.moveCards(
                card, Zone.BATTLEFIELD, source, game,
                false, true, false, null
        );
        return true;
    }
}

class MissyFirstChoice extends VillainousChoice {

    MissyFirstChoice() {
        super("They sacrifice an artifact", "Sacrifice an artifact");
    }

    @Override
    public boolean doChoice(Player player, Game game, Ability source) {
        TargetSacrifice target = new TargetSacrifice(StaticFilters.FILTER_CONTROLLED_PERMANENT_ARTIFACT);
        if (!target.canChoose(player.getId(), source, game)) {
            return false;
        }
        player.choose(Outcome.Sacrifice, target, source, game);
        Permanent permanent = game.getPermanent(target.getFirstTarget());
        return permanent != null && permanent.sacrifice(source, game);
    }
}

class MissySecondChoice extends VillainousChoice {

    MissySecondChoice() {
        super("chaos ensues", "Chaos ensues");
    }

    @Override
    public boolean doChoice(Player player, Game game, Ability source) {
        return RollPlanarDieEffect.chaosEnsues(game, source);
    }
}
