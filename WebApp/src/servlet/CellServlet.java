package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.CoordinateDeserializer;
import deserializer.UpdateCellDtoDeserializer;
import dto.UpdateCellDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.sheet.SheetManager;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet(urlPatterns = "/cell")
public class CellServlet extends HttpServlet {

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
        builder.registerTypeAdapter(UpdateCellDto.class, new UpdateCellDtoDeserializer());
        Gson gson = builder.create();
        UpdateCellDto updateCellDto = gson.fromJson(reader, UpdateCellDto.class);
        try {
            sheetManager.UpdateCellByCoordinate(sheetName,updateCellDto.coordinate(),updateCellDto.newValue());}
        catch (LoopConnectionException e) { throw new RuntimeException(e);}
    }

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
        builder.registerTypeAdapter(Coordinate.class, new CoordinateDeserializer());
        Gson gson = builder.create();
        Coordinate coordinate = gson.fromJson(reader, Coordinate.class);
        resp.getWriter().write(gson.toJson(sheetManager.GetCellByCoordinate(sheetName,coordinate)));
    }
}




