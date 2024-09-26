package servlet;

import SessionUtils.SessionUtils;
import com.google.gson.Gson;
import dto.SheetDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.sheet.SheetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static constant.Constants.*;

@WebServlet(urlPatterns = SHEET)
public class SheetServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userName = SessionUtils.GetUserName(req);
        System.out.println(userName + " Try to create new sheet");
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        if(userName != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            SheetDto sheet = GSON.fromJson(reader, SheetDto.class);
            try {
                sheetManager.SetSheet(userName, sheet);
                System.out.println("Sheet named " +sheet.Name() + " created by " + userName);
            }
            catch(Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String sheetName = req.getParameter(SHEET_NAME);
        resp.getWriter().write(GSON.toJson(sheetManager.getSheet(sheetName)));
    }
}
