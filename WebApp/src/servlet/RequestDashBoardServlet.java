package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.sheet.SheetManager;

import java.io.IOException;

import static constant.Constants.*;

@WebServlet(urlPatterns = REQUEST_DASHBOARD)
public class RequestDashBoardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String sheetName = req.getParameter(SHEET_NAME);
        resp.getWriter().write(GSON.toJson(sheetManager.GetRequestDashBoard(sheetName)));
    }
}