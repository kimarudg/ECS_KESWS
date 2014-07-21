package ECSKESWSService;

import UtilityPackage.UtilityClass;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import logger.ECSKESWSLogger;
import databaselayer.DBDAO;
import xmlmapper.XmlFileMapper;
import xmlparser.ECSConsignmentDoc;
import xmlparser.KESWSConsignmentDoc;
import fileProcessor.FileProcessor;
import java.io.FileNotFoundException;
import java.util.ConcurrentModificationException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import xmlmapper.XMLFileValidator;

/**
 * @author kim
 *
 */
public class IncomingMessageProcessor extends Thread {

    private XmlFileMapper mapper;
    private DBDAO dbao;
    private FileProcessor fileprocessor;//Class used for processing files and retriving and moving  files
    private volatile boolean stop = false;// used to stop thread
    private static Lock lock = new ReentrantLock();//locking mechanism to have just one thread run

    @Override
    public void run() {
        boolean endthread = false;

        //check locking mechanism to have just one instance of the thread run
        try {
            if (IncomingMessageProcessor.lock.tryLock()) {
                while (!stop && !endthread) {

                    fileprocessor = new FileProcessor();
                    try{
                    fileprocessor.retrieveMessage(ConfigMapper.getMHXUserProfileFilePath(), ConfigMapper.getSenderId(), ConfigMapper.getInboxFolder(), true);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        // send notification 
                    }
                    fileprocessor.readInboxXmlFilesForProcessor(ConfigMapper.getInboxFolder());
                    final List<String> inboxfiles = fileprocessor.getInboxFilesForProcessing();
                    while (inboxfiles.size() >= 1) {


                        String fileName = (String) inboxfiles.get(inboxfiles.size() - 1);
                        /**
                         * configuration to pull file types 1-22
                         *
                         */
                        try {
                            if ((fileName.contains(ConfigMapper.getFilesTypestoReceive().get(0).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(1).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(2).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(3).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(4).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(5).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(6).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(7).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(8).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(9).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(10).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(11).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(12).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(13).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(14).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(15).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(16).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(17).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(18).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(19).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(20).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(21).toString())
                                    || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(22).toString()))) {
                                try {
                                    //String transactionType, String transactionDetails, String KESWSID, String ECSInvoiceNo, String relatedTransactionDetails, String fileDetails) {

                                    dbao = new DBDAO();
                                    int transactionid = dbao.trackTransactionDetails("PROCESS " + fileName.substring(12, 20), "RECEIPT DOCUMENT " + fileName, 0, " ", " ", " ", fileName);
                                    int lasttransactionid = dbao.getLastTransactionId();
                                    //ensure its just one item transacted at a time using db table
                                   System.out.println("lasttransactionid" +lasttransactionid+"transactionid" +transactionid+" FILE NAME"+fileName);
                                    if (transactionid == lasttransactionid) {
                                        mapper = new XmlFileMapper();
                                        JAXBContext context = null;
                                        context = JAXBContext.newInstance(KESWSConsignmentDoc.class);
                                        Unmarshaller um = null;
                                        um = context.createUnmarshaller();
                                        KESWSConsignmentDoc keswsConsignmentDocumentObj = null;
                                        ECSConsignmentDoc ecsConsignmentDocumentObj = null;
                                        keswsConsignmentDocumentObj = (KESWSConsignmentDoc) um.unmarshal(new FileReader(ConfigMapper.getInboxFolder() + fileName));
                                        boolean validfile = false;
                                        boolean validfilefortransaction = false;
                                        XMLFileValidator xmlvalidator = new XMLFileValidator();
                                        validfile = xmlvalidator.validateAgainstXSD(ConfigMapper.getInboxFolder() + fileName, "C:\\ECSKESWS\\service\\MDA_CommonTypes1.xsd", "C:\\ECSKESWS\\service\\CONDOC1.xsd");
                                        if (validfile) {
                                            validfilefortransaction = true;
                                            dbao.trackTransactionDetails("FILESTATUS", " ", 1, " ", " ", "VALID FILE", fileName);
                                        } else {
                                            //check warehouse location
                                            //internal product details
                                            boolean validwarehouseLocation = false;
                                            boolean validinpd = false;
                                            UtilityClass uc = new UtilityClass();
                                            validwarehouseLocation = uc.checkNull(keswsConsignmentDocumentObj.getDocumentDetails()
                                                    .getConsignmentDocDetails().getCDExporter().getWarehouseCode());
                                            validwarehouseLocation = uc.checkNull(keswsConsignmentDocumentObj.getDocumentDetails().getConsignmentDocDetails()
                                                    .getCDExporter().getWarehouseLocation());
                                            validinpd = xmlvalidator.validinternalProductDetails(keswsConsignmentDocumentObj);
                                            if (validinpd && validwarehouseLocation) {
                                                validfile = true;
                                                validfilefortransaction = true;
                                                dbao.trackTransactionDetails("FILESTATUS", " ", 1, " ", " ", "VALID FILE", fileName);

                                            } else {
                                                dbao.trackTransactionDetails("FILESTATUS", " ", 0, " ", " ", xmlvalidator.getErrorDetails(), fileName);
                                                File file1 = new File(ConfigMapper.getInboxFolder()
                                                        + fileName);
                                                List<String> movelist = new LinkedList<String>();
                                                boolean add = movelist.add(fileName);
                                                inboxfiles.remove(fileName);
                                                if (add) {
                                                    fileprocessor.moveXmlFilesProcessed(
                                                            ConfigMapper.getInboxFolder(),
                                                            ConfigMapper.getErrorFolder(), movelist);
                                                }

                                                if (file1.delete()) {
                                                    //System.out.println(file1.getName() + " is deleted!");
                                                    break;
                                                } else {
                                                    // System.out.println("Delete operation is failed");
                                                }
                                            }
                                        }

                                        if (validfile && validfilefortransaction) {
                                            ecsConsignmentDocumentObj = mapper.getMappedECSConsignmentDoc(keswsConsignmentDocumentObj);
                                            dbao.createECSconsignmentdetails(ecsConsignmentDocumentObj, mapper.mapKESWSClientPintoECSClientId(keswsConsignmentDocumentObj));
                                            dbao.createECSconsignmentCertificate(dbao.getCreatedECSconsignmentId());
                                            dbao.createECSconsignmentVariatyQuantities(
                                                    keswsConsignmentDocumentObj,
                                                    ecsConsignmentDocumentObj,
                                                    dbao.getCreatedECSconsignmentId());
                                            List<String> processlist = new LinkedList<String>();
                                            boolean addprocesslist = processlist.add(fileName);
                                            if (addprocesslist) {
                                                fileprocessor.moveXmlFilesProcessed(
                                                        ConfigMapper.getInboxFolder(),
                                                        ConfigMapper.getProcessingFolder(),
                                                        processlist);
                                                for (Iterator<String> iterator2 = processlist.iterator(); iterator2
                                                        .hasNext();) {
                                                    String string = (String) iterator2.next();
                                                    //System.out.println("Source pl:" + ConfigMapper.getInboxFolder() + string);
                                                    //System.out.println("Destination pl:" + ConfigMapper.getProcessingFolder() + string);

                                                }
                                            }

                                        }



                                    }
                                } catch (JAXBException ex) {
                                    Logger.getLogger(IncomingMessageProcessor.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (FileNotFoundException ex) {
                                    Logger.getLogger(IncomingMessageProcessor.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            //  move to achieve alone or to error file

                            //delete file        
                            File file = new File(ConfigMapper.getInboxFolder()
                                    + fileName);
                            List<String> movelist = new LinkedList<String>();
                            boolean add = movelist.add(fileName);
                            inboxfiles.remove(fileName);
                            if (add) {
                                fileprocessor.moveXmlFilesProcessed(
                                        ConfigMapper.getInboxFolder(),
                                        ConfigMapper.getInboxArchiveFolder(), movelist);

                                for (Iterator<String> iterator2 =
                                        movelist.iterator(); iterator2.hasNext();) {
                                    String string = (String) iterator2.next(); //
                                    /**
                                     * System.out.println("Source in:" +
                                     * ConfigMapper.getInboxFolder() + string);
                                     * // System.out.println("Destination in:" +
                                     * ConfigMapper.getInboxArchiveFolder() +
                                     * string); *
                                     */
                                }


                            }
                            if (file.delete()) {
                                // System.out.println(file.getName() + " is deleted!");
                                break;
                            } else {
                                // System.out.println("Delete operation is failed.");
                            }
                        }

                        File file = new File(ConfigMapper.getInboxFolder()
                                + fileName);
                        List<String> movelist = new LinkedList<String>();
                        boolean add = movelist.add(fileName);
                        inboxfiles.remove(fileName);
                        if (add) {
                            fileprocessor.moveXmlFilesProcessed(
                                    ConfigMapper.getInboxFolder(),
                                    ConfigMapper.getInboxArchiveFolder(), movelist);
                        }
                        System.gc();
                        if (file.delete()) {
                            System.out.println(file.getName() + " is deleted!");
                            break;
                        } else {
                            System.out.println("Delete operation is failed");
                        }
                    }
                    try {
                        java.lang.Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }
        } finally {
            IncomingMessageProcessor.lock.unlock();
        }
    }

    public void stopIncomingMessageProcessor() {
        stop = true;
    }
}
