package servlet;

import servlet.utils.SessionUtils;
import dto.UpdateRequestDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.enums.PermissionType;
import shticell.manager.sheet.SheetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static constant.Constants.*;

@WebServlet(urlPatterns = UPDATE_REQUEST)
public class UpdateRequestStatusServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String userName = SessionUtils.GetUserName(req);
        String sheetName = req.getParameter(SHEET_NAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        UpdateRequestDto updateRequestDto = GSON.fromJson(reader, UpdateRequestDto.class);;
        if(sheetManager.isPermit(sheetName,userName, PermissionType.OWNER)){
            sheetManager.UpdateRequestStatus(sheetName, updateRequestDto.reqId(), updateRequestDto.approved());
        }else{
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }

    }
}
