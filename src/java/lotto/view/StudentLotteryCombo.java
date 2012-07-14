/* Basic class to hold all the bits of information about a lottery event
 * that are needed by the Upcoming Events view that students see */

package lotto.view;

import lotto.model.LotteryEvent;

public class StudentLotteryCombo {
    private LotteryEvent event;
    private String phase;
    private Boolean registered = false;
    private Boolean ticketAssigned = false;
    private Boolean ticketClaimed = false;
    private Boolean canCancel = false;

    public LotteryEvent getEvent() {
        return event;
    }

    public void setEvent(LotteryEvent event) {
        this.event = event;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    public Boolean getTicketAssigned() {
        return ticketAssigned;
    }

    public void setTicketAssigned(Boolean ticketAssigned) {
        this.ticketAssigned = ticketAssigned;
    }

    public Boolean getTicketClaimed() {
        return ticketClaimed;
    }

    public void setTicketClaimed(Boolean ticketClaimed) {
        this.ticketClaimed = ticketClaimed;
    }

    public Boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(Boolean canCancel) {
        this.canCancel = canCancel;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

}