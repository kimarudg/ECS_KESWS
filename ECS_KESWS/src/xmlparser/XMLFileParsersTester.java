package xmlparser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Formatter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import ECSKESWSService.ConfigMapper;
import xmlparser.KESWSConsignmentDoc.DocumentDetails;
import xmlparser.KESWSConsignmentDoc.DocumentDetails.ConsignmentDocDetails.CDProductDetails.ItemDetails;

public class XMLFileParsersTester {
	/*
	 * This file phrases the xml files based on predefined fields on the
	 * database source and destination and creates a source objects and
	 * destination objects.
	 
	
	public static void main(String[] args) throws JAXBException {
		 
	    System.out.println();
	    new ConfigMapper();
	    System.out.println("Output from our XML File: ");
	    JAXBContext context = JAXBContext.newInstance(KESWSConsignmentDoc.class);
	    Unmarshaller um = context.createUnmarshaller();
	    KESWSConsignmentDoc consignmentDocumentObj = null;
		try {
			consignmentDocumentObj = (KESWSConsignmentDoc) um.unmarshal(new FileReader( ConfigMapper.getKESWSXMLSourceFolder()+"CD2013KEPHISKEPHISPHC0000001622_1.xml"));
			consignmentDocumentObj.getDocumentHeader().getDocumentReference().getCommonRefNumber();
			 //mapKESWSClientConsigneeIdtoECSClientIdConsigneeId(consignmentDocumentObj);
		     //mapKESWSConsignmentCODtoECSConsignmentCOD(consignmentDocumentObj);
			 //mapCountryKESWSConsignmentPOEtoECSConsignmentPOE(consignmentDocumentObj);
			 //mapKESWSConsignmentMOCtoECSClientConsignmentMOC(consignmentDocumentObj);
			 //mapKESWSConsignmentILtoECSClientConsignmentIL(consignmentDocumentObj);
			 try {
				System.out.println(stringToXMLGregorianCalendar(consignmentDocumentObj.getDocumentDetails().getConsignmentDocDetails().getKEPHISHeaderFields().getPreferredInspectionDate()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DatatypeConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    DocumentDetails lists= consignmentDocumentObj.getDocumentDetails();
	     
	      System.out.println(lists.getConsignmentDocDetails().getCDExporter().tin);
	     
	
	}

public static javax.xml.datatype.XMLGregorianCalendar stringToXMLGregorianCalendar(String s) 
    throws ParseException, 
            DatatypeConfigurationException
{
	    String ss=s.substring(12,14);
	    String mm=s.substring(10,12);
	    String HH=s.substring(8, 10);
	    String day=s.substring(0, 2);
	    String month=s.substring(2, 4);
	    String year=s.substring(4, 8);
	    
	    s=year+"-"+month+"-"+day+"T"+HH+":"+mm+":"+ss;
javax.xml.datatype.XMLGregorianCalendar result = null;
java.util.Date date;
SimpleDateFormat simpleDateFormat;
GregorianCalendar gregorianCalendar;
simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                date =   simpleDateFormat.parse(s);        
                gregorianCalendar = 
                    (GregorianCalendar)GregorianCalendar.getInstance();
                gregorianCalendar.setTime(date);
                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
                return result;
}
**/
}
