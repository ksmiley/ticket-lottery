/*  */
package lotto.control.admin;

import java.lang.Object;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lotto.dao.VenueInfoDao;
import lotto.model.VenueInfo;
import lotto.model.VenueRow;
import lotto.model.VenueRowId;
import lotto.model.VenueSeat;
import lotto.model.VenueSection;
import net.sf.sojo.interchange.Serializer;
import net.sf.sojo.interchange.json.JsonSerializer;
import org.apache.commons.collections.ComparatorUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


public class AdminVenueSeatsController extends MultiActionController {
    private VenueInfoDao venueInfoDao;

    /* Display the section editor page. This needs the VenueInfo (for the name),
     * and it needs a list of sections that can be edited. The sections list
     * will be sorted in natural order by the display label. */
    public ModelAndView showMainPage(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException {
        // get the venue id out of the request and parse it to a number
        String venueStr = request.getParameter("venue");
        if (venueStr == null) {
            throw new ServletException("Venue must be a number");
        }
        int venueId = Integer.parseInt(venueStr);
        // try to get the venue from the database
        VenueInfo venueInfo = venueInfoDao.getVenue(venueId);
        // also need the sections associated with the venue. they're returned
        // as a set but need to be sorted, so immediately put them into a List
        List<VenueSection> venueSections = new ArrayList<VenueSection>(venueInfo.getVenueSections());
        // sort the sections by their label in natural order
        Collections.sort(venueSections, new SectionNaturalComparator());

        // store the venue id in the session so it's available when the 
        // browser makes AJAX calls
        session.setAttribute("curVenue", venueId);

        ModelAndView mv = new ModelAndView("admin/venue_editseats");
        mv.addObject("venue", venueInfo);
        mv.addObject("sections", venueSections);
        return mv;
    }

    /*
     * AJAX CONTROLLERS
     * showMainPage() is the only action that displays a full page. the following
     * all respond to AJAX calls from the above page
     */

    /*  */
    public ModelAndView loadSection(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException {
        // get the section id out of the request and parse it to a number
        String sectionStr = request.getParameter("loadSectionId");
        if (sectionStr == null) {
            throw new ServletException("Section ID must be a number");
        }
        int sectionId = Integer.parseInt(sectionStr);

        // get the rows and seats from the db in an efficient way (this method
        // will try to load it all at once, instead of the default lazy loading
        // seats one row at a time as they're accessed)
        List<VenueRow> allRows = venueInfoDao.getRowsWithSeats(sectionId);
        // need to massage the data into a different, more compact list format
        // before sending it to the client
        List<List> rowsFormed = new ArrayList<List>();
        for (VenueRow oneRow: allRows) {
            // the rows are represented as a list of lists to make sure their
            // order is preserved across the wire and on the client side
            List<Object> newRow = new ArrayList<Object>();
            // the row id and label will be the first two items on every row list
            newRow.add(oneRow.getId().getRow());
            newRow.add(oneRow.getLabel());
            // now add the seat numbers to the rest of the list
            for (Object oneSeat: oneRow.getVenueSeats()) {
                // this is simpler than it looks: cast oneSeat to VenueSeat,
                // since that's what it is, then get its VenueSeatId, then get
                // the seat number out of that, and add it on to the row
                newRow.add(((VenueSeat) oneSeat).getId().getSeatNo());
            }
            rowsFormed.add(newRow);  // add the finished row to the list of rows
        }

        // pull down the section info, too, in case the client needs the
        // location or label
        VenueSection sectionInfo = venueInfoDao.getSection(sectionId);

        // send rows and seats to client as JSON
        ModelAndView mv = new ModelAndView("jsonView");
        mv.addObject("rows", rowsFormed);
        mv.addObject("label", sectionInfo.getLabel());
        mv.addObject("location", sectionInfo.getLocation());
        mv.addObject("position", sectionInfo.getPosition());
        return mv;
    }

    /* Add a section with a label and optional description entered by the user.
     * Sends the generated section ID back to the client. */
    public ModelAndView addSection(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException {
        // get input variables
        String newLabel = request.getParameter("name");
        String newLocation = request.getParameter("location");

        // need to load the venue so the new section object can be associated with it
        int venueId = (Integer) session.getAttribute("curVenue");
        VenueInfo curVenue = venueInfoDao.getVenue(venueId);

        // now add the new section
        int newSectionId = venueInfoDao.addSection(curVenue, newLabel, newLocation);

        // send the new section id back to the client so it can be used to refer
        // to the newly added section
        ModelAndView mv = new ModelAndView("jsonView");
        mv.addObject("sectionId", newSectionId);
        return mv;
    }

    public ModelAndView saveSection(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException {
        // get the section id out of the request and parse it to a number
        String sectionStr = request.getParameter("saveSectionId");
        if (sectionStr == null) {
            throw new ServletException("Section ID must be a number");
        }
        int sectionId = Integer.parseInt(sectionStr);
        VenueSection sectionObj = venueInfoDao.getSection(sectionId);

        // initialize a JSON parser, then use it to decode the data sent by
        // the client. the client's data needs to be merged with the existing
        // database structure, so instead of wiping out what's there and
        // dumping in the new data, figure out what was added, deleted, or
        // changed
        Serializer jsonParser = new JsonSerializer();
        List<List> rowList = (ArrayList) jsonParser.deserialize(request.getParameter("rows"));
        // to do the comparison, the list needs to be easier to access, so
        // translate it into a Map with the row id as keys
        Map<Integer, SectionRow> rowMap = makeRowMap(rowList);
        // get the current section data from the database so it can be compared
        List<VenueRow> currentRows = venueInfoDao.getRowsWithSeats(sectionId);

        // iterate over every row to look for ones that have been removed
        // or modified. also flag each one as "seen", so a second loop can
        // check for rows in the new data that have been added
        // modifying the list in the middle of iterating through it is
        // dangerous, so keep track of which rows and seats to delete and
        // do it at the end
        List<VenueRow> rowsToDelete = new ArrayList<VenueRow>();
        List<VenueSeat> seatsToDelete = new ArrayList<VenueSeat>();
        for (VenueRow oldRow: currentRows) {
            // see if this row's id is in the data the client sent back.
            // if not, the user deleted this row
            SectionRow newRow = rowMap.get(oldRow.getId().getRow());
            if (newRow == null) {
                rowsToDelete.add(oldRow);
            } else {
                newRow.setSeen(true);
                Boolean changed = false;
                if (!oldRow.getLabel().contentEquals(newRow.getLabel())) {
                    oldRow.setLabel(newRow.getLabel());
                    changed = true;
                }
                if (oldRow.getPosition() == null || oldRow.getPosition() != newRow.getPosition()) {
                    oldRow.setPosition(newRow.getPosition());
                    changed = true;
                }
                if (changed) {
                    venueInfoDao.saveRow(oldRow);
                }
                // see which seats have changed, using same approach.
                // sweep through old row's seats, delete any that aren't in
                // new data, mark the other new ones as "seen", then add
                // the unseen ones in a second pass
                for (Object seatObj: oldRow.getVenueSeats()) {
                    VenueSeat oldSeat = (VenueSeat) seatObj;
                    int seatNo = oldSeat.getId().getSeatNo();
                    SectionSeat newSeat = newRow.getSeats().get(seatNo);
                    // seat has been deleted
                    if (newSeat == null) {
                        seatsToDelete.add(oldSeat);
                    } else {
                        newSeat.setSeen(true);
                    }
                }
                // sweep through new data and add any seats that weren't
                // seen in the first pass
                for (SectionSeat newSeat: newRow.getSeats().values()) {
                    if (!newSeat.getSeen()) {
                        venueInfoDao.addSeat(newSeat.getSeatNo(), oldRow);
                    }
                }
            }
        }
        // delete rows and seats flagged for deletion
        for (VenueRow deleteRow: rowsToDelete) {
            venueInfoDao.removeRow(deleteRow);
        }
        for (VenueSeat deleteSeat: seatsToDelete) {
            venueInfoDao.removeSeat(deleteSeat);
        }

        // sweep over new data and add any rows that weren't seen in the
        // first pass, and add all their seats
        for (SectionRow newRow: rowMap.values()) {
            if (!newRow.getSeen()) {
                // assemble a new row object, which requires a separate row id
                VenueRowId createRowId = new VenueRowId();
                createRowId.setSectionId(sectionId);
                createRowId.setRow(newRow.getRowId());
                VenueRow createRow = new VenueRow();
                createRow.setId(createRowId);
                createRow.setLabel(newRow.getLabel());
                createRow.setPosition(newRow.getPosition());
                // persist it
                venueInfoDao.addRow(createRow, sectionObj);
                // now add all the seats
                for (SectionSeat newSeat: newRow.getSeats().values()) {
                    venueInfoDao.addSeat(newSeat.getSeatNo(), createRow);
                }
            }
        }
        // finished. persistent seat map should match the one sent by the client

        return new ModelAndView("jsonView", "ok", true);
    }

    private Map<Integer, SectionRow> makeRowMap(List<List> rowList) {
        Map rowMap = new HashMap<Integer, List>();
        // the row indicies are position numbers, so have to iterate the
        // array like this to track them
        for (int pos = 0; pos < rowList.size(); pos++) {
            List<Object> oneRow = rowList.get(pos);
            // convert them into classes (defined below) to make it easier
            // to work with later
            SectionRow newRow = new SectionRow();
            long t = (Long) oneRow.get(0);
            newRow.setRowId((int) t);
            newRow.setLabel((String) oneRow.get(1));
            newRow.setPosition(pos);
            newRow.setSeen(false);
            // also convert the seats, so they can have "seen" flags
            Map<Integer, SectionSeat> seatList = new HashMap<Integer, SectionSeat>();
            for (int i = 2; i < oneRow.size(); i++) {
                long l = (Long) oneRow.get(i);
                Integer seatNo = (int) l;
                seatList.put(seatNo, new SectionSeat(seatNo));
            }
            newRow.setSeats(seatList);
            rowMap.put(newRow.getRowId(), newRow);
        }
        return rowMap;
    }

    /* Sometimes the client needs to save the section its working on and load
     * another. Because taking a trip to the server is expensive, this method
     * lets the client do both in one swoop. It really just calls the standard
     * load and save controllers, and returns the ModelAndView of the load */
    public ModelAndView saveAndLoadSection(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException {
        saveSection(request, response, session);
        return loadSection(request, response, session);
    }

    /* accessor so Spring can inject the DAO object */
    public void setVenueInfoDao(VenueInfoDao venueInfoDao) {
        this.venueInfoDao = venueInfoDao;
    }
}


/* Comparator so the sections can be sorted by their labels in natural order.
 * For example,
 */
/*class SectionNaturalComparator implements Comparator {
    private Comparator naturalComparator;

    /* Apache Commons provides a natural order comparator. it will do the heavy
     * lifting. SectionComparator is only needed because we want to sort by
     * VenueSection.label, not VenueSection */
 /*   public SectionNaturalComparator() {
        naturalComparator = ComparatorUtils.naturalComparator();
    }

    public int compare(Object o1, Object o2) {
        return naturalComparator.compare(((VenueSection) o1).getLabel(), ((VenueSection) o2).getLabel());
    }    
}*/



/* Classes to represent a section while processing a save command sent by
 * the client */
class SectionRow {
    private Boolean seen;
    private int rowId;
    private String label;
    private int position;
    private Map<Integer, SectionSeat> seats;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Map<Integer, SectionSeat> getSeats() {
        return seats;
    }

    public void setSeats(Map<Integer, SectionSeat> seats) {
        this.seats = seats;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}
class SectionSeat {
    private Boolean seen;
    private int seatNo;

    public SectionSeat(int seatNo) {
        this.seatNo = seatNo;
        this.seen = false;
    }

    public int getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(int seatNo) {
        this.seatNo = seatNo;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}