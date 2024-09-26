package servlet;


import SessionUtils.SessionUtils;
import constant.Constants;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.sheet.SheetManager;
import shticell.manager.enums.PermissionType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static constant.Constants.ADD_REQUEST;
import static constant.Constants.GSON;

@WebServlet(urlPatterns = ADD_REQUEST)
public class AddRequestServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println(".....");
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String userName = SessionUtils.GetUserName(req);
        String sheetName = req.getParameter(Constants.SHEET);
        if(!sheetManager.getSheetOwner(sheetName).equals(userName)){
            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            PermissionType permissionType = GSON.fromJson(reader, PermissionType.class);
            System.out.println(userName + "request " + permissionType +" permission for sheet: " + sheetName);
            sheetManager.AddRequestPermission(sheetName, userName,permissionType);
        }else{
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}