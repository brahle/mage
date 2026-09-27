package mage.cards.t;

import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.common.ActivateAsSorceryActivatedAbility;
import mage.abilities.common.SkipUntapOptionalAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.ContinuousRuleModifyingEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.UntapTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.common.FilterNonlandPermanent;
import mage.filter.predicate.mageobject.AnotherPredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.ZoneChangeEvent;
import mage.game.permanent.Permanent;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author Susucr
 */
public final class ThePandorica extends CardImpl {

    private static final FilterNonlandPermanent filter = new FilterNonlandPermanent("another target nonland permanent");

    static {
        filter.add(AnotherPredicate.instance);
    }

    public ThePandorica(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{2}{W}");

        this.supertype.add(SuperType.LEGENDARY);

        // You may choose not to untap The Pandorica during your untap step.
        this.addAbility(new SkipUntapOptionalAbility());

        // {1}{W}, {T}: Untap another target nonland permanent, then it phases out. It can't phase in for as long as The Pandorica remains tapped. When The Pandorica becomes untapped or leaves the battlefield, that permanent phases in. Activate only as a sorcery.
        Ability ability = new ActivateAsSorceryActivatedAbility(
                new UntapTargetEffect().setText("untap another target nonland permanent, then it phases out"),
                new ManaCostsImpl<>("{1}{W}")
        );
        ability.addCost(new TapSourceCost());
        ability.addEffect(new ThePandoricaPhaseOutEffect());
        ability.addTarget(new TargetPermanent(filter));
        this.addAbility(ability);
    }

    private ThePandorica(final ThePandorica card) {
        super(card);
    }

    @Override
    public ThePandorica copy() {
        return new ThePandorica(this);
    }
}

class ThePandoricaPhaseOutEffect extends OneShotEffect {

    ThePandoricaPhaseOutEffect() {
        super(Outcome.Benefit);
        staticText = "it can't phase in for as long as {this} remains tapped. When {this} becomes untapped or leaves the battlefield, that permanent phases in";
    }

    private ThePandoricaPhaseOutEffect(final ThePandoricaPhaseOutEffect effect) {
        super(effect);
    }

    @Override
    public ThePandoricaPhaseOutEffect copy() {
        return new ThePandoricaPhaseOutEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent sourcePermanent = source.getSourcePermanentIfItStillExists(game);
        Permanent targetPermanent = game.getPermanent(source.getFirstTarget());
        if (targetPermanent == null) {
            return false;
        }

        // If The Pandorica leaves the battlefield before its activated ability resolves,
        // target nonland permanent untaps, but it won't phase out and the delayed triggered ability won't be created.
        if (sourcePermanent == null) {
            return true;
        }

        targetPermanent.phaseOut(game);
        MageObjectReference mor = new MageObjectReference(targetPermanent, game);
        game.addEffect(new ThePandoricaPhasePreventEffect(mor), source);
        game.addDelayedTriggeredAbility(new ThePandoricaDelayedTriggeredAbility(mor), source);
        return true;
    }
}

class ThePandoricaPhasePreventEffect extends ContinuousRuleModifyingEffectImpl {

    private final MageObjectReference mor;

    ThePandoricaPhasePreventEffect(MageObjectReference mor) {
        super(Duration.WhileOnBattlefield, Outcome.Neutral);
        this.mor = mor;
    }

    private ThePandoricaPhasePreventEffect(final ThePandoricaPhasePreventEffect effect) {
        super(effect);
        this.mor = effect.mor;
    }

    @Override
    public ThePandoricaPhasePreventEffect copy() {
        return new ThePandoricaPhasePreventEffect(this);
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.PHASE_IN;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        Permanent sourcePermanent = source.getSourcePermanentIfItStillExists(game);
        return sourcePermanent != null
                && sourcePermanent.isTapped()
                && this.mor.refersTo(event.getTargetId(), game);
    }
}

class ThePandoricaDelayedTriggeredAbility extends DelayedTriggeredAbility {

    ThePandoricaDelayedTriggeredAbility(MageObjectReference mor) {
        super(new ThePandoricaPhaseInEffect(mor), Duration.EndOfGame, true, false);
    }

    private ThePandoricaDelayedTriggeredAbility(final ThePandoricaDelayedTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public ThePandoricaDelayedTriggeredAbility copy() {
        return new ThePandoricaDelayedTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.UNTAPPED
                || event.getType() == GameEvent.EventType.ZONE_CHANGE;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (!event.getTargetId().equals(this.getSourceId())) {
            return false;
        }
        if (event.getType() == GameEvent.EventType.UNTAPPED) {
            return true;
        }
        if (event.getType() == GameEvent.EventType.ZONE_CHANGE) {
            return ((ZoneChangeEvent) event).getFromZone() == Zone.BATTLEFIELD;
        }
        return false;
    }

    @Override
    public String getRule() {
        return "When {this} becomes untapped or leaves the battlefield, that permanent phases in.";
    }
}

class ThePandoricaPhaseInEffect extends OneShotEffect {

    private final MageObjectReference mor;

    ThePandoricaPhaseInEffect(MageObjectReference mor) {
        super(Outcome.Benefit);
        this.mor = mor;
        staticText = "that permanent phases in";
    }

    private ThePandoricaPhaseInEffect(final ThePandoricaPhaseInEffect effect) {
        super(effect);
        this.mor = effect.mor;
    }

    @Override
    public ThePandoricaPhaseInEffect copy() {
        return new ThePandoricaPhaseInEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = mor.getPermanent(game);
        if (permanent != null) {
            permanent.phaseIn(game);
        }
        return true;
    }
}
