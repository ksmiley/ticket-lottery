/*
 * Validate user input when adding or modifying a venue
 */

package lotto.validator;

import lotto.model.VenueInfo;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class VenueInfoValidator implements Validator {
    public boolean supports(Class aClass) {
        return VenueInfo.class.equals(aClass);
    }

    public void validate(Object obj, Errors errors) {
        /* currently the only validation needed is to make sure a venue name is entered */
        //VenueInfo venue = (VenueInfo) obj;  // not needed if only using ValidationUtils
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required", "Required field");
    }
}