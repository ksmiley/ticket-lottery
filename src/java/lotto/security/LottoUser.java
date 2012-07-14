/* Overloads the Spring Security object User so that the user id and realname
 * can be stored in the SecurityContext, instead of having to look them up
 * during each request. */

package lotto.security;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.User;

public class LottoUser extends User {
    private int userId;
    private String realName;

    public LottoUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, GrantedAuthority[] authorities) throws IllegalArgumentException {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
