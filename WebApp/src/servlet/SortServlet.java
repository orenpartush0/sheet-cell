package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.SortDtoDeserializer;
import dto.SortDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.sheet.SheetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet(urlPatterns = "/sort")
public class SortServlet extends HttpServlet {
    private final String MANAGER = "manager";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = (SheetManager) getServletContext().getAttribute(MANAGER);
        if (sheetManager == null) {
            sheetManager = new SheetManager();
            getServletContext().setAttribute(MANAGER, sheetManager);
        }

        String sheetName = req.getParameter("sheetName");
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.registerTypeAdapter(SortDto.class, new SortDtoDeserializer());
        Gson gson = builder.create();
        SortDto sortDto = gson.fromJson(reader, SortDto.class);
        resp.getWriter().write(gson.toJson(sheetManager.applySort(sheetName,sortDto.cols(),sortDto.range())));
    }
}
