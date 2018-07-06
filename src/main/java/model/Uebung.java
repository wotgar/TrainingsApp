package model;

import java.io.*;;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Uebung {
    int id;
    private String bezeichnung, beschreibung, verletzungsrisiko, schwierigkeitsgrad, kategorie, muskelgruppe;
    private File bild;
    private byte[] image;
    String encodedString;

    public String getBildName() {
        return bild.getName();
    }

    private List<String> muskelgruppen = new ArrayList<>();

    // Constructors without ID for new entries
    public Uebung(String bezeichnung, String beschreibung, String kategorie, String schwierigkeitsgrad,
                  String verletzungsrisiko, String muskelgruppe, File bild) {
        this.bezeichnung = bezeichnung;
        this.beschreibung = beschreibung;
        this.verletzungsrisiko = verletzungsrisiko;
        this.schwierigkeitsgrad = schwierigkeitsgrad;
        this.kategorie = kategorie;
        this.muskelgruppe = muskelgruppe;
        this.bild = bild;
    }

    public Uebung(String bezeichnung, String beschreibung, String kategorie, String schwierigkeitsgrad,
                  String verletzungsrisiko, List<String> muskelgruppen, File bild) {
        this.bezeichnung = bezeichnung;
        this.beschreibung = beschreibung;
        this.verletzungsrisiko = verletzungsrisiko;
        this.schwierigkeitsgrad = schwierigkeitsgrad;
        this.kategorie = kategorie;
        this.muskelgruppen = muskelgruppen;
        this.bild = bild;
    }

    // Constructors with ID for entries read from database
    public Uebung(int id, String bezeichnung, String beschreibung, String kategorie, String schwierigkeitsgrad,
                  String verletzungsrisiko, String muskelgruppe, File bild) {
        this.id = id;
        this.bezeichnung = bezeichnung;
        this.beschreibung = beschreibung;
        this.verletzungsrisiko = verletzungsrisiko;
        this.schwierigkeitsgrad = schwierigkeitsgrad;
        this.kategorie = kategorie;
        this.muskelgruppe = muskelgruppe;
        this.bild = bild;
    }

    public Uebung(int id, String bezeichnung, String beschreibung, String kategorie, String schwierigkeitsgrad,
                  String verletzungsrisiko, List<String> muskelgruppen, File bild) {
        this.id = id;
        this.bezeichnung = bezeichnung;
        this.beschreibung = beschreibung;
        this.verletzungsrisiko = verletzungsrisiko;
        this.schwierigkeitsgrad = schwierigkeitsgrad;
        this.kategorie = kategorie;
        this.muskelgruppen = muskelgruppen;
        this.bild = bild;
    }

    public Uebung(int id, String bezeichnung, String beschreibung, String kategorie, String schwierigkeitsgrad,
                  String verletzungsrisiko, String muskelgruppe, File bild, byte[] image) {
        this.id = id;
        this.bezeichnung = bezeichnung;
        this.beschreibung = beschreibung;
        this.verletzungsrisiko = verletzungsrisiko;
        this.schwierigkeitsgrad = schwierigkeitsgrad;
        this.kategorie = kategorie;
        this.muskelgruppe = muskelgruppe;
        this.bild = bild;
        this.image = image;
    }

    public Uebung(int id, String bezeichnung, String beschreibung, String kategorie, String schwierigkeitsgrad,
                  String verletzungsrisiko, List<String> muskelgruppen, byte[] image) {
        this.id = id;
        this.bezeichnung = bezeichnung;
        this.beschreibung = beschreibung;
        this.verletzungsrisiko = verletzungsrisiko;
        this.schwierigkeitsgrad = schwierigkeitsgrad;
        this.kategorie = kategorie;
        this.muskelgruppen = muskelgruppen;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public String getVerletzungsrisiko() {
        return verletzungsrisiko;
    }

    public String getSchwierigkeitsgrad() {
        return schwierigkeitsgrad;
    }

    public String getKategorie() {
        return kategorie;
    }

    public String getMuskelgruppe() {
        return muskelgruppe;
    }

    public List<String> getMuskelgruppen() {
        return muskelgruppen;
    }

    public File getBild() {
        return bild;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getMuskelgruppenString() {
        String string = "";
        for (String s : muskelgruppen) {
            string += s + ", ";
        }
        string = string.substring(0, string.length()-2);
        return string;
    }

    public String getEncodedString() {
        String preset = "data:image/jpg;base64,";
        encodedString = Base64.getEncoder().encodeToString(image);
        return preset+encodedString;
    }

    @Override
    public String toString() {
        return "Uebung{" +
                "bezeichnung='" + bezeichnung + '\'' +
                ", beschreibung='" + beschreibung + '\'' +
                ", verletzungsrisiko='" + verletzungsrisiko + '\'' +
                ", schwierigkeitsgrad='" + schwierigkeitsgrad + '\'' +
                ", kategorie='" + kategorie + '\'' +
                ", muskelgruppe='" + muskelgruppe + '\'' +
                '}';
    }


}
