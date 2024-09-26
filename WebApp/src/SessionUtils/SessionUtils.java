package SessionUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import constant.Constants;

public class SessionUtils {
    public static String GetUserName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.USER_NAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }
}
