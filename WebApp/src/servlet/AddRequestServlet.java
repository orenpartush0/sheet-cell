package servlet;


import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.sheet.SheetManager;
import shticell.manager.enums.PermissionType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet("/addRequest")
public class AddRequestServlet extends HttpServlet {

    private final String MANAGER = "manager";

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = (SheetManager) getServletContext().getAttribute(MANAGER);
        if (sheetManager == null) {
            sheetManager = new SheetManager();
            getServletContext().setAttribute(MANAGER, sheetManager);
        }

        String sheetName = req.getParameter("sheetName");
        String userName = req.getParameter("userName");
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        Gson gson = new Gson();
        PermissionType permissionType = gson.fromJson(reader, PermissionType.class);
        sheetManager.AddRequestPermission(sheetName, userName,permissionType);
    }
}