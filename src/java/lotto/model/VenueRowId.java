package lotto.model;
// Generated Dec 5, 2009 7:37:40 PM by Hibernate Tools 3.2.1.GA



/**
 * VenueRowId generated by hbm2java
 */
public class VenueRowId  implements java.io.Serializable {


     private int sectionId;
     private int row;

    public VenueRowId() {
    }

    public VenueRowId(int sectionId, int row) {
       this.sectionId = sectionId;
       this.row = row;
    }
   
    public int getSectionId() {
        return this.sectionId;
    }
    
    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }
    public int getRow() {
        return this.row;
    }
    
    public void setRow(int row) {
        this.row = row;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof VenueRowId) ) return false;
		 VenueRowId castOther = ( VenueRowId ) other; 
         
		 return (this.getSectionId()==castOther.getSectionId())
 && (this.getRow()==castOther.getRow());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getSectionId();
         result = 37 * result + this.getRow();
         return result;
   }   


}

