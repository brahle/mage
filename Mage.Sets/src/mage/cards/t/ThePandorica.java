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
        Permanent targetPermanent = game.getPermanent(source.getFirstTarget());
        if (targetPermanent == null) {
            return false;
        }

        targetPermanent.phaseOut(game);

        Permanent sourcePermanent = source.getSourcePermanentIfItStillExists(game);
        if (sourcePermanent == null || !sourcePermanent.isTapped()) {
            return true;
        }

        String key = "PandoricaExpired_" + source.getSourceId() + "_" + UUID.randomUUID();
        MageObjectReference mor = new MageObjectReference(targetPermanent, game);
        game.addEffect(new ThePandoricaPhasePreventEffect(mor, key), source);
        game.addDelayedTriggeredAbility(new ThePandoricaDelayedTriggeredAbility(mor, key), source);
        return true;
    }
}

class ThePandoricaPhasePreventEffect extends ContinuousRuleModifyingEffectImpl {

    private final MageObjectReference mor;
    private final String key;

    ThePandoricaPhasePreventEffect(MageObjectReference mor, String key) {
        super(Duration.Custom, Outcome.Neutral);
        this.mor = mor;
        this.key = key;
    }

    private ThePandoricaPhasePreventEffect(final ThePandoricaPhasePreventEffect effect) {
        super(effect);
        this.mor = effect.mor;
        this.key = effect.key;
    }

    @Override
    public ThePandoricaPhasePreventEffect copy() {
        return new ThePandoricaPhasePreventEffect(this);
    }

    @Override
    public boolean isInactive(Ability source, Game game) {
        if (Boolean.TRUE.equals(game.getState().getValue(key))) {
            return true;
        }
        Permanent sourcePermanent = source.getSourcePermanentIfItStillExists(game);
        if (sourcePermanent == null || !sourcePermanent.isTapped()) {
            return true;
        }
        return super.isInactive(source, game);
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.PHASE_IN;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (Boolean.TRUE.equals(game.getState().getValue(key))) {
            discard();
            return false;
        }
        Permanent sourcePermanent = source.getSourcePermanentIfItStillExists(game);
        if (sourcePermanent == null || !sourcePermanent.isTapped()) {
            discard();
            return false;
        }
        return this.mor.refersTo(event.getTargetId(), game);
    }
}

class ThePandoricaDelayedTriggeredAbility extends DelayedTriggeredAbility {

    private final String key;

    ThePandoricaDelayedTriggeredAbility(MageObjectReference mor, String key) {
        super(new ThePandoricaPhaseInEffect(mor, key), Duration.EndOfGame, true, false);
        this.key = key;
    }

    private ThePandoricaDelayedTriggeredAbility(final ThePandoricaDelayedTriggeredAbility ability) {
        super(ability);
        this.key = ability.key;
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
            game.getState().setValue(key, true);
            return true;
        }
        if (event.getType() == GameEvent.EventType.ZONE_CHANGE) {
            if (((ZoneChangeEvent) event).getFromZone() == Zone.BATTLEFIELD) {
                game.getState().setValue(key, true);
                return true;
            }
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
    private final String key;

    ThePandoricaPhaseInEffect(MageObjectReference mor, String key) {
        super(Outcome.Benefit);
        this.mor = mor;
        this.key = key;
        staticText = "that permanent phases in";
    }

    private ThePandoricaPhaseInEffect(final ThePandoricaPhaseInEffect effect) {
        super(effect);
        this.mor = effect.mor;
        this.key = effect.key;
    }

    @Override
    public ThePandoricaPhaseInEffect copy() {
        return new ThePandoricaPhaseInEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        game.getState().setValue(key, true);
        Permanent permanent = mor.getPermanent(game);
        if (permanent != null) {
            permanent.phaseIn(game);
        }
        return true;
    }
}
