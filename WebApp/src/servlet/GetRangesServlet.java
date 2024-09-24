package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.Manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet(urlPatterns = "/getRanges")
public class GetRangesServlet extends HttpServlet {
    private final String MANAGER = "manager";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if (manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
        }
        String sheetName = req.getParameter("sheetName");
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        Gson gson = new Gson();
        resp.getWriter().write(gson.toJson(manager.GetRanges(sheetName)));
    }
}
