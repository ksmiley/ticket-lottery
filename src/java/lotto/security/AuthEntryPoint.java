/* Override Spring Security's default entry point. Examines URL to see if
 * a request for an admin or user resource, and redirects to the appropriate
 * login page. */

package lotto.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.security.ui.AuthenticationEntryPoint;
import org.springframework.security.ui.savedrequest.SavedRequest;


public class AuthEntryPoint implements AuthenticationEntryPoint {
    private String adminLoginPage;
    private String userLoginPage;

    public String getAdminLoginPage() {
        return adminLoginPage;
    }

    public void setAdminLoginPage(String adminLoginPage) {
        this.adminLoginPage = adminLoginPage;
    }

    public String getUserLoginPage() {
        return userLoginPage;
    }

    public void setUserLoginPage(String userLoginPage) {
        this.userLoginPage = userLoginPage;
    }

    public void commence(ServletRequest request,
                         ServletResponse response,
                         AuthenticationException authException)
                         throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        SavedRequest origRequest = (SavedRequest) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY);
        String targetUrl = origRequest.getRequestURI();
        if (targetUrl.contains("/manage")) {
            ((HttpServletResponse) response).sendRedirect(session.getServletContext().getContextPath() + getAdminLoginPage());
        } else {
            ((HttpServletResponse) response).sendRedirect(session.getServletContext().getContextPath() + getUserLoginPage());
        }
    }

}
