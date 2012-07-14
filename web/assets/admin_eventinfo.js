/* Methods to make the add/modify event info screen work. Almost exclusively
 * related to date handling and display. The server wants a date and time
 * together in a field, but the input is easier if they're spread across four.
 * This code is the bridge between the two. */
$(function() {
    /* start by sweeping over all the hidden input fields within the
     * datetime_fields div and converting them into usable input. each should
     * have a label associated with it, so also have to re-associate the labels
     * to the new input boxes */
    $(".datetime_fields label").each(function() {
        var idStr = $(this).attr('for').toString();
        // select the label, build a row around it, and re-associate the label
        // to the newly created input box
        $(this)
            .wrap('<div class="datetime_row"></div>')
            .after(getDatetimeRowHtml(idStr))
            .attr('for', idStr + '_date')
        ;
        // if the hidden field has a date in it, copy it to the new fields
        var dateStr = $('#' + idStr).val();
        if (dateStr.length > 0) {
            var hourStart = dateStr.indexOf(' ');
            var colon = dateStr.indexOf(':');
            var date = dateStr.substring(0, hourStart)
            var hour = parseInt(dateStr.substring(hourStart + 1, colon), 10);
            var minutes = parseInt(dateStr.substring(colon + 1), 10);
            var ampm = 0;
            if (hour > 11) {
                hour -= 12;
                ampm = 12;
            }
            $('#' + idStr + '_date').val(date);
            $('#' + idStr + '_hour').val(hour);
            $('#' + idStr + '_minutes').val(minutes);
            $('#' + idStr + '_ampm').val(ampm);
        }
    });
    $('#eventVenue option[value="-"]').css({
        'font-size': '12px',
        'color': '#555',
        'text-align': 'center'
    });
    // make all the newly created date fields into datepickers
    $('.datetime_row .date').datepicker({
        showAnim: 'fadeIn',
        duration: 'fast'
    });
    $('#startTime_date').datepicker(
        'option', 'onClose',
        function() {
            var date = $(this).datepicker('getDate');
            if (
                date != null &&
                $('.hasDatepicker')
                    .filter('input[value*="/"]')
                    .not('#startTime_date')
                    .size() == 0
            ) {
                var oneDay = 24 * 3600 * 1000;
                $('#endTime_date').datepicker('setDate', date);
                $('#registerStartTime_date').datepicker('setDate', new Date(date - 12*oneDay));
                $('#distributionTime_date').datepicker('setDate', new Date(date - 10*oneDay));
                $('#claimEndTime_date').datepicker('setDate', new Date(date - 7*oneDay));
                $('#cancelEndTime_date').datepicker('setDate', new Date(date - 2*oneDay));
                $('#saleEndTime_date').datepicker('setDate', new Date(date - 3*oneDay));
            }
        }
    );

    // since the user input fields are fake, need to intercept the form submit
    // and copy the entered data into the hidden fields
    $('#form_actions input[type="submit"]').click(function() {
        $(".datetime_fields input[type='hidden']").each(function() {
            var idStr = $(this).attr('id').toString();
            // the AM/PM field has values 0/12 and hours is 0-11, so the two
            // can be added to get 24-hour time
            var hours = parseInt($('#' + idStr + '_hour').val()) + parseInt($('#' + idStr + '_ampm').val());
            $(this).val(
                $('#' + idStr + "_date").val() + " " + hours + ":" + $('#' + idStr + '_minute').val()
            );
        });
    });
});

function getDatetimeRowHtml(idStr) {
    return '<input type="text" class="date" name="' + idStr + '_date" id="' + idStr + '_date"/>' +
    '<select name="' + idStr + '_hour" id="' + idStr + '_hour" class="hour">' +
    '<option value="0">12</option>' +
    '<option value="1">1</option>' +
    '<option value="2">2</option>' +
    '<option value="3">3</option>' +
    '<option value="4">4</option>' +
    '<option value="5">5</option>' +
    '<option value="6">6</option>' +
    '<option value="7">7</option>' +
    '<option value="8">8</option>' +
    '<option value="9">9</option>' +
    '<option value="10">10</option>' +
    '<option value="11">11</option>' +
    '</select>' +
    ':' +
    '<select name="' + idStr + '_minute" id="' + idStr + '_minute" class="minutes">' +
    '<option value="0">00</option>' +
    '<option value="5">05</option>' +
    '<option value="10">10</option>' +
    '<option value="15">15</option>' +
    '<option value="20">20</option>' +
    '<option value="25">25</option>' +
    '<option value="30">30</option>' +
    '<option value="35">35</option>' +
    '<option value="40">40</option>' +
    '<option value="45">45</option>' +
    '<option value="50">50</option>' +
    '<option value="55">55</option>' +
    '</select>' +
    '<select name="' + idStr + '_ampm" id="' + idStr + '_ampm" class="ampm">' +
    '<option value="0">AM</option>' +
    '<option value="12">PM</option>' +
    '</select>';
}