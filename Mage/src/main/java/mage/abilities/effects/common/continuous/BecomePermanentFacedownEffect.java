package mage.abilities.effects.common.continuous;

import mage.MageObjectReference;
import mage.ObjectColor;
import mage.abilities.Ability;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.cards.Card;
import mage.cards.Cards;
import mage.constants.Duration;
import mage.constants.EmptyNames;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.SubLayer;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.targetpointer.FixedTarget;
import mage.target.targetpointer.FixedTargets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author TheElk801
 */
public class BecomePermanentFacedownEffect extends ContinuousEffectImpl {

    public interface PermanentApplier {
        void apply(Permanent permanent, Game game, Ability source);
    }

    private final PermanentApplier applier;
    private boolean foundPermanent = false;

    public BecomePermanentFacedownEffect(PermanentApplier applier, Collection<? extends Card> cards, Game game) {
        super(Duration.Custom, Layer.CopyEffects_1, SubLayer.FaceDownEffects_1b, Outcome.Neutral);
        this.applier = applier;
        this.setTargetPointer(new FixedTargets(
                cards.stream()
                        .map(card -> new MageObjectReference(card, game, 1))
                        .collect(Collectors.toSet())
        ));
    }

    public BecomePermanentFacedownEffect(PermanentApplier applier, Cards cards, Game game) {
        this(applier, cards.getCards(game), game);
    }

    public BecomePermanentFacedownEffect(PermanentApplier applier, Card card, Game game) {
        this(applier, Collections.singletonList(card), game);
    }

    public BecomePermanentFacedownEffect(PermanentApplier applier, Permanent permanent, Game game) {
        super(Duration.Custom, Layer.CopyEffects_1, SubLayer.FaceDownEffects_1b, Outcome.Neutral);
        this.applier = applier;
        this.setTargetPointer(new FixedTarget(permanent, game));
    }

    private BecomePermanentFacedownEffect(final BecomePermanentFacedownEffect effect) {
        super(effect);
        this.applier = effect.applier;
        this.foundPermanent = effect.foundPermanent;
    }

    @Override
    public BecomePermanentFacedownEffect copy() {
        return new BecomePermanentFacedownEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Set<Permanent> permanents = this
                .getTargetPointer()
                .getTargets(game, source)
                .stream()
                .map(game::getPermanent)
                .filter(Objects::nonNull)
                .filter(permanent -> permanent.isFaceDown(game))
                .collect(Collectors.toSet());
        if (permanents.isEmpty()) {
            if (foundPermanent) {
                discard();
            }
            return false;
        }
        foundPermanent = true;
        for (Permanent permanent : permanents) {
            permanent.setName(EmptyNames.FACE_DOWN_CREATURE.getObjectName());
            permanent.removeAllSuperTypes(game);
            permanent.removeAllCardTypes(game);
            permanent.removeAllSubTypes(game);
            permanent.getColor().setColor(ObjectColor.COLORLESS);
            permanent.getManaCost().clear();
            List<Ability> abilitiesToRemove = permanent.getAbilities().stream()
                    .filter(a -> !a.getWorksFaceDown())
                    .collect(Collectors.toList());
            permanent.removeAbilities(abilitiesToRemove, source.getSourceId(), game);
            applier.apply(permanent, game, source);
        }
        return true;
    }
}
