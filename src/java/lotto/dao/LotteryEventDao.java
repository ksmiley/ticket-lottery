/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotto.dao;

import java.util.List;
import lotto.model.LotteryEvent;
import lotto.model.LotteryRegistration;
import lotto.model.LotterySeat;
import lotto.model.StudentGroup;
import lotto.model.StudentInfo;
import org.springframework.dao.DataAccessException;

/**
 *
 * @author keith
 */
public interface LotteryEventDao {

    LotteryEvent getEvent(int lotteryId) throws DataAccessException;

    void saveEvent(LotteryEvent event) throws DataAccessException;

    LotteryRegistration getLotteryRegistration(int lotteryId, int studentId) throws DataAccessException;

    void saveLotteryRegistration(LotteryRegistration reg) throws DataAccessException;

    void removeLotteryRegistration(LotteryRegistration reg) throws DataAccessException;

    void addLotteryRegistration(LotteryRegistration register, LotteryEvent event, StudentInfo student) throws DataAccessException;

    void addLotteryRegistration(LotteryRegistration register, LotteryEvent event, StudentInfo student, StudentGroup group) throws DataAccessException;

    List<LotteryEvent> getUpcomingEvents() throws DataAccessException;

    List<LotteryEvent> getUpcomingEvents(int limit) throws DataAccessException;

    List<LotteryEvent> getUpcomingEvents_StudentHasRegistered(int studentId) throws DataAccessException;

    List<LotteryEvent> getUpcomingEvents_StudentHasTicket(int studentId) throws DataAccessException;

    Boolean isStudentLotterySeatClaimed(int lotteryId, int studentId) throws DataAccessException;

    LotterySeat assignSeatToStudent(LotteryEvent lottery, StudentInfo student) throws DataAccessException;

    List<LotterySeat> getSeatsByStudent(int lotteryId, int studentId) throws DataAccessException;

    List<Object[]> getVenueSeatsByLottery(int lotteryId, int sectionId) throws DataAccessException;

    LotterySeat getLotterySeat(int lotteryId, int sectionId, int rowId, int seatNo) throws DataAccessException;

    void saveLotterySeat(LotterySeat seat) throws DataAccessException;

    void removeLotterySeatsInSection(int lotteryId, int sectionId) throws DataAccessException;
}
