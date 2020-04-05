package com.theoctopus.java.HybridRestAssured.Utilities.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Common {

    private static final Logger LOGGER = LoggerFactory.getLogger(Common.class);

    public static String getStackTrace(Exception e) {
        String sStackTrace = "";
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sStackTrace = "\n" + sw.toString(); // stack trace as a string

            return sStackTrace;
        } catch (Exception ex) {
            LOGGER.error( ex.getMessage());
            return sStackTrace;
        }
    }

    public static List<String> GetIterations(String sIterations, String Separator) {
        List<String> arrIterations = new ArrayList<String>();
        try {
            String min, max;
            String[] swap;
            int i, j;
            String[] arrRange;
            String[] arrTmp = sIterations.split(Separator);
            for (i = 0; i < arrTmp.length; i++) {
                arrRange = arrTmp[i].split("-");
                if (arrRange.length > 1) {
                    min = arrRange[0];
                    max = arrRange[1];
                    if (Integer.parseInt(min) > Integer.parseInt(max)) {
                        String temp;
                        temp = min;
                        min = max;
                        max = temp;
                        // SwapArgs(min, max);
                    }
                    for (j = Integer.parseInt(min); j <= Integer.parseInt(max); j++) {
                        arrIterations.add(String.valueOf(j));
                    }
                } else
                    arrIterations.add(arrTmp[i]);
            }
            return arrIterations;
        } catch (Exception ex) {
            LOGGER.error( "GetIterations Function Failed.", ex);
            return arrIterations;
        }
    }

    public static ArrayList<String> getListOfMethodsInClass(Class clazz) {
        ArrayList<String> al = null;
        try {
            al = new ArrayList<String>();
            for (Method method : clazz.getDeclaredMethods()) {
                al.add(method.getName());
            }
        } catch (Exception e) {
            LOGGER.error( "getListOfMethodsInClass method failed for exception: ", e);
        }
        return al;
    }

    public static ArrayList<String> getListOfExcludedMethods() {
        ArrayList<String> al = null;
        try {

            Hashtable<Integer, Hashtable<String, String>> ExcludedDriverDataTable = DataManager.GetExcelDataTable("select * from Driver where Execution_Flag = 'NO'");

            al = new ArrayList<String>();
            for (int i = 1; i <= ExcludedDriverDataTable.size(); i++) {
                al.add(ExcludedDriverDataTable.get(i).get("Function_Name".toUpperCase()));
            }

        } catch (Exception e) {
            LOGGER.error( "getListOfMethodsInClass method failed for exception: ", e);
        }
        return al;
    }

}
