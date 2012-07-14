package lotto.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import lotto.model.LotteryEvent;
import lotto.model.LotteryRegistration;
import lotto.model.LotteryRegistrationId;
import lotto.model.LotterySeat;
import lotto.model.StudentGroup;
import lotto.model.StudentInfo;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author keith
 */
public class HibernateLotteryEventDao extends HibernateDaoSupport implements LotteryEventDao {

    public LotteryEvent getEvent(int lotteryId) throws DataAccessException {
        return (LotteryEvent) getHibernateTemplate().get(LotteryEvent.class, lotteryId);
    }

    public void saveEvent(LotteryEvent event) throws DataAccessException {
        getHibernateTemplate().saveOrUpdate(event);
        getHibernateTemplate().flush();
    }

    public LotteryRegistration getLotteryRegistration(int lotteryId, int studentId) throws DataAccessException {
        LotteryRegistrationId regId = new LotteryRegistrationId(studentId, lotteryId);
        return (LotteryRegistration) getHibernateTemplate().get(LotteryRegistration.class, regId);
    }

    public void saveLotteryRegistration(LotteryRegistration reg) throws DataAccessException {
        getHibernateTemplate().save(reg);
        getHibernateTemplate().flush();
    }

    public void removeLotteryRegistration(LotteryRegistration reg) throws DataAccessException {
        // these look circular, but they're needed to disassociate this object
        // from its "parent" objects
        reg.getLotteryEvent().getLotteryRegistrations().remove(reg);
        reg.getStudentInfo().getLotteryRegistrations().remove(reg);
        if (reg.getStudentGroup() != null) {
            reg.getStudentGroup().getLotteryRegistrations().remove(reg);
        }
        getHibernateTemplate().delete(reg);
        getHibernateTemplate().flush();
    }

    public void addLotteryRegistration(LotteryRegistration register, LotteryEvent event, StudentInfo student) throws DataAccessException {
        event.addLotteryRegistration(register);
        student.addLotteryRegistration(register);
        register.setRegisterTime(new Date());
        getHibernateTemplate().save(register);
        getHibernateTemplate().flush();
    }

    public void addLotteryRegistration(LotteryRegistration register, LotteryEvent event, StudentInfo student, StudentGroup group) throws DataAccessException {
        event.addLotteryRegistration(register);
        student.addLotteryRegistration(register);
        group.addLotteryRegistration(register);
        register.setRegisterTime(new Date());
        getHibernateTemplate().save(register);
        getHibernateTemplate().flush();
    }

    public List<LotteryEvent> getUpcomingEvents() throws DataAccessException {
        return getHibernateTemplate().find("from LotteryEvent e where e.startTime > current_timestamp() order by e.startTime asc");
    }

    public List<LotteryEvent> getUpcomingEvents(final int limit) throws DataAccessException {
        // more verbose than the unlimited version from above because find()
        // doesn't support changing the number of results returned
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery("from LotteryEvent e where e.startTime > current_timestamp() order by e.startTime asc");
                q.setMaxResults(limit);
                return q.list();
            }
        });
    }

    public List<LotteryEvent> getUpcomingEvents_StudentHasRegistered(int studentId) throws DataAccessException {
        return getHibernateTemplate().find(
            "from LotteryEvent e where " +
            "e.startTime > current_timestamp() and " +
            "e.lotteryId in (" +
            "   select r.id.lotteryId from " +
            "   LotteryRegistration r where " +
            "   r.id.studentId = ?" +
            ")" +
            "order by e.startTime asc", studentId
        );
    }

    public List<LotteryEvent> getUpcomingEvents_StudentHasTicket(int studentId) throws DataAccessException {
        return getHibernateTemplate().findByNamedParam(
            "from LotteryEvent e where " +
            "e.id in " +
            "    (select s.id.lotteryId from " +
            "    LotterySeat s where " +
            "        (s.studentInfo is not null and s.studentInfo.id = :studentId) or " +
            "        (s.studentGroup is not null and s.studentGroup in " +
            "            (select r.studentGroup from " +
            "            LotteryRegistration r where " +
            "            r.id.studentId = :studentId and " +
            "            r.id.lotteryId = s.id.lotteryId) " +
            "        ) " +
            "    )", "studentId", studentId
        );
    }

    public Boolean isStudentLotterySeatClaimed(int lotteryId, int studentId) throws DataAccessException {
        Object[] args = {lotteryId, studentId};
        return getHibernateTemplate().find(
            "from LotterySeat s where " +
            "s.id.lotteryId = ? and " +
            "s.studentInfo.studentId = ? and " +
            "s.claimed > 0", args
        ).size() > 0 ? true : false;
    }

    public LotterySeat assignSeatToStudent(LotteryEvent lottery, StudentInfo student) throws DataAccessException {
        HibernateTemplate ht = getHibernateTemplate();
        Object[] args = {lottery.getLotteryId(), student.getStudentId(), lottery.getLotteryId(), student.getStudentId()};
        List<LotterySeat> seats = ht.find(
            "from LotterySeat ls where " +
            "ls.id.lotteryId = ? and ( " +
            "	ls.studentInfo.studentId = ? or ( " +
            "		ls.studentInfo.studentId is null and " +
            "		ls.studentGroup.groupId in ( " +
            "			select lr.studentGroup.groupId from " +
            "			LotteryRegistration lr where " +
            "			lr.id.lotteryId = ? and " +
            "			lr.id.studentId = ? " +
            "		) " +
            "	) " +
            ") " +
            "order by ls.id.row asc, ls.id.seatNo asc", args
        );
        if (seats.size() > 0) {
            LotterySeat oneSeat = seats.get(0);
            student.addLotterySeat(oneSeat);
            oneSeat.setClaimed((short) 1);
            ht.saveOrUpdate(oneSeat);
            ht.flush();
            return oneSeat;
        } else {
            return null;
        }
    }

    public List<LotterySeat> getSeatsByStudent(int lotteryId, int studentId) throws DataAccessException {
        Object[] args = {lotteryId, studentId};
        return getHibernateTemplate().find(
            "from LotterySeat s where " +
            "s.id.lotteryId = ? and " +
            "s.studentInfo.studentId = ?", args
        );
    }

    public List<Object[]> getVenueSeatsByLottery(int lotteryId, int sectionId) throws DataAccessException {
        /* yes, this is the ugliest query I've ever seen, too. but it needed to
         * work and the left outer join just wasn't working out */
        Object[] args = {lotteryId, lotteryId, lotteryId, sectionId};
        return getHibernateTemplate().find(
            "select vs.id.row, vr.label, vs.id.seatNo, (" +
            "   select ls1.id.lotteryId from " +
            "   LotterySeat ls1 " +
            "   where ls1.id.lotteryId = ? and " +
            "   ls1.id.section = vs.id.sectionId and " +
            "   ls1.id.row = vs.id.row and " +
            "   ls1.id.seatNo = vs.id.seatNo" +
            "), (" +
            "   select ls2.claimed from " +
            "   LotterySeat ls2 where " +
            "   ls2.id.lotteryId = ? and " +
            "   ls2.id.section = vs.id.sectionId and " +
            "   ls2.id.row = vs.id.row and " +
            "   ls2.id.seatNo = vs.id.seatNo" +
            "), (" +
            "   select ls3.paid from " +
            "   LotterySeat ls3 where " +
            "   ls3.id.lotteryId = ? and " +
            "   ls3.id.section = vs.id.sectionId and " +
            "   ls3.id.row = vs.id.row and " +
            "   ls3.id.seatNo = vs.id.seatNo" +
            ") from " +
            "VenueSeat as vs join vs.venueRow as vr where " +
            "vs.id.sectionId = ? " +
            "order by vr.position asc, vs.id.seatNo asc", args
        );
    }

    public LotterySeat getLotterySeat(int lotteryId, int sectionId, int rowId, int seatNo) throws DataAccessException {
        Object[] args = {lotteryId, sectionId, rowId, seatNo};
        List<LotterySeat> results = getHibernateTemplate().find(
            "from LotterySeat ls where " +
            "ls.id.lotteryId = ? and " +
            "ls.id.section = ? and " +
            "ls.id.row = ? and " +
            "ls.id.seatNo = ?", args
        );
        if (results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    public void saveLotterySeat(LotterySeat seat) throws DataAccessException {
        getHibernateTemplate().saveOrUpdate(seat);
        getHibernateTemplate().flush();
    }

    public void removeLotterySeatsInSection(final int lotteryId, final int sectionId) throws DataAccessException {
        HibernateTemplate ht = getHibernateTemplate();
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                int resultCount = session.
                    createQuery("delete LotterySeat ls where ls.id.lotteryId = ? and ls.id.section = ?")
                    .setParameter(0, lotteryId)
                    .setParameter(1, sectionId)
                    .executeUpdate();
                return resultCount;
            }
        });
    }
}