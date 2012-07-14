/* Simple controller that just maps a URL to a view. Doesn't do any
 * other processing. */
package lotto.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class StaticController extends AbstractController {

    protected ModelAndView handleRequestInternal(
            HttpServletRequest request, 
            HttpServletResponse response) throws Exception {
        String fullPath = request.getRequestURI();
        String filename = fullPath.substring(fullPath.lastIndexOf('/') + 1, fullPath.lastIndexOf('.'));
        ModelAndView mv = new ModelAndView();
        if ("about".equals(filename)) {
            mv.setViewName("about");
        } else if ("contact".equals(filename)) {
            mv.setViewName("contact");
        } else if ("help".equals(filename)) {
            mv.setViewName("help");
        } else if ("login".equals(filename)) {
            // another hack to work around Spring Security. if the user hits the
            // admin page and enters an incorrect login, Spring will send them
            // to /login.htm (the user login page). so this detects if it's
            // an attempted admin login and shows the admin screen
            String loginErr = (String) request.getParameter("login_error");
            String username = null;
            HttpSession session = request.getSession();
            if (session != null) {
                username = (String) session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY);
            }
            if (loginErr != null && username != null && username.contains("admin/")) {
                mv.setViewName("admin/login");
            } else {
                mv.setViewName("login");
            }
        } else if ("adminlogin".equals(filename)) {
            mv.setViewName("admin/login");
        } else {
            //mv.setViewName("default");
            mv.setViewName("login");
        }
        return mv;
    }

}