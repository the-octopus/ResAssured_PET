package com.theoctopus.java.HybridRestAssured.Utilities.core;


import com.aventstack.extentreports.ExtentTest;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;


public class Global {

    private static final Logger LOGGER = LoggerFactory.getLogger(Global.class);

    public static class Test {

        public static String URL;
        public static String BrowserType;
        public static String RunLang;
        public static String RunType;
        public static String RunEnvironment;
        public static String ProjectName;
        public static Integer testStatus;
        public static Hashtable<String, String> TestDataRow;

        public static WebDriver Browser;

        public static long BrowserTimeOut;
        public static Hashtable<String, String> EnviromentVars;
        public static boolean isHeadless =false;




        public static void InitializeTestVars(Hashtable<String, String> envVars)
        {

            try
            {
                LOGGER.info( "Initialize Test Variables Function Started.");
                EnviromentVars = envVars;
                BrowserTimeOut = 20;
                try{
                    if(envVars.get("HEADLESS_MODE").trim().equalsIgnoreCase("true")){
                        isHeadless = true;
                    }
                }catch (Exception e){}
                testStatus = 0;

                URL = envVars.get("URL").trim();                
                BrowserType = envVars.get("Explorer").trim();
                ProjectName = envVars.get("ProjectName").trim();
                RunType = envVars.get("RunType").trim();
                RunLang = envVars.get("RunLang").trim();
                RunEnvironment = envVars.get("RunEnvironment").trim();

                
            }
            catch (Exception ex)
            {
                LOGGER.error( "Initialize Test Variables Function Failed.", ex);
            }

        }


    }

    public static class Environment {

        public static String FrameworkPath = "";
        public static String ControlFileName = "";

        public static void InitializeEnvironmentVars()
        {
            try {
                LOGGER.info("Initialize Environment Variables Function Started.");

                FrameworkPath = System.getProperty("user.dir");
                ControlFileName = FrameworkPath + DataManager.getPropertyFile("ControlFileName").trim();


            }catch (Exception e){
                LOGGER.error("Initialize Environment Variables Function Failed.",e);
            }
        }



    }

    public static class Reporter
    {

        public static int ScreenCaptureCount;
        public static int TestCaseIterations;
        public static int CycleTotalTestCases;
        public static String ReportsFolder;
        public static String RunResultFolder;
        public static String ScreenShootFolder;
        public static String ReportFileName;
        public static String extentReportXMLPath;
        public static ExtentTest extentTestReporter ;

        //Initailize Reporter variables

        public static void InitializeReporterVars()
        {
            try {

                LOGGER.info("Initialize Reporter Variables Function Started.");

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd-hhmmss");

                ScreenCaptureCount = 0;

                TestCaseIterations = 0;

                CycleTotalTestCases = 0;

                ReportsFolder = Global.Environment.FrameworkPath + DataManager.getPropertyFile("ReportPath") + "\\";

                RunResultFolder = ReportsFolder + "Automation_Result_" + df.format(new Date()).replace("-", "_");

                ScreenShootFolder = RunResultFolder + "\\ScreenShots_" + Global.Test.ProjectName;

                ReportFileName = RunResultFolder + "\\" + "Automation_Report.html";

                extentReportXMLPath = Global.Environment.FrameworkPath + DataManager.getPropertyFile("ExtentReportXMLPath");

            }catch (Exception e){
                LOGGER.error("Initialize Reporter Variables Function Failed.",e);
            }
        }



    }


}
