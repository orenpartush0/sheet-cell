package servlet;

import SessionUtils.SessionUtils;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.sheet.SheetManager;

import java.io.IOException;

import static servlet.constant.Constants.GSON;

@WebServlet("/sheetDashBoard")
public class SheetDashBoardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String userName = SessionUtils.GetUserName(req);
        resp.getWriter().write(GSON.toJson(sheetManager.GetSheetsDashBoard(userName)));
    }
}
