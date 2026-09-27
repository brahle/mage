package mage.cards.t;

import mage.MageInt;
import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.BatchTriggeredAbility;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.AttacksTriggeredAbility;
import mage.abilities.dynamicvalue.common.CountersSourceCount;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.hint.Hint;
import mage.abilities.hint.ValueHint;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.PhasedOutBatchEvent;
import mage.game.events.PhasedOutEvent;
import mage.game.events.ZoneChangeBatchEvent;
import mage.game.events.ZoneChangeEvent;
import mage.game.permanent.Permanent;
import mage.game.permanent.PermanentToken;
import mage.players.Player;
import mage.target.common.TargetAnyTarget;

import java.util.UUID;

/**
 * @author Susucr
 */
public final class TheWarDoctor extends CardImpl {

    private static final Hint hint = new ValueHint("Time counters on {this}", new CountersSourceCount(CounterType.TIME));

    public TheWarDoctor(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{R}{W}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.TIME_LORD);
        this.subtype.add(SubType.DOCTOR);
        this.power = new MageInt(3);
        this.toughness = new MageInt(5);

        // Whenever one or more other permanents phase out and whenever one or more other cards are put into exile from anywhere, put a time counter on The War Doctor.
        this.addAbility(new TheWarDoctorExileTriggeredAbility());
        this.addAbility(new TheWarDoctorPhaseOutTriggeredAbility());

        // Whenever The War Doctor attacks, it deals damage equal to the number of time counters on it to any target. If a creature dealt damage this way would die this turn, exile it instead.
        Ability ability = new AttacksTriggeredAbility(new TheWarDoctorDamageEffect());
        ability.addTarget(new TargetAnyTarget());
        ability.addHint(hint);
        this.addAbility(ability);
    }

    private TheWarDoctor(final TheWarDoctor card) {
        super(card);
    }

    @Override
    public TheWarDoctor copy() {
        return new TheWarDoctor(this);
    }
}

class TheWarDoctorExileTriggeredAbility extends TriggeredAbilityImpl implements BatchTriggeredAbility<ZoneChangeEvent> {

    TheWarDoctorExileTriggeredAbility() {
        super(Zone.BATTLEFIELD, new AddCountersSourceEffect(CounterType.TIME.createInstance()));
    }

    private TheWarDoctorExileTriggeredAbility(final TheWarDoctorExileTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public TheWarDoctorExileTriggeredAbility copy() {
        return new TheWarDoctorExileTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.ZONE_CHANGE_BATCH;
    }

    @Override
    public boolean checkEvent(ZoneChangeEvent event, Game game) {
        if (event.getToZone() != Zone.EXILED) {
            return false;
        }
        if (event.getFromZone() == Zone.EXILED) {
            return false;
        }
        if (event.getTarget() instanceof PermanentToken) {
            return false;
        }
        Card card = game.getCard(event.getTargetId());
        if (card == null || card.isCopy()) {
            return false;
        }
        return !card.getId().equals(getSourceId()) && !card.getMainCard().getId().equals(getSourceId());
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        return !getFilteredEvents((ZoneChangeBatchEvent) event, game).isEmpty();
    }

    @Override
    public String getRule() {
        return "Whenever one or more other permanents phase out and whenever one or more other cards "
                + "are put into exile from anywhere, put a time counter on {this}.";
    }
}

class TheWarDoctorPhaseOutTriggeredAbility extends TriggeredAbilityImpl implements BatchTriggeredAbility<PhasedOutEvent> {

    TheWarDoctorPhaseOutTriggeredAbility() {
        super(Zone.BATTLEFIELD, new AddCountersSourceEffect(CounterType.TIME.createInstance()));
        this.setRuleVisible(false);
    }

    private TheWarDoctorPhaseOutTriggeredAbility(final TheWarDoctorPhaseOutTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public TheWarDoctorPhaseOutTriggeredAbility copy() {
        return new TheWarDoctorPhaseOutTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.PHASED_OUT_BATCH;
    }

    @Override
    public boolean checkEvent(PhasedOutEvent event, Game game) {
        return !getSourceId().equals(event.getTargetId());
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        return !getFilteredEvents((PhasedOutBatchEvent) event, game).isEmpty();
    }
}

class TheWarDoctorDamageEffect extends OneShotEffect {

    TheWarDoctorDamageEffect() {
        super(Outcome.Damage);
        staticText = "it deals damage equal to the number of time counters on it to any target. "
                + "If a creature dealt damage this way would die this turn, exile it instead";
    }

    private TheWarDoctorDamageEffect(final TheWarDoctorDamageEffect effect) {
        super(effect);
    }

    @Override
    public TheWarDoctorDamageEffect copy() {
        return new TheWarDoctorDamageEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanentOrLKIBattlefield(source.getSourceId());
        int count = permanent != null ? permanent.getCounters(game).getCount(CounterType.TIME) : 0;
        if (count <= 0) {
            return false;
        }
        UUID targetId = getTargetPointer().getFirst(game, source);
        Permanent targetPermanent = game.getPermanent(targetId);
        if (targetPermanent != null) {
            int damageDealt = targetPermanent.damage(count, source.getSourceId(), source, game, false, true);
            if (damageDealt > 0 && targetPermanent.isCreature(game)) {
                game.addEffect(new TheWarDoctorExileReplacementEffect(new MageObjectReference(targetPermanent, game)), source);
            }
            return true;
        }
        Player targetPlayer = game.getPlayer(targetId);
        if (targetPlayer != null) {
            targetPlayer.damage(count, source.getSourceId(), source, game);
            return true;
        }
        return false;
    }
}

class TheWarDoctorExileReplacementEffect extends ReplacementEffectImpl {

    private final MageObjectReference mor;

    TheWarDoctorExileReplacementEffect(MageObjectReference mor) {
        super(Duration.EndOfTurn, Outcome.Exile);
        this.mor = mor;
        staticText = "If a creature dealt damage this way would die this turn, exile it instead";
    }

    private TheWarDoctorExileReplacementEffect(final TheWarDoctorExileReplacementEffect effect) {
        super(effect);
        this.mor = effect.mor;
    }

    @Override
    public TheWarDoctorExileReplacementEffect copy() {
        return new TheWarDoctorExileReplacementEffect(this);
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        ((ZoneChangeEvent) event).setToZone(Zone.EXILED);
        return false;
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.ZONE_CHANGE;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        ZoneChangeEvent zce = (ZoneChangeEvent) event;
        return zce.isDiesEvent() && mor.equals(new MageObjectReference(zce.getTarget(), game));
    }
}
