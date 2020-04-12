package com.theoctopus.java.HybridRestAssured.Utilities.core;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.lang.reflect.InvocationTargetException;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.theoctopus.java.HybridRestAssured.Utilities.core.Common.getStackTrace;


public class Driver {


    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);
    public static Hashtable<Integer, Hashtable<String, String>> DriverDataTable = new Hashtable<Integer, Hashtable<String, String>>();

    private static void BrowserLanuch(String sBrowser, String sURL) {
        try {
            if (sBrowser.toUpperCase().equals("CHROME")) {
                LOGGER.info("Launch CHROME Browser Started.");

                WebDriverManager.chromedriver().setup();


                if (Global.Test.isHeadless) {
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("headless");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("start-maximized");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--disable-extensions");
                    options.setPageLoadStrategy(PageLoadStrategy.NONE);

                    Global.Test.Browser = new ChromeDriver(options);
                    Global.Test.Browser.manage().window().setPosition(new Point(-2000, 0));
                } else {
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--start-maximized");
                    options.setExperimentalOption("useAutomationExtension", false);
                    options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
                    Map<String, Object> prefs = new HashMap<String, Object>();
                    prefs.put("credentials_enable_service", false);
                    prefs.put("profile.password_manager_enabled", false);
                    options.setExperimentalOption("prefs", prefs);
                    options.setPageLoadStrategy(PageLoadStrategy.NONE);

                    Global.Test.Browser = new ChromeDriver(options);
                }
                Global.Test.Browser.manage().window().maximize();
                Global.Test.Browser.manage().timeouts().implicitlyWait(Global.Test.BrowserTimeOut, TimeUnit.SECONDS);
            } else if (sBrowser.toUpperCase().equals("IE")) {
                LOGGER.info("Launch IE Browser Started.");


                WebDriverManager.edgedriver().setup();
                Global.Test.Browser = new EdgeDriver();
                Global.Test.Browser.manage().window().maximize();
                Global.Test.Browser.manage().timeouts().implicitlyWait(Global.Test.BrowserTimeOut, TimeUnit.SECONDS);
            } else if (sBrowser.toUpperCase().equals("FIREFOX")) {
                LOGGER.info("Launch FIREFOX Browser Started.");


                WebDriverManager.firefoxdriver().setup();
                Global.Test.Browser = new FirefoxDriver();
                Global.Test.Browser.manage().window().maximize();
                Global.Test.Browser.manage().timeouts().implicitlyWait(Global.Test.BrowserTimeOut, TimeUnit.SECONDS);
            }

            Global.Test.Browser.get(sURL.trim());

        } catch (Exception ex) {

            System.out.println("\n\n Open Browser Error <<<" + getStackTrace(ex) + ">>>");
            System.out.println("\n\n ========= END OF EXECUTION ========= Exit Code [ " + Global.Test.testStatus + " ] =========");
            LOGGER.error("Launch Browser Failed.", ex);
            try {
                Global.Test.Browser.quit();
            } catch (Exception e) {
            }
        }

    }

    public static void EnvironmentInitialize(String RunEnvironment) {

        try {
            LOGGER.info("Test Environment Initialize Started.");

            if (RunEnvironment.trim().toUpperCase().contains("WEB")) {
                BrowserLanuch(Global.Test.BrowserType, Global.Test.URL);
            }

        } catch (Exception ex) {
            System.out.println("\n\n Start Test Application Failed For Error <<<" + getStackTrace(ex) + ">>>");
            System.out.println("\n\n ========= END OF EXECUTION ========= Exit Code [ 1 ] =========");
            LOGGER.error("Test Environment Initialize Failed.", ex);
            try {
                Global.Test.Browser.quit();
            } catch (Exception e) {
            }

        }

    }

    public static List<XmlSuite> buildTestNgSuite(Hashtable<Integer, Hashtable<String, String>> driverTable) {
        //Initialize
        XmlSuite xmlSuite = null;
        XmlTest xmlTest = null;
        XmlClass xmlClass = null;

        final String ANSI_RESET = "\u001B[0m";
        final String CYAN_BOLD = "\033[1;36m";

        try {
            // Build suite
            LOGGER.info("Build TestNg Command Line Suite Started.");

            xmlSuite = new XmlSuite();
            xmlSuite.setName(Global.Test.ProjectName + "_Suite");
            xmlSuite.setVerbose(1);
            try {
                xmlSuite.addListener("com.theoctopus.java.HybridRestAssured.Utilities.listeners.invokeMethodListener");
                xmlSuite.addListener("com.theoctopus.java.HybridRestAssured.Utilities.listeners.ITestListenerReporter");

            } catch (Exception ex) {
                LOGGER.error("Adding Listeners To TestNg Command Line Suite Failed.", ex);
            }


            //Build test
            LOGGER.info("Adding Test Root to TestNg Command Line Suite");
            xmlTest = new XmlTest(xmlSuite);
            xmlTest.setName(Global.Test.ProjectName + "_Test");
            xmlTest.setPreserveOrder(true);

            ArrayList lstTestScenarioMethods = Common.getListOfMethodsInClass(Class.forName(DataManager.getPropertyFile("testScenariosClass").trim()));

            //Build test Class
            LOGGER.info("Adding Test Methods to TestNg Command Line Suite");
            xmlClass = new XmlClass(DataManager.getPropertyFile("testScenariosClass").trim());
            List<XmlInclude> methodsList = new ArrayList<XmlInclude>();

            // define a list of all marked with no execution in driver sheet
            ArrayList<String> excludedMethods = new ArrayList<String>();
            excludedMethods = Common.getListOfExcludedMethods();

            String testFunctionName = "";


            for (int i = 1; i <= driverTable.size(); i++) {
                testFunctionName = driverTable.get(i).get("Function_Name".toUpperCase()).trim();
                if (testFunctionName.length() > 0) {
                    if (lstTestScenarioMethods.contains(testFunctionName)) {
                        XmlInclude testMethod = new XmlInclude(testFunctionName, i - 1);
                        testMethod.setXmlClass(xmlClass);
                        methodsList.add(testMethod);
                    } else {
                        //if method is not in test scenarios class add it to execlude list
                        excludedMethods.add(testFunctionName);
                        LOGGER.error(testFunctionName + "() method not found in TestScenarios.java");
                        System.out.println(CYAN_BOLD + "[ERROR] " + testFunctionName + "() METHOD NOT FOUND IN TestScenarios.java" + ANSI_RESET);
                    }
                }
            }


            if (methodsList.size() == 0) {
                LOGGER.error("None of the TESTs are marked as YES in input Excel ControlFile \\ Marked TESTs are not found in TestScenarios.java");
                System.out.println(CYAN_BOLD + "None of the TESTs are marked as YES in input Excel ControlFile \\ Marked TESTs are not found in TestScenarios.java\n" + ANSI_RESET);
            }

            xmlClass.setIncludedMethods(methodsList);
            xmlClass.setExcludedMethods(excludedMethods);
            List<XmlClass> classList = new ArrayList<XmlClass>();
            classList.add(xmlClass);
            xmlTest.setXmlClasses(classList);

            LOGGER.info("Build Final TestNg Command Line Suite");
            List<XmlSuite> suitesList = new ArrayList<XmlSuite>();
            suitesList.add(xmlSuite);

            return suitesList;

        } catch (Exception Ex) {
            LOGGER.error("Build TestNg Command Line Suite Failed.", Ex);
            return null;
        }


    }

    public static int runTestNgSuite(List<XmlSuite> suitList) {

        try {

            LOGGER.info("Run TestNg Command Line Suite Started.");
            // create object for TestNG
            TestNG tng = new TestNG();

            // Add the suite list
            tng.setXmlSuites(suitList);

            // Run the suite
            tng.run();

            Global.Test.testStatus = tng.getStatus();

            System.out.println("\n========= END OF EXEC ========= Exit Code [" + Global.Test.testStatus + "] =========");


        } catch (Exception ex) {
            LOGGER.error("Run TestNg Command Line Suite Failed.", ex);
            System.out.println(getStackTrace(ex));
            System.out.println("\n\n ========= END OF EXEC ========= Exit Code [ 1 ] =========");

        }

        return Global.Test.testStatus;

    }

    public static void initializeTestConfigurations() {

        String query = "";
        String RQMID = "";
        // Initializing all global variables and report configurations
        try {

            if (Global.Environment.FrameworkPath.equals("")) {

                LOGGER.info("Initialize Test Configurations Started.");

                Global.Environment.InitializeEnvironmentVars();
                Global.Test.InitializeTestVars(DataManager.GetExcelDictionary("select Name,Value from Environment"));
                Global.Reporter.InitializeReporterVars();

                if (DriverDataTable == null || DriverDataTable.size() < 1) {
                    query = "select * from Driver where Execution_Flag = 'YES'";
                    DriverDataTable = DataManager.GetExcelDataTable(query);

                    /// Make Test Environment Ready (WEB, Mobile Or both of them).
                    EnvironmentInitialize(Global.Test.RunEnvironment);
                }
            }
        } catch (Exception Ex) {
            LOGGER.error("Initialize Test,Environment,Reporter Configurations Failed.", Ex);
            System.out.println(getStackTrace(Ex));
            System.out.println("\n\n ========= END OF EXEC ========= Exit Code [ 1 ] =========");
        }


    }

    public static Integer ExecuteTestSuite() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {


        List<XmlSuite> xmlSuiteList = new ArrayList<XmlSuite>();

        try {


            initializeTestConfigurations();

            xmlSuiteList = buildTestNgSuite(DriverDataTable);

            runTestNgSuite(xmlSuiteList);


        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("\t\t Driver Error <<<" + getStackTrace(ex)+ ">>>");
            System.out.println("\n\n ========= END OF EXEC ========= Exit Code [1] =========");
            LOGGER.error("Execute TestNG Command line Suite Failed.", ex);
        } finally {

            try {
                Global.Test.Browser.quit();
            } catch (Exception e) {
            }

            System.exit(Global.Test.testStatus == 1 ? 1 : 0);

            return Global.Test.testStatus;
        }


    }


}
