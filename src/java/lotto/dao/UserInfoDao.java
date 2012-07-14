/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotto.dao;

import java.util.List;
import lotto.model.AdminInfo;
import lotto.model.LotteryEvent;
import lotto.model.StudentGroup;
import lotto.model.StudentInfo;
import org.springframework.dao.DataAccessException;

/**
 *
 * @author keith
 */
public interface UserInfoDao {
    public StudentInfo getStudent(int studentId) throws DataAccessException;

    public StudentInfo getStudentByUsername(String username) throws DataAccessException;

    public StudentGroup getStudentGroup(int groupId) throws DataAccessException;

    public List<StudentGroup> getStudentGroupsByName_Fuzzy(int lotteryId, String name, int limit) throws DataAccessException;

    public List<StudentGroup> getStudentGroupsByName_Fuzzy(int lotteryId, String name) throws DataAccessException;

    public void addStudentGroup(StudentGroup group, LotteryEvent event, StudentInfo student) throws DataAccessException;

    public AdminInfo getAdmin(int adminId) throws DataAccessException;

    public AdminInfo getAdminByUsername(String username) throws DataAccessException;
}
