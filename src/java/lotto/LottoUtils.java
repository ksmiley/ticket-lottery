/* Helpful static functions
 */

package lotto;

import java.util.Date;
import lotto.dao.UserInfoDao;
import lotto.model.AdminInfo;
import lotto.model.LotteryEvent;
import lotto.model.StudentInfo;
import lotto.security.LottoUser;
import org.springframework.security.context.SecurityContextHolder;


public class LottoUtils {
    static public StudentInfo getCurrentStudent(UserInfoDao userInfoDao) {
        LottoUser student = (LottoUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userInfoDao.getStudent(student.getUserId());
    }

    static public AdminInfo getCurrentAdmin(UserInfoDao userInfoDao) {
        LottoUser admin = (LottoUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userInfoDao.getAdmin(admin.getUserId());
    }

    static public String determineEventPhase(LotteryEvent event) {
        Date now = new Date();  // get current time, for determining event phase
        String phase = "";
        if (event.getRegisterStartTime().before(now) && event.getDistributionTime().after(now)) {
            phase = "register";
        } else if (event.getDistributionTime().before(now) && event.getClaimEndTime().after(now)) {
            phase = "claim";
        } else if (event.getClaimEndTime().before(now) && event.getSaleEndTime().after(now)) {
            phase = "sales";
        } else if (event.getSaleEndTime().before(now) && event.getStartTime().after(now)) {
            phase = "closed";
        } else if (event.getRegisterStartTime().after(now)) {
            phase = "notopen";
        }
        return phase;
    }

    static public Boolean canCancelTicket(LotteryEvent event) {
        Date now = new Date();
        return (event.getDistributionTime().before(now) && event.getCancelEndTime().after(now));
    }
}