package r01f.ejie.xlnets.model;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.util.types.Strings;

@Slf4j
@NoArgsConstructor
public final class XLNetsPrintWritter {

/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Imprime de forma legible un documento XML
     */
    public static void print(Document documento, Object streamSalida) {
        StreamResult result = null;

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty(OutputKeys.VERSION, "1.0");

            DOMSource source = new DOMSource(documento);

            if (streamSalida instanceof PrintStream)
                result = new StreamResult((PrintStream)streamSalida);
            else if (streamSalida instanceof PrintWriter)
                result = new StreamResult((PrintWriter)streamSalida);

            transformer.transform(source, result);
            return;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return;
        }
    }
    /**
     * Convierte un xml en cadena
     * @param documento
     * @return
     */
    public static String convertToString(Node documento) {
    	String outXml = "";
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.METHOD,"xml");
            transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
            transformer.setOutputProperty(OutputKeys.VERSION,"1.0");

            DOMSource source = new DOMSource(documento);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(stream);

            transformer.transform(source, result);

            outXml = stream.toString();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            log.error("Error converting the xml doc to String: {}",ex.getMessage(),ex);
            outXml = Strings.customized("Error converting the xml doc to String: {}\n{}",
            							ex.getMessage(),
            							Throwables.getStackTraceAsString(ex));
        }
        return outXml;
    }

}