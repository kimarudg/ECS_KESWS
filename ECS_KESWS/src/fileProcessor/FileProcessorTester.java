package fileProcessor;

import java.io.IOException;
import java.util.Calendar;

import xmlparser.KESWSConsignmentDoc;
import ECSKESWSService.ConfigMapper;
import com.sns.mhx.util.MHAccess;
import logger.ECSKESWSLogger;

public class FileProcessorTester {
	 public static void main(String[] args) throws IOException {
             String consignee="WUHU / SHANSHAN TEA (CO).,LTD";
           consignee=  consignee.replaceAll("[^A-Za-z0-9 ]", " ");
           System.out.println("consignee:" + consignee);
		  
	   // new ConfigMapper();
           //  ECSKESWSLogger.setup();
             //ECSKESWSLogger.mailnotification("wdwdwdwd");
		//FileProcessor fileprocessor=new FileProcessor();
		//fileprocessor.readInboxXmlFilesForProcessor(ConfigMapper.getInboxFolder());
		//System.out.println(ConfigMapper.getMHXUserProfileFilePath());
		//String profilePath = ConfigMapper.getMHXUserProfileFilePath();
 /**
                MHAccess mhx = new MHAccess();
boolean initialized = mhx.initUser(profilePath); 
if (initialized) 
{
 System.out.println("User profile initialised");
} 
else
{
 System.out.println("Error in initialisation");
 System.exit(1);
}

                //fileprocessor.retrieveMessage(ConfigMapper.getMHXUserProfileFilePath(), "keswsmhbuser", ConfigMapper.getMHXUserProfileFilePath(), true);
		
		/* *
		java.util.Date utilDate = new java.util.Date();  
		
		cal.setTime(utilDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);    
		java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime()); // your sql date
		System.out.println("utilDate:" + utilDate);
		System.out.println("sqlDate:" + sqlDate);
	
	  }
         **/
}
}
