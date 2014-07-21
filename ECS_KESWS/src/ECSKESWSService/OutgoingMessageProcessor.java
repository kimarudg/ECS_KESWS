package ECSKESWSService;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import logger.ECSKESWSLogger;
import xmlparser.KEPHISECSConsignmentInspectionReport;
import xmlparser.KESWSConsignmentDoc;
import databaselayer.DBDAO;
import fileProcessor.FileProcessor;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OutgoingMessageProcessor extends Thread {

    private volatile boolean stop = false;// used to stop thread
    private static Lock lock = new ReentrantLock();//locking mechanism to have just one thread run
    public OutgoingMessageProcessor() {
        // TODO Auto-generated constructor stub
    }
    /**
     * @param args
     */
    @Override
    public void run() {
        boolean endthread = false;
        try {
            if (OutgoingMessageProcessor.lock.tryLock()) {
                DBDAO dbao_omp = new DBDAO();
                FileProcessor fileprocessor = new FileProcessor();
                List<String> filesinQue = new ArrayList<String>();
                while (!stop && !endthread) {
                    fileprocessor.readFilesBeingProcessed(ConfigMapper.getProcessingFolder());
                    List<String> filespendingprocessing = fileprocessor.getFilesbeingProcessed();
                    for (Iterator<String> iterator = filespendingprocessing.iterator(); iterator.hasNext();) {
                        String fileName = (String) iterator.next();
                        String deleteFile = "";
                        if (fileName.contains(ConfigMapper.getFilesTypestoReceive().get(0).toString())
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
                                || fileName.contains(ConfigMapper.getFilesTypestoReceive().get(22).toString())) {
                            JAXBContext context = null;
                            try {
                                context = JAXBContext.newInstance(KESWSConsignmentDoc.class);
                                Unmarshaller um = null;
                                um = context.createUnmarshaller();
                                KESWSConsignmentDoc keswsConsignmentDocumentObj = null;
                                keswsConsignmentDocumentObj = (KESWSConsignmentDoc) um.unmarshal(new FileReader(ConfigMapper.getProcessingFolder() + fileName));
                                String InvoiceNumber = keswsConsignmentDocumentObj.getDocumentHeader().getDocumentReference().getCommonRefNumber();
                                Double versionNumber = Double.parseDouble(keswsConsignmentDocumentObj.getDocumentDetails().getConsignmentDocDetails().getCDStandard().getVersionNo());
                                String submittedTime = keswsConsignmentDocumentObj.getDocumentDetails().getConsignmentDocDetails().getCDStandard().getUpdatedDate();
                                // System.out.println(" s t"+keswsConsignmentDocumentObj.getDocumentDetails().getConsignmentDocDetails().getCDStandard().getUpdatedDate());
                                // Query file creation time if more than 48 hours delete the file 
                                Date date = new Date(new File(ConfigMapper.getProcessingFolder() + fileName).lastModified());
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                                String fileCreatedOnDate = sdf.format(date);
                                sdf.setTimeZone(TimeZone.getTimeZone("GMT+3"));
                                Date curdate = new Date();
                                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                                String curDate = sdf1.format(curdate);
                                sdf.setTimeZone(TimeZone.getTimeZone("GMT+3")); ;
                                Date d1 = sdf1.parse(fileCreatedOnDate);
                                Date d2 = sdf1.parse(curDate);
                                //System.out.println("File  date " + d1);
                                //System.out.println("Current date " + d2);
                                //System.out.println("FILE: " + fileName + " INVOICE NUMBER: " + InvoiceNumber + " Version Number: " + versionNumber + " FILE CREATION DATE: " + fileCreatedOnDate + " CURRENT DATE: " + curDate);
                                //in milliseconds
                                long diff = d2.getTime() - d1.getTime();
                                long diffSeconds = diff / 1000 % 60;
                                long diffMinutes = diff / (60 * 1000) % 60;
                                long diffHours = diff / (60 * 60 * 1000) % 24;
                                long diffDays = diff / (24 * 60 * 60 * 1000);
                                //System.out.print(diffDays + " days ");
                                //System.out.print(diffHours + " hours ");
                                // System.out.print(diffMinutes + " minutes ");
                                //System.out.print(diffSeconds + " seconds ");
                                // if days more than 1 delete item from processing box
                                // send reject message
                                // check issue status OR reject status id either generate file 
                                //System.out.println("File creation time"+new java.util.Date((long)new File(ConfigMapper.getProcessingFolder() + fileName).lastModified()*1000));
                                if (dbao_omp.getECSconsignmentStatus(InvoiceNumber).contains("SUBMITTED")) {
                                    //inspection status 0
                                    String additionaldetails = submittedTime;
                                    dbao_omp.trackTransactionDetails("SUBMITTEDINSPECTIONSTATUS", 0, fileName, additionaldetails);
                                    if (dbao_omp.issendResponseDetails1(fileName)) {
                                        KEPHISECSConsignmentInspectionReport resObj = new KEPHISECSConsignmentInspectionReport();
                                        KEPHISECSConsignmentInspectionReport.DocumentHeader resObjDocHeader = new KEPHISECSConsignmentInspectionReport.DocumentHeader();
                                        KEPHISECSConsignmentInspectionReport.DocumentDetails resObjDocDetails = new KEPHISECSConsignmentInspectionReport.DocumentDetails();
                                        resObjDocHeader.setMsgId("OGA_CD_RES");//OGA_CD_RES
                                        resObjDocHeader.setRefno(InvoiceNumber.substring(0, InvoiceNumber.length() - 2));
                                        String msgdate = new SimpleDateFormat("MMddyyyyHHmmss").format(new Date());
                                        resObjDocHeader.setMsgdate(msgdate);
                                        resObjDocHeader.setMsgFunc(9);
                                        resObjDocHeader.setReceiver("KESWS");
                                        resObjDocHeader.setSender("KEPHIS-ECS");
                                        resObjDocHeader.setVersion(versionNumber);
                                        resObjDocDetails.setApprovalType("D");//I and D
                                        resObjDocDetails.setCertificateNo("  ");
                                        resObjDocDetails.setConsignmentRefnumber(InvoiceNumber.substring(0, InvoiceNumber.length() - 2));
                                        resObjDocDetails.setInspectionRequired("Y");
                                        resObjDocDetails.setPermitNo(0);
                                        resObjDocDetails.setRemarks(dbao_omp.getECSFinalconsignmentInspectionResult(InvoiceNumber));
                                        resObjDocDetails.setStatus("AP");
                                        resObjDocDetails.setVarietyInspectionRemarks(dbao_omp.getECSconsignmentInspectionFindings(InvoiceNumber));
                                        resObjDocDetails.setVerNo("1");
                                        resObj.setDocumentHeader(resObjDocHeader);
                                        resObj.setDocumentDetails(resObjDocDetails);
                                        JAXBContext contextresObj = JAXBContext.newInstance(KEPHISECSConsignmentInspectionReport.class);
                                        Marshaller resObjm = contextresObj.createMarshaller();
                                        resObjm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                                        String file = ConfigMapper.getOutboxFolder() + "OG_CD_RES-" + InvoiceNumber.substring(0, InvoiceNumber.length() - 2) + "-1-" + "B-" + msgdate + ".xml";
                                        resObjm.marshal(resObj, new File(file));
                                        String[] attachments = new String[]{"", ""};
                                        String attachment = "";
                                        //if(true){
                                        if (fileprocessor.submitMessage(ConfigMapper.getMHXUserProfileFilePath(), ConfigMapper.getSenderId(), file, attachments, "OG_CD_RES-" + InvoiceNumber.substring(0, InvoiceNumber.length() - 2) + "-1-" + "B-" + msgdate + ".xml", "B", "OG_CD_RES", attachment)) {
                                            filesinQue.add(file);
                                            dbao_omp.setResponseStatusDetails(1, fileName, file, "RESPONSE1STATUS");
                                        }
                                    }
                                }
                                if (dbao_omp.getECSconsignmentStatus(InvoiceNumber).contains("PLANNED")) {
                                    //inspection status 0
                                    String additionaldetails = submittedTime;
                                    dbao_omp.trackTransactionDetails("PLANNEDINSPECTIONSTATUS", 0, fileName, additionaldetails); 
                                }
                                if (dbao_omp.getECSconsignmentStatus(InvoiceNumber).contains("PENDING")) {
                                    //inspection status 1
                                    String additionaldetails = submittedTime;
                                    dbao_omp.trackTransactionDetails("PENDINGINSPECTIONSTATUS", 1, fileName, additionaldetails); 
                                }
                                if (dbao_omp.getECSconsignmentStatus(InvoiceNumber).contains("REJECTED")) {
                                    //inspection status 1
                                    String additionaldetails = submittedTime;
                                    dbao_omp.trackTransactionDetails("REJECTEDINSPECTIONSTATUS", 1, fileName, additionaldetails); 
                                    if (dbao_omp.issendResponseDetails2(fileName)) {
                                        KEPHISECSConsignmentInspectionReport resObj2 = new KEPHISECSConsignmentInspectionReport();
                                        KEPHISECSConsignmentInspectionReport.DocumentHeader resObjDocHeader2 = new KEPHISECSConsignmentInspectionReport.DocumentHeader();
                                        KEPHISECSConsignmentInspectionReport.DocumentDetails resObjDocDetails2 = new KEPHISECSConsignmentInspectionReport.DocumentDetails();
                                        resObjDocHeader2.setMsgId("OGA_CD_RES");//OGA_CD_RES
                                        resObjDocHeader2.setRefno(InvoiceNumber.substring(0, InvoiceNumber.length() - 2));
                                        String msgdate2 = new SimpleDateFormat("MMddyyyyHHmmss").format(new Date());
                                        resObjDocHeader2.setMsgdate(msgdate2);
                                        resObjDocHeader2.setMsgFunc(9);
                                        resObjDocHeader2.setReceiver("KESWS");
                                        resObjDocHeader2.setSender("KEPHIS-ECS");
                                        resObjDocHeader2.setVersion(versionNumber);
                                        resObjDocDetails2.setApprovalType("I");//I and D
                                        resObjDocDetails2.setCertificateNo(dbao_omp.getECSCertificateDetails(InvoiceNumber));
                                        resObjDocDetails2.setConsignmentRefnumber(InvoiceNumber.substring(0, InvoiceNumber.length() - 2));
                                        resObjDocDetails2.setInspectionRequired("N");
                                        resObjDocDetails2.setPermitNo(0);
                                        resObjDocDetails2.setRemarks(dbao_omp.getECSFinalconsignmentInspectionResult(InvoiceNumber));
                                        resObjDocDetails2.setStatus("RJ");
                                        resObjDocDetails2.setVarietyInspectionRemarks(dbao_omp.getECSconsignmentInspectionFindings(InvoiceNumber));
                                        resObjDocDetails2.setVerNo("1");
                                        resObj2.setDocumentHeader(resObjDocHeader2);
                                        resObj2.setDocumentDetails(resObjDocDetails2);
                                        JAXBContext contextresObj2 = JAXBContext.newInstance(KEPHISECSConsignmentInspectionReport.class);
                                        Marshaller resObjm2 = contextresObj2.createMarshaller();
                                        resObjm2.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                                        //resObjm2.marshal(resObj2, System.out);
                                        String file2 = ConfigMapper.getOutboxFolder() + "OG_CD_RES-" + InvoiceNumber.substring(0, InvoiceNumber.length() - 2) + "-1-" + "B-" + msgdate2 + ".xml";
                                        resObjm2.marshal(resObj2, new File(file2));
                                        String[] attachments = new String[]{"", ""};
                                        String attachment = "";
                                        //  if(true){
                                        if (fileprocessor.submitMessage(ConfigMapper.getMHXUserProfileFilePath(), ConfigMapper.getSenderId(), file2, attachments, "OG_CD_RES-" + InvoiceNumber.substring(0, InvoiceNumber.length() - 2) + "-1-" + "B-" + msgdate2 + ".xml", "B", "OG_CD_RES", attachment)) {
                                            dbao_omp.setResponseStatusDetails(1, fileName, file2, "RESPONSE2STATUS");
                                        }
                                    }
                                }
                                if (dbao_omp.getECSconsignmentStatus(InvoiceNumber).contains("ISSUED")) {
                                    String additionaldetails = submittedTime;
                                    dbao_omp.trackTransactionDetails("ISSUEDINSPECTIONSTATUS", 1, fileName, additionaldetails);
                                    if (dbao_omp.issendResponseDetails2(fileName)) {
                                        KEPHISECSConsignmentInspectionReport resObj2 = new KEPHISECSConsignmentInspectionReport();
                                        KEPHISECSConsignmentInspectionReport.DocumentHeader resObjDocHeader2 = new KEPHISECSConsignmentInspectionReport.DocumentHeader();
                                        KEPHISECSConsignmentInspectionReport.DocumentDetails resObjDocDetails2 = new KEPHISECSConsignmentInspectionReport.DocumentDetails();
                                        resObjDocHeader2.setMsgId("OGA_CD_RES");//OGA_CD_RES
                                        resObjDocHeader2.setRefno(InvoiceNumber.substring(0, InvoiceNumber.length() - 2));
                                        String msgdate2 = new SimpleDateFormat("MMddyyyyHHmmss").format(new Date());
                                        resObjDocHeader2.setMsgdate(msgdate2);
                                        resObjDocHeader2.setMsgFunc(9);
                                        resObjDocHeader2.setReceiver("KESWS");
                                        resObjDocHeader2.setSender("KEPHIS-ECS");
                                        resObjDocHeader2.setVersion(versionNumber);
                                        resObjDocDetails2.setApprovalType("I");//I and D
                                        resObjDocDetails2.setCertificateNo(dbao_omp.getECSCertificateDetails(InvoiceNumber));
                                        resObjDocDetails2.setConsignmentRefnumber(InvoiceNumber.substring(0, InvoiceNumber.length() - 2));
                                        resObjDocDetails2.setInspectionRequired("N");
                                        resObjDocDetails2.setPermitNo(0);
                                        resObjDocDetails2.setRemarks(dbao_omp.getECSFinalconsignmentInspectionResult(InvoiceNumber));
                                        resObjDocDetails2.setStatus("AP");
                                        resObjDocDetails2.setVarietyInspectionRemarks(dbao_omp.getECSconsignmentInspectionFindings(InvoiceNumber));
                                        resObjDocDetails2.setVerNo("1");
                                        resObj2.setDocumentHeader(resObjDocHeader2);
                                        resObj2.setDocumentDetails(resObjDocDetails2);
                                        JAXBContext contextresObj2 = JAXBContext.newInstance(KEPHISECSConsignmentInspectionReport.class);
                                        Marshaller resObjm2 = contextresObj2.createMarshaller();
                                        resObjm2.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                                        //resObjm2.marshal(resObj2, System.out);
                                        String file2 = ConfigMapper.getOutboxFolder() + "OG_CD_RES-" + InvoiceNumber.substring(0, InvoiceNumber.length() - 2) + "-1-" + "B-" + msgdate2 + ".xml";
                                        resObjm2.marshal(resObj2, new File(file2));
                                        String[] attachments = new String[]{"", ""};
                                        String attachment = "";
                                        // if(true){
                                        if (fileprocessor.submitMessage(ConfigMapper.getMHXUserProfileFilePath(), ConfigMapper.getSenderId(), file2, attachments, "OG_CD_RES-" + InvoiceNumber.substring(0, InvoiceNumber.length() - 2) + "-1-" + "B-" + msgdate2 + ".xml", "B", "OG_CD_RES", attachment)) {
                                            dbao_omp.setResponseStatusDetails(1, fileName, file2, "RESPONSE2STATUS");
                                        }
                                    }

                                    if (diffMinutes > 30) {
                                        //email inspector
                                       // ECSKESWSLogger.mailnotification("Kindly check the consignment " + InvoiceNumber + " on ecs it has been pending for 1/2 hour");

                                    }
                                    if (diffHours > 1) {
                                        //email it support
                                       // ECSKESWSLogger.mailnotification("Kindly check the consignment " + InvoiceNumber + " on ecs it has been pending for 1 hour and will be deleted in one hours time");


                                    }

                                    if (diffHours > 2) {
                                        //delete
                                        deleteFile = fileName;

                                    }

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                ECSKESWSLogger.Log(e.toString(),"SEVERE");
                            }

                        }

                        /**
                         * *
                         * query database for consignements received and
                         * response messages not sent by checking on saved files
                         * in processing box and get application refrence query
                         * ecs database based on refrence id create response
                         * message object create response message file and save
                         * to out box send file remove files from processing box
                         * determine if response message is generated by the
                         * system check outbox if so send response messsage to
                         * KESWS if not query response message and create outbox
                         * if successfull else repeat process\ INSERT INTO
                         * `ecshscodepc`.`transaction_logs` (`ID`,
                         * `RELATEDTRANSACTIONID`, `TRANSACTIONTYPE`,
                         * `TRANSACTIONDETAILS`, `RECEIVESTATUS`, `RECEIVETIME`,
                         * `PROCESSSTATUS`, `PROCESSTIME`, `RESPONSESTATUS`,
                         * `REPONSETIME`, `ACHIVESTATUS`) VALUES (NULL, '0',
                         * '1', 'RECEIPT', '1', NULL, NULL, NULL, NULL, NULL,
                         * NULL);
                         *
                         */
                        //delete certificate 
                        File file = new File(ConfigMapper.getProcessingFolder() + deleteFile);
                        filespendingprocessing.remove(deleteFile);

                        if (file.delete()) {
                            //System.out.println(file.getName() + " is deleted!");
                            break;
                        } else {
                            //System.out.println("Delete operation is failed.");
                        }

                    }

                    try {
                        java.lang.Thread.sleep(120000);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }


            }
        } finally {
            OutgoingMessageProcessor.lock.unlock();
        }

    }

    public void stopOutgoingMessageProcessor() {
        stop = true;
    }
}
