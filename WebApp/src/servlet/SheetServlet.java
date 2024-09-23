package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.AuthDtoDeserializer;
import deserializer.SheetDtoDeserializer;
import dto.AuthDto;
import dto.SheetDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.Manager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet(urlPatterns = "/sheet")
public class SheetServlet extends HttpServlet {

    private final String MANAGER = "manager";

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if( manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
        }

        String userName = req.getParameter("userName");
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SheetDto.class, new SheetDtoDeserializer());
        Gson gson = builder.create();
        SheetDto sheet = gson.fromJson(reader, SheetDto.class);
        manager.SetSheet(userName,sheet);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if(manager == null) {
            manager = new Manager();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.registerTypeAdapter(AuthDto.class, new AuthDtoDeserializer());
        Gson gson = builder.create();
        AuthDto authDto = gson.fromJson(reader, AuthDto.class);
        resp.getWriter().write(gson.toJson(manager.getSheet(authDto.userName(), authDto.SheetName()),SheetDto.class));
    }

}
