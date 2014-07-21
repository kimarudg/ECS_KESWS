package xmlmapper;

import UtilityPackage.UtilityClass;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import xmlparser.KESWSConsignmentDoc;

public class XMLFileValidator {

    private String ErrorDetails;
    
    public XMLFileValidator(){
    ErrorDetails =" ";
    }
    
    public String getErrorDetails() {
        return ErrorDetails;
    }

    public void setErrorDetails(String ErrorDetails) {
        this.ErrorDetails = ErrorDetails;
    }
    
    

    public  final boolean validateAgainstXSD(String SourceXml, String xsd, String xsd1) {

        try {
            Source[] schemaFile = new Source[2];
            schemaFile[0] = new StreamSource(new File(xsd));
            schemaFile[1] = new StreamSource(new File(xsd1));
            Source xmlFile = new StreamSource(new File(SourceXml));
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaFile);
            

            Validator validator = schema.newValidator();
           
            try {
                validator.validate(xmlFile);
                   System.out.println(xmlFile.getSystemId() + " is valid");
                   
                return true;
            } catch (SAXException e) {
                 System.out.println(xmlFile.getSystemId() + " is NOT valid");
                  System.out.println("Reason: " + e.getLocalizedMessage());
                  setErrorDetails("Reason: " + e.getLocalizedMessage().replace("'", " "));
                return false;
            } catch (IOException ex) {
                Logger.getLogger(XMLFileValidator.class.getName()).log(Level.SEVERE, null, ex);
                setErrorDetails("Reason: " + ex.getLocalizedMessage().replace("'", " "));
                return false;
            }
        } catch (SAXException ex) {
            Logger.getLogger(XMLFileValidator.class.getName()).log(Level.SEVERE, null, ex);
            setErrorDetails("Reason: " + ex.getLocalizedMessage().replace("'", " "));
            return false;
        }
       
   
    }
    public boolean validinternalProductDetails(KESWSConsignmentDoc doc) {
        boolean vpcode = false;
         UtilityClass uc=new UtilityClass();
        List<KESWSConsignmentDoc.DocumentDetails.ConsignmentDocDetails.CDProductDetails.ItemDetails> itemdetails;
        itemdetails = doc.getDocumentDetails().getConsignmentDocDetails().getCDProductDetails().getItemDetails();

        for (Iterator<KESWSConsignmentDoc.DocumentDetails.ConsignmentDocDetails.CDProductDetails.ItemDetails> iterator = itemdetails.iterator(); iterator
                .hasNext();) {
            KESWSConsignmentDoc.DocumentDetails.ConsignmentDocDetails.CDProductDetails.ItemDetails itemDetails2 = (KESWSConsignmentDoc.DocumentDetails.ConsignmentDocDetails.CDProductDetails.ItemDetails) iterator.next();
            vpcode = uc.checkNull(itemDetails2.getCDProduct1().getInternalProductNo());
            if (vpcode == false) {
                return vpcode;
            }
        }
        return vpcode;

    }
    
    /**
     * check inspection location check producer details check internal product
     * details
     *
     */
 
}
