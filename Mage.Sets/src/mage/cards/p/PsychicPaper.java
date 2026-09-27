package mage.cards.p;

import mage.abilities.Ability;
import mage.abilities.common.AsBecomesAttachedToCreatureSourceAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.common.ChooseACardNameEffect;
import mage.abilities.effects.common.ChooseCreatureTypeEffect;
import mage.abilities.effects.common.combat.CantBeBlockedAttachedEffect;
import mage.abilities.effects.common.continuous.GainAbilityAttachedEffect;
import mage.abilities.keyword.EquipAbility;
import mage.abilities.keyword.WardAbility;
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

        // As Psychic Paper becomes attached to a creature, choose a creature card name and a creature type.
        AsBecomesAttachedToCreatureSourceAbility attachAbility = new AsBecomesAttachedToCreatureSourceAbility(
                new ChooseACardNameEffect(ChooseACardNameEffect.TypeOfName.CREATURE_NAME),
                "choose a creature card name and a creature type"
        );
        attachAbility.addEffect(new ChooseCreatureTypeEffect(Outcome.Neutral));
        this.addAbility(attachAbility);

        // Equipped creature has ward {1}, it can't be blocked, and its name and creature type are the last chosen name and creature type.
        Ability ability = new SimpleStaticAbility(new GainAbilityAttachedEffect(
                new WardAbility(new GenericManaCost(1), false), AttachmentType.EQUIPMENT
        ).setText("Equipped creature has ward {1}"));
        ability.addEffect(new CantBeBlockedAttachedEffect(AttachmentType.EQUIPMENT)
                .setText(", it can't be blocked")
        );
        ability.addEffect(new PsychicPaperEffect());
        this.addAbility(ability);

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
        staticText = ", and its name and creature type are the last chosen name and creature type";
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
                SubType chosenSubType = ChooseCreatureTypeEffect.getChosenCreatureType(equipment.getId(), game);
                if (chosenSubType != null) {
                    creature.removeAllCreatureTypes(game);
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
