package lotto.dao;

import java.util.List;
import lotto.model.VenueInfo;
import lotto.model.VenueRow;
import lotto.model.VenueSeat;
import lotto.model.VenueSeatId;
import lotto.model.VenueSection;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


public class HibernateVenueInfoDao extends HibernateDaoSupport implements VenueInfoDao {

    public List<VenueInfo> getAllVenues() throws DataAccessException {
        return getHibernateTemplate().find("from VenueInfo vi order by vi.name asc");
    }

    public VenueInfo getVenue(int venueId) throws DataAccessException {
        return (VenueInfo) getHibernateTemplate().get(VenueInfo.class, venueId);
    }

    public void saveVenue(VenueInfo venue) throws DataAccessException {
        getHibernateTemplate().saveOrUpdate(venue);
        getHibernateTemplate().flush();
    }

    public List<VenueRow> getRowsInSection(int sectionId) throws DataAccessException {
        return getHibernateTemplate().find("from VenueRow vr where vr.id.sectionId = ? order by position asc", sectionId);
    }

    public List<VenueRow> getRowsWithSeats(int sectionId) throws DataAccessException {
        // using Critieria instead of HQL on this one so the FetchMode can be
        // adjusted. otherwise it'll lazy load the seats, and this is hopefully
        // more efficient
        DetachedCriteria q = DetachedCriteria.forClass(lotto.model.VenueRow.class)
                .add(Restrictions.eq("id.sectionId", sectionId))
                .addOrder(Order.asc("position"))
                .addOrder(Order.asc("id.row"))
                .setFetchMode("venueSeats", FetchMode.SELECT);
        return getHibernateTemplate().findByCriteria(q);
    }

    public void addRow(VenueRow row, VenueSection section) throws DataAccessException {
        section.addVenueRow(row);
        getHibernateTemplate().save(row);
        getHibernateTemplate().flush();
    }

    public void removeRow(VenueRow row) throws DataAccessException {
        // delete link from section
        VenueSection parent = row.getVenueSection();
        parent.getVenueRows().remove(row);
        // database will take care of deleting seats
        getHibernateTemplate().delete(row);
        getHibernateTemplate().flush();
    }

    public void saveRow(VenueRow row) throws DataAccessException {
        getHibernateTemplate().update(row);
        getHibernateTemplate().flush();
    }

    public void addSeat(VenueSeat seat, VenueRow row) throws DataAccessException {
        row.addVenueSeat(seat);
        getHibernateTemplate().save(seat);
        getHibernateTemplate().flush();
    }

    // convenience method that will create the seat object and fill it in
    // for the given row, then pass it and row to the other addSeat()
    public void addSeat(int seatNo, VenueRow row) throws DataAccessException {
        VenueSeatId createSeatId = new VenueSeatId();
        createSeatId.setSectionId(row.getVenueSection().getSectionId());
        createSeatId.setRow(row.getId().getRow());
        createSeatId.setSeatNo(seatNo);

        VenueSeat createSeat = new VenueSeat();
        createSeat.setId(createSeatId);

        addSeat(createSeat, row);
    }

    public void removeSeat(VenueSeat seat) throws DataAccessException {
        // delete the link from the parent, then delete the actual seat
        VenueRow parent = seat.getVenueRow();
        parent.getVenueSeats().remove(seat);
        getHibernateTemplate().delete(seat);
        getHibernateTemplate().flush();
    }

    public VenueSection getSection(int sectionId) throws DataAccessException {
        return (VenueSection) getHibernateTemplate().get(VenueSection.class, sectionId);
    }

    public Integer addSection(VenueInfo venue, String label, String location) throws DataAccessException {
        VenueSection newSection = new VenueSection();
        newSection.setLabel(label);
        newSection.setLocation(location);
        venue.addVenueSection(newSection);
        int newId = (Integer) getHibernateTemplate().save(newSection);
        getHibernateTemplate().flush();
        return newId;
    }
}