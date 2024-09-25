package servlet;

import SessionUtils.SessionUtils;
import dto.RequestDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.sheet.SheetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static constant.Constants.GSON;

@WebServlet("/updateRequest")
public class UpdateRequestStatusServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String userName = SessionUtils.GetUserName(req);
        String sheetName = req.getParameter("sheetName");

        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        RequestDto requestDto = GSON.fromJson(reader, RequestDto.class);
        sheetManager.UpdateRequestStatus(sheetName, userName,requestDto.reqId(),requestDto.approved());
    }
}
