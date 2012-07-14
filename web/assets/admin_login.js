$(function() {
    $('#adm_login_send').click(function() {
       $('#j_username').val("admin/" + $('#adm_login_user').val());
       $('#j_password').val($('#adm_login_pass').val());
    });
});