package servlet;

import servlet.utils.SessionUtils;
import dto.SheetDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.enums.PermissionType;
import shticell.manager.sheet.SheetManager;
import shticell.sheet.impl.SheetImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static constant.Constants.*;

@WebServlet(urlPatterns = SHEET)
public class SheetServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userName = SessionUtils.GetUserName(req);
        System.out.println(userName + " Try to create new sheet");
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        if(userName != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            SheetDto sheet = GSON.fromJson(reader, SheetDto.class);
            try {
                sheetManager.SetSheet(userName, sheet);
                System.out.println("Sheet named " +sheet.Name() + " created by " + userName);
            }
            catch(Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String sheetName = req.getParameter(SHEET_NAME);
        String userName = SessionUtils.GetUserName(req);
        if(sheetManager.isPermit(sheetName,userName, PermissionType.READER)) {
            int numOfChanges = sheetManager.GetNumOfChanges(sheetName);
            req.getSession(true).setAttribute(VERSION, numOfChanges);
            resp.getWriter().write(GSON.toJson(sheetManager.getSheet(sheetName)));
        }else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
