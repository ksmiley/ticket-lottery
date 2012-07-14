/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotto.dao;

import java.sql.SQLException;
import java.util.List;
import lotto.model.AdminInfo;
import lotto.model.LotteryEvent;
import lotto.model.StudentGroup;
import lotto.model.StudentInfo;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author keith
 */
public class HibernateUserInfoDao extends HibernateDaoSupport implements UserInfoDao {

    public AdminInfo getAdminByUsername(String username) throws DataAccessException {
        List<AdminInfo> results = getHibernateTemplate().find("from AdminInfo ai where ai.username = ?", username);
        return results.isEmpty() ? null : results.get(0);
    }

    public AdminInfo getAdmin(int adminId) throws DataAccessException {
        return (AdminInfo) getHibernateTemplate().get(AdminInfo.class, adminId);
    }

    public StudentInfo getStudent(int studentId) throws DataAccessException {
        return (StudentInfo) getHibernateTemplate().get(StudentInfo.class, studentId);
    }

    public StudentInfo getStudentByUsername(String username) throws DataAccessException {
        List<StudentInfo> results = getHibernateTemplate().find("from StudentInfo si where si.username = ?", username);
        return results.isEmpty() ? null : results.get(0);
    }

    public StudentGroup getStudentGroup(int groupId) throws DataAccessException {
        return (StudentGroup) getHibernateTemplate().get(StudentGroup.class, groupId);
    }

    public List<StudentGroup> getStudentGroupsByName_Fuzzy(final int lotteryId, final String name, final int limit) throws DataAccessException {
        // use the database's LIKE comparison to search for substrings, and
        // convert to lowercase first so it's case insensitive. and use
        // the callback interface so the number of rows returned doesn't get
        // out of hand
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery(
                        "from StudentGroup g " +
                        "inner join fetch g.studentInfo where " +
                        "g.lotteryEvent.lotteryId = ? and " +
                        "lower(g.name) like ?"
                );
                q.setParameter(0, lotteryId);
                q.setParameter(1, "%" + name.toLowerCase() + "%");
                if (limit > 0) {
                    q.setMaxResults(limit);
                }
                return q.list();
            }
        });
    }

    public List<StudentGroup> getStudentGroupsByName_Fuzzy(int lotteryId, String name) throws DataAccessException {
        return getStudentGroupsByName_Fuzzy(lotteryId, name, 0);
    }

    public void addStudentGroup(StudentGroup group, LotteryEvent event, StudentInfo student) throws DataAccessException {
        event.addStudentGroup(group);
        student.addStudentGroup(group);
        getHibernateTemplate().save(event);
        getHibernateTemplate().flush();
    }

}