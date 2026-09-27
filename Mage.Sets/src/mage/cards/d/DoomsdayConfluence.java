package mage.cards.d;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.Mode;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.OptionalAdditionalModeSourceCosts;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.effects.common.SacrificeAllEffect;
import mage.abilities.effects.common.discard.DiscardEachPlayerEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.game.Game;
import mage.game.permanent.token.DalekToken;
import mage.util.CardUtil;

/**
 * @author Susucr
 */
public final class DoomsdayConfluence extends CardImpl {

    private static final FilterCreaturePermanent filterNonartifactCreature = new FilterCreaturePermanent("nonartifact creature");

    static {
        filterNonartifactCreature.add(Predicates.not(CardType.ARTIFACT.getPredicate()));
    }

    public DoomsdayConfluence(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{X}{X}{B}");

        // Choose X. You may choose the same mode more than once.
        this.getSpellAbility().getModes().setMinModes(0);
        this.getSpellAbility().getModes().setMaxModes(100);
        this.getSpellAbility().getModes().setMayChooseSameModeMoreThanOnce(true);
        this.getSpellAbility().getModes().setChooseText("Choose X. You may choose the same mode more than once.");
        this.addAbility(new DoomsdayConfluenceAbility());

        // • Each player sacrifices a nonartifact creature of their choice.
        this.getSpellAbility().addEffect(new SacrificeAllEffect(filterNonartifactCreature));

        // • Create a 3/3 black Dalek artifact creature token with menace.
        this.getSpellAbility().addMode(new Mode(new CreateTokenEffect(new DalekToken())));

        // • Each opponent discards a card.
        this.getSpellAbility().addMode(new Mode(new DiscardEachPlayerEffect(TargetController.OPPONENT)));
    }

    private DoomsdayConfluence(final DoomsdayConfluence card) {
        super(card);
    }

    @Override
    public DoomsdayConfluence copy() {
        return new DoomsdayConfluence(this);
    }
}

class DoomsdayConfluenceAbility extends SimpleStaticAbility implements OptionalAdditionalModeSourceCosts {

    DoomsdayConfluenceAbility() {
        super(Zone.ALL, null);
        this.setRuleVisible(false);
    }

    private DoomsdayConfluenceAbility(final DoomsdayConfluenceAbility ability) {
        super(ability);
    }

    @Override
    public DoomsdayConfluenceAbility copy() {
        return new DoomsdayConfluenceAbility(this);
    }

    @Override
    public void changeModes(Ability ability, Game game) {
        int x = CardUtil.getSourceCostsTag(game, ability, "X", 0);
        ability.getModes().setMinModes(x);
        ability.getModes().setMaxModes(x);
    }

    @Override
    public void addOptionalAdditionalCosts(Ability ability, Game game) {
    }

    @Override
    public String getCastMessageSuffix() {
        return null;
    }
}
