package database;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;

import java.io.*;

public class StreamToFile {

//----------------------------------------------------------------------------------------------------------------------

    // Verzeichnis der im Front End benutzten Bilddateien
    private String folder = "src/main/webapp/resources/images/";

//----------------------------------------------------------------------------------------------------------------------

    // Diese Methode kopiert eine Datei mittels Streams in eine temporäre Datei
    public File fileCopy(UploadedFile file) throws IOException {

        InputStream in = file.getInputstream();
        File temp = new File(folder + "tempImage.jpg");
        try {
            OutputStream out = new FileOutputStream(temp);
            IOUtils.copy(in, out);
            in.close();
            out.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        return temp;
    }

//----------------------------------------------------------------------------------------------------------------------

    // Diese Methode liest einen BLOB in eine eigens erstellte Datei
    public File readDatabaseStream(String entityName, InputStream in) {
        File temp = new File(folder + entityName + "Image.jpg");
        try {
            OutputStream out = new FileOutputStream(temp);
            IOUtils.copy(in, out);
            in.close();
            out.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        return temp;
    }

//----------------------------------------------------------------------------------------------------------------------
    //HINWEIS: Diese Methode funktioniert nicht wirklich. Das zurückgegebene Array scheint leer zu sein!
    // Diese Methode konvertiert einen BLOB Stream in ein byte array
    public byte[] convertToByteArray(InputStream in) {
        File temp = new File(folder + "default.jpg");
        byte[] image = null;
        if (in != null) {
            try {
                image = IOUtils.toByteArray(in);
                System.out.println("STREAM: " + in + "\nIMAGE: "+ image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                image = IOUtils.toByteArray(new FileInputStream(temp));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("IMAGE++++++: " + image);
        return image;
    }

//----------------------------------------------------------------------------------------------------------------------

    // Diese Methode verweist auf eine default-Datei und gibt diese als byte array zurück
    public byte[] defaultByteArray() {
        File temp = new File(folder + "default.jpg");
        byte[] image = null;
        try {
            image = IOUtils.toByteArray(new FileInputStream(temp));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

//----------------------------------------------------------------------------------------------------------------------

    // Diese Methode speichert Queries in einer SQL-Datei
    public void writeFile(String sql) {
        try {
            PrintWriter writer = new PrintWriter((new FileOutputStream(new File("java_inserts.sql"), true)));
            writer.print(sql + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}