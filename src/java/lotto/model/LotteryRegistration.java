package lotto.model;
// Generated Dec 5, 2009 7:37:40 PM by Hibernate Tools 3.2.1.GA


import java.util.Date;

/**
 * LotteryRegistration generated by hbm2java
 */
public class LotteryRegistration  implements java.io.Serializable {


     private LotteryRegistrationId id;
     private StudentGroup studentGroup;
     private LotteryEvent lotteryEvent;
     private StudentInfo studentInfo;
     private Date registerTime;

    public LotteryRegistration() {
    }

	
    public LotteryRegistration(LotteryRegistrationId id, LotteryEvent lotteryEvent, StudentInfo studentInfo, Date registerTime) {
        this.id = id;
        this.lotteryEvent = lotteryEvent;
        this.studentInfo = studentInfo;
        this.registerTime = registerTime;
    }
    public LotteryRegistration(LotteryRegistrationId id, StudentGroup studentGroup, LotteryEvent lotteryEvent, StudentInfo studentInfo, Date registerTime) {
       this.id = id;
       this.studentGroup = studentGroup;
       this.lotteryEvent = lotteryEvent;
       this.studentInfo = studentInfo;
       this.registerTime = registerTime;
    }
   
    public LotteryRegistrationId getId() {
        return this.id;
    }
    
    public void setId(LotteryRegistrationId id) {
        this.id = id;
    }
    public StudentGroup getStudentGroup() {
        return this.studentGroup;
    }
    
    public void setStudentGroup(StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
    }
    public LotteryEvent getLotteryEvent() {
        return this.lotteryEvent;
    }
    
    public void setLotteryEvent(LotteryEvent lotteryEvent) {
        this.lotteryEvent = lotteryEvent;
    }
    public StudentInfo getStudentInfo() {
        return this.studentInfo;
    }
    
    public void setStudentInfo(StudentInfo studentInfo) {
        this.studentInfo = studentInfo;
    }
    public Date getRegisterTime() {
        return this.registerTime;
    }
    
    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }




}


