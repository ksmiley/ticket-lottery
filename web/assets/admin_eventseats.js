/* Implementation code for the edit event seating screens (both add and modify).
 * Requires jQuery.
 */

$(document).ready(function(){
    // setup interface as soon as page loads. mostly involves wiring up buttons
    // and registering event handlers

    // setup a private namespace under the jQuery object to hold some state
    // information (like a flag indicating no section is loaded)
    $.lotto = {
        'sectionLoaded': false
    };

    // hide the editing bar. it'll be shown once a section has been loaded
    $('#edit_command_bar').hide();

    // hide loading spinner and set it to appear when ajax calls are in progress
    $('#loading')
        .hide()
        .ajaxStart(function() {
            $(this).show();
        })
        .ajaxStop(function() {
            $(this).hide();
        })
    ;

    // wire up Finished Editing button to save any changes and return user
    // to the admin home screen
    $('#finished').click(function() {
        var homeUrl = $.lottoBaseUrl + "/" + "manage/home.htm";
        // if the section hasn't been modified, don't bother saving
        if ($.lotto.sectionLoaded && $.lotto.sectionModified) {
            // send the new data to the server with a saveCommand and wait
            // for a response before redirecting to the home page
            $.post(
                scriptUrl("manage/event_seats.htm"),
                {
                    action: "saveSection",
                    saveSectionId: $.lotto.loadedSection,
                    rows: $.toJSON(allRowsAndSeats())
                },
                function() {
                    window.location.replace(homeUrl);
                },
                "jsonp"
            );
        } else {
            window.location.replace(homeUrl);
        }
    });

    // set the seat chart as selectable and sortable
    $.lotto.dragging = false;
    $.lotto.isSelecting = true;
    $("#seat_chart")
        .selectable({
            filter: 'li ul li',
            cancel: 'a, div.rowmarker',
            selecting: function(event, ui) {
                if (!$.lotto.dragging) {
                    $.lotto.dragging = true;
                    if ($(ui.selecting).hasClass('real-selected')) {
                        $.lotto.isSelecting = false;
                    } else {
                        $.lotto.isSelecting = true;
                    }
                }
            },
            stop: function(event, ui) {
                $.lotto.dragging = false;
                if ($.lotto.isSelecting) {
                    $('.ui-selected').addClass('real-selected').removeClass('ui-selected');
                } else {
                    $('.ui-selected').removeClass('real-selected ui-selected');
                }
                $.lotto.sectionModified = true;
            }
        })
        .disableSelection()
    ;

    // make all the list items under the section_list element be clickable,
    // and set their click handler to start a loadAndSave call to the server.
    // use a live event so if we add another section, it automatically
    // gets the event binding
    $('#section_list li').live('click', sectionClicked);
});

function sectionClicked() {
    var idStr = this.id.toString();
    var sectionId = parseInt(idStr.substring(idStr.indexOf('-')+1), 10);
    // make sure we aren't about to reload the same data
    if ($.lotto.sectionLoaded && $.lotto.loadedSection == sectionId) {
        return false;
    }

    // if a section is already loaded and has been changed, it needs to be
    // saved before the new one is loaded. to avoid making two trips to the
    // server, there's a special command that will do both'
    var command = {};
    if ($.lotto.sectionLoaded && $.lotto.sectionModified) {
        command.action = 'saveAndLoadSection';
        command.loadSectionId = sectionId;
        command.saveSectionId = $.lotto.loadedSection;
        command.rows = $.toJSON(allRowsAndSeats());
    } else {
        command.action = 'loadSection';
        command.loadSectionId = sectionId;
    }

    $.lotto.loadedSection = sectionId;  // stash this away for the callback
    // really only need POST for the save case, but won't hurt to always use it
    $.post(
        scriptUrl("manage/event_seats.htm"),
        command,
        sectionDataLoaded,
        "jsonp"
    );
    return false;
}

function sectionDataLoaded(data) {
    // squirrel away the data, for the undo function
    $.lotto.lastSectionLoad = data;
    $.lotto.sectionModified = false;
    // put the new section label in after "Editing section: "
    $('#cur_section').html(data.label);
    // in case this was the first section load, make sure the flag is off
    // and the command bar is shown
    if (!$.lotto.sectionLoaded) {
        $.lotto.sectionLoaded = true;
        $('#edit_command_bar').show();
        $('#instructions').remove();
    }
    // remove the selected class from whichever section in the list was
    // currently open, then add it to the just-loaded section
    $('#section_list_col ul li.selected').removeClass('selected');
    $('#sec-' + $.lotto.loadedSection).addClass('selected');
    // remove all the existing rows and seats from the screen
    $('#seat_chart').empty();
    // loop over all the returned rows. each row is represented as an array
    // where row[0] is the row id, row[1] is the label, and row[3..n] are
    // seats 0..n-2.
    $.each(data.rows, function() {
        var seatList = $('<ul/>');   // new unordered list to hold "seats"
        // loop over the seats in the row and make an <li> for each.
        // row[0] is the row id and row[1] is the label, so start at row[2]
        var rowId = -1, rowLabel = "";
        for (var i = 0; i < this.length; i++) {
            rowId = this[i][0];
            rowLabel = this[i][1];
            var classes = "";
            if (this[i][3] > 0) { classes  = "real-selected included"; }
            if (this[i][4] > 0) { classes += " claimed"; }
            if (this[i][5] > 0) { classes += " paid";    }
            $('<li/>').text(this[i][2]).addClass(classes).appendTo(seatList);
        }

        if (rowId != -1) {
            // build the new row, which is held in an <li>
            var nr = $('<li/>');
            nr.attr('id', 'row-' + rowId);  // give it an id based on its row number
            nr.append($('<div class="rowmarker"/>').text(rowLabel));
            nr.append(seatList);

            // finished building row, so add it to the screen
            $('#seat_chart').append(nr);
        }
    });
    // tell Selectable to refresh, since everything changed
    $('#seat_chart').selectable('refresh');
}

function allRowsAndSeats() {
    var rows = new Array();
    $('#seat_chart > li').each(function() {
        var oneRow = new Array();
        var idStr = $(this).attr('id');
        // get the rowId out of the id attribute and put it at the front of
        // this row's array
        oneRow.push( parseInt(idStr.substring(idStr.indexOf('-')+1), 10) );
        // now iterate over the seats and add them
        $(this).find('li').each(function() {
            // store the seats as numbers
            if ($(this).hasClass("real-selected")) {
                oneRow.push(parseInt($(this).text()));
            }
        });
        // save the finished row
        rows.push(oneRow);
    });
    return rows;
}