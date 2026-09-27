package mage.cards.d;

import mage.abilities.effects.common.ExileAllEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterNonlandPermanent;
import mage.filter.predicate.permanent.EnteredThisTurnPredicate;

import java.util.UUID;

/**
 * @author Susucr
 */
public final class DontBlink extends CardImpl {

    private static final FilterPermanent filter
            = new FilterNonlandPermanent("nonland permanents that entered the battlefield this turn");

    static {
        filter.add(EnteredThisTurnPredicate.instance);
    }

    public DontBlink(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{2}{U}");

        // Exile all nonland permanents that entered the battlefield this turn.
        this.getSpellAbility().addEffect(new ExileAllEffect(filter));
    }

    private DontBlink(final DontBlink card) {
        super(card);
    }

    @Override
    public DontBlink copy() {
        return new DontBlink(this);
    }
}
