package servlet;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import netscape.javascript.JSObject;
import servlet.utils.ServletUtils;
import servlet.utils.SessionUtils;
import shticell.manager.sheet.SheetManager;

import java.io.IOException;

import static constant.Constants.*;

@WebServlet(urlPatterns = NEED_TO_BE_UPDATED)
public class NeedToBeUpdatedServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer numOfChanges = SessionUtils.GetSheetVersionUserPossess(req);
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String sheetName = req.getParameter(SHEET_NAME);
        if (numOfChanges == null || sheetName == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            boolean result = (numOfChanges < sheetManager.GetNumOfChanges(sheetName));
            System.out.println("i am also here");
            System.out.println(GSON.toJson(result));
            resp.getWriter().write(GSON.toJson(result));
        }
    }
}

