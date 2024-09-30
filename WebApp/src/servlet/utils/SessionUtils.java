package servlet.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import static constant.Constants.*;

public class SessionUtils {
    public static String GetUserName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(USER_NAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static Integer GetSheetVersionUserPossess(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(VERSION) : null;
        return sessionAttribute != null ? (Integer) sessionAttribute : null;
    }


}
