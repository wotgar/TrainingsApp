package servlets;

import org.primefaces.json.JSONObject;
import util.StreamToFile;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;


@WebServlet(name="SetSettings", urlPatterns = {"/SetSettings"})
public class SetSettings extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        StringBuilder jsonBuff = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jsonBuff.append(line);
        } catch (Exception e) { /*error*/ }

        System.out.println("Request JSON string :" + jsonBuff.toString());
        new StreamToFile().writeJson(jsonBuff.toString());
    }
}
