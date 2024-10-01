package servlet;

import servlet.utils.SessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.user.UserManager;

import java.io.IOException;

import static constant.Constants.USER;
import static constant.Constants.USER_NAME;

@WebServlet(urlPatterns = USER)
public class UserServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserManager userManager = ServletUtils.GetUserManager(getServletContext());
        String userFromSession = SessionUtils.GetUserName(req);
        if(userFromSession == null) {
            String userName = req.getParameter(USER_NAME);
            if(userName == null || userName.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }else{
                synchronized (this) {
                    if (!userManager.addUser(userName)) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    } else {
                        req.getSession(true).setAttribute(USER_NAME, userName);
                    }
                }
            }
        }

        System.out.println(SessionUtils.GetUserName(req) + " successfully logged in");
    }
}


