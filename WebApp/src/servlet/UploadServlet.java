package servlet;

import dto.SheetDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import servlet.utils.ServletUtils;
import servlet.utils.SessionUtils;
import shticell.jaxb.SchemBaseJaxb;
import shticell.manager.sheet.SheetManager;
import shticell.sheet.api.Sheet;
import shticell.sheet.range.Range;

import java.io.*;

import static constant.Constants.*;


@MultipartConfig()
@WebServlet(urlPatterns = UPLOAD_SHEET_XML_FORMAT)
public class UploadServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        SheetManager sheetManager = ServletUtils.GetSheetManager(getServletContext());
        String userName = SessionUtils.GetUserName(req);

        Part part = req.getPart("file");
        InputStream inputStream = part.getInputStream();

        try {
            sheetManager.UploadSheetXml(userName, inputStream);
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
