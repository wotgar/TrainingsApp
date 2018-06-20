package beans;

import database.DatenbankVerbindung;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;

@ManagedBean
@ApplicationScoped
public class ImageBean {
//----------------------------------------------------------------------------------------------------------------------
    private byte[] byteArray = null;
    private StreamedContent image;
    private int id;
//----------------------------------------------------------------------------------------------------------------------

    // In diesem Gebilde fand ich keine Lösung, wie ich das richtige byteArray aus dem Front End übergeben sollte
    @PostConstruct
    public void init() {
        image = new DefaultStreamedContent(new ByteArrayInputStream(byteArray)); // your byte array
    }
    public byte[] getByteArray() {
        return byteArray;
    }
    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }
    public StreamedContent getImage() {
        return image;
    }
//----------------------------------------------------------------------------------------------------------------------

    // Diese Variante funktioniert zwar, aber nur mit der statischen ID 5. Somit ist auch nicht klar, ob unterschiedliche
    // Bilder geladen werden würden.
    public StreamedContent getMyImage() throws IOException {
        byte[] buffer = null;
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
        System.out.println("PARAMS MAP: " + params);
        String param = params.get("uebungId");
        System.out.println("PARAM: " + param);
        //int uebungId = (int) Integer.parseInt(param);
        if (fc.getRenderResponse()) {
            // Rendering the HTML. Return a stub StreamedContent so
            // that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // Browser is requesting the image. Return a real
            // StreamedContent with the image bytes.
            try {
                buffer = new DatenbankVerbindung().getProductImage(5);
            } catch (SQLException e) {
                // e.printStackTrace();
            }
            InputStream input = new ByteArrayInputStream(buffer);
            StreamedContent stream = new DefaultStreamedContent(input,"image/jpeg");
            return stream;
        }
    }
//----------------------------------------------------------------------------------------------------------------------

    // Diese Variante lädt alle Bilder mit der zuletzt eingegebenen ID, sprich, jedes Feld bekommt letztlich dasselbe Bild
    public StreamedContent myImage(int uebungId) throws IOException {
        byte[] buffer = null;
        FacesContext fc = FacesContext.getCurrentInstance();
        System.out.println("ID+: " + uebungId);
        System.out.println("THIS ID: " + this.id);
        if (fc.getRenderResponse()) {
            // Rendering the HTML. Return a stub StreamedContent so
            // that it will generate right URL.
            this.id = uebungId;
            return new DefaultStreamedContent();
        } else {
            // Browser is requesting the image. Return a real
            // StreamedContent with the image bytes.
            try {
                buffer = new DatenbankVerbindung().getProductImage(this.id);
            } catch (SQLException e) {
                // e.printStackTrace();
            }
            if (buffer != null) {
                InputStream input = new ByteArrayInputStream(buffer);
                StreamedContent stream = new DefaultStreamedContent(input,"image/jpeg");
                return stream;
            } else {
                return null;
            }
        }
    }

}
