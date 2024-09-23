package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.Manager;
import java.io.IOException;

@WebServlet(urlPatterns = "/user")
public class UserServlet extends HttpServlet {

    private final String MANAGER = "manager";

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if (manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
        }

        String userName = req.getParameter("userName");
        manager.addUser(userName);
    }
}


