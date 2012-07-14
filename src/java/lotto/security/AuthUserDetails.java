/* Called by Spring Security to look up a given username */

package lotto.security;

import java.util.ArrayList;
import java.util.List;
import lotto.dao.UserInfoDao;
import lotto.model.AdminInfo;
import lotto.model.StudentInfo;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

public class AuthUserDetails implements UserDetailsService {
    private UserInfoDao userInfoDao;

    public void setUserInfoDao(UserInfoDao userInfoDao) {
        this.userInfoDao = userInfoDao;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(1);
        String foundUsername, foundPassword, realName;
        int userId;
        boolean enabled;
        // the admin and student login forms both funnel through this method,
        // with the difference being that the admin form prepends "admin/" to
        // the username before submitting. (yes, it's a hack to get around
        // the complexity of setting Spring Security up for multiple security
        // realms). 
        int separator = username.indexOf('/');
        // looks like this is an admin username
        if (separator > 0 && username.substring(0, separator).equals("admin")) {
            String adminUsername = username.substring(separator + 1);
            AdminInfo admin = userInfoDao.getAdminByUsername(adminUsername);
            if (admin == null) {
                throw new UsernameNotFoundException("Admin user not found");
            }
            foundUsername = admin.getUsername();
            foundPassword = admin.getPassword();
            realName = admin.getName();
            userId = admin.getAdminId();
            enabled = admin.getActive() > 0 ? true : false;
            authorities.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
        } else {
            StudentInfo student = userInfoDao.getStudentByUsername(username);
            if (student == null) {
                throw new UsernameNotFoundException("Student user not found");
            }
            foundUsername = student.getUsername();
            foundPassword = student.getPassword();
            realName = student.getName();
            userId = student.getStudentId();
            enabled = student.getActive() > 0 ? true : false;
            authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
        }

        LottoUser user = new LottoUser(
            foundUsername, foundPassword,
            enabled, true, true, true,
            authorities.toArray(new GrantedAuthority[] {})
        );
        user.setRealName(realName);
        user.setUserId(userId);
        return user;
    }

}
