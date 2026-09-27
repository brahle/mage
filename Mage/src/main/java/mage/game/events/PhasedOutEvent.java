package mage.game.events;

import java.util.UUID;

/**
 * @author Susucr
 */
public class PhasedOutEvent extends GameEvent {

    public PhasedOutEvent(UUID targetId, UUID playerId) {
        super(EventType.PHASED_OUT, targetId, null, playerId);
    }
}
