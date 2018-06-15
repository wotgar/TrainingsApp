package database;

import model.Uebung;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatenbankVerbindung {

    Connection conn;
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement ps = null;

    public Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.OracleDriver"); // Initialisiert den JDBC-Treiber
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//ts-ws20.techspring.lan:1521/XE"
                                                , "gsi", "gsi");

            conn.setAutoCommit(true); // AutoCommit bedeutet, dass die DB ohne Nachfrage bearbeitet wird
        } catch (SQLException sql) {
            sql.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return conn;
    }

    public void closeConnection() {
        try {
            conn.close();
            if (stmt != null) stmt.close();
            if (ps  != null ) ps.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //	-------------------- Uebungen ----------------------------------------------------------------------------------------

    public List<Uebung> getUebungen() {
        List<Uebung> list = new ArrayList<>();

        this.conn = getConnection(); // Verbindungsmethode aufrufen
        try {
            stmt = conn.createStatement();
            String sql = "select * from view_uebungen_gesamtansicht";
            rs = stmt.executeQuery(sql); // ResultSet auslesen
            while (rs.next()) {
                String bezeichnung = rs.getString("BEZEICHNUNG_UEBUNG");
                String beschreibung = rs.getString("BESCHREIBUNG_UEBUNG");
                String kategorie = rs.getString("BEZEICHNUNG_UEBUNGSKATEGORIE");
                String schwierigkeitsgrad = rs.getString("BEZEICHNUNG_SCHWIERIGKEITSGRAD");
                String verletzungsrisiko = rs.getString("BEZEICHNUNG_VERLETZUNGSRISIKO");
                String muskelgruppe = rs.getString("BEZEICHNUNG_MUSKELGRUPPE");

                list.add(new Uebung(bezeichnung, beschreibung, kategorie, schwierigkeitsgrad,
                        verletzungsrisiko, muskelgruppe)); // Ausgelesene Werte als Objekt der Liste hinzufügen
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally { // Egal ob Erfolg oder nicht, wir schließen die Verbindung, Statement und
            // ResultSet ordentlich ab
            closeConnection();
        }
        return list;
    }

    public void addUebung(Uebung uebung) {

        this.conn = getConnection();
        ps = null;
        String sql = "insert into UEBUNG (bezeichnung_uebung, beschreibung_uebung, schwierigkeitsgrad, " +
                "verletzungsrisiko, uebungskategorie) values (?, ?, ?, ?, ?)"; // "?" ist ein Platzhalter
        /*
        String sql = "insert into UEBUNG (bezeichnung_uebung, beschreibung_uebung, schwierigkeitsgrad, " +
                "verletzungsrisiko, uebungskategorie) values ('"
                + uebung.getBezeichnung() + "', '"
                + uebung.getBeschreibung() + "', "
                + getId("SCHWIERIGKEITSGRAD", uebung.getSchwierigkeitsgrad()) + ", "
                + getId("VERLETZUNGSRISIKO", uebung.getVerletzungsrisiko()) + ", "
                + getId("UEBUNGSKATEGORIE", uebung.getKategorie()) + ")";
        */
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, uebung.getBezeichnung()); // setString befüllt den Platzhalter "?"
            ps.setString(2, uebung.getBeschreibung());
            ps.setInt(3, getId("SCHWIERIGKEITSGRAD", uebung.getSchwierigkeitsgrad()));
            ps.setInt(4, getId("VERLETZUNGSRISIKO", uebung.getVerletzungsrisiko()));
            ps.setInt(5, getId("UEBUNGSKATEGORIE", uebung.getKategorie()));
            ps.execute(); // NICHT executeQuery! Query nur zum Auslesen, nicht für Inserts/Updates
            /*
            stmt = conn.createStatement();
            stmt.execute(sql);
            writeFile(sql);
            */

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        int id = getNeueId("UEBUNG");
        for (String m : uebung.getMuskelgruppen()) { // Adressliste des Kunden anlegen
            int i = getId("MUSKELGRUPPE", m);
            addZwischentabelle("MUSKELGRUPPE_UEBUNG" ,id, i);
        }
    }

    //	-------------------- Einzelne Strings-Listen -------------------------------------------------------------------

    public List<String> getStringList(String tabelle, String kennung) {
        List<String> list = new ArrayList<>();
        ps = null;

        this.conn = getConnection(); // Verbindungsmethode aufrufen
        try {
            stmt = conn.createStatement();

            String sql = "select * from " + tabelle;
            rs = stmt.executeQuery(sql); // ResultSet auslesen
            while (rs.next()) {
                String bezeichnung = rs.getString(kennung);

                list.add(new String(bezeichnung)); // Ausgelesene Werte als Objekt der Liste hinzufügen
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally { // Egal ob Erfolg oder nicht, wir schließen die Verbindung, Statement und
            // ResultSet ordentlich ab
            closeConnection();
        }
        return list;
    }

    //	-------------------- IDs neuer Einträge auslesen ---------------------------------------------------------------

    public int getNeueId(String tabelle) { // Damit man die Zwischentabelle bei Erstellung schreiben kann
        conn = getConnection();
        PreparedStatement ps = null;
        int id = 0;
        String sql = "select max(id) from " + tabelle;

        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                return 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                ps.close();
            } catch (SQLException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        return id;
    }

    //	-------------------- IDs bestimmter Einträge auslesen ----------------------------------------------------------

    public int getId(String tabelle, String kennung) { // Damit man die Zwischentabelle bei Erstellung schreiben kann
        conn = getConnection();
        PreparedStatement ps = null;
        int id = 0;
        String sql = "SELECT id FROM " + tabelle + " WHERE BEZEICHNUNG_" + tabelle + " = '" + kennung + "'";
        System.out.println(sql);

        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                return 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                ps.close();
            } catch (SQLException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        return id;
    }

    //  -------------------- Werte in Zwischentabelle geben ------------------------------------------------------------

    public void addZwischentabelle(String tabelle, int idOne, int idTwo) {

        conn = getConnection();
        PreparedStatement ps = null;
        String sql = "insert into " + tabelle + " (uebung, muskelgruppe) values " +
                     "(" + idOne + ", " + idTwo + ")";

        try {
            ps = conn.prepareStatement(sql);
            /*
                ps.setInt(1, idOne);
                ps.setInt(2, idTwo);
            */

            ps.execute(); // NICHT executeQuery! Query nur zum Auslesen, nicht für Inserts/Updates
            writeFile(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                ps.close();
            } catch (SQLException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

    //  -------------------- Queries in einer SQL-Datei speichern ------------------------------------------------------

    public void writeFile(String sql) {
        try {
            PrintWriter writer = new PrintWriter((new FileOutputStream(new File("java_inserts.sql"), true)));
            writer.print(sql + ";\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
