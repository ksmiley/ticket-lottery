/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotto.dao;

import java.util.List;
import lotto.model.VenueInfo;
import lotto.model.VenueRow;
import lotto.model.VenueSeat;
import lotto.model.VenueSection;
import org.springframework.dao.DataAccessException;

/**
 *
 * @author keith
 */
public interface VenueInfoDao {

    List<VenueInfo> getAllVenues() throws DataAccessException;

    VenueInfo getVenue(int venueId) throws DataAccessException;

    void saveVenue(VenueInfo venue) throws DataAccessException;

    List<VenueRow> getRowsInSection(int sectionId) throws DataAccessException;
    
    List<VenueRow> getRowsWithSeats(int sectionId) throws DataAccessException;

    void addRow(VenueRow row, VenueSection section) throws DataAccessException;

    void removeRow(VenueRow row) throws DataAccessException;

    void saveRow(VenueRow row) throws DataAccessException;

    void addSeat(VenueSeat seat, VenueRow row) throws DataAccessException;

    void addSeat(int seatNo, VenueRow row) throws DataAccessException;

    void removeSeat(VenueSeat seat) throws DataAccessException;

    VenueSection getSection(int sectionId) throws DataAccessException;

    Integer addSection(VenueInfo venue, String label, String location) throws DataAccessException;
}
