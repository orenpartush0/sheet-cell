package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.RangeDeserializer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import servlet.utils.SessionUtils;
import shticell.manager.enums.PermissionType;
import shticell.manager.sheet.SheetManager;
import shticell.sheet.range.Range;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static constant.Constants.*;

@WebServlet(urlPatterns = RANGE)
public class RangeServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String sheetName = req.getParameter(SHEET_NAME);
        String userName = SessionUtils.GetUserName(req);

        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        Range range = GSON.fromJson(reader, Range.class);

        if(sheetName != null && !sheetName.isEmpty() && sheetManager.isPermit(sheetName,userName, PermissionType.WRITER)) {
            if(!sheetManager.isRangeInUse(sheetName,range.rangeName())){
                sheetManager.AddRange(sheetName, range);
            }
            else{
                resp.sendError(HttpServletResponse.SC_CONFLICT,"Range already exist");
            }

        }else{
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String userName = SessionUtils.GetUserName(req);
        String sheetName = req.getParameter(SHEET_NAME);
        String rangeName = req.getParameter(RANGE_NAME);
        if(!sheetName.isEmpty() && !rangeName.isEmpty() && sheetManager.isPermit(sheetName,userName, PermissionType.WRITER)
        && sheetManager.isRangeInUse(sheetName,rangeName)){
            try {
                sheetManager.removeRange(sheetName, rangeName);
            } catch (Exception e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,"Range in use");
            }
        }else{
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,"Range not found");
        }

    }

}
