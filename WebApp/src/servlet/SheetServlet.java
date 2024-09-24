package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.SheetDtoDeserializer;
import dto.SheetDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.sheet.SheetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet(urlPatterns = "/sheet")
public class SheetServlet extends HttpServlet {

    private final String MANAGER = "manager";

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = (SheetManager) getServletContext().getAttribute(MANAGER);
        if( sheetManager == null) {
            sheetManager = new SheetManager();
            getServletContext().setAttribute(MANAGER, sheetManager);
        }

        String userName = req.getParameter("userName");
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SheetDto.class, new SheetDtoDeserializer());
        Gson gson = builder.create();
        SheetDto sheet = gson.fromJson(reader, SheetDto.class);
        sheetManager.SetSheet(userName,sheet);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = (SheetManager) getServletContext().getAttribute(MANAGER);
        if(sheetManager == null) {
            sheetManager = new SheetManager();
        }

        String sheetName = req.getParameter("sheetName");
        Gson gson  = new Gson();
        resp.getWriter().write(gson.toJson(sheetManager.getSheet(sheetName)));
    }
}
