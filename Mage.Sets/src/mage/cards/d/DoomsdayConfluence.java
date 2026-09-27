package mage.cards.d;

import java.util.UUID;
import mage.abilities.Mode;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.effects.common.DrawCardSourceControllerEffect;
import mage.abilities.effects.common.LoseLifeTargetEffect;
import mage.abilities.effects.common.ReturnFromGraveyardToHandTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.filter.StaticFilters;
import mage.game.permanent.token.DalekToken;
import mage.target.common.TargetCardInYourGraveyard;
import mage.target.common.TargetOpponent;

/**
 * @author Susucr
 */
public final class DoomsdayConfluence extends CardImpl {

    public DoomsdayConfluence(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{4}{B}{B}");

        // Choose three. You may choose the same mode more than once.
        this.getSpellAbility().getModes().setMinModes(3);
        this.getSpellAbility().getModes().setMaxModes(3);
        this.getSpellAbility().getModes().setMayChooseSameModeMoreThanOnce(true);

        // - Target opponent loses 3 life and you draw a card;
        this.getSpellAbility().addEffect(new LoseLifeTargetEffect(3));
        this.getSpellAbility().addEffect(new DrawCardSourceControllerEffect(1, true).concatBy("and"));
        this.getSpellAbility().addTarget(new TargetOpponent());

        // - Create a 3/3 black Dalek artifact creature token with menace;
        this.getSpellAbility().addMode(new Mode(new CreateTokenEffect(new DalekToken())));

        // - Return target creature card from your graveyard to your hand.
        this.getSpellAbility().addMode(new Mode(new ReturnFromGraveyardToHandTargetEffect())
                .addTarget(new TargetCardInYourGraveyard(StaticFilters.FILTER_CARD_CREATURE_YOUR_GRAVEYARD)));
    }

    private DoomsdayConfluence(final DoomsdayConfluence card) {
        super(card);
    }

    @Override
    public DoomsdayConfluence copy() {
        return new DoomsdayConfluence(this);
    }
}
