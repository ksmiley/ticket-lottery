/* Display the student home screen, with all the upcoming events */

package lotto.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lotto.LottoUtils;
import lotto.dao.LotteryEventDao;
import lotto.model.LotteryEvent;
import lotto.security.LottoUser;
import lotto.view.StudentLotteryCombo;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


public class HomeController extends AbstractController {
    private LotteryEventDao lotteryEventDao;

    public void setLotteryEventDao(LotteryEventDao lotteryEventDao) {
        this.lotteryEventDao = lotteryEventDao;
    }

    protected ModelAndView handleRequestInternal(
            HttpServletRequest request, 
            HttpServletResponse response) throws Exception {
        // get info about logged in user
        LottoUser student = (LottoUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // grab the next 10 events, then grab lists of events that the user has
        // a ticket for or has registered for. that information will be merged
        // to create a list to give to the model
        List<LotteryEvent> upcomingEvents =
                lotteryEventDao.getUpcomingEvents(10);
        List<LotteryEvent> events_haveRegistered =
                lotteryEventDao.getUpcomingEvents_StudentHasRegistered(student.getUserId());
        List<LotteryEvent> events_haveTickets = 
                lotteryEventDao.getUpcomingEvents_StudentHasTicket(student.getUserId());

        List<StudentLotteryCombo> modelEvents = new ArrayList<StudentLotteryCombo>(10);
        for (LotteryEvent oneEvent: upcomingEvents) {
            StudentLotteryCombo combo = new StudentLotteryCombo();
            combo.setEvent(oneEvent);
            // mark whether student is registerd
            if (events_haveRegistered.contains(oneEvent)) {
                combo.setRegistered(true);
            } else {
                combo.setRegistered(false);
            }
            // mark whether student has a ticket available. if there is one,
            // then figure out (from the database) whether the student has
            // already claimed the ticket
            if (events_haveTickets.contains(oneEvent)) {
                combo.setTicketAssigned(true);
                combo.setTicketClaimed(
                    lotteryEventDao.isStudentLotterySeatClaimed(
                        oneEvent.getLotteryId(), student.getUserId()
                    )
                );
            } else {
                combo.setTicketAssigned(false);
                combo.setTicketClaimed(false);
            }

            // now figure out the current event phase, based on the current time
            combo.setPhase(LottoUtils.determineEventPhase(oneEvent));

            // figure out whether ticket canceling is currently allowed
            combo.setCanCancel(LottoUtils.canCancelTicket(oneEvent));

            // add the expanded event object to the list
            modelEvents.add(combo);
        }

        ModelAndView mv = new ModelAndView("home");
        mv.addObject("events", modelEvents);
        return mv;
    }
}
