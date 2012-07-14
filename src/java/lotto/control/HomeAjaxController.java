/* Handle AJAX commands sent from the student home page, such as cancel
 * ticket, withdraw from lottery, etc. */

package lotto.control;

import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lotto.LottoUtils;
import lotto.dao.LotteryEventDao;
import lotto.dao.UserInfoDao;
import lotto.model.LotteryEvent;
import lotto.model.LotteryRegistration;
import lotto.model.LotteryRegistrationId;
import lotto.model.LotterySeat;
import lotto.model.StudentGroup;
import lotto.model.StudentInfo;
import lotto.view.StudentLotteryCombo;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


public class HomeAjaxController extends MultiActionController {
    private LotteryEventDao lotteryEventDao;
    private UserInfoDao userInfoDao;

    // handles "Register for lottery" button, after user has been prompted
    // to join as an individual or as with a group. the user might hit the
    // findGroup call (below) several times if they need to find a group.
    // this method handles all three possibilties for registering
    public ModelAndView lottoRegister(HttpServletRequest request,
                                      HttpServletResponse response,
                                      HttpSession session)
                                      throws ServletException {
        StudentInfo student = LottoUtils.getCurrentStudent(userInfoDao);
        // figure out the lottery to register for
        LotteryEvent lottery = lotteryEventDao.getEvent(parseLotteryId(request));

        // make sure the lottery is actually in the registration phase
        if (!"register".equals(LottoUtils.determineEventPhase(lottery))) {
            throw new ServletException("Lottery not in registration phase");
        }

        // prep a registration object and set it up for an individual
        // registration. if it turns out this is a group registration,
        // that'll get added to it
        LotteryRegistrationId registerId = new LotteryRegistrationId(student.getStudentId(), lottery.getLotteryId());
        LotteryRegistration register = new LotteryRegistration();
        register.setId(registerId);

        String regType = request.getParameter("regtype");
        if ("curgroup".equals(regType)) {
            // it's an existing group, so find the group in question
            String groupStr = request.getParameter("groupid");
            if (groupStr == null) {
                throw new ServletException("Lottery must be a number");
            }
            int groupId = Integer.parseInt(groupStr);
            StudentGroup group = userInfoDao.getStudentGroup(groupId);
            register.setStudentGroup(group);
            lotteryEventDao.addLotteryRegistration(register, lottery, student, group);
        } else if ("newgroup".equals(regType)) {
            String groupName = (String) request.getParameter("groupname");
            if (groupName == null) {
                throw new ServletException("Group name must be specified");
            }
            // create the new group and persist it, then add the registration
            StudentGroup group = new StudentGroup(lottery, student, groupName);
            userInfoDao.addStudentGroup(group, lottery, student);
            lotteryEventDao.addLotteryRegistration(register, lottery, student, group);
        } else {
            lotteryEventDao.addLotteryRegistration(register, lottery, student);
        }

        // set up the object needed for the view. we'll send a page fragment
        // back with a new row for this event saying the user is registered
        StudentLotteryCombo viewInfo = new StudentLotteryCombo();
        viewInfo.setEvent(lottery);
        viewInfo.setPhase("register");
        viewInfo.setRegistered(true);   // this is what changed
        viewInfo.setCanCancel(false);
        viewInfo.setTicketAssigned(false);
        viewInfo.setTicketClaimed(false);
        
        return new ModelAndView("page_fragment", "event", viewInfo);
    }

    // cancel a previous registration
    public ModelAndView lottoWithdraw(HttpServletRequest request,
                                      HttpServletResponse response,
                                      HttpSession session)
                                      throws ServletException {
        StudentInfo student = LottoUtils.getCurrentStudent(userInfoDao);
        // figure out the lottery to withdraw from
        LotteryEvent lottery = lotteryEventDao.getEvent(parseLotteryId(request));

        // make sure the lottery is actually in the registration phase
        if (!"register".equals(LottoUtils.determineEventPhase(lottery))) {
            throw new ServletException("Lottery not in registration phase");
        }

        // fetch the current registration record and, if it exists, delete it
        LotteryRegistration registration = lotteryEventDao.getLotteryRegistration(lottery.getLotteryId(), student.getStudentId());
        if (registration != null) {
            lotteryEventDao.removeLotteryRegistration(registration);
        }

        // set up the object needed for the view. we'll send a page fragment
        // back with a new row for this event saying the user can registered
        StudentLotteryCombo viewInfo = new StudentLotteryCombo();
        viewInfo.setEvent(lottery);
        viewInfo.setPhase("register");
        viewInfo.setRegistered(false);   // this is what changed
        viewInfo.setCanCancel(false);
        viewInfo.setTicketAssigned(false);
        viewInfo.setTicketClaimed(false);

        return new ModelAndView("page_fragment", "event", viewInfo);
    }


    public ModelAndView ticketCancel(HttpServletRequest request,
                                      HttpServletResponse response,
                                      HttpSession session)
                                      throws ServletException {
        StudentInfo student = LottoUtils.getCurrentStudent(userInfoDao);
        // figure out the lottery to withdraw from
        LotteryEvent lottery = lotteryEventDao.getEvent(parseLotteryId(request));
        // make sure the lottery is still allowing ticket cancelation
        if (!LottoUtils.canCancelTicket(lottery)) {
            throw new ServletException("Lottery doesn't allow ticket canceling");
        }

        // to cancel a ticket, need to find any tickets assigned to the user
        // in the seats table. also need to remove them from the group they
        // registered with, so they can't re-claim and steal a ticket from
        // someone else in the group.
        // note that an assigned seat could also be part of a group's allotment,
        // but we specifically don't take change that. that's in case we're
        // still in the claim phase, and canceling will make the distribution
        // of this block a little more efficient
        List<LotterySeat> assigned = lotteryEventDao.getSeatsByStudent(lottery.getLotteryId(), student.getStudentId());
        for (LotterySeat oneSeat: assigned) {
            //student.getLotterySeats().remove(oneSeat);
            oneSeat.setStudentInfo(null);
            oneSeat.setClaimed(Short.valueOf((short) 0));
            lotteryEventDao.saveLotterySeat(oneSeat);
        }
        LotteryRegistration reg = lotteryEventDao.getLotteryRegistration(lottery.getLotteryId(), student.getStudentId());
        if (reg != null &&  reg.getStudentGroup() != null) {
            reg.getStudentGroup().getLotteryRegistrations().remove(reg);
            lotteryEventDao.saveLotteryRegistration(reg);
        }

        // set up the object needed for the view. we'll send a page fragment
        // back with a new row for this event
        StudentLotteryCombo viewInfo = new StudentLotteryCombo();
        viewInfo.setEvent(lottery);
        viewInfo.setPhase(LottoUtils.determineEventPhase(lottery));
        viewInfo.setRegistered(true);
        viewInfo.setCanCancel(true);
        viewInfo.setTicketAssigned(false);   // this is what changed
        viewInfo.setTicketClaimed(false);    // and this

        return new ModelAndView("page_fragment", "event", viewInfo);
    }

    public ModelAndView ticketClaim(HttpServletRequest request,
                                      HttpServletResponse response,
                                      HttpSession session)
                                      throws ServletException {
        StudentInfo student = LottoUtils.getCurrentStudent(userInfoDao);
        // figure out the lottery to claim a ticket in
        LotteryEvent lottery = lotteryEventDao.getEvent(parseLotteryId(request));

        // make sure the lottery is actually in the registration phase
        if (!"claim".equals(LottoUtils.determineEventPhase(lottery))) {
            throw new ServletException("Lottery not in claim phase");
        }

        LotterySeat assigned = lotteryEventDao.assignSeatToStudent(lottery, student);
        ModelAndView mv = new ModelAndView("jsonView");
        if (assigned == null) {
            mv.addObject("error", "No ticket to assign");
        } else {
            mv.addObject("section", assigned.getId().getSection());
            mv.addObject("row", assigned.getId().getRow());
            mv.addObject("seat", assigned.getId().getSeatNo());
        }
        return mv;
    }

    public ModelAndView findGroup(HttpServletRequest request,
                                      HttpServletResponse response,
                                      HttpSession session)
                                      throws ServletException {
        //StudentInfo student = LottoUtils.getCurrentStudent(userInfoDao);
        // figure out the lottery to withdraw from
        LotteryEvent lottery = lotteryEventDao.getEvent(parseLotteryId(request));
        String searchFor = request.getParameter("searchFor");

        List<StudentGroup> groups = userInfoDao.getStudentGroupsByName_Fuzzy(lottery.getLotteryId(), searchFor, 20);

        ModelAndView mv = new ModelAndView("findgroup_results");
        mv.addObject("searchTerm", searchFor);
        mv.addObject("lottery", lottery);
        mv.addObject("groups", groups);
        return mv;
    }

    public void setLotteryEventDao(LotteryEventDao lotteryEventDao) {
        this.lotteryEventDao = lotteryEventDao;
    }

    public void setUserInfoDao(UserInfoDao userInfoDao) {
        this.userInfoDao = userInfoDao;
    }



    private int parseLotteryId(HttpServletRequest request)
                             throws ServletException {
        String lotteryStr = request.getParameter("lottery");
        if (lotteryStr == null) {
            throw new ServletException("Lottery must be a number");
        }
        return Integer.parseInt(lotteryStr);
    }
}