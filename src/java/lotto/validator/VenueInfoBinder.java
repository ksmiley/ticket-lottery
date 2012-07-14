/* Make LotteryEvent object work as a command bean by loading the appropriate
 * VenueInfo based on the text (int, really) setting */
package lotto.validator;

import java.beans.PropertyEditorSupport;
import lotto.dao.VenueInfoDao;
import lotto.model.VenueInfo;


public class VenueInfoBinder extends PropertyEditorSupport {
    private VenueInfoDao venueInfoDao;

    public VenueInfoBinder(VenueInfoDao venueInfoDao) {
        this.venueInfoDao = venueInfoDao;
    }

    public String getAsText() {
        VenueInfo venue = (VenueInfo) getValue();
        if (venue != null) {
            return venue.getVenueId().toString();
        } else {
            return "";
        }
    }

    public void setAsText(final String text) {
        if (text != null && !text.isEmpty() && !"-".equals(text)) {
            int venueId = Integer.parseInt(text);
            VenueInfo venue = venueInfoDao.getVenue(venueId);
            setValue(venue);
        } else {
            setValue(null);
        }
    }

    public void setVenueInfoDao(VenueInfoDao venueInfoDao) {
        this.venueInfoDao = venueInfoDao;
    }
}
