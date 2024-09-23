package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.PutRangeDtoDeserializer;
import deserializer.RemoveAndGetRangeDeserializer;
import dto.PutRangeDto;
import dto.RemoveAndGetRangeDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.Manager;

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

        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.registerTypeAdapter(PutRangeDto.class, new PutRangeDtoDeserializer());
        Gson gson = builder.create();
        PutRangeDto putRangeDto = gson.fromJson(reader, PutRangeDto.class);
        manager.AddRange(putRangeDto.authDto().userName(), putRangeDto.authDto().SheetName(), putRangeDto.range());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if (manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.registerTypeAdapter(RemoveAndGetRangeDto.class, new RemoveAndGetRangeDeserializer());
        Gson gson = builder.create();
        RemoveAndGetRangeDto removeAndGetRange = gson.fromJson(reader, RemoveAndGetRangeDto.class);
        resp.getWriter().write(gson.toJson(manager.GetRangeDto(removeAndGetRange.auth().userName(),
                removeAndGetRange.auth().SheetName(), removeAndGetRange.rangeName())));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if (manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.registerTypeAdapter(RemoveAndGetRangeDto.class, new RemoveAndGetRangeDeserializer());
        Gson gson = builder.create();
        RemoveAndGetRangeDto removeAndGetRange = gson.fromJson(reader, RemoveAndGetRangeDto.class);
        try {manager.removeRange(removeAndGetRange.auth().userName(),
                removeAndGetRange.auth().SheetName(),
                removeAndGetRange.rangeName());}
        catch (Exception e) {throw new RuntimeException(e);}
    }

}
