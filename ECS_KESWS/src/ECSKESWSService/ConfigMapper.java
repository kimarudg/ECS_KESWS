package ECSKESWSService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import logger.ECSKESWSLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

final public class ConfigMapper {

    private static String InboxFolder;
    private static String OutboxFolder;
   // private static String IntergrationDatabaseUrl;
    private static String ECSDatabaseUrl;
    private static String ECSDatabaseuser;
    private static String ECSDatabasepassword;
   // private static String IntergrationDatabaseuser;
   // private static String IntergrationDatabasepassword;
    private static String LogLevel;
    private static String LogFile;
    private static String MHXUserProfileFilePath;
    private static String InboxArchiveFolder;
    private static String OutboxArchiveFolder;
    private static String ProcessingFolder;
    private static String errorFiles;
    private static List<String> FilesTypestoReceive;
    private static String SenderId;
    private static String Email1;
    private static String Email2;

    public ConfigMapper() {
        try {
            File fXmlFile = new File("config.xml");
            FilesTypestoReceive = new ArrayList<String>();
            //  System.out.println("Config file path "+fXmlFile.getAbsolutePath());
            // ECSKESWSLogger.Log(fXmlFile.getAbsolutePath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("applicationconfiguration");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    setInboxFolder(eElement.getElementsByTagName("InboxFolder").item(0).getTextContent());
                    setOutboxFolder(eElement.getElementsByTagName("OutboxFolder").item(0).getTextContent());
                    setErrorFolder(eElement.getElementsByTagName("AdditionalFolders").item(0).getTextContent());
                    setOutboxArchiveFolder(eElement.getElementsByTagName("OutboxArchiveFolder").item(0).getTextContent());
                    setECSDatabaseUrl(eElement.getElementsByTagName("ECSDatabaseUrl").item(0).getTextContent());
                    setECSDatabaseuser(eElement.getElementsByTagName("ECSDatabaseuser").item(0).getTextContent());
                    setECSDatabasepassword(eElement.getElementsByTagName("ECSDatabasepassword").item(0).getTextContent());
                   // setIntergrationDatabasename(eElement.getElementsByTagName("IntergrationDatabaseurl").item(0).getTextContent());
                   // setIntergrationDatabaseuser(eElement.getElementsByTagName("IntergrationDatabaseuser").item(0).getTextContent());
                   // setIntergrationDatabasepassword(eElement.getElementsByTagName("IntergrationDatabasepassword").item(0).getTextContent());
                    setMHXUserProfileFilePath(eElement.getElementsByTagName("MHXUserProfileFilePath").item(0).getTextContent());
                    setLogFile(eElement.getElementsByTagName("LogFile").item(0).getTextContent());
                    setInboxArchiveFolder(eElement.getElementsByTagName("InboxArchiveFolder").item(0).getTextContent());
                    setOutboxArchiveFolder(eElement.getElementsByTagName("OutboxArchiveFolder").item(0).getTextContent());
                    setProcessingFolder(eElement.getElementsByTagName("ProcessingFolder").item(0).getTextContent());
                    setSenderId(eElement.getElementsByTagName("SenderId").item(0).getTextContent());
                    setEmail1(eElement.getElementsByTagName("Email1").item(0).getTextContent());
                    setEmail2(eElement.getElementsByTagName("Email2").item(0).getTextContent());
                    for (int temp1 = 1; temp1 < 23; temp1++) {
                        setFilesTypestoReceive(eElement.getElementsByTagName("InboxFilesType" + (temp1) + "toReceive").item(0).getTextContent());
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ConfigMapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ConfigMapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfigMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getInboxFolder() {
        return InboxFolder;
    }

    public static void setInboxFolder(String kESWSXMLSourceFolder) {
        InboxFolder = kESWSXMLSourceFolder;
    }
   public static String getErrorFolder() {
        return errorFiles;
    }

    public static void setErrorFolder(String ErrorFiles) {
        errorFiles = ErrorFiles;
    }

    public static String getOutboxFolder() {
        return OutboxFolder;
    }

    public static void setOutboxFolder(String kESWSXMLResponseFolder) {
        OutboxFolder = kESWSXMLResponseFolder;
    }

    public static String getLogLevel() {
        return LogLevel;
    }

    public static void setLogLevel(String logLevel) {
        LogLevel = logLevel;
    }

    public static String getLogFile() {
        return LogFile;
    }

    public static void setLogFile(String logFile) {
        LogFile = logFile;
    }

    public static String getECSDatabaseUrl() {
        return ECSDatabaseUrl;
    }

    public static void setECSDatabaseUrl(String eCSDatabaseUrl) {
        ECSDatabaseUrl = eCSDatabaseUrl;
    }

    public static String getSenderId() {
        return SenderId;
    }

    public static void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public static String getEmail1() {
        return Email1;
    }

    public static void setEmail1(String email1) {
        Email1 = email1;
    }

    public static String getEmail2() {
        return Email2;
    }

    public static void setEmail2(String email2) {
        Email2 = email2;
    }

    public static String getECSDatabaseuser() {
        return ECSDatabaseuser;
    }

    public static void setECSDatabaseuser(String eCSDatabaseuser) {
        ECSDatabaseuser = eCSDatabaseuser;
    }

    public static String getECSDatabasepassword() {
        return ECSDatabasepassword;
    }

    public static void setECSDatabasepassword(String eCSDatabasepassword) {
        ECSDatabasepassword = eCSDatabasepassword;
    }

    public static String getMHXUserProfileFilePath() {
        return MHXUserProfileFilePath;
    }

    public static void setMHXUserProfileFilePath(String mHXUserProfileFilePath) {
        MHXUserProfileFilePath = mHXUserProfileFilePath;
    }

    public static String getInboxArchiveFolder() {
        return InboxArchiveFolder;
    }

    public static void setInboxArchiveFolder(
            String kESWSXMLInboxArchiveFolder) {
        InboxArchiveFolder = kESWSXMLInboxArchiveFolder;
    }

    public static String getProcessingFolder() {
        return ProcessingFolder;
    }

    public static void setProcessingFolder(String processingFolder) {
        ProcessingFolder = processingFolder;
    }

    public static String getOutboxArchiveFolder() {
        return OutboxArchiveFolder;
    }

    public static void setOutboxArchiveFolder(String outboxArchiveFolder) {
        OutboxArchiveFolder = outboxArchiveFolder;
    }

    public static List<String> getFilesTypestoReceive() {
        return FilesTypestoReceive;
    }

    public static void setFilesTypestoReceive(String FiletoReceive) {
        //System.out.println("file :" + FiletoReceive);
        boolean add = FilesTypestoReceive.add(FiletoReceive);
    }
    /**
    public static String getIntergrationDatabasename() {
        return IntergrationDatabaseUrl;
    }

    public static void setIntergrationDatabasename(
            String intergrationDatabasename) {
        IntergrationDatabaseUrl = intergrationDatabasename;
    }

    public static String getIntergrationDatabaseuser() {
        return IntergrationDatabaseuser;
    }

    public static void setIntergrationDatabaseuser(
            String intergrationDatabaseuser) {
        IntergrationDatabaseuser = intergrationDatabaseuser;
    }

    public static String getIntergrationDatabasepassword() {
        return IntergrationDatabasepassword;
    }

    public static void setIntergrationDatabasepassword(
            String intergrationDatabasepassword) {
        IntergrationDatabasepassword = intergrationDatabasepassword;
    }
  **/
}
