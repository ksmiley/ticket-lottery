/*
 * The jQuery javascript library requires the server to support JSON-P, even
 * though that's really only needed for accessing remote sites. The spring-json
 * view doesn't yet support JSON-P, per this issue:
 * http://jira.springframework.org/browse/SJS-40
 *
 * This class, written by Raul Raja Martinez, is copied from the comments
 * on that bug entry.
 *
 */

package lotto.view;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.json.JsonView;
import org.springframework.web.servlet.view.json.JsonWriterConfiguratorTemplateRegistry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * JSONP support for spring-json
 */
public class JsonPSupportView extends JsonView {

    private boolean enableJsonpSupport;

    public boolean isEnableJsonpSupport() {
        return enableJsonpSupport;
    }

    public void setEnableJsonpSupport(boolean enableJsonpSupport) {
        this.enableJsonpSupport = enableJsonpSupport;
    }

    @SuppressWarnings("unchecked")
    public void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType(getContentType());
        response.setCharacterEncoding(getEncoding());
        RequestContext rc = getRequestContext(model);
        BindingResult br = getBindingResult(model);
        JsonWriterConfiguratorTemplateRegistry configuratorTemplateRegistry = getConfiguratorTemplateRegistry(request);

        if (hasErrors(rc, br)) {
            getJsonViewCallback().onPopulateErrors(model, rc, br);
            triggerJsonErrors(model, rc, br, request, response);
        } else {
            getJsonViewCallback().onPopulateSuccess(model, rc, br);
        }

        if (getHijackSafe()) {
            response.getWriter().print("/*" + getHijackSafePrefixPostFix());
        }

        String jsonpCallback = request.getParameter("callback");

        if (isEnableJsonpSupport()) {
            response.getWriter().print(jsonpCallback + "(");
        }

        getJsonWriter().convertAndWrite(model, configuratorTemplateRegistry, response.getWriter(), br);

        if (getHijackSafe()) {
            response.getWriter().print(getHijackSafePrefixPostFix() + "*/");
        }

        if (isEnableJsonpSupport()) {
            response.getWriter().print(");");
        }
    }
}
