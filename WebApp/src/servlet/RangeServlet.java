package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.RangeDeserializer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.Manager;
import shticell.sheet.range.Range;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet(urlPatterns = "/range")
public class RangeServlet extends HttpServlet {

    private final String MANAGER = "manager";

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if (manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
       }

        String sheetName = req.getParameter("sheetName");
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.registerTypeAdapter(Range.class, new RangeDeserializer());
        Gson gson = builder.create();
        Range range = gson.fromJson(reader, Range.class);
        manager.AddRange(sheetName,range);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if (manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
        }


        String sheetName = req.getParameter("sheetName");
        String rangeName = req.getParameter("rangeName");
        Gson gson  = new Gson();

        resp.getWriter().write(gson.toJson(manager.GetRangeDto(sheetName,rangeName)));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if (manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String sheetName = req.getParameter("sheetName");
        String rangeName = req.getParameter("rangeName");
        Gson gson  = new Gson();

        try {manager.removeRange(sheetName,rangeName);
        } catch (Exception e) { throw new RuntimeException(e);}
    }

}
