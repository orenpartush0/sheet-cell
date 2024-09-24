package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.RangeDeserializer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.sheet.SheetManager;
import shticell.sheet.range.Range;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet(urlPatterns = "/range")
public class RangeServlet extends HttpServlet {

    private final String MANAGER = "manager";

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = (SheetManager) getServletContext().getAttribute(MANAGER);
        if (sheetManager == null) {
            sheetManager = new SheetManager();
            getServletContext().setAttribute(MANAGER, sheetManager);
       }

        String sheetName = req.getParameter("sheetName");
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.registerTypeAdapter(Range.class, new RangeDeserializer());
        Gson gson = builder.create();
        Range range = gson.fromJson(reader, Range.class);
        sheetManager.AddRange(sheetName,range);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = (SheetManager) getServletContext().getAttribute(MANAGER);
        if (sheetManager == null) {
            sheetManager = new SheetManager();
            getServletContext().setAttribute(MANAGER, sheetManager);
        }


        String sheetName = req.getParameter("sheetName");
        String rangeName = req.getParameter("rangeName");
        Gson gson  = new Gson();

        resp.getWriter().write(gson.toJson(sheetManager.GetRangeDto(sheetName,rangeName)));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = (SheetManager) getServletContext().getAttribute(MANAGER);
        if (sheetManager == null) {
            sheetManager = new SheetManager();
            getServletContext().setAttribute(MANAGER, sheetManager);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String sheetName = req.getParameter("sheetName");
        String rangeName = req.getParameter("rangeName");
        Gson gson  = new Gson();

        try {
            sheetManager.removeRange(sheetName,rangeName);
        } catch (Exception e) { throw new RuntimeException(e);}
    }

}
