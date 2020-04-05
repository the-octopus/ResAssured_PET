package com.theoctopus.java.HybridRestAssured.Utilities.listeners;


import com.theoctopus.java.HybridRestAssured.Utilities.core.DataManager;
import com.theoctopus.java.HybridRestAssured.Utilities.core.Global;
import com.theoctopus.java.HybridRestAssured.Utilities.core.Common;
import com.theoctopus.java.HybridRestAssured.Utilities.core.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class invokeMethodListener implements IInvokedMethodListener, IAnnotationTransformer {


    private static final Logger LOGGER = LoggerFactory.getLogger(invokeMethodListener.class);
    String InvokedMethodListenerOldFunName = "";
    Integer InvokedMethodListenerCounter = 0;
    Integer currentDriverTableRow = 0;
    List<String> transformedMethods = new ArrayList<String>();

    String CurrentRowNo;

    Hashtable<Integer, Hashtable<String, String>> TestDataTable;
    Hashtable<Integer, Hashtable<String, String>> MasterTestDataTable;

    public void transform(ITestAnnotation testAnnotation, Class aClass, Constructor constructor, Method method) {
        String fName = "";
        List<String> dataRows = null;

        try {
            if (Driver.DriverDataTable.size() > 0) {
                LOGGER.info("Transform Test Function (" + method.getName() + ") started.");
                for (int i = 1; i <= Driver.DriverDataTable.size(); i++) {

                    fName = Driver.DriverDataTable.get(i).get("Function_Name".toUpperCase());
                    if (fName.trim().toUpperCase().equals(method.getName().toUpperCase().trim())) {
                        dataRows = Common.GetIterations(Driver.DriverDataTable.get(i).get("TestDataSheetRowNo".toUpperCase()), ",");
                        testAnnotation.setInvocationCount(dataRows.size());
                        testAnnotation.setDescription(Driver.DriverDataTable.get(i).get("Function_Name".toUpperCase()));
                        transformedMethods.add(method.getName().toUpperCase().trim());
                        break;
                    }

                }
            } else {
                if (Global.Reporter.extentTestReporter != null)
                    Global.Reporter.extentTestReporter.error("Transform Test Function[ " + method.getName() + " ] Failed DriverDataTable is Empty.");
                else
                    LOGGER.error("Transform Test Function[ " + method.getName() + " ] Failed DriverDataTable is Empty.");
            }

        } catch (Exception e) {
            LOGGER.error("Transform Test Function (" + method.getName() + ") Failed.", e);
        }

    }

    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
        if (!iInvokedMethod.isTestMethod()) {
            return;
        }

        String Func = "";

        try {


            Func = iInvokedMethod.getTestMethod().getMethodName();


            if (!transformedMethods.contains(Func.trim().toUpperCase())) {
                InvokedMethodListenerCounter = 0;
                InvokedMethodListenerOldFunName = "";
                return;
            }

            if (InvokedMethodListenerOldFunName.equals(Func)) {
                InvokedMethodListenerCounter++;
                System.out.println("\n\t............ Start Iteration # [ " + InvokedMethodListenerCounter + " ]............\n");
                LOGGER.info("\n\t............ Start Iteration # [ " + InvokedMethodListenerCounter + " ]............\n");

            } else {

                currentDriverTableRow++;
                InvokedMethodListenerCounter++;


                System.out.println("\n\n_________ Start Test Case [ " + Func + " ] [Running: " + currentDriverTableRow + " out of " + Driver.DriverDataTable.size() + "] _________");
                System.out.println("\n\t............ Start Iteration # [ " + InvokedMethodListenerCounter + " ]............\n");
                LOGGER.info("\n\n_________ Start Test Case [ " + Func + " ] [Running: " + currentDriverTableRow + " out of " + Driver.DriverDataTable.size() + "] _________");
                LOGGER.info("\n\t............ Start Iteration # [ " + InvokedMethodListenerCounter + " ]............\n");


                TestDataTable = DataManager.GetAllTestDataForDriverRow(Driver.DriverDataTable.get(currentDriverTableRow).get("RowID".toUpperCase()));
            }

            if (TestDataTable.size() > 0) {

                Global.Test.TestDataRow = TestDataTable.get(InvokedMethodListenerCounter);
                CurrentRowNo = Global.Test.TestDataRow.get("RowID".toUpperCase());

                if (Driver.DriverDataTable.get(currentDriverTableRow).get("TestDataSheetName".toUpperCase()).equalsIgnoreCase("Master")) {
                    MasterTestDataTable = DataManager.GetAllTestDataForMasterRow(CurrentRowNo);
                }

                try {
                    Global.Reporter.extentTestReporter.getModel().setDescription(Global.Test.TestDataRow.get("TestCaseDescription".toUpperCase()));
                } catch (Exception e) {
                }
                Global.Reporter.extentTestReporter.info("Test Function[ " + Func + " ] Execution Started");
                InvokedMethodListenerOldFunName = Func;
            } else {
                Global.Test.TestDataRow = null;
                Global.Reporter.extentTestReporter.error("Test [ " + Func + " ]  Failed to get Test Data Rows (" + Driver.DriverDataTable.get(currentDriverTableRow).get("TestDataSheetRowNo".toUpperCase()) + ") " +
                        "OF Sheet (" + Driver.DriverDataTable.get(currentDriverTableRow).get("TestDataSheetName".toUpperCase()) + ")");
                LOGGER.error("Test [ " + Func + " ]  Failed to get Test Data Rows (" + Driver.DriverDataTable.get(currentDriverTableRow).get("TestDataSheetRowNo".toUpperCase()) + ") " +
                        "OF Sheet (" + Driver.DriverDataTable.get(currentDriverTableRow).get("TestDataSheetName".toUpperCase()) + ")");
                throw new SkipException("Failed to Initialize Function [ " + Func + " ] Test Data Rows.");
            }
        } catch (Exception e) {
            LOGGER.error("beforeInvocation Function Failed.", e);
            throw new SkipException("Failed to Initialize Function [ " + Func + " ] Test Data Rows.");
        }


    }

    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult result) {
        if (!iInvokedMethod.isTestMethod()) {
            return;
        }


        try {


            String message = "";
            message = (result.getThrowable() != null) ? result.getThrowable().toString() : result.getMethod().getMethodName();
            if (result.getStatus() == 1) {
                System.out.println("\t\t[PASS]  " + message);
            } else {
                System.out.println("\t\t[FAIL]  " + message);
                LOGGER.error("Test Function (" + iInvokedMethod.getTestMethod().getMethodName() + ") Failed for Exception: ", result.getThrowable());
            }


            System.out.println("\n\t............ End Iteration .....................");
            LOGGER.info("\n\t............ End Iteration .....................");


            if (iInvokedMethod.getTestMethod().getCurrentInvocationCount()+1 == iInvokedMethod.getTestMethod().getInvocationCount()) {
                InvokedMethodListenerCounter = 0;
                InvokedMethodListenerOldFunName = "";
                System.out.println("\n_________ End Test Case _________");
                LOGGER.info("\n_________ End Test Case _________");

            }


        } catch (Exception e) {
            LOGGER.error("afterInvocation Function Failed.", e);
        }

    }

}
