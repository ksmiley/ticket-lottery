package lotto.validator;

import lotto.model.LotteryEvent;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


public class LotteryEventValidator implements Validator {
    public boolean supports(Class aClass) {
        return LotteryEvent.class.equals(aClass);
    }

    public void validate(Object obj, Errors errors) {
        // make sure all the required fields are filled in
        String required[] = {
            "displayName", "venueInfo", "startTime", "endTime",
            "registerStartTime", "distributionTime", "claimEndTime",
            "cancelEndTime", "saleEndTime"
        };
        for (String check: required) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, check, "field.required", "Required field");
        }
        // if everything else is ok, make sure the dates all fall in a logical order
        if (!errors.hasErrors()) {
            LotteryEvent event = (LotteryEvent) obj;
            
        }
    }
}
