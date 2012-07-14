/* Handle AJAX calls from the add/edit event seats screen */

package lotto.control.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lotto.dao.LotteryEventDao;
import lotto.dao.VenueInfoDao;
import lotto.model.LotteryEvent;
import lotto.model.LotterySeat;
import lotto.model.LotterySeatId;
import lotto.model.VenueSeat;
import lotto.model.VenueSeatId;
import lotto.model.VenueSection;
import net.sf.sojo.interchange.Serializer;
import net.sf.sojo.interchange.json.JsonSerializer;
import org.apache.commons.collections.ComparatorUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


public class AdminEventSeatsController extends MultiActionController {
    private LotteryEventDao lotteryEventDao;
    private VenueInfoDao venueInfoDao;

    public ModelAndView showMainPage(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException {
        // get the event id out of the request and parse it to a number
        String eventStr = request.getParameter("event");
        if (eventStr == null) {
            throw new ServletException("Event must be a number");
        }
        int eventId = Integer.parseInt(eventStr);
        // try to get the venue from the database
        LotteryEvent eventInfo = lotteryEventDao.getEvent(eventId);
        // also need the sections associated with the venue. they're returned
        // as a set but need to be sorted, so immediately put them into a List
        List<VenueSection> venueSections = new ArrayList<VenueSection>(eventInfo.getVenueInfo().getVenueSections());
        // sort the sections by their label in natural order
        Collections.sort(venueSections, new SectionNaturalComparator());

        // store the event id in the session so it's available when the
        // browser makes AJAX calls
        session.setAttribute("curEvent", eventId);

        ModelAndView mv = new ModelAndView("admin/event_editseats");
        mv.addObject("event", eventInfo);
        mv.addObject("sections", venueSections);
        return mv;
    }

    public ModelAndView loadSection(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException {
        // get the section id out of the request and parse it to a number
        String sectionStr = request.getParameter("loadSectionId");
        if (sectionStr == null) {
            throw new ServletException("Section ID must be a number");
        }
        int sectionId = Integer.parseInt(sectionStr);
        int eventId = (Integer) session.getAttribute("curEvent");

        List<Object[]> allSeats = lotteryEventDao.getVenueSeatsByLottery(eventId, sectionId);

        List<List> rowsFormed = new ArrayList<List>();
        List<Object[]> curRow = null;
        int lastRowId = -1;
        for (Object[] oneSeat: allSeats) {
            if (lastRowId == -1 || ((Integer) oneSeat[0]) != lastRowId) {
                if (lastRowId != -1) {
                    rowsFormed.add(curRow);
                }
                lastRowId = (Integer) oneSeat[0];
                curRow = new ArrayList<Object[]>();
            }
            curRow.add(oneSeat);
        }
        rowsFormed.add(curRow);

        VenueSection sectionInfo = venueInfoDao.getSection(sectionId);

        ModelAndView mv = new ModelAndView("jsonView");
        mv.addObject("rows", rowsFormed);
        mv.addObject("label", sectionInfo.getLabel());
        mv.addObject("location", sectionInfo.getLocation());
        mv.addObject("position", sectionInfo.getPosition());
        return mv;
    }

    public ModelAndView saveSection(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException {
        // get the section id out of the request and parse it to a number
        String sectionStr = request.getParameter("saveSectionId");
        if (sectionStr == null) {
            throw new ServletException("Section ID must be a number");
        }
        int sectionId = Integer.parseInt(sectionStr);
        int eventId = (Integer) session.getAttribute("curEvent");

        LotteryEvent event = lotteryEventDao.getEvent(eventId);

        // initialize a JSON parser, then use it to decode the data sent by
        // the client. the client's data can just be used to replace what's
        // currently in LotteryEvents
        Serializer jsonParser = new JsonSerializer();
        List<List> rowList = (ArrayList) jsonParser.deserialize(request.getParameter("rows"));

        lotteryEventDao.removeLotterySeatsInSection(eventId, sectionId);
        for (List<Object> oneRow: rowList) {
            long rowId = (Long) oneRow.get(0);
            for (int i = 1; i < oneRow.size(); i++) {
                long seatNo = (Long) oneRow.get(i);
                LotterySeat oldSeat = lotteryEventDao.getLotterySeat(eventId, sectionId, (int) rowId, (int) seatNo);
                if (oldSeat == null) {
                    VenueSeatId seatRefId = new VenueSeatId(sectionId, (int) rowId, (int) seatNo);
                    VenueSeat seatRef = new VenueSeat();
                    seatRef.setId(seatRefId);
                    LotterySeatId newSeatId = new LotterySeatId(eventId, sectionId, (int) rowId, (int) seatNo);
                    LotterySeat newSeat = new LotterySeat(newSeatId, event, seatRef);
                    event.getLotterySeats().add(newSeat);
                    lotteryEventDao.saveLotterySeat(newSeat);
                } else {
                    lotteryEventDao.saveLotterySeat(oldSeat);
                }
            }
        }

        return new ModelAndView("jsonView", "ok", true);
    }

    public ModelAndView saveAndLoadSection(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException {
        saveSection(request, response, session);
        return loadSection(request, response, session);
    }

    public void setLotteryEventDao(LotteryEventDao lotteryEventDao) {
        this.lotteryEventDao = lotteryEventDao;
    }

    public void setVenueInfoDao(VenueInfoDao venueInfoDao) {
        this.venueInfoDao = venueInfoDao;
    }
}


/* Comparator so the sections can be sorted by their labels in natural order.
 */
class SectionNaturalComparator implements Comparator {
    private Comparator naturalComparator;

    /* Apache Commons provides a natural order comparator. it will do the heavy
     * lifting. SectionComparator is only needed because we want to sort by
     * VenueSection.label, not VenueSection */
    public SectionNaturalComparator() {
        naturalComparator = ComparatorUtils.naturalComparator();
    }

    public int compare(Object o1, Object o2) {
        return naturalComparator.compare(((VenueSection) o1).getLabel(), ((VenueSection) o2).getLabel());
    }
}