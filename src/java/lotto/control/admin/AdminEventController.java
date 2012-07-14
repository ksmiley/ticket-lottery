
package lotto.control.admin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import lotto.LottoUtils;
import lotto.dao.LotteryEventDao;
import lotto.dao.UserInfoDao;
import lotto.dao.VenueInfoDao;
import lotto.model.AdminInfo;
import lotto.model.LotteryEvent;
import lotto.model.VenueInfo;
import lotto.validator.LotteryEventValidator;
import lotto.validator.VenueInfoBinder;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class AdminEventController extends SimpleFormController {
    private LotteryEventDao lotteryEventDao;
    private VenueInfoDao venueInfoDao;
    private UserInfoDao userInfoDao;

    public AdminEventController() {
        setCommandClass(lotto.model.LotteryEvent.class);
        setCommandName("lotteryEvent");
        setSuccessView("/manage/event_seats.htm");  // will redirect to here
        setFormView("admin/event_editinfo");
        setValidator(new LotteryEventValidator());
    }

    protected Map referenceData(HttpServletRequest request) throws Exception {
        Map ref = new HashMap<String, Object>();
        ref.put("venues", venueInfoDao.getAllVenues());
        return ref;
    }

    // check if this is a modify form. if so, get the current data out of the
    // database instead of creating a new, empty event object
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        LotteryEvent command = null;
        String eventParam = request.getParameter("event");
        if (eventParam == null) {
            command = new LotteryEvent();
        } else {
            command = lotteryEventDao.getEvent(Integer.parseInt(eventParam));
            if (command == null) {
                throw new ServletException("Specified event not found");
            }
        }
        return command;
    }

    // custom binder to be able to parse all the date fields
    protected void initBinder(HttpServletRequest request,
                              ServletRequestDataBinder binder)
                              throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, false);
        binder.registerCustomEditor(Date.class, dateEditor);

        VenueInfoBinder venueBinder = new VenueInfoBinder(venueInfoDao);
        binder.registerCustomEditor(lotto.model.VenueInfo.class, venueBinder);
    }

    // form has been filled out, submitted, and validated, so save the either new
    // or updated venue and redirect to the seating editor
    protected ModelAndView onSubmit(Object command) throws Exception {
        AdminInfo admin = LottoUtils.getCurrentAdmin(userInfoDao);
        LotteryEvent event = (LotteryEvent) command;
        event.setAdminInfo(admin);  // set logged in admin as creator
        lotteryEventDao.saveEvent(event);

        return new ModelAndView(new RedirectView(getSuccessView(), true), "event", event.getLotteryId());
    }

    public void setLotteryEventDao(LotteryEventDao lotteryEventDao) {
        this.lotteryEventDao = lotteryEventDao;
    }

    public void setUserInfoDao(UserInfoDao userInfoDao) {
        this.userInfoDao = userInfoDao;
    }

    public void setVenueInfoDao(VenueInfoDao venueInfoDao) {
        this.venueInfoDao = venueInfoDao;
    }
    
}
