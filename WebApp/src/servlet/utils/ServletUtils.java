package servlet.utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import constant.Constants;
import shticell.manager.sheet.SheetManager;
import shticell.manager.user.UserManager;

@WebServlet
public class ServletUtils {

    public final static Object userLock = new Object();
    public final static Object sheetLock = new Object();

    public static UserManager GetUserManager(ServletContext servletContext){
        synchronized (userLock){
            if(servletContext.getAttribute(Constants.USER_MANAGER) == null){
                servletContext.setAttribute(Constants.USER_MANAGER, new UserManager());
            }
        }

        return (UserManager)servletContext.getAttribute(Constants.USER_MANAGER);
    }

    public static SheetManager GetSheetManager(ServletContext servletContext){
        synchronized (sheetLock){
            if(servletContext.getAttribute(Constants.SHEET_MANAGER) == null){
                servletContext.setAttribute(Constants.SHEET_MANAGER, new SheetManager());
            }
        }

        return (SheetManager) servletContext.getAttribute(Constants.SHEET_MANAGER);
    }
}
