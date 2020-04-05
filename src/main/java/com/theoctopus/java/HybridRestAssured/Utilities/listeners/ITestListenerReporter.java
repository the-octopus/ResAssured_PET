package com.theoctopus.java.HybridRestAssured.Utilities.listeners;


import com.theoctopus.java.HybridRestAssured.Utilities.core.DataManager;
import com.theoctopus.java.HybridRestAssured.Utilities.core.Global;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.theoctopus.java.HybridRestAssured.Utilities.core.Driver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.asserts.SoftAssert;
import java.io.File;

import java.util.Calendar;
import java.util.Date;


public class ITestListenerReporter implements ITestListener {

    private static ExtentTest parentTest;
    private static ExtentTest childTest;
    private static ExtentReports extent;
    private static ExtentHtmlReporter htmlReporter;
    private static TakesScreenshot ts;
    private static File src;
    private static SoftAssert assertion = new SoftAssert();
    private static WebDriver driver;
    private int ITestListenerCounter = 1;
    private String ITestListenerOldFunName = "";
    private static final Logger LOGGER = LoggerFactory.getLogger(ITestListenerReporter.class);

    

    public void onTestStart(ITestResult result) {
        String funcName = "";
        String testName = "";
        String testDescription = "";
        try {
            funcName = result.getMethod().getMethodName();
            testName = funcName;
            testDescription = (result.getMethod().getDescription() == null ? funcName : result.getMethod().getDescription());

            LOGGER.info( "Execute Test Case [ " + funcName + " ] Started.");

            if (ITestListenerOldFunName.equals(funcName)) {
                ITestListenerCounter++;
                testName = funcName + "_Iteration_" + ITestListenerCounter;
                childTest = parentTest.createNode(testName, testDescription);
                Global.Reporter.extentTestReporter = childTest;

            } else {
                ITestListenerCounter = 0;
                funcName = result.getMethod().getMethodName();
                parentTest = extent.createTest(testName, testDescription);
                testName = funcName + "_Iteration_" + ITestListenerCounter;
                childTest = parentTest.createNode(testName, testDescription);
                Global.Reporter.extentTestReporter = childTest;
            }

            ITestListenerOldFunName = funcName;

                driver = Global.Test.Browser;

        } catch (Exception e) {
            LOGGER.error( "onTestStart Function Failed.", e);
        }

    }

    public void onTestSuccess(ITestResult result) {

        try {
            String message = "";

            message = (result.getThrowable() != null) ? result.getThrowable().toString() : result.getMethod().getMethodName();

            LOGGER.info( message);

            if ((driver != null) && (Global.Test.RunEnvironment.toLowerCase().contains("web") || Global.Test.RunEnvironment.toLowerCase().contains("mobile"))) {

                Global.Reporter.ScreenCaptureCount++;
                ts = (TakesScreenshot) driver;
                src = ts.getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(src, new File(Global.Reporter.ScreenShootFolder + "\\Screen" + Global.Reporter.ScreenCaptureCount + ".png"));
                Global.Reporter.extentTestReporter.pass((String) message,
                        MediaEntityBuilder.createScreenCaptureFromPath("ScreenShots_" + Global.Test.ProjectName + "/Screen" + Global.Reporter.ScreenCaptureCount + ".png").build());


            } else {

                Global.Reporter.extentTestReporter.pass((String) message);
            }

        } catch (Exception e) {
            LOGGER.error( "onTestSuccess Function Failed.", e);
        }
    }

    public void onTestFailure(ITestResult result) {
        try {
            String message = "";

            message = (result.getThrowable() != null) ? result.getThrowable().toString() : result.getMethod().getMethodName();

            LOGGER.info( message);

            if ((driver != null) && (Global.Test.RunEnvironment.toLowerCase().contains("web") || Global.Test.RunEnvironment.toLowerCase().contains("mobile"))) {


                Global.Reporter.ScreenCaptureCount++;

                ts = (TakesScreenshot) driver;
                src = ts.getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(src, new File(Global.Reporter.ScreenShootFolder + "\\Screen" + Global.Reporter.ScreenCaptureCount + ".png"));
                Global.Reporter.extentTestReporter.fail((String) message,
                        MediaEntityBuilder.createScreenCaptureFromPath("ScreenShots_" + Global.Test.ProjectName + "/Screen" + Global.Reporter.ScreenCaptureCount + ".png").build());

            } else {


                Global.Reporter.extentTestReporter.fail((String) message);
            }

        } catch (Exception e) {
            LOGGER.error( "onTestFailure Function Failed.", e);
        }
    }

    public void onTestSkipped(ITestResult result) {

        try {
            String message = "";
            message = (result.getThrowable() != null) ? result.getThrowable().toString() : result.getMethod().getMethodName() ;

            LOGGER.info( message);

            if ((driver != null) && (Global.Test.RunEnvironment.toLowerCase().contains("web") || Global.Test.RunEnvironment.toLowerCase().contains("mobile"))) {
                System.out.println("\t\t[SKIP]  " + message);
                LOGGER.info("\t\t[SKIP]  " + message);
                Global.Reporter.ScreenCaptureCount++;
                ts = (TakesScreenshot) driver;
                src = ts.getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(src, new File(Global.Reporter.ScreenShootFolder + "\\Screen" + Global.Reporter.ScreenCaptureCount + ".png"));
                Global.Reporter.extentTestReporter.skip((String) message,
                        MediaEntityBuilder.createScreenCaptureFromPath("ScreenShots_" + Global.Test.ProjectName + "/Screen" + Global.Reporter.ScreenCaptureCount + ".png").build());

            } else {
                System.out.println("\t\t[SKIP]  " + message);
                LOGGER.info("\t\t[SKIP]  " + message);
                Global.Reporter.extentTestReporter.skip((String) message);
            }

        } catch (Exception e) {
            LOGGER.error( "Skip Function Failed.", e);
        }
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {


    }

    public void onStart(ITestContext context) {

        try {
            LOGGER.info("Start of executing ( " + context.getName() + " ) .");

            Driver.initializeTestConfigurations();

            // checking the Reports Main Folder
            File tempDie;

            tempDie = new File(Global.Reporter.ReportsFolder);
            if (!tempDie.exists())
                tempDie.mkdirs();

            tempDie = new File(Global.Reporter.RunResultFolder);
            if (!tempDie.exists())
                tempDie.mkdirs();

            tempDie = new File(Global.Reporter.ScreenShootFolder);
            if (!tempDie.exists())
                tempDie.mkdirs();

            LOGGER.info( "Initialize ExtentReporter .");

            htmlReporter = new ExtentHtmlReporter(Global.Reporter.ReportFileName);
            extent = new ExtentReports();
            htmlReporter.loadXMLConfig(new File(Global.Reporter.extentReportXMLPath));
            extent.attachReporter(htmlReporter);
            extent.setSystemInfo("Project Name", Global.Test.ProjectName);
            extent.setSystemInfo("Browser", Global.Test.BrowserType);
        } catch (Exception e) {
            LOGGER.error("onStart Function Failed .", e);
        }

    }

    public void onFinish(ITestContext context) {

        try {

            if (DataManager.getPropertyFile("doLogout").equalsIgnoreCase("yes")) {
                LOGGER.info( "Execute Application LogOut function.");

            }


            if (Global.Test.RunEnvironment.trim().toUpperCase().contains("WEB")) {
                try {
                    Global.Test.Browser.quit();
                } catch (Exception _ex) {
                }
            }

        } catch (Exception e) {

            extent.flush();

        } finally {

            extent.flush();
            System.out.println("\n_________ End Test  _________");
            LOGGER.info("\n_________ End Test  _________");
        }


    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }


}
