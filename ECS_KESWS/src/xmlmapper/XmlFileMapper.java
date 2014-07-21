/**
 *
 */
package xmlmapper;

import UtilityPackage.UtilityClass;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import logger.ECSKESWSLogger;
import databaselayer.DBConnector;
import databaselayer.DBDAO;
import xmlparser.ECSConsignmentDoc;
import xmlparser.KESWSConsignmentDoc;
import xmlparser.KESWSConsignmentDoc.DocumentDetails.ConsignmentDocDetails.CDProductDetails.ItemDetails;
import xmlparser.KESWSConsignmentDoc.DocumentDetails.ConsignmentDocDetails.CDStandardTwo.Attachments;

/**
 * @author kim
 *
 */
public class XmlFileMapper {

    private ECSConsignmentDoc desObj;
    private KESWSConsignmentDoc souObj;
    private UtilityClass utilclass;

    /**
     * This class does mapping from the source XML to the destination XML with
     * special considerations to translations
     */
    public XmlFileMapper() {
        // TODO Auto-generated constructor stub

        utilclass = new UtilityClass();
    }

    // map client application date and time
    private int mapKESWSConsignmentMOCtoECSClientConsignmentMOC(
            KESWSConsignmentDoc SourceDoc) {
        DBDAO dbdao = new DBDAO();
        String MOC = SourceDoc.getDocumentDetails().getConsignmentDocDetails()
                .getCDTransport().getModeOfTransportDesc();
        return dbdao.getMOCId(MOC);

    }

    // map client id to client id
    public int mapKESWSClientConsigneeIdtoECSClientIdConsigneeId(
            KESWSConsignmentDoc SourceDoc) {
        int consigneeId = 0;
        String consigneeName;
        Integer CongineeIdRef;
        CongineeIdRef = 0;
        DBDAO dbdao = new DBDAO();
        consigneeName = SourceDoc.getDocumentDetails()
                .getConsignmentDocDetails().getCDConsignee().getName();
        String consigneeCountry = SourceDoc.getDocumentDetails()
                .getConsignmentDocDetails().getCDConsignee().getPosCountry();

        try {
            CongineeIdRef = CongineeIdRef.parseInt(SourceDoc.getDocumentDetails()
                    .getConsignmentDocDetails().getCDConsignee().getMDARefNo());



        } catch (Exception e) {
            CongineeIdRef = 0;
        }
        consigneeId = dbdao.getConsigneeId(consigneeName, mapKESWSClientPintoECSClientId(SourceDoc), consigneeCountry, CongineeIdRef);
        if (consigneeId == 0) {
            dbdao.createClientConsignee(SourceDoc);
        }
        consigneeId = dbdao.getConsigneeId(consigneeName,
                mapKESWSClientPintoECSClientId(SourceDoc), consigneeCountry, CongineeIdRef);
        return consigneeId;
    }

    // map client pin to client id
    public int mapKESWSClientPintoECSClientId(KESWSConsignmentDoc SourceDoc) {
        String pin;
        int clientid;
        DBDAO dbdao = new DBDAO();
        pin = SourceDoc.getDocumentDetails().getConsignmentDocDetails()
                .getCDExporter().getTIN();
        clientid = dbdao.getClientId(pin);
        if (clientid == 0) {
            dbdao.createClientDetails(SourceDoc);
        }
        clientid = dbdao.getClientId(pin);
        return clientid;
    }

    public ECSConsignmentDoc getMappedECSConsignmentDoc(
            final KESWSConsignmentDoc SourceDoc) {
        String p_inspectionDate = "";
        String marksandnumbers = "LABELLED";
        Integer shipmentdate = new Integer(utilclass.getCurrentTime().substring(0, 8));
        shipmentdate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()).toString());
        double totalconsignmrntweight = getTotalConsignementWeight(SourceDoc
                .getDocumentDetails().getConsignmentDocDetails()
                .getCDProductDetails().getItemDetails());
        try {
            p_inspectionDate = formatDateString2(SourceDoc.getDocumentDetails()
                    .getConsignmentDocDetails().getKEPHISHeaderFields()
                    .getPreferredInspectionDate());
            shipmentdate = SourceDoc.getDocumentDetails().getConsignmentDocDetails()
                    .getCDTransport().getShipmentDate();
            marksandnumbers = SourceDoc.getDocumentDetails()
                    .getConsignmentDocDetails().getCDTransport()
                    .getMarksAndNumbers();
        } catch (Exception e) {
            p_inspectionDate = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            shipmentdate = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()).toString());
        }
        ECSConsignmentDoc desObj = new ECSConsignmentDoc();
        desObj.setInvoicenr(SourceDoc.getDocumentHeader()
                .getDocumentReference().getCommonRefNumber());
        desObj.setConsigneeID(mapKESWSClientConsigneeIdtoECSClientIdConsigneeId(SourceDoc));
        desObj.setCountryofdestination(mapKESWSConsignmentCODtoECSConsignmentCOD(SourceDoc));
        desObj.setPointofentry(mapCountryKESWSConsignmentPOEtoECSConsignmentPOE(SourceDoc));
        desObj.setMeansofconveyance(mapKESWSConsignmentMOCtoECSClientConsignmentMOC(SourceDoc));
        desObj.setInspectionlocation(mapKESWSConsignmentILtoECSClientConsignmentIL(SourceDoc));
        desObj.setDateofdeparture(formatDateString(shipmentdate));
        desObj.setTimeofdeparture(p_inspectionDate);
        desObj.setPreferredinspectiondate(p_inspectionDate);
        desObj.setPreferredinspectiontime("01:11");
        desObj.setDistinguishingmarks(marksandnumbers);
        desObj.setConsignementweight(totalconsignmrntweight);
        desObj.setAdditionalinformation(getAttachDocumentCEI(SourceDoc.getDocumentDetails()
                .getConsignmentDocDetails().getCDStandardTwo().getAttachments()));
        return desObj;
    }

    private double getTotalConsignementWeight(List<ItemDetails> itemDetails) {
        double itemTotalWeight = 0;
        for (Iterator<ItemDetails> iterator = itemDetails.iterator(); iterator
                .hasNext();) {
            ItemDetails itemDetails2 = (ItemDetails) iterator.next();
            itemTotalWeight += itemDetails2.getCDProduct1()
                    .getItemNetWeight();
        }
        return itemTotalWeight;
    }

    private long getTotalNetConsignementWeight(List<ItemDetails> itemDetails) {
        long itemTotalWeight = 0;
        for (Iterator<ItemDetails> iterator = itemDetails.iterator(); iterator
                .hasNext();) {
            ItemDetails itemDetails2 = (ItemDetails) iterator.next();
            itemTotalWeight += itemDetails2.getCDProduct1()
                    .getItemNetWeight();
        }
        return itemTotalWeight;
    }

    private long getTotalGrossConsignementWeight(List<ItemDetails> itemDetails) {
        long itemTotalWeight = 0;
        for (Iterator<ItemDetails> iterator = itemDetails.iterator(); iterator
                .hasNext();) {
            ItemDetails itemDetails2 = (ItemDetails) iterator.next();
            itemTotalWeight += itemDetails2.getCDProduct1()
                    .getItemNetWeight();
        }
        return itemTotalWeight;
    }

    private String getAttachDocumentCEI(List<Attachments> attachmentDetails) {
        String commercialInvoiceNr = " ";
        for (Iterator<Attachments> iterator = attachmentDetails.iterator(); iterator
                .hasNext();) {
            Attachments Attachments2 = (Attachments) iterator.next();
            if (Attachments2.getAttachDocumentCode().contains("CEI")) {
                commercialInvoiceNr = Attachments2.getAttachDocumentRefNo();
            }
        }
        return commercialInvoiceNr;
    }

    private int mapKESWSConsignmentILtoECSClientConsignmentIL(
            KESWSConsignmentDoc SourceDoc) {
        int inspectionLocationId;
        DBDAO dbdao = new DBDAO();
        try {
            inspectionLocationId = dbdao.getECSInlocId(SourceDoc.getDocumentDetails()
                    .getConsignmentDocDetails().getCDExporter().getWarehouseCode(),
                    SourceDoc.getDocumentDetails().getConsignmentDocDetails()
                    .getCDExporter().getWarehouseLocation());
        } catch (Exception e) {
            inspectionLocationId = 1;
        }
        return inspectionLocationId;
    }

    private String mapCountryKESWSConsignmentPOEtoECSConsignmentPOE(
            KESWSConsignmentDoc SourceDoc) {
        String POE;
        POE = SourceDoc.getDocumentDetails().getConsignmentDocDetails().getCDTransport().getPortOfArrivalDesc();
        return POE;
    }

    private String mapKESWSConsignmentCODtoECSConsignmentCOD(
            KESWSConsignmentDoc SourceDoc) {
        String COD;
        DBDAO dbdao = new DBDAO();
        COD = SourceDoc.getDocumentDetails().getConsignmentDocDetails()
                .getCDConsignee().getPhyCountry();
        return dbdao.getECSCOD(COD);

    }

    public ECSConsignmentDoc getDesObj() {
        return desObj;
    }

    public void setDesObj(ECSConsignmentDoc desObj) {
        this.desObj = desObj;
    }

    public KESWSConsignmentDoc getSouObj() {
        return souObj;
    }

    public void setSouObj(KESWSConsignmentDoc souObj) {
        this.souObj = souObj;
    }

    public String formatDateString2(String date) {
        try {
            if (date.length() >= 8) {

                String year = date.substring(4, 8);
                String month = date.substring(2, 4);
                String day = date.substring(0, 2);
                date = year + "-" + month + "-" + day;
            } else {
                date = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                return date;
            }
        } catch (Exception e) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            return date;
        }
        return date;
    }

    public String formatDateString(String date) {
        try {
            if (date.length() >= 8) {
                String day = date.substring(6, 8);
                String month = date.substring(4, 6);
                String year = date.substring(0, 4);
                date = year + "-" + month + "-" + day;
            } else {
                date = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                return date;
            }
        } catch (Exception e) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            return date;
        }
        return date;
    }

    public String formatDateStringT(String date) { 
        date = 2013 + "-" + 01 + "-" + 01; 
        String day = date.substring(6, 8); 
        String month = date.substring(4, 6); 
        String year = date.substring(0, 4); 
        date = year + "-" + month + "-" + day; 
        return date;
    }

    public String formatDateStringTime(String time) { 
        String hr = time.substring(8, 10); 
        String min = time.substring(10, 12); 
        time = hr + ":" + min; 
        return time;
    }

    public String formatDateString(Integer date) { 
        if (date.toString(date).length() < 8) {
            date = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()).toString());
        }
        String day = date.toString().substring(6, 8); 
        String month = date.toString().substring(4, 6); 
        String year = date.toString().substring(0, 4); 
        String rdate = year + "-" + month + "-" + day; 
        return rdate;
    }

    public javax.xml.datatype.XMLGregorianCalendar stringToXMLGregorianCalendar(
            String s) throws ParseException, DatatypeConfigurationException {
        String ss = s.substring(12, 14);
        String mm = s.substring(10, 12);
        String HH = s.substring(8, 10);
        String day = s.substring(0, 2);
        String month = s.substring(2, 4);
        String year = s.substring(4, 8); 
        s = year + "-" + month + "-" + day + "T" + HH + ":" + mm + ":" + ss;
        javax.xml.datatype.XMLGregorianCalendar result = null;
        java.util.Date date;
        SimpleDateFormat simpleDateFormat;
        GregorianCalendar gregorianCalendar;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        date = simpleDateFormat.parse(s);
        gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        gregorianCalendar.setTime(date);
        result = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                gregorianCalendar);
        return result;
    }

    public int getProducerId(String producerName, int clientId,
            String productCountry) {
        Connection conn;
        int producerId = 0;
        if (producerName == null) {
            producerName = "unknown producer";
        }
        DBConnector dao = new DBConnector();
        conn = dao.GetECSDBConnector();
        Statement stmt;
        try {
            stmt = conn.createStatement();
            String sql;
            // Check using name and client id
            sql = "SELECT `ID`  FROM `PRODUCER` WHERE `FIRM_NAME` LIKE '%"
                    + producerName + "%' AND `CNT_ID` LIKE '%" + productCountry
                    + "%' AND `CLT_ID`=" + clientId + ";";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                producerId = rs.getInt("ID");
            } else {
                sql = "SELECT `ID`  FROM `PRODUCER` WHERE `CNT_ID` LIKE '%"
                        + productCountry + "%' AND `CLT_ID`=" + clientId + ";";
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    producerId = rs.getInt("ID");
                }

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ECSKESWSLogger.Log(e.toString(), "SEVERE");
        }
        return producerId;
    }
}
