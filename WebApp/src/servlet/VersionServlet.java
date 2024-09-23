package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.AuthDtoDeserializer;
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.registerTypeAdapter(AuthDto.class, new AuthDtoDeserializer());
        Gson gson = builder.create();
        AuthDto authDto = gson.fromJson(reader, AuthDto.class);
        resp.getWriter().write(gson.toJson(manager.GetSheetByVersion(authDto.userName(),authDto.SheetName(),version), SheetDto.class));
    }

}
