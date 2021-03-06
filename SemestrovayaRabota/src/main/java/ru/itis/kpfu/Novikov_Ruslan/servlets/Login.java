package ru.itis.kpfu.Novikov_Ruslan.servlets;

import ru.itis.kpfu.Novikov_Ruslan.DAO.UserDao;
import ru.itis.kpfu.Novikov_Ruslan.DAO.UserDaoImpl;
import ru.itis.kpfu.Novikov_Ruslan.DAO.UserService;
import ru.itis.kpfu.Novikov_Ruslan.DAO.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


@WebServlet("/login")

public class Login extends HttpServlet {

    static UserDao dao;
    static UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        dao = UserDaoImpl.getInstance();
        userService = new UserServiceImpl(dao);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        if (session.getAttribute("user") != null) {
            response.sendRedirect("/");
            return;
        }

        getServletConfig().getServletContext().getRequestDispatcher("/unregistered/login.ftl").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        request.setAttribute("name", request.getParameter("name"));
        request.setAttribute("password", request.getParameter("password"));

        if(login(request, response)) {
            response.sendRedirect("/");
        } else {
            request.getRequestDispatcher("/unregistered/login.ftl").forward(request, response);
        }
    }

    public static boolean login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = (String) request.getAttribute("name");
        String password = (String) request.getAttribute("password");

        dao = UserDaoImpl.getInstance();
        userService = new UserServiceImpl(dao);

        if ("admin".equals(username) && "admin".equals(password)) {
            request.getSession().setAttribute("admin", "admin");
            return true;
        } else {

            if (userService.isRegistered(username)) {

                if (dao.getUser(username).getPasswordHash().equals(password)) {

                    request.getSession().setAttribute("user", username);
                    addCookies(response, username);
                    return true;

                } else {
                    request.setAttribute("error", "Не верный пароль");
                    return false;
                }
            } else {
                request.setAttribute("error", "Пользователь не найден");
                return false;
            }
        }
    }





    public static void addCookies(HttpServletResponse response, String key) throws UnsupportedEncodingException {
        // Create cookies for first and last names.
        Cookie name = new Cookie("users",
                URLEncoder.encode(key, "UTF-8"));

        name.setMaxAge(60 * 60 * 24 * 25);

        response.addCookie(name);

    }
}