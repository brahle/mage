package mage.cards.p;

import mage.abilities.Ability;
import mage.abilities.common.AsEntersBattlefieldAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.common.ChooseACardNameEffect;
import mage.abilities.effects.common.ChooseCreatureTypeEffect;
import mage.abilities.keyword.EquipAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.UUID;

/**
 * @author Susucr
 */
public final class PsychicPaper extends CardImpl {

    public PsychicPaper(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{2}");

        this.subtype.add(SubType.EQUIPMENT);

        // As Psychic Paper enters the battlefield, choose a card name and a creature type.
        AsEntersBattlefieldAbility entersAbility = new AsEntersBattlefieldAbility(
                new ChooseACardNameEffect(ChooseACardNameEffect.TypeOfName.ALL),
                "choose a card name and a creature type"
        );
        entersAbility.addEffect(new ChooseCreatureTypeEffect(Outcome.Neutral));
        this.addAbility(entersAbility);

        // Equipped creature has the chosen name and is the chosen creature type in addition to its other types.
        this.addAbility(new SimpleStaticAbility(new PsychicPaperEffect()));

        // Equip {2}
        this.addAbility(new EquipAbility(2));
    }

    private PsychicPaper(final PsychicPaper card) {
        super(card);
    }

    @Override
    public PsychicPaper copy() {
        return new PsychicPaper(this);
    }
}

class PsychicPaperEffect extends ContinuousEffectImpl {

    PsychicPaperEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Benefit);
        staticText = "equipped creature has the chosen name and is the chosen creature type in addition to its other types";
    }

    private PsychicPaperEffect(final PsychicPaperEffect effect) {
        super(effect);
    }

    @Override
    public PsychicPaperEffect copy() {
        return new PsychicPaperEffect(this);
    }

    @Override
    public boolean apply(Layer layer, SubLayer sublayer, Ability source, Game game) {
        Permanent equipment = source.getSourcePermanentIfItStillExists(game);
        if (equipment == null || equipment.getAttachedTo() == null) {
            return false;
        }
        Permanent creature = game.getPermanent(equipment.getAttachedTo());
        if (creature == null) {
            return false;
        }
        switch (layer) {
            case TextChangingEffects_3:
                String chosenName = (String) game.getState().getValue(equipment.getId().toString() + ChooseACardNameEffect.INFO_KEY);
                if (chosenName != null && !chosenName.isEmpty()) {
                    creature.setName(chosenName);
                }
                break;
            case TypeChangingEffects_4:
                SubType chosenSubType = (SubType) game.getState().getValue(equipment.getId() + "_type");
                if (chosenSubType != null) {
                    creature.addSubType(game, chosenSubType);
                }
                break;
        }
        return true;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return false;
    }

    @Override
    public boolean hasLayer(Layer layer) {
        return layer == Layer.TextChangingEffects_3 || layer == Layer.TypeChangingEffects_4;
    }
}
