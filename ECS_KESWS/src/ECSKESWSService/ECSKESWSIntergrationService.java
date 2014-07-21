package ECSKESWSService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import logger.ECSKESWSLogger;

public class ECSKESWSIntergrationService {

    private static ECSKESWSIntergrationService serviceInstance = new ECSKESWSIntergrationService();
    private IncomingMessageProcessor incomingmessageprocessor = new IncomingMessageProcessor();
    private OutgoingMessageProcessor outgoingmessageprocessor = new OutgoingMessageProcessor();
    private boolean stopped = false;

    public static void main(String[] args) {

        String cmd = "start";
        if (args.length > 0) {
            cmd = args[0];
        }
        if ("start".equals(cmd)) {
            serviceInstance.start();
        } else {
            serviceInstance.stop();
        }

        /**
         * // TODO Auto-generated method stub try { ECSKESWSLogger.setup();
         * (new IncomingMessageProcessor()).start(); (new
         * OutgoingMessageProcessor()).start();
         *
         * } catch (IOException e) { // TODO Auto-generated catch block
         * e.printStackTrace(); ECSKESWSLogger.Log(e.toString());
         * ECSKESWSLogger.maillogs(); }
         *
         */
    }

    /**
     * Start this service instance
     */
    public void start() {

        stopped = false;
        try {
            ECSKESWSLogger.setup();
        } catch (IOException ex) {
            Logger.getLogger(ECSKESWSIntergrationService.class.getName()).log(Level.SEVERE, null, ex);
        }
      incomingmessageprocessor.start();
      outgoingmessageprocessor.start();
      System.out.println("Number of active threads from the given thread: " + Thread.activeCount());
        while (!stopped) {

            synchronized (this) {
                try {
                    this.wait(60000);  // wait 1 minute
                } catch (InterruptedException ie) {
                }
            }
        }

    }

    /**
     * Stop this service instance
     */
    public void stop() {
        stopped = true;
        incomingmessageprocessor.stopIncomingMessageProcessor();
        outgoingmessageprocessor.stopOutgoingMessageProcessor();
        synchronized (this) {
            this.notify();
        }
        
         
    }
}
