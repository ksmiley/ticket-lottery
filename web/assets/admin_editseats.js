/* Implementation code for the edit venue seating screens (both add and modify).
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

    // wire undo button so reverts the section information to the last value
    // loaded from the server
    $('#undo_section').click(function() {
        if ($.lotto.sectionLoaded) {
            sectionDataLoaded($.lotto.lastSectionLoad);
        }
        return false;
    })
    // wire delete selection button to remove seats. also, if all the seats in
    // a row are removed, it deletes the row
    $('#del_seats').click(function() {
        // examine all the selected items
        $('.ui-selected').each(function() {
            // stash the parent away before deleting
            var parent = $(this).parent();
            $(this).remove();
            // check if the row is now empty. if so, delete the parent row too
            if (parent.find('li').length == 0) {
                parent.parent().remove();
            }
        });
        $.lotto.sectionModified = true;
        return false;
    });

    // wire up Finished Editing button to save any changes and return user
    // to the admin home screen
    $('#finished').click(function() {
        var homeUrl = $.lottoBaseUrl + "/" + "manage/home.htm";
        // if the section hasn't been modified, don't bother saving
        if ($.lotto.sectionLoaded && $.lotto.sectionModified) {
            // send the new data to the server with a saveCommand and wait
            // for a response before redirecting to the home page
            $.post(
                scriptUrl("manage/venue_seats.htm"),
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
    $("#seat_chart")
        .selectable({
            filter: 'li ul li',
            cancel: 'a, div.rowmarker'
        })
        .sortable({
            items: 'li[id]',
            handle: 'div.rowmarker',
            cursor: 'move',
            axis: 'y',
            containment: 'parent',
            delay: 350,
            update: function() {
                // trip this flag so the data will get saved
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
    // set up automatic event binding for the add seats buttons
    $('#seat_chart li a').live('click', addSeatClicked);


    // set up the add section form, which uses a modal dialog
    $('#add_section_form').dialog({
        autoOpen: false,
        width: 250,
        modal: true,
        resizable: false,
        buttons: {
            'Add Section': addSectionClicked,
            'Cancel': function() {
                $(this).dialog('close');
            }
        },
        close: function() {
            $('#add_section_label').val('');
            $('#add_section_location').val('');
        }
    });
    // wire button to open it
    $('#add_section').click(function() {
        $('#add_section_form').dialog('open');
    });


    // set up add rows form
    $('#add_rows_slider').slider({
        min: 1,
        max: 40,
        value: 1,
        slide: function(event, ui) {
            $('#add_rows_num').text(ui.value);
        },
        change: function(event, ui) {
            $('#add_rows_num').text(ui.value);
        }
    });
    $('#add_seats_slider').slider({
        min: 1,
        max: 100,
        value: 10,
        slide: function(event, ui) {
            $('#add_seats_num').text(ui.value);
        },
        change: function(event, ui) {
            $('#add_seats_num').text(ui.value);
        }
    });
    $('#add_rows_form').dialog({
        autoOpen: false,
        width: 250,
        modal: true,
        resizable: false,
        buttons: {
            'Add Rows': addRowsClicked,
            'Cancel': function() {
                $(this).dialog('close');
            }
        },
        open: function() {
            $('#add_rows_name').val(calculateNextRow());
        },
        close: function() {
            $('#add_rows_name').val('');
            $('#add_rows_slider').slider('value', 1);
            $('#add_seats_slider').slider('value', 10);
        }
    });
    // wire button to open it
    $('#add_rows').click(function() {
        $('#add_rows_form').dialog('open');
    });


    // if there are no sections, start with the add section dialog open
    if ($('#section_list li').length == 0) {
        $('#add_section_form').dialog('open');
    }
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
        scriptUrl("manage/venue_seats.htm"),
        command,
        sectionDataLoaded,
        "jsonp"
    );
    return false;
}

function sectionDataLoaded(data) {
    // squirrel away the data, for the undo function
    $.lotto.lastSectionLoad = data;
    $.lotto.highestRowNumber = highestRowNumber();
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
        for (var i = 2; i < this.length; i++) {
            // make a new li with the seat number as text and add it to the <ul>
            $('<li/>').text(this[i]).appendTo(seatList);
        }

        // build the new row, which is held in an <li>
        var nr = $('<li/>');
        nr.attr('id', 'row-' + this[0]);  // give it an id based on its row number
        // add the rowmarker to the beginning with the label from row[1]
        nr.append($('<div class="rowmarker"/>').text(this[1]));
        // make the addseat link at the end and wire it to the add seat routine
        nr.append($('<a>&gt;</a>'));
        // add the seats that were generated above
        nr.append(seatList);

        // finished building row, so add it to the screen
        $('#seat_chart').append(nr);
    });
    // tell Selectable to refresh, since everything changed
    $('#seat_chart').selectable('refresh');
}

function addSeatClicked() {
    var container = $(this).parent().find('ul');
    var newSeatNum = container.find('li').length > 0 ?
        parseInt($(':last', container).text()) + 1 : 1;
    container.append($('<li/>').text(newSeatNum));
    $.lotto.sectionModified = true;
    return false;
}

function addSectionClicked() {
    var newName = $.trim($('#add_section_label').val());
    var newLocation = $.trim($('#add_section_location').val());
    if (newName.length > 0) {
        $.post(scriptUrl("manage/venue_seats.htm"),
            { action: 'addSection', name: newName, location: newLocation },
            function(data) {
                $('<li/>')
                    .attr('id', 'sec-' + data.sectionId)
                    .text(newName)
                    .appendTo($('#section_list'))
                ;
            },
            "jsonp"
        );
        $(this).dialog('close');
    }
}

function addRowsClicked() {
    var rowsToAdd = $('#add_rows_slider').slider('option', 'value');
    var seatsPerRow = $('#add_seats_slider').slider('option', 'value');
    var curRowLabel = $('#add_rows_name').val().toString();
    var curRowNum = parseInt($.lotto.highestRowNumber) + 1;

    for (var i = 0; i < rowsToAdd; i++) {
        var seatList = $('<ul/>');   // new unordered list to hold "seats"
        // generate seats 1..seatsPerRow
        for (var j = 1; j <= seatsPerRow; j++) {
            // make a new li with the seat number as text and add it to the <ul>
            $('<li/>').text(j).appendTo(seatList);
        }

        // build the new row, which is held in an <li>
        var nr = $('<li/>');
        // give it an id based on the current row number, then increment the
        // row number
        nr.attr('id', 'row-' + curRowNum++);
        // add the rowmarker to the beginning with the label from row[1]
        nr.append($('<div class="rowmarker"/>').text(curRowLabel));
        // make the addseat link at the end and wire it to the add seat routine
        nr.append($('<a>&gt;</a>'));
        // add the seats that were generated above
        nr.append(seatList);

        // finished building row, so add it to the screen
        $('#seat_chart').append(nr);

        curRowLabel = magicIncrement(curRowLabel);
    }
    $.lotto.highestRowNumber = curRowNum - 1;
    $.lotto.sectionModified = true;
    $(this).dialog('close');
}

function highestRowNumber() {
    var highest = 0;
    $.each($.lotto.lastSectionLoad.rows, function() {
        if (this[0] > highest) {
            highest = this[0];
        }
    });
    return highest;
}

function calculateNextRow() {
    var labels = new Array();
    $('.rowmarker').each(function() {
        labels.push($(this).text());
    });
    if (labels.length == 0) {
        return "1";
    }
    labels.sort(natcompare);
    return magicIncrement(labels[labels.length-1]);
}

function allRowsAndSeats() {
    var rows = new Array();
    $('#seat_chart > li').each(function() {
        var oneRow = new Array();
        var idStr = $(this).attr('id');
        // get the rowId out of the id attribute and put it at the front of
        // this row's array'
        oneRow.push( parseInt(idStr.substring(idStr.indexOf('-')+1), 10) );
        // next put the label in, which is inside the rowmarker div
        oneRow.push($(this).find(".rowmarker").text());
        // now iterate over the seats and add them
        $(this).find('li').each(function() {
            // store the seats as numbers
            oneRow.push(parseInt($(this).text()));
        });
        // save the finished row
        rows.push(oneRow);
    });
    return rows;
}