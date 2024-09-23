package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.AuthDtoDeserializer;
import dto.AuthDto;
import dto.CellDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.Manager;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet(urlPatterns = "/cell")
public class CellServlet extends HttpServlet {

    private final String MANAGER = "manager";

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if (manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
        }

        Coordinate coordinate = CoordinateFactory.getCoordinate(req.getParameter("coordinate"));
        String newValue = req.getParameter("newValue");
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.registerTypeAdapter(AuthDto.class, new AuthDtoDeserializer());
        Gson gson = builder.create();
        AuthDto authDto = gson.fromJson(reader, AuthDto.class);
        manager.UpdateCellByCoordinate(authDto.userName(),authDto.SheetName(),coordinate,newValue);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if (manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
        }

        Coordinate coordinate = CoordinateFactory.getCoordinate(req.getParameter("coordinate"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.registerTypeAdapter(AuthDto.class, new AuthDtoDeserializer());
        Gson gson = builder.create();
        AuthDto authDto = gson.fromJson(reader, AuthDto.class);
        resp.getWriter().write(gson.toJson(manager.GetCellByCoordinate(authDto.userName(),authDto.SheetName(),coordinate),CellDto.class));
    }
}




