package model;

import java.util.ArrayList;
import java.util.List;

public class Uebung {
    private String bezeichnung, beschreibung, verletzungsrisiko, schwierigkeitsgrad, kategorie, muskelgruppe;
    private List<String> muskelgruppen = new ArrayList<>();

    public Uebung(String bezeichnung, String beschreibung, String kategorie, String schwierigkeitsgrad,
                  String verletzungsrisiko, String muskelgruppe) {
        this.bezeichnung = bezeichnung;
        this.beschreibung = beschreibung;
        this.verletzungsrisiko = verletzungsrisiko;
        this.schwierigkeitsgrad = schwierigkeitsgrad;
        this.kategorie = kategorie;
        this.muskelgruppe = muskelgruppe;
    }

    public Uebung(String bezeichnung, String beschreibung, String kategorie, String schwierigkeitsgrad,
                  String verletzungsrisiko, List<String> muskelgruppen) {
        this.bezeichnung = bezeichnung;
        this.beschreibung = beschreibung;
        this.verletzungsrisiko = verletzungsrisiko;
        this.schwierigkeitsgrad = schwierigkeitsgrad;
        this.kategorie = kategorie;
        this.muskelgruppen = muskelgruppen;
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

    @Override
    public String toString() {
        return "Uebung{" +
                "bezeichnung='" + bezeichnung + '\'' +
                ", beschreibung='" + beschreibung + '\'' +
                ", verletzungsrisiko='" + verletzungsrisiko + '\'' +
                ", schwierigkeitsgrad='" + schwierigkeitsgrad + '\'' +
                ", kategorie='" + kategorie + '\'' +
                '}';
    }
}
