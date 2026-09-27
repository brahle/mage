package mage.cards.d;

import mage.abilities.Ability;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.keyword.CyclingAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.WatcherScope;
import mage.constants.Zone;
import mage.game.Game;
import mage.MageObjectReference;
import mage.game.events.EntersTheBattlefieldEvent;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.game.stack.Spell;
import mage.players.Player;
import mage.watchers.Watcher;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Susucr
 */
public final class DontBlink extends CardImpl {

    public DontBlink(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{1}{U}");

        // Until end of turn, if one or more creatures would enter from exile or after being cast from exile, their owners shuffle them into their libraries instead.
        this.getSpellAbility().addEffect(new DontBlinkEffect());
        this.getSpellAbility().addWatcher(new DontBlinkWatcher());

        // Cycling {2}
        this.addAbility(new CyclingAbility(new ManaCostsImpl<>("{2}")));
    }

    private DontBlink(final DontBlink card) {
        super(card);
    }

    @Override
    public DontBlink copy() {
        return new DontBlink(this);
    }
}

class DontBlinkEffect extends ReplacementEffectImpl {

    DontBlinkEffect() {
        super(Duration.EndOfTurn, Outcome.Detriment);
        staticText = "Until end of turn, if one or more creatures would enter from exile or after being cast from exile, their owners shuffle them into their libraries instead";
    }

    private DontBlinkEffect(final DontBlinkEffect effect) {
        super(effect);
    }

    @Override
    public DontBlinkEffect copy() {
        return new DontBlinkEffect(this);
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        Card targetCard = game.getCard(event.getTargetId());
        if (targetCard == null && event instanceof EntersTheBattlefieldEvent) {
            targetCard = ((EntersTheBattlefieldEvent) event).getTarget();
        }
        if (targetCard != null) {
            Player owner = game.getPlayer(targetCard.getOwnerId());
            if (owner != null) {
                owner.shuffleCardsToLibrary(targetCard, game, source);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.ENTERS_THE_BATTLEFIELD;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        EntersTheBattlefieldEvent entersEvent = (EntersTheBattlefieldEvent) event;
        Permanent permanent = entersEvent.getTarget();
        if (permanent == null || !permanent.isCreature(game)) {
            return false;
        }
        if (entersEvent.getFromZone() == Zone.EXILED) {
            return true;
        }
        DontBlinkWatcher watcher = game.getState().getWatcher(DontBlinkWatcher.class);
        return watcher != null && watcher.wasCastFromExile(event.getTargetId(), game);
    }
}

class DontBlinkWatcher extends Watcher {

    private final Set<MageObjectReference> spellsCastFromExile = new HashSet<>();

    DontBlinkWatcher() {
        super(WatcherScope.GAME);
    }

    @Override
    public void watch(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.SPELL_CAST && event.getZone() == Zone.EXILED) {
            Spell spell = game.getSpell(event.getTargetId());
            if (spell == null) {
                spell = game.getSpell(event.getSourceId());
            }
            if (spell != null) {
                spellsCastFromExile.add(new MageObjectReference(spell.getSourceId(), spell.getZoneChangeCounter(game), game));
                if (!spell.getSourceId().equals(spell.getMainCard().getId())) {
                    spellsCastFromExile.add(new MageObjectReference(spell.getMainCard().getId(), spell.getZoneChangeCounter(game), game));
                }
            }
        }
    }

    public boolean wasCastFromExile(UUID cardId, Game game) {
        int zcc = game.getState().getZoneChangeCounter(cardId);
        if (spellsCastFromExile.contains(new MageObjectReference(cardId, zcc, game))) {
            return true;
        }
        Card card = game.getCard(cardId);
        if (card != null && !card.getId().equals(card.getMainCard().getId())) {
            int mainZcc = game.getState().getZoneChangeCounter(card.getMainCard().getId());
            return spellsCastFromExile.contains(new MageObjectReference(card.getMainCard().getId(), mainZcc, game));
        }
        return false;
    }

    @Override
    public void reset() {
        super.reset();
        spellsCastFromExile.clear();
    }
}
