package lotto.control.admin;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lotto.dao.LotteryEventDao;
import lotto.dao.VenueInfoDao;
import lotto.model.LotteryEvent;
import lotto.model.VenueInfo;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * After the admin logs in, displays the home screen with an overview of
 * event and venue information. This controller just needs to pull down
 * lists with that information.
 */
public class AdminHomeController extends AbstractController {
    private LotteryEventDao lotteryEventDao;
    private VenueInfoDao venueInfoDao;

    public void setVenueInfoDao(VenueInfoDao venueInfoDao) {
        this.venueInfoDao = venueInfoDao;
    }

    public void setLotteryEventDao(LotteryEventDao lotteryEventDao) {
        this.lotteryEventDao = lotteryEventDao;
    }

    protected ModelAndView handleRequestInternal(
            HttpServletRequest request, 
            HttpServletResponse response) throws Exception {
        
        List<LotteryEvent> upcomingEvents = lotteryEventDao.getUpcomingEvents();
        List<VenueInfo> allVenues = venueInfoDao.getAllVenues();
        
        ModelAndView mv = new ModelAndView("admin/home");
        mv.addObject("events", upcomingEvents);
        mv.addObject("venues", allVenues);
        return mv;
    }
}