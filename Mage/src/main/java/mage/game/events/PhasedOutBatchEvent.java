package mage.game.events;

/**
 * @author Susucr
 */
public class PhasedOutBatchEvent extends BatchEvent<PhasedOutEvent> {

    public PhasedOutBatchEvent(PhasedOutEvent firstEvent) {
        super(EventType.PHASED_OUT_BATCH, false, false, false, firstEvent);
    }
}
