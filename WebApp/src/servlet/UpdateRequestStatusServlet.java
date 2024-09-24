package servlet;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.manager.Manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet("/updateRequest")
public class UpdateRequestStatusServlet extends HttpServlet {

    private final String MANAGER = "manager";

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Manager manager = (Manager) getServletContext().getAttribute(MANAGER);
        if (manager == null) {
            manager = new Manager();
            getServletContext().setAttribute(MANAGER, manager);
        }

        String sheetName = req.getParameter("sheetName");
        String userName = req.getParameter("userName");
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        Gson gson = new Gson();
        boolean accept = gson.fromJson(reader, Boolean.class);
        manager.UpdateRequestStatus(sheetName, userName, accept);
    }
}
