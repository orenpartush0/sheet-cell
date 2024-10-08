package servlet;

import servlet.utils.SessionUtils;
import constant.Constants;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.enums.PermissionType;
import shticell.manager.sheet.SheetManager;

import java.io.IOException;

import static constant.Constants.GSON;

@WebServlet(urlPatterns = Constants.GET_RANGES)
public class GetRangesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String sheetName = req.getParameter(Constants.SHEET_NAME);
        String userName = SessionUtils.GetUserName(req);
        if(sheetName != null && !sheetName.isEmpty() && sheetManager.isPermit(sheetName,userName, PermissionType.READER)) {
            resp.getWriter().write(GSON.toJson(sheetManager.GetRanges(sheetName)));
        }
        else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
