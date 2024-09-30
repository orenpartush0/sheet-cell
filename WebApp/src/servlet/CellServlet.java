package servlet;

import servlet.utils.SessionUtils;
import constant.Constants;
import dto.UpdateCellDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.enums.PermissionType;
import shticell.manager.sheet.SheetManager;
import shticell.sheet.exception.LoopConnectionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static constant.Constants.GSON;
import static constant.Constants.VERSION;

@WebServlet(urlPatterns = Constants.CELL)
public class CellServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String sheetName = req.getParameter(Constants.SHEET_NAME);
        String user = SessionUtils.GetUserName(req);

        if(sheetName != null && !sheetName.isEmpty() && sheetManager.isPermit(sheetName,user, PermissionType.WRITER)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            UpdateCellDto updateCellDto = GSON.fromJson(reader, UpdateCellDto.class);
            try {
                sheetManager.UpdateCellByCoordinate(sheetName, updateCellDto.coordinate(), updateCellDto.newValue());
                int numOfChanges = sheetManager.GetNumOfChanges(sheetName);
                req.getSession(true).setAttribute(VERSION, numOfChanges);
            } catch (LoopConnectionException e) {
                resp.sendError(500, e.getMessage());
            }
        }
        else{
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}




