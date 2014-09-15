package fileProcessor;

import ECSKESWSService.ConfigMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import xmlparser.KEPHISECSConsignmentInspectionReport;
import logger.ECSKESWSLogger;

import com.sns.base.Response;
import com.sns.mhx.util.MHAccess;
import com.sns.mhx.util.MHAccessUnix;
import java.util.logging.Level;

/**
 * 
 * @author kimarudg
 * @added added imports to get the date
 */
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FileProcessor {

	private List<String> InboxFilesForProcessing;
	private List<String> InboxFilesForArchiveing;
	private List<String> FilesbeingProcessed;
	private List<String> OutboxFilesForProcessing;
	private List<String> OutboxFilesForArchiveing;
	private final static Logger LOGGER = Logger.getLogger(FileProcessor.class
		      .getName());



	public FileProcessor() {
	 
	}

	public void readInboxXmlFilesForProcessor(String pathname) {
		List<String> results = new ArrayList<String>();
		File[] files = new File(pathname).listFiles();
		for (File file : files) {
			if (file.isFile()) {
				results.add(file.getName());
			}
		}
		setInboxFilesForProcessing(results);
	}
	public void readFilesBeingProcessed(String pathname) {
		List<String> results = new ArrayList<String>();
		File[] files = new File(pathname).listFiles();
		for (File file : files) {
			if (file.isFile()) {
				results.add(file.getName());
			}
		}
		setFilesbeingProcessed(results);
	}
	public void moveXmlFilesProcessed(String sourcePathname,
			String destPathname, List<String> files) {

		
            InputStream inStream = null;
                String destPath_name;
		OutputStream outStream = null;
                //get the current date and format it as as string
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String currentDate = dateFormat.format(date);
                //check if there is a directory
                /**/
                File theDir = new File (destPathname + currentDate );
                if (!theDir.exists()){
                    theDir.mkdir();
                }
                
                
                
                destPath_name = destPathname + currentDate +"\\";
                System.out.println("Destination is "+destPath_name);
		File [] sourceFiles = new File[files.size()+1] ;
		int i=0;
		for(String filename: files){
			sourceFiles[i]=new File(sourcePathname+filename);
			i++;
		}
		i=0;
		for (File sourcefile : sourceFiles) {
		try {
			if(sourcefile!= null && sourcefile.length() != 0){
				
			File sfile = sourcefile;
			File dfile = new File(destPath_name+files.get(i));
				
			inStream = new FileInputStream(sfile);
			outStream = new FileOutputStream(dfile);
			byte[] buffer = new byte[1024];
			int length;
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}
			inStream.close();
			outStream.close();
		    System.out.println("File is copied successful to destination!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			ECSKESWSLogger.Log(e.toString(), "SEVERE");
		}
		}
	}

	public void deleteXmlFilesProcessed(String pathname, List<String> files) {
		for (int i = 0; i < files.size(); i++) {
			String string = files.get(i);
			File file = new File(pathname + string);
			if (file.delete()) {
				System.out.println(file.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}
		}
	}

	// TODO read configuration details

	// TODO

	// TODO read xml file name

	// TODO return file name

	// TODO log read file name on transaction tables log status file read

	// TODO addFilesToInboxFolderForprocessing adds files to inbox folder for
	// processing
	// TODO uses Java API for MHX agent

	// TODO getInboxFilesForProcessing
	// TODO returns list of file from inbox folder
	// TODO setInboxFilesToProcess
	// TODO populates list of file from inbox folde
	// TODO moveProcessedInboxFilesToArchive
	// TODO removeProcessedInboxFiles

	public void  addFilesToInboxFolderForprocessing (){
		
	}
	
	public List<String> getInboxFilesForArchiveing() {
		return InboxFilesForArchiveing;
	}

	public void setInboxFilesForArchiveing(List<String> inboxFilesForArchiveing) {
		InboxFilesForArchiveing = inboxFilesForArchiveing;
	}

	public List<String> getInboxFilesForProcessing() {
		return InboxFilesForProcessing;
	}

	public void setInboxFilesForProcessing(List<String> inboxFilesForProcessing) {
		InboxFilesForProcessing = inboxFilesForProcessing;
	}

	public List<String> getOutboxFilesForProcessing() {
		return OutboxFilesForProcessing;
	}

	public void setOutboxFilesForProcessing(
			List<String> outboxFilesForProcessing) {
		OutboxFilesForProcessing = outboxFilesForProcessing;
	}

	public List<String> getOutboxFilesForArchiveing() {
		return OutboxFilesForArchiveing;
	}

	public void setOutboxFilesForArchiveing(
			List<String> outboxFilesForArchiveing) {
		OutboxFilesForArchiveing = outboxFilesForArchiveing;
	}
	
	public boolean retrieveMessage(String userProfileFilePath, String senderId, String responseFilePath, boolean responseFileByMsgId) {
        MHAccess mhAccess = new MHAccess();
            
        boolean isUserProfileOk = mhAccess.initUser(userProfileFilePath);
        
        
        if (!isUserProfileOk) {
        	
        	  LOGGER.severe("The user profile path " + userProfileFilePath + " is invalid or could not be used");
        	  LOGGER.severe("Failed to retrieve messsages for user");
                 // ECSKESWSLogger.mailnotification("The user profile path " + userProfileFilePath + " is invalid or could not be used "+"Failed to retrieve messsages for user");
                  
                  return false;
        }
        
        // prepare retrieve parameters
        Properties retrieveParameters = new Properties();
        retrieveParameters.put("filename", responseFilePath); // Response file path
        retrieveParameters.put("zipfile", new java.io.File(responseFilePath).getName() + ".zip"); // temp zip file name, will get deleted after use
        if (senderId != null)
        retrieveParameters.put("sender_id", senderId); // Response Sender
        retrieveParameters.put("sfl", "SUBM_DT"); // Retrieval Sort by - response message time
        retrieveParameters.put("sor", "A"); // Retrieval Sort order - ascending
        retrieveParameters.put("loc", "I"); // Retrieve from Inbox Archive A
        if (responseFileByMsgId) {
              retrieveParameters.put("split", "Y"); // split responses to individual file
              retrieveParameters.put("renamedownloadfile", "subj"); // rename downloaded file by message id
        } else
              retrieveParameters.put("split", "N"); // Combines all retrieved responses to single file
                    
        // Retrieve responses
        Response response = mhAccess.retrieveMsg(retrieveParameters);
        boolean isRetrieved = response.isOk();
        
        // Logout of MHAccess session
        mhAccess.endSession();
        
        if (isRetrieved) {
              @SuppressWarnings("rawtypes")
			List responses = (List) response.getReturnObj();
              // responses.get(0) - is status of the request
              Properties status = (Properties) responses.get(0);
              if ("0".equals(status.getProperty("requestStatus"))) {
                    // success response
                    for (int i = 1; i < responses.size(); i++) {
                          Map responseDetails = (Map) responses.get(i);
                          String messageId = (String) responseDetails.get("msgId");
                          String filePath = (String) responseDetails.get("path");
                          LOGGER.info("Retrieved " + messageId + " by the file " + filePath);
                          System.out.println("Retrieved " + messageId + " by the file " + filePath);
                          
                    }
              } else if ("Mailbox is empty".equals(status.getProperty("MailboxMsg"))){
                    // no messages for user
              }
              LOGGER.info("Retrieved " + (responses.size() - 1));
        } else
              LOGGER.info("No responses were received from " + senderId);
        
        return isRetrieved;
  }

	public static Logger getLogger() {
		return LOGGER;
	}
	public boolean submitMessage(String userProfileFilePath, String recipientId, String messageFilePath, String[] attachments, 
            String subject, String contentId, String documentType,String attachment) {
		 MHAccess mhAccess = new MHAccess();
      
      // Initialze user profile
      boolean isUserProfileOk = mhAccess.initUser(userProfileFilePath);
      if (!isUserProfileOk) {
            LOGGER.severe("The user profile path " + userProfileFilePath + " is invalid or could not be used");
            LOGGER.severe("Failed to submit messsage from the user");
            return false;
      }
      
      // Prepare submit parameters
      Properties submitParameters = new Properties();
      submitParameters.put("filename", messageFilePath); // EDI/message file path
      //submitParameters.put("attach", attachments);
      if (attachments != null && attachments.length > 0) {
            StringBuffer attachmentList = new StringBuffer();
            for (int i = 0; i < attachments.length; i++) {
                  if (attachmentList.length() > 0) attachmentList.append(",");
                  attachmentList.append(attachments[i]);
            }
            submitParameters.put("attachment_files", attachmentList.toString()); // attachment file paths
      }
      submitParameters.put("cont_type", "F"); // Free Text
      submitParameters.put("recip_id", recipientId); // Recipient Id
      if (subject != null)
            submitParameters.put("subj", subject); // Subject
      if (contentId != null)
            submitParameters.put("cont_id", contentId); // Content Id
      if (documentType != null)
            submitParameters.put("doc_type", documentType); // Document Type
      submitParameters.put("notifn", "D"); // Delivery notification
      //submitParameters.put("zipfile", ZipfileParameter);
      //LOGGER.severe("PROPERTIESFILE"+submitParameters);
      // Submit message
      Response response = mhAccess.submitMsg(submitParameters);
      boolean isSubmitted = response.isOk();
      
      // Logout of MHAccess session
      mhAccess.endSession();
      
      if (isSubmitted)
            LOGGER.info("Submitted " + messageFilePath + " to " + recipientId);
      else
            LOGGER.severe("Failed to submit message file " + messageFilePath + " to " + recipientId);
      
      return isSubmitted;
}
	public void createResponsefile(){
		KEPHISECSConsignmentInspectionReport responseObj;
		
		responseObj=new KEPHISECSConsignmentInspectionReport();
		
		
	}

	public List<String> getFilesbeingProcessed() {
		return FilesbeingProcessed;
	}

	public void setFilesbeingProcessed(List<String> filesbeingProcessed) {
		FilesbeingProcessed = filesbeingProcessed;
	}

}

