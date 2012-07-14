package lotto.control.admin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import lotto.dao.VenueInfoDao;
import lotto.model.VenueInfo;
import lotto.validator.VenueInfoValidator;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;


public class AdminVenueController extends SimpleFormController {
    private VenueInfoDao venueInfoDao;

    public AdminVenueController() {
        setCommandClass(lotto.model.VenueInfo.class);
        setCommandName("venueInfo");
        setSuccessView("/manage/venue_seats.htm");  // will redirect to here
        setFormView("admin/venue_editinfo");
        setValidator(new VenueInfoValidator());
    }

    // check if this is a modify form. if so, get the current data out of the
    // database instead of creating a new, empty venue object
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        VenueInfo command = null;
        String venueParam = request.getParameter("venue");
        // if a venue option was specified, try to load that venue's information
        // from the database. otherwise, this form is being used to add a new
        // venue, so create a blank object
        if (venueParam == null) {
            command = new VenueInfo();
        } else {
            command = venueInfoDao.getVenue(Integer.parseInt(venueParam));
            if (command == null) {
                throw new ServletException("Specified venue not found");
            }
        }
        return command;
    }

    // form has been filled out, submitted, and validated, so save the either new
    // or updated venue and redirect to the seating editor
    protected ModelAndView onSubmit(Object command) throws Exception {
        VenueInfo venue = (VenueInfo) command;
        venueInfoDao.saveVenue(venue);

        return new ModelAndView(new RedirectView(getSuccessView(), true), "venue", venue.getVenueId());
    }

    public void setVenueInfoDao(VenueInfoDao venueInfoDao) {
        this.venueInfoDao = venueInfoDao;
    }
}