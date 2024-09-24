package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.SheetDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.Manager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet(urlPatterns = "/version")
public class VersionServlet extends HttpServlet {
    private final String MANAGER = "manager";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if (manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
        }

        int version = Integer.parseInt(req.getParameter("version"));
        String sheetName = req.getParameter("sheetName");
        Gson gson  = new Gson();
        resp.getWriter().write(gson.toJson(manager.GetSheetByVersion(sheetName,version), SheetDto.class));
    }
}
