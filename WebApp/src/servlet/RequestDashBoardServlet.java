package servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.sheet.SheetManager;

import java.io.IOException;

@WebServlet("/requestDashBoard")
public class RequestDashBoardServlet extends HttpServlet {

    private final String MANAGER = "manager";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SheetManager sheetManager = (SheetManager) getServletContext().getAttribute(MANAGER);
        if( sheetManager == null) {
            sheetManager = new SheetManager();
            getServletContext().setAttribute(MANAGER, sheetManager);
        }

        String sheetName = req.getParameter("sheetName");
        Gson gson = new Gson();
        resp.getWriter().write(gson.toJson(sheetManager.GetRequestDashBoard(sheetName)));
    }
}