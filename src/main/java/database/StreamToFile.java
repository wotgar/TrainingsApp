package database;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;

import java.io.*;

public class StreamToFile {

    private String folder = "src/main/webapp/resources/images/";

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

}