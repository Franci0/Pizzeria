/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Francesco
 */
@WebServlet(name = "Banner",
        urlPatterns = {"/Banner"},
        initParams = {
            @WebInitParam(name = "DBUrl", value = "jdbc:derby://localhost:1527/Pizzeria", description = "URL del DB"),
            @WebInitParam(name = "DBuser", value = "admin", description = "Account per accedere al DB"),
            @WebInitParam(name = "DBpwd", value = "admin", description = "Password per accedere al DB")
        }
)

public class Banner extends HttpServlet {

    private static String url = "";
    private static String user = "";
    private static String pwd = "";

    /**
     * @param config
     * @throws javax.servlet.ServletException
     * @see Servlet#init(ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init();
        ServletContext ctx = config.getServletContext();
        user = "APP";
        url = "jdbc:derby://localhost:1527/Pizzeria";
        pwd = "admin";

        try {
            DriverManager.registerDriver(new org.apache.derby.jdbc.ClientDriver());
        } catch (SQLException e) {
            ServletException e1 = new ServletException(e.getMessage());
            throw e1;
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String indexUrl = response.encodeURL("index.html");
        String azione = request.getParameter("action");
        HttpSession s = request.getSession();

        String username = "";
        String password = "";

        Object usernameObj = request.getParameter("nome");
        Object passwordObj = request.getParameter("pwd");

        //controllo che esistano come parametri (non è la prima chiamata)
        if (usernameObj != null) {
            username = usernameObj.toString();
        }

        if (passwordObj != null) {
            password = passwordObj.toString();
        }

        PrintWriter out = response.getWriter();
        try {
            /*out.println("<html><head>");
            out.println("<tile>Da Luigi</title></head>");
            out.println("<body>");*/

            if (azione != null && azione.equals("invalida")) {
                s.invalidate();
                out.println("<p><a href=\"" + indexUrl + "\"Ricarica</a></p>");

            } else //controllo se l'utente è registrato da database
             if (!username.isEmpty() && !password.isEmpty() && checkUser(username, password)) {
                    s.setAttribute("Username", username);
                    //inserire link di logout
                    out.println("<p>Benvenuto " + username + "<a href=\"" + indexUrl + "?action=invalida\"> Logout</a></p>");
                } else {
                    out.println("<form action=Banner method=post>");
                    out.println("<p>Login</p>");
                    out.println("<p>Username<input type=text name=nome></p>");
                    out.println("<p>Password <input type=text name=pwd></p>");
                    out.println("<p><input type=submit name=submit value=Login></p></form>");
                }
        } finally {
            //out.println("</body></html>");
            out.close();
        }
    }

    private boolean checkUser(String username, String password) {
        try {
            Connection c = DriverManager.getConnection(url, user, pwd);
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM UTENTI");

            while (rs.next()) {
                if (rs.getString("USERNAME").equals(username) && rs.getString("PASSWORD").equals(password)) {
                    return true;
                }
            }

            rs.close();
            st.close();
            c.close();
        } catch (SQLException e) {
            e.getMessage();
        }

        return false;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
