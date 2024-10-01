package servlet;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import constant.Constants;
import deserializer.UpdateCellDtoDeserializer;
import dto.UpdateCellDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.utils.ServletUtils;
import servlet.utils.SessionUtils;
import shticell.manager.enums.PermissionType;
import shticell.manager.sheet.SheetManager;
import shticell.sheet.exception.LoopConnectionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static constant.Constants.*;

@WebServlet(urlPatterns = DYNAMIC_CALCULATE)
public class DynamicCalculateServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String sheetName = req.getParameter(SHEET_NAME);
        String userName = SessionUtils.GetUserName(req);

        if(sheetName != null && !sheetName.isEmpty() && sheetManager.isPermit(sheetName,userName, PermissionType.READER)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
            builder.registerTypeAdapter(UpdateCellDto.class, new UpdateCellDtoDeserializer());
            UpdateCellDto updateCellDto = GSON.fromJson(reader, UpdateCellDto.class);
            resp.getWriter().write(GSON.toJson(sheetManager.applyDynamicCalculate(sheetName, updateCellDto.coordinate(), updateCellDto.newValue())));

        }
    }
}
