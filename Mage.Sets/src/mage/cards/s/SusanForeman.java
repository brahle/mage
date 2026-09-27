package mage.cards.s;

import mage.MageInt;
import mage.abilities.keyword.DoctorsCompanionAbility;
import mage.abilities.keyword.PlainswalkAbility;
import mage.abilities.mana.GreenManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.SuperType;

import java.util.UUID;

/**
 * @author Susucr
 */
public final class SusanForeman extends CardImpl {

    public SusanForeman(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{G}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.TIME_LORD);
        this.subtype.add(SubType.HUMAN);
        this.power = new MageInt(2);
        this.toughness = new MageInt(3);

        // Plainswalk
        this.addAbility(new PlainswalkAbility());

        // {T}: Add {G}.
        this.addAbility(new GreenManaAbility());

        // Doctor's companion
        this.addAbility(DoctorsCompanionAbility.getInstance());
    }

    private SusanForeman(final SusanForeman card) {
        super(card);
    }

    @Override
    public SusanForeman copy() {
        return new SusanForeman(this);
    }
}
