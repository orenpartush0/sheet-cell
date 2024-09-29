package servlet;

import dto.SortDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import servlet.utils.SessionUtils;
import shticell.manager.enums.PermissionType;
import shticell.manager.sheet.SheetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static constant.Constants.*;

@WebServlet(urlPatterns = SORT)
public class SortServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String user = SessionUtils.GetUserName(req);
        String sheetName = req.getParameter(SHEET_NAME);
        if(sheetName!=null && !sheetName.isEmpty() && sheetManager.isPermit(sheetName,user, PermissionType.READER)){
            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            SortDto sortDto = GSON.fromJson(reader, SortDto.class);
            resp.getWriter().write(GSON.toJson(sheetManager.applySort(sheetName,sortDto.cols(),sortDto.range())));
        }
        else{
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }

    }
}
