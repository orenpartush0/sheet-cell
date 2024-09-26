package servlet;

import constant.Constants;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.sheet.SheetManager;

import java.io.IOException;

import static constant.Constants.GSON;

@WebServlet(urlPatterns = Constants.REQUEST_DASHBOARD)
public class RequestDashBoardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String sheetName = req.getParameter("sheetName");
        System.out.println("try to get " +sheetName + "sheet requestDashBoard");
        resp.getWriter().write(GSON.toJson(sheetManager.GetRequestDashBoard(sheetName)));
    }
}