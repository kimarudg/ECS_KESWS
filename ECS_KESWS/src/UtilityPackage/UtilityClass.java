/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UtilityPackage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import xmlparser.KESWSConsignmentDoc;

/**
 *
 * @author kim
 */
public class UtilityClass {

    public java.sql.Date getCurrentDate() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Date(today.getTime());
    }

    /**
     *
     * @param a
     * @return
     */
    public boolean checkNull(String... strings) {
        for (String string : strings) {
            if (string == null || string.isEmpty()) {
                return false;
            }
        }
        return true;
    }



    public String getCurrentTime() {
        Date curdate = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf1.format(curdate);
    }
}
