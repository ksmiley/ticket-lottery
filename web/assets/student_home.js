$(document).ready(function() {
    // set up a namespace to store some state variables
    $.lotto = {};

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

    // set up dialogs
    // first, the confirm cancel ticket confirmation dialog
    $('#confirm_cancel_ticket').dialog({
        autoOpen: false,
        width: 300,
        modal: true,
        resizable: false,
        buttons: {
            'Cancel ticket': doCancelTicket,
            'Don\'t cancel': function() {
                $(this).dialog('close');
            }
        }
    });
    // now wire up buttons to open the dialog. they'll need to populate a
    // state variable so the dialog knows which event was clicked
    $('.ticket_cancel').live('click', function() {
       var row = $(this).parent().parent().parent();
       // save the event id in case the user confirms, the callback will need it
       $.lotto.curEvent = parseId(row.attr('id'));
       // insert the event name into the dialog
       $('#confirm_cancel_ticket .event_name').text(row.find('h2').text());
       $('#confirm_cancel_ticket').dialog('open');
    });

    // the withdraw from lottery confirmation dialog
    $('#confirm_lottery_withdraw').dialog({
        autoOpen: false,
        width: 300,
        modal: true,
        resizable: false,
        buttons: {
            'Withdraw from lottery': doWithdraw,
            'Don\'t withdraw': function() {
                $(this).dialog('close');
            }
        }
    });
    // wire up buttons to open the dialog. they'll need to populate a
    // state variable so the dialog knows which event was clicked
    $('.lotto_withdraw').live('click', function() {
       var row = $(this).parent().parent().parent();
       // save the event id in case the user confirms, the callback will need it
       $.lotto.curEvent = parseId(row.attr('id'));
       // insert the event name into the dialog
       $('#confirm_lottery_withdraw .event_name').text(row.find('h2').text());
       $('#confirm_lottery_withdraw').dialog('open');
    });

    // the registration dialog. this one's complex, since it has several
    // clickable elements inside of it, and it in turn can open another modal
    // dialog (to search for groups)
    $('#lotto_register').dialog({
        autoOpen: false,
        width: 300,
        modal: true,
        resizable: false,
        buttons: {
            'Register': doRegisterLotto,
            'Cancel': function() {
                $(this).dialog('close');
            }
        },
        close: function() {
            $('#newgroup_name').val('');
            $('#curgroup_name').text('');
            $('#lotto_register input').removeAttr('checked');
            $('#newgroup_entry, #curgroup_entry').hide();
            $.lotto.lastRegisterSelect = null;
        }
    });
    // pre-hide a few elements within those dialogs
    $('#newgroup_entry, #curgroup_entry').hide();
    $('#find_groups_results').hide();
    // wire the radio buttons inside the registration dialog
    $('#lotto_register_indiv').click(function() {
        // make sure the blocks that go with the other two are hidden
        $('#newgroup_entry, #curgroup_entry').hide();
        $.lotto.lastRegisterSelect = "indiv";
    });
    $('#lotto_register_curgroup').click(function() {
        $('#newgroup_entry').hide();
        $('#curgroup_entry').show();
        if ($.trim($('#curgroup_name').text().toString()).length == 0) {
            $('#find_groups').dialog('open');
        }
    });
    $('#lotto_register_newgroup').click(function() {
        $('#newgroup_entry').show();
        $('#curgroup_entry').hide();
        $('#newgroup_name').focus();
        $.lotto.lastRegisterSelect = "newgroup";
    });
    $('#curgroup_change').click(function() {
        $('#find_groups').dialog('open');
    });

    // wire up buttons to open the dialog. they'll need to populate a
    // state variable so the dialog knows which event was clicked
    $('.lotto_register').live('click', function() {
       var row = $(this).parent().parent();
       // save the event id in case the user confirms, the callback will need it
       $.lotto.curEvent = parseId(row.attr('id'));
       // insert the event name into the dialog
       $('#lotto_register .event_name').text(row.find('h2').text());
       $('#lotto_register input').removeAttr('checked');
       $('#lotto_register').dialog('open');
    });

    // set up the Find Group dialog, which is called from the register dialog
    $('#find_groups').dialog({
        autoOpen: false,
        width: 400,
        minWidth: 400,
        minHeight: 155,
        modal: true,
        resizable: true,
        zIndex: 100,
        buttons: {
            'Cancel': function() {
                $(this).dialog('close');
            }
        },
        close: function() {
            // trim the whitespace from the current group name and see if there's
            // anything left. if not, switch the selection to one of the other
            // buttons based on which one was selected last
            $('#find_groups_searchfor').val('');
            $('#find_groups_results').empty().hide();
            $(this)
                .dialog('option', 'minHeight', 155)
                .dialog('option', 'height', 155)
            ;
            if ($.trim($('#curgroup_name').text().toString()).length == 0) {
                var revertTo = $.lotto.lastRegisterSelect
                    ? $.lotto.lastRegisterSelect : 'indiv';
                $('#lotto_register_' + revertTo).click();
            }
        },
        resize: function() {
            $('#find_groups_results').height(
                $(this).height() - $('form', this).height() - 12
            );
        }
    });
    // wire the search button in the find groups dialog to actually search
    $('#find_groups_dosearch').click(function() {
        // make sure there's something to search for
        var searchTerm = $('#find_groups_searchfor').val().toString();
        if ($.trim(searchTerm).length == 0) {
            return false;
        }
        $('#find_groups_results').load(
            scriptUrl('home_ajax.htm'),
            {
                action: 'findGroup',
                lottery: $.lotto.curEvent,
                searchFor: searchTerm
            },
            groupFindResultsLoaded
        );
        return false;
    });


    $('#claim_ticket').dialog({
        autoOpen: false,
        width: 300,
        modal: true,
        resiazable: false,
        buttons: {
            'Pay for ticket': function() {
                $(this).dialog('close');
            },
            'Cancel ticket': function() {
                $(this).dialog('close');
                $('#confirm_cancel_ticket').dialog('open');
            }
        }
    });
    $('.ticket_claim').live('click', function() {
       var row = $(this).parent().parent();
       // save the event id in case the user confirms, the callback will need it
       $.lotto.curEvent = parseId(row.attr('id'));
       // insert the event name into the dialog
       $('#claim_ticket .event_name').text(row.find('h2').text());
       $('#confirm_cancel_ticket .event_name').text(row.find('h2').text());
       //$('#claim_ticket').dialog('open');

       $.post(
           scriptUrl("home_ajax.htm"),
           {
               action: 'ticketClaim',
               lottery: $.lotto.curEvent
           },
           function(data) {
               $('#claim_ticket .section').text(data.section);
               $('#claim_ticket .row').text(data.row);
               $('#claim_ticket .seat').text(data.seat);
               $('#claim_ticket').dialog('open');
           },
           'jsonp'
       );
    });
});

function groupFindResultsLoaded(data, status) {
    $('#find_groups_results').show();
    $('#find_groups').dialog('option', 'minHeight', 325);
    $('#find_groups').resize();
    // wire all the Join buttons up to close the dialog and set the current
    // group to the given value
    $('#find_groups #group_list li .join_group').click(function() {
        var row = $(this).parent();
        $.lotto.selectedGroup = parseId(row.attr('id'));
        $('#curgroup_name').text(row.find('h2').text().toString());
        $('#find_groups').dialog('close');
    });
}

// after user confirms canceling a ticket, actually do it
function doCancelTicket() {
    $('#event-' + $.lotto.curEvent).load(
        scriptUrl("home_ajax.htm"),
        {
            action: 'ticketCancel',
            lottery: $.lotto.curEvent
        },
        function() {
            $('#confirm_cancel_ticket').dialog('close');
        }
    );
}

// after user confirms withdrawing from lottery, actually do it
function doWithdraw() {
    $('#event-' + $.lotto.curEvent).load(
        scriptUrl("home_ajax.htm"),
        {
            action: 'lottoWithdraw',
            lottery: $.lotto.curEvent
        },
        function() {
            $('#confirm_lottery_withdraw').dialog('close');
        }
    );
}

function doRegisterLotto() {
    var command = { 
        action: 'lottoRegister',
        lottery: $.lotto.curEvent
    };
    if ($('#lotto_register_curgroup').attr('checked')) {
        command.regtype = 'curgroup';
        command.groupid = $.lotto.selectedGroup;
    } else if ($('#lotto_register_newgroup').attr('checked')) {
        var newName = $.trim($('#newgroup_name').val());
        if (newName.length == 0) {
            return false;
        }
        command.regtype = 'newgroup';
        command.groupname = newName;
    } else {
        command.regtype = 'indiv';
    }
    $('#event-' + $.lotto.curEvent).load(
        scriptUrl("home_ajax.htm"),
        command,
        function() {
            $('#lotto_register').dialog('close');
        }
    );
}