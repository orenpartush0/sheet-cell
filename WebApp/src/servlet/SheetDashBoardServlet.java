package servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.Manager;

import java.io.IOException;

@WebServlet("/sheetDashBoard")
public class SheetDashBoardServlet extends HttpServlet {

    private final String MANAGER = "manager";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if( manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
        }

        String userName = req.getParameter("userName");
        Gson gson = new Gson();
        resp.getWriter().write(gson.toJson(manager.GetSheetsDashBoard(userName)));
    }
}
