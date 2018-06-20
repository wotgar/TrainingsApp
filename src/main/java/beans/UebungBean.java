package beans;

import database.DatenbankVerbindung;
import database.StreamToFile;
import model.Uebung;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "uebungBean")
@SessionScoped
public class UebungBean {
    private int id;
    private String bezeichnung, beschreibung, verletzungsrisiko, schwierigkeitsgrad, kategorie;
    private File bild;
    private UploadedFile uploadedFile;
    private List<Uebung> uebungen = new ArrayList<>();
    private List<String> muskelgruppen  = new ArrayList<>();
    private List<String> selectedMuskelgruppen  = new ArrayList<>();
    private List<String> kategorien  = new ArrayList<>();
    private List<String> grade  = new ArrayList<>();
    private List<String> risiken  = new ArrayList<>();

    public UebungBean() {
        refreshLists();
    }

    private void refreshLists() {
        uebungen = new DatenbankVerbindung().getUebungen();
        muskelgruppen = new DatenbankVerbindung().getStringList("MUSKELGRUPPE", "BEZEICHNUNG_MUSKELGRUPPE");
        kategorien = new DatenbankVerbindung().getStringList("UEBUNGSKATEGORIE", "BEZEICHNUNG_UEBUNGSKATEGORIE");
        grade = new DatenbankVerbindung().getStringList("SCHWIERIGKEITSGRAD", "BEZEICHNUNG_SCHWIERIGKEITSGRAD");
        risiken = new DatenbankVerbindung().getStringList("VERLETZUNGSRISIKO", "BEZEICHNUNG_VERLETZUNGSRISIKO");
    }

    public List<Uebung> getUebungen() {
        return uebungen;
    }

    public List<String> getMuskelgruppen() {
        return muskelgruppen;
    }

    public List<String> getKategorien() {
        return kategorien;
    }

    public List<String> getGrade() {
        return grade;
    }

    public List<String> getRisiken() {
        return risiken;
    }

    public void setSelectedMuskelgruppen(List<String> selectedMuskelgruppen) {
        this.selectedMuskelgruppen = selectedMuskelgruppen;
    }

    public List<String> getSelectedMuskelgruppen() {
        return selectedMuskelgruppen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getVerletzungsrisiko() {
        return verletzungsrisiko;
    }

    public void setVerletzungsrisiko(String verletzungsrisiko) {
        this.verletzungsrisiko = verletzungsrisiko;
    }

    public String getSchwierigkeitsgrad() {
        return schwierigkeitsgrad;
    }

    public void setSchwierigkeitsgrad(String schwierigkeitsgrad) {
        this.schwierigkeitsgrad = schwierigkeitsgrad;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public File getBild() {
        return bild;
    }

    public void setBild(File bild) {
        this.bild = bild;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }


    //--------------------------------------------- Database Methods ---------------------------------------------------

    public String addUebung() {
        Uebung u = new Uebung(bezeichnung, beschreibung, kategorie, schwierigkeitsgrad, verletzungsrisiko,
                selectedMuskelgruppen, bild != null ? bild : null);
        //System.out.println(u.toString());
        //for (String s : selectedMuskelgruppen) System.out.println(s);
        new DatenbankVerbindung().addUebung(u);
        refreshLists();
        return "index";
    }

    //--------------------------------------------- Uploaded File Methods ----------------------------------------------

    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("Successful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
        uploadedFile = event.getFile();
        try {
            bild = new StreamToFile().fileCopy(uploadedFile);
            System.out.println("BILDDATEI: " + bild);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
