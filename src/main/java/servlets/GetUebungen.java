package servlets;

import database.DatenbankVerbindung;
import model.Uebung;
import util.JsonConverter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name="GetUebungen", urlPatterns = {"/GetUebungen"})
public class GetUebungen extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter(); // PrintWriter statt ServletOutputStream, weil letzterer Binärdaten sendet, die nicht in UTF-8 codiert werden können!

        List<Uebung> uebungen = new ArrayList<>(new DatenbankVerbindung().getUebungen());
        for(Uebung u : uebungen) { // Wir entfernen die Image Filestreams, weil das JSON sonst unleserlich wird.
            u.setImage(null);
        }

        JsonConverter converter = new JsonConverter();
        String output = converter.convertUebungenToJson(uebungen);

        out.print(output);
    }
}
