package lotto.model;
// Generated Dec 5, 2009 7:37:40 PM by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;

/**
 * VenueInfo generated by hbm2java
 */
public class VenueInfo  implements java.io.Serializable {


     private Integer venueId;
     private String name;
     private String addrLine1;
     private String addrLine2;
     private String addrCity;
     private String addrState;
     private String addrZip;
     private String phoneNumber;
     private Set venueSections = new HashSet(0);
     private Set lotteryEvents = new HashSet(0);

    public VenueInfo() {
    }

	
    public VenueInfo(String name) {
        this.name = name;
    }
    public VenueInfo(String name, String addrLine1, String addrLine2, String addrCity, String addrState, String addrZip, String phoneNumber, Set venueSections, Set lotteryEvents) {
       this.name = name;
       this.addrLine1 = addrLine1;
       this.addrLine2 = addrLine2;
       this.addrCity = addrCity;
       this.addrState = addrState;
       this.addrZip = addrZip;
       this.phoneNumber = phoneNumber;
       this.venueSections = venueSections;
       this.lotteryEvents = lotteryEvents;
    }
   
    public Integer getVenueId() {
        return this.venueId;
    }
    
    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getAddrLine1() {
        return this.addrLine1;
    }
    
    public void setAddrLine1(String addrLine1) {
        this.addrLine1 = addrLine1;
    }
    public String getAddrLine2() {
        return this.addrLine2;
    }
    
    public void setAddrLine2(String addrLine2) {
        this.addrLine2 = addrLine2;
    }
    public String getAddrCity() {
        return this.addrCity;
    }
    
    public void setAddrCity(String addrCity) {
        this.addrCity = addrCity;
    }
    public String getAddrState() {
        return this.addrState;
    }
    
    public void setAddrState(String addrState) {
        this.addrState = addrState;
    }
    public String getAddrZip() {
        return this.addrZip;
    }
    
    public void setAddrZip(String addrZip) {
        this.addrZip = addrZip;
    }
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public Set getVenueSections() {
        return this.venueSections;
    }
    
    public void setVenueSections(Set venueSections) {
        this.venueSections = venueSections;
    }
    public void addVenueSection(VenueSection venueSection) {
        venueSection.setVenueInfo(this);
        venueSections.add(venueSection);
    }
    public Set getLotteryEvents() {
        return this.lotteryEvents;
    }
    
    public void setLotteryEvents(Set lotteryEvents) {
        this.lotteryEvents = lotteryEvents;
    }
    public void addLotteryEvent(LotteryEvent lotteryEvent) {
        lotteryEvent.setVenueInfo(this);
        lotteryEvents.add(lotteryEvent);
    }


}


