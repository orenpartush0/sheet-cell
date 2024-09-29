package servlet;

import servlet.utils.SessionUtils;
import constant.Constants;
import dto.SheetDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.enums.PermissionType;
import shticell.manager.sheet.SheetManager;

import java.io.IOException;

import static constant.Constants.GSON;

@WebServlet(urlPatterns = Constants.GET_SHEET_BY_VERSION)
public class VersionServlet extends HttpServlet {

    private boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String user = SessionUtils.GetUserName(req);
        String sheetName = req.getParameter(Constants.SHEET_NAME);
        String versionStr = req.getParameter(Constants.VERSION);

        if(sheetName != null && isNumeric(versionStr) && !sheetName.isEmpty() && sheetManager.isPermit(sheetName, user, PermissionType.READER)) {
            int version = Integer.parseInt(versionStr);
            resp.getWriter().write(GSON.toJson(sheetManager.GetSheetByVersion(sheetName,version), SheetDto.class));
        }else{
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
