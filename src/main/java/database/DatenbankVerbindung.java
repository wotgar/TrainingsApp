package database;

import model.Uebung;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatenbankVerbindung {

    Connection conn;
    StreamToFile streams = new StreamToFile();
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement ps = null;

    // Verbindung herstellen
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

    // Verbindung schließen
    public void closeConnection() {
        try {
            conn.close();
            if (stmt != null) stmt.close();
            if (ps  != null ) ps.close();
            if (rs != null ) rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  -------------------- Bilddateien auslesen ----------------------------------------------------------------------

    public byte[] getProductImage(int productId) throws IOException,
            SQLException {

        this.conn = getConnection();
        PreparedStatement stmt = null;
        byte[] productImage = null;
        System.out.println("ID-: " + productId);

        stmt = conn.prepareStatement("select * from uebung where id=?");
        stmt.setInt(1, productId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            productImage = rs.getBytes("bild_uebungsablauf");
        }

        rs.close();
        conn.close();

        System.out.println("PRODUCT IMAGE: " + productImage);
        return productImage;
    }

//	-------------------- Uebungen ----------------------------------------------------------------------------------
    public List<Uebung> getUebungenMuskelgruppen() {
        List<Uebung> list = new ArrayList<>();

        this.conn = getConnection(); // Verbindungsmethode aufrufen
        try {
            stmt = conn.createStatement();
            String sql = "select * from view_uebungen_ohne_mg";
            rs = stmt.executeQuery(sql); // ResultSet auslesen
            while (rs.next()) {
                int id = rs.getInt("ID");
                String bezeichnung = rs.getString("BEZEICHNUNG_UEBUNG");
                String beschreibung = rs.getString("BESCHREIBUNG_UEBUNG");
                String kategorie = rs.getString("BEZEICHNUNG_UEBUNGSKATEGORIE");
                String schwierigkeitsgrad = rs.getString("BEZEICHNUNG_SCHWIERIGKEITSGRAD");
                String verletzungsrisiko = rs.getString("BEZEICHNUNG_VERLETZUNGSRISIKO");
                //----------------------------------------------------------------------------------------------------------
                // HINWEIS: Hier kommen die BLOB-Geschichten!
                // BLOB als Byte Array auslesen:
                byte[] productImage = rs.getBytes("bild_uebungsablauf");
                // Prüfen ob das ausgelesene Array befüllt ist und als Wert entsprechend dieses Array oder ein Default Array übergeben
                byte[] image = productImage != null ? productImage : streams.defaultByteArray();
                //----------------------------------------------------------------------------------------------------------
                List<String> muskelgruppen = getZwischentabelle("VIEW_UEBUNGEN_MUSKELGRUPPEN", "BEZEICHNUNG_MUSKELGRUPPE", id);
                // Ausgelesene Werte als Objekt der Klasse Uebung der Liste hinzufügen:
                list.add(new Uebung(id, bezeichnung, beschreibung, kategorie, schwierigkeitsgrad,
                        verletzungsrisiko, muskelgruppen, image));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        } finally { // Egal ob Erfolg oder nicht, wir schließen die Verbindung, Statement und ResultSet ordentlich ab
            closeConnection();
        }
        return list;
    }

//----------------------------------------------------------------------------------------------------------------------
    public List<Uebung> getUebungen() { // HINWEIS: Diese Methode ist letztlich obsolet und nur zur Veranschaulichung da.
        List<Uebung> list = new ArrayList<>();

        this.conn = getConnection(); // Verbindungsmethode aufrufen
        try {
            stmt = conn.createStatement();
            String sql = "select * from view_uebungen_gesamtansicht";
            rs = stmt.executeQuery(sql); // ResultSet auslesen
            while (rs.next()) {
                int id = rs.getInt("ID");
                String bezeichnung = rs.getString("BEZEICHNUNG_UEBUNG");
                String beschreibung = rs.getString("BESCHREIBUNG_UEBUNG");
                String kategorie = rs.getString("BEZEICHNUNG_UEBUNGSKATEGORIE");
                String schwierigkeitsgrad = rs.getString("BEZEICHNUNG_SCHWIERIGKEITSGRAD");
                String verletzungsrisiko = rs.getString("BEZEICHNUNG_VERLETZUNGSRISIKO");
                String muskelgruppe = rs.getString("BEZEICHNUNG_MUSKELGRUPPE");
            //----------------------------------------------------------------------------------------------------------
            // HINWEIS: Hier kommen die BLOB-Geschichten!
                // BLOB als BinaryStream auslesen:
                InputStream bildInput = rs.getBinaryStream("BILD_UEBUNGSABLAUF");
                // Ausgelesenen Stream in eine automatisch erzeugte Bilddatei speichern:
                File bild = bildInput == null ? new File("default.jpg") : streams.readDatabaseStream(bezeichnung, bildInput);
                // BLOB als Byte Array auslesen:
                byte[] productImage = rs.getBytes("bild_uebungsablauf");
                // Prüfen ob das ausgelesene Array befüllt ist und als Wert entsprechend dieses Array oder ein Default Array übergeben
                byte[] image = productImage != null ? productImage : streams.defaultByteArray();
            //----------------------------------------------------------------------------------------------------------

            // Ausgelesene Werte als Objekt der Klasse Uebung der Liste hinzufügen:
                list.add(new Uebung(id, bezeichnung, beschreibung, kategorie, schwierigkeitsgrad,
                            verletzungsrisiko, muskelgruppe, bild, image));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        } finally { // Egal ob Erfolg oder nicht, wir schließen die Verbindung, Statement und ResultSet ordentlich ab
            closeConnection();
        }
        return list;
    }

//----------------------------------------------------------------------------------------------------------------------

    public void addUebung(Uebung uebung) {

        this.conn = getConnection();
        ps = null;
        File image = uebung.getBild() != null ? uebung.getBild() : new File("src/main/webapp/resources/images/default.jpg");

        // PreparedStatement oder CallableStatement kann nicht als String ausgelesen werden.
        // Um eine Textdatei schreiben zu können, legen wir den Befehl noch einmal an:
        String prcWriteFile = "DECLARE o_result VARCHAR2(15); BEGIN prc_insert_uebung ('"
                + uebung.getBezeichnung() + "', "
                + getId("UEBUNGSKATEGORIE", uebung.getKategorie()) + ", "
                + getId("SCHWIERIGKEITSGRAD", uebung.getSchwierigkeitsgrad()) + ", "
                + getId("VERLETZUNGSRISIKO", uebung.getVerletzungsrisiko()) + ", "
                + null + ", '" // BLOB kann händisch nicht geschrieben werden, deshalb NULL
                + uebung.getBeschreibung() + "', "
                + "o_result); END;" +
                "\n -- IMAGE FILE LOCATION: " + image; // Speicherort der Bilddatei für den BLOB
                                                        // FIXME: Aktuell wird die temporäre Datei angegeben!
        // SQL-Befehl in eine Datei schreiben:
        streams.writeFile(prcWriteFile);

        // Tatsächlicher INSERT, Variante mit Aufruf einer Prozedur:
        try {
            FileInputStream fis = new FileInputStream(image);
            String result = null;
            String procedureCall = "BEGIN prc_insert_uebung(?, ?, ?, ?, ?, ?, ?); END;";
            CallableStatement callStmt = conn.prepareCall(procedureCall);
            callStmt.registerOutParameter(7, Types.VARCHAR);
            callStmt.setString(1, uebung.getBezeichnung()); // setString befüllt den Platzhalter "?"
            callStmt.setInt(2, getId("UEBUNGSKATEGORIE", uebung.getKategorie()));
            callStmt.setInt(3, getId("SCHWIERIGKEITSGRAD", uebung.getSchwierigkeitsgrad()));
            callStmt.setInt(4, getId("VERLETZUNGSRISIKO", uebung.getVerletzungsrisiko()));
            callStmt.setBinaryStream(5, fis, (int) image.length());
            callStmt.setString(6, uebung.getBeschreibung());
            callStmt.execute();
            result = callStmt.getString(7);
            System.out.println(result);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        // Zwischentabelle für Muskelgruppen schreiben:
        int id = getNeueId("UEBUNG");
        for (String m : uebung.getMuskelgruppen()) {
            int i = getId("MUSKELGRUPPE", m);
            addZwischentabelle("MUSKELGR_UEBUNG" ,id, i);
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    public void updateUebung(Uebung uebung) {

        this.conn = getConnection();
        ps = null;
        File image = uebung.getBild() != null ? uebung.getBild() : new File("src/main/webapp/resources/images/default.jpg");

        // PreparedStatement oder CallableStatement kann nicht als String ausgelesen werden.
        // Um eine Textdatei schreiben zu können, legen wir den Befehl noch einmal an:
        String prcWriteFile = "DECLARE o_result VARCHAR2(15); BEGIN prc_update_uebung ("
                + uebung.getId() + ", '"
                + uebung.getBezeichnung() + "', "
                + getId("UEBUNGSKATEGORIE", uebung.getKategorie()) + ", "
                + getId("SCHWIERIGKEITSGRAD", uebung.getSchwierigkeitsgrad()) + ", "
                + getId("VERLETZUNGSRISIKO", uebung.getVerletzungsrisiko()) + ", "
                + null + ", '" // BLOB kann händisch nicht geschrieben werden, deshalb NULL
                + uebung.getBeschreibung() + "', "
                + "o_result); END;" +
                "\n -- IMAGE FILE LOCATION: " + image; // Speicherort der Bilddatei für den BLOB
                                                        // FIXME: Aktuell wird die temporäre Datei angegeben!
        // SQL-Befehl in eine Datei schreiben:
        streams.writeFile(prcWriteFile);

        // Tatsächlicher INSERT, Variante mit Aufruf einer Prozedur:
        try {
            FileInputStream fis = image != null ? new FileInputStream(image) : null;
            String result = null;
            String procedureCall = "BEGIN prc_update_uebung(?, ?, ?, ?, ?, ?, ?, ?); END;";
            CallableStatement callStmt = conn.prepareCall(procedureCall);
            callStmt.registerOutParameter(8, Types.VARCHAR);
            callStmt.setInt(1, uebung.getId());
            callStmt.setString(2, uebung.getBezeichnung()); // setString befüllt den Platzhalter "?"
            callStmt.setInt(3, getId("UEBUNGSKATEGORIE", uebung.getKategorie()));
            callStmt.setInt(4, getId("SCHWIERIGKEITSGRAD", uebung.getSchwierigkeitsgrad()));
            callStmt.setInt(5, getId("VERLETZUNGSRISIKO", uebung.getVerletzungsrisiko()));
            callStmt.setBinaryStream(6, fis, (int) image.length());
            callStmt.setString(7, uebung.getBeschreibung());
            callStmt.execute();
            result = callStmt.getString(8);
            System.out.println(result);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        // Zwischentabelle für Muskelgruppen neu schreiben:
        int id = uebung.getId();
        resetZwischenwerte("muskelgruppen", id);
        for (String m : uebung.getMuskelgruppen()) {
            int i = getId("MUSKELGRUPPE", m);
            addZwischentabelle("MUSKELGR_UEBUNG" ,id, i);
        }
    }

    //----------------------------------------------------------------------------------------------------------------------

    public void deleteUebung(int id) {

        this.conn = getConnection();
        resetZwischenwerte("muskelgruppen", id); // Zuerst müssen die Constraints aus der Zwischentabelle weg

        // Um eine Textdatei schreiben zu können, legen wir den Befehl noch einmal an:
        String sql = "DECLARE o_result VARCHAR2(15); BEGIN prc_delete_uebung ("
                + id + ", o_result); END;";
        // SQL-Befehl in eine Datei schreiben:
        streams.writeFile(sql);

        // DELETE mit Aufruf einer Prozedur:
        try {
            String result = null;
            String procedureCall = "BEGIN prc_delete_uebung(?, ?); END;";
            CallableStatement callStmt = conn.prepareCall(procedureCall);
            callStmt.registerOutParameter(2, Types.VARCHAR);
            callStmt.setInt(1, id);
            callStmt.execute();
            result = callStmt.getString(2);
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
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
        //System.out.println(sql);
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

        // PreparedStatement oder CallableStatement kann nicht als String ausgelesen werden.
        // Um eine Textdatei schreiben zu können, legen wir den Befehl noch einmal komplett an:
        String sql = "DECLARE o_result VARCHAR2(15); BEGIN prc_insert_" + tabelle + "(" + idOne + ", " + idTwo + ", o_result); END;";
        streams.writeFile(sql);

        // Tatsächlicher INSERT mit Aufruf einer Prozedur:
        try {
            String result = null;
            String procedureCall = "BEGIN prc_insert_" + tabelle + "(?, ?, ?); END;";
            CallableStatement callStmt = conn.prepareCall(procedureCall);
            callStmt.registerOutParameter(3, Types.VARCHAR);
            callStmt.setInt(1, idOne); // setString befüllt den Platzhalter "?"
            callStmt.setInt(2, idTwo);
            callStmt.execute();
            result = callStmt.getString(3);
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    //  -------------------- Werte aus Zwischentabelle lesen ------------------------------------------------------------

    public List<String> getZwischentabelle(String tabelle, String kennung, int id) {

        List<String> list = new ArrayList<>();
        ResultSet rs = null; // Damit es keine Konflikte gibt, bekommt diese Methode ein eigenes ResultSet
        Connection conn = getConnection(); // Und eine eigene Verbindung
        try {
            stmt = conn.createStatement();
            String sql = "select * from " + tabelle + " WHERE ID = " + id;
            //System.out.println("SQL: " + sql);
            rs = stmt.executeQuery(sql); // ResultSet auslesen
            while (rs.next()) {
                String bezeichnung = rs.getString(kennung);
                list.add(new String(bezeichnung)); // Ausgelesene Werte als Objekt der Liste hinzufügen
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        return list;
    }

    //  -------------------- Werte in Zwischentabelle zurücksetzen -----------------------------------------------------

    public void resetZwischenwerte(String kennung, int id) {

        Connection conn = getConnection();
        // Aufruf einer Prozedur, um Datensätze zu entfernen:
        try {
            String result = null;
            String procedureCall = "BEGIN prc_reset_" + kennung + "(?, ?); END;";
            CallableStatement callStmt = conn.prepareCall(procedureCall);
            callStmt.registerOutParameter(2, Types.VARCHAR);
            callStmt.setInt(1, id);
            callStmt.execute();
            result = callStmt.getString(2);
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        // Denselben Aufruf abgewandelt in einer SQL-Datei speichern:
        String sql = "DECLARE o_result VARCHAR2(15); BEGIN prc_reset_" + kennung + "(" + id + ", o_result); END;";
        streams.writeFile(sql);
    }

}

//  -------------------- Beispiel für SQL-Befehle und PreparedStatements ohne Prozeduren: --------------------------

    /* public void addUebung(Uebung uebung) {
        this.conn = getConnection();
        ps = null;
        File image = uebung.getBild() != null ? uebung.getBild() : new File("src/main/webapp/resources/images/default.jpg");

        // PreparedStatement oder CallableStatement kann nicht als String ausgelesen werden. Um eine Textdatei schreiben
        // zu können, legen wir den Befehl noch einmal auf die alte, umständliche Art an:
        try {
            String sqlWriteFile = "insert into UEBUNG (bezeichnung_uebung, beschreibung_uebung, schwierigkeitsgrad, " +
                    "verletzungsrisiko, uebungskategorie, bild_uebungsablauf) values ('"
                    + uebung.getBezeichnung() + "', '"
                    + uebung.getBeschreibung() + "', "
                    + getId("SCHWIERIGKEITSGRAD", uebung.getSchwierigkeitsgrad()) + ", "
                    + getId("VERLETZUNGSRISIKO", uebung.getVerletzungsrisiko()) + ", "
                    + getId("UEBUNGSKATEGORIE", uebung.getKategorie()) + ", "
                    + image + ")";
            // SQL-Befehl in eine Datei schreiben:
            streams.writeFile(sqlWriteFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tatsächlicher INSERT, Variante mit PreparedStatement:
        String sql = "insert into UEBUNG (bezeichnung_uebung, beschreibung_uebung, schwierigkeitsgrad, " +
                    "verletzungsrisiko, uebungskategorie, bild_uebungsablauf) values (?, ?, ?, ?, ?, ?)"; // "?" ist ein Platzhalter
        try {
            FileInputStream fis = new FileInputStream(image);
            ps = conn.prepareStatement(sql);
            ps.setString(1, uebung.getBezeichnung()); // setString befüllt den Platzhalter "?"
            ps.setString(2, uebung.getBeschreibung());
            ps.setInt(3, getId("SCHWIERIGKEITSGRAD", uebung.getSchwierigkeitsgrad()));
            ps.setInt(4, getId("VERLETZUNGSRISIKO", uebung.getVerletzungsrisiko()));
            ps.setInt(5, getId("UEBUNGSKATEGORIE", uebung.getKategorie()));
            ps.setBlob(6, uebung.setBinaryStream(5, fis, (int) image.length());
            ps.execute(); // NICHT executeQuery! Query nur zum Auslesen, nicht für Inserts/Updates
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        */