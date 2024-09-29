package servlet;

import servlet.utils.SessionUtils;
import constant.Constants;
import dto.FilterDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import shticell.manager.enums.PermissionType;
import shticell.manager.sheet.SheetManager;
import shticell.util.Filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static constant.Constants.GSON;

@WebServlet(urlPatterns = Constants.FILTER)
public class FilterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String sheetName = req.getParameter(Constants.SHEET_NAME);
        String userName = SessionUtils.GetUserName(req);

        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        FilterDto filterDto = GSON.fromJson(reader, FilterDto.class);


        if(sheetName != null && !sheetName.isEmpty() && sheetManager.isPermit(sheetName,userName, PermissionType.READER)) {
            res.getWriter().write(GSON.toJson(sheetManager.applyFilter(sheetName,filterDto.range(),filterDto.filters())));
        }
    }
}
