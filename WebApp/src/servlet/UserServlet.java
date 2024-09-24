package servlet;

import SessionUtils.SessionUtils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.constant.Constants;
import servlet.utils.ServletUtils;
import shticell.manager.sheet.SheetManager;
import shticell.manager.user.UserManager;

import java.io.IOException;

@WebServlet(urlPatterns = "/user")
public class UserServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserManager userManager = ServletUtils.GetUserManager(getServletContext());
        String userFromSession = SessionUtils.GetUserName(req);

        if(userFromSession == null) {
            String userName = SessionUtils.GetUserName(req);
            if(userName == null || userName.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }else{
                if(!userManager.addUser(userName)){
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getOutputStream().print("User already exists");
                }else {
                    req.getSession(true).setAttribute(Constants.USER, userManager);
                }
            }
        }
    }
}


