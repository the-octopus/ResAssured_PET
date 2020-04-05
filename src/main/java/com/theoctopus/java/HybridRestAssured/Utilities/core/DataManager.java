package com.theoctopus.java.HybridRestAssured.Utilities.core;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import static com.theoctopus.java.HybridRestAssured.Utilities.core.Common.getStackTrace;
import static com.theoctopus.java.HybridRestAssured.Utilities.core.Global.Environment.ControlFileName;

public class DataManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataManager.class);
    static Properties prop = new Properties();
    static InputStream input = null;
    static String filePath = null;

    public static String getPropertyFile(String prptString) {
        String property = "";
        try {
            LOGGER.info("getPropertyFile Function Started.");

            String resourceName = "config.properties"; // could also be a constant
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Properties props = new Properties();
            
            InputStream resourceStream = loader.getResourceAsStream(resourceName);
            props.load(resourceStream);            

            if (props.size() > 0)
                property = props.getProperty(prptString);

            return property;

        } catch (Exception e) {
            LOGGER.error("getPropertyFile Function Failed.\n" + getStackTrace(e));
            return property;
        }
    }

    public static Hashtable<Integer, Hashtable<String, String>> GetExcelDataTable(String strQuery) {
        String filePath = null;
        Recordset recordset = null;
        com.codoid.products.fillo.Connection connection = null;
        ArrayList list = null;
        int columns = 0;
        int rowNumber = 1;
        Hashtable<Integer, Hashtable<String, String>> DataTable = null;

        try {
            LOGGER.info( "Getting data Rows with query: [ " + strQuery + " ]");

            //Import Folio liberary
            Fillo fillo = new Fillo();
            connection = fillo.getConnection(ControlFileName);
            try {
                recordset = connection.executeQuery(strQuery);
            } catch (FilloException fex) {
            }

            DataTable = new Hashtable<Integer, Hashtable<String, String>>();
            if (null != recordset) {
                if (recordset.getCount() > 0) {
                    ArrayList<String> fnames = recordset.getFieldNames();
                    columns = fnames.size();
                    while (recordset.next()) {
                        Hashtable<String, String> DataRow = new Hashtable<String, String>();
                        for (int i = 0; i < columns; i++) {
                            DataRow.put(recordset.getField(i).name().toUpperCase(), recordset.getField(i).value());
                        }

                        DataTable.put(rowNumber, DataRow);
                        rowNumber++;
                    }
                } else {
                    LOGGER.info( "Get Excel DataTable Function , RecordSet is Zero.");

                }
            } else {
                LOGGER.info( "Get Excel DataTable Function , RecordSet is Zero.");

            }

            return DataTable;

        } catch (Exception e) {
           LOGGER.error( "Get Excel DataTable Function Failed.\n" + getStackTrace(e));
            return DataTable;
        } finally {
            if (recordset != null) {
                recordset.close();
            }
            if (connection != null)
                try {
                    connection.close();
                } catch (Exception e) {
                   LOGGER.error( "Get Excel DataTable Function Failed.\n" + getStackTrace(e));
                }
        }

    }


    public static Hashtable<Integer, Hashtable<String, String>> GetAllTestDataForDriverRow(String DriverRowID) {
//        String filePath = null;
        String selectRows = "";
        String selectQuery = "";
        Recordset recordset = null;
        int columns = 0, rowNumber = 1;
        Fillo fillo;
        com.codoid.products.fillo.Connection connection = null;
        Hashtable<String, String> driverDataRows = new Hashtable<String, String>();
        Hashtable<Integer, Hashtable<String, String>> DataTable = new Hashtable<Integer, Hashtable<String, String>>();

        try {
            LOGGER.info( "Getting Driver data Rows with RowID: [ " + DriverRowID + " ]");

            try {
                driverDataRows = GetExcelDataTable("Select TestDataSheetName,TestDataSheetRowNo from Driver where RowID='" + DriverRowID + "'").get((1));

            } catch (Exception e) {
               LOGGER.error( "Getting Driver data Rows with RowID: [ " + DriverRowID + " ] Failed \n" + getStackTrace(e));
            }

            if (driverDataRows != null) {

                List<String> RowsNo = Common.GetIterations(driverDataRows.get("TestDataSheetRowNo".toUpperCase()), ",");
                if (RowsNo.size() > 0) {

                    for (int i = 0; i < RowsNo.size(); i++) {

                        selectQuery = "Select * from " + driverDataRows.get("TestDataSheetName".toUpperCase()) + " where RowID = '" + RowsNo.get(i) + "'";
                        LOGGER.info( "Getting Test Data Row With Query :  " + selectQuery);
                        try {
                            fillo = new Fillo();
                            connection = fillo.getConnection(ControlFileName);

                            try {
                                recordset = connection.executeQuery(selectQuery);
                            } catch (FilloException fex) {
                            }
                            if (null != recordset) {
                                if (recordset.getCount() > 0) {
                                    columns = recordset.getFieldNames().size();
                                    while (recordset.next()) {
                                        Hashtable<String, String> DataRow = new Hashtable<String, String>();
                                        for (int j = 0; j < columns; j++) {
                                            DataRow.put(recordset.getField(j).name().toUpperCase(), recordset.getField(j).value());
                                        }
                                        DataTable.put(rowNumber, DataRow);
                                        rowNumber++;
                                    }
                                } else {

                                   LOGGER.error( "Invalid query:  " + selectQuery);
                                }
                            } else {

                               LOGGER.error( "Invalid query:  " + selectQuery);
                            }
                        } catch (Exception e) {
                           LOGGER.error( "Invalid Test Sheet Name, QRY:  " + selectQuery + "\n" + getStackTrace(e));
                        }
                    }
                } else {
                   LOGGER.error( "No data found from with query:  " + selectQuery);
                }

            } else {
               LOGGER.error( "No data found from with query:  " + selectQuery);
                DataTable = null;
            }

            return DataTable;
        } catch (Exception e) {
           LOGGER.error( "No data found from with query:  " + selectQuery);
            return DataTable;
        }
    }

    public static Hashtable<Integer, Hashtable<String, String>> GetAllTestDataForMasterRow(String MasterRowID) {
        Hashtable<Integer, Hashtable<String, String>> DataTable = new Hashtable<Integer, Hashtable<String, String>>();
        int iIndex = 1;
        Workbook wb = null;

        try {
            FileInputStream inputStream = new FileInputStream(new File(ControlFileName));

            String strFileExtension = ControlFileName.substring(ControlFileName.indexOf("."));
            if (strFileExtension.equals(".xlsx")) {
                wb = new XSSFWorkbook(inputStream);
            } else {
                wb = new HSSFWorkbook(inputStream);
            }

            Sheet sheet = wb.getSheet("Master");
            Row headerRow = sheet.getRow(0);
            Row row = sheet.getRow(Integer.parseInt(MasterRowID));
            for (int j = 0; j < row.getLastCellNum(); j++) {

                String strColName = new DataFormatter().formatCellValue(headerRow.getCell(j));
                String strColValue = new DataFormatter().formatCellValue(row.getCell(j));
                if (!strColValue.trim().equals("") & !strColName.equalsIgnoreCase("ROWID") & !strColName.equalsIgnoreCase("Description")) {
                    List<String> iterations = Common.GetIterations(strColValue, ",");
                    for (int i = 0; i < iterations.size(); i++) {
                        DataTable.put(iIndex, DataManager.GetExcelDataTable("select * from " + strColName + " where RowID=" + iterations.get(i)).get(1));
                        iIndex++;
                    }
                }
            }

        } catch (Exception e) {
           LOGGER.error( "GetAllTestDataForMasterRow failed for exception: " + e.getMessage());
        }
        return DataTable;
    }

    public static Hashtable<String, String> GetExcelDictionary(String strQuery) {
//        String filePath = null;
        Recordset recordset = null;
        com.codoid.products.fillo.Connection connection = null;
        // String filePath="D:\\My Docs\\QA Store\\Office\\Automation learning\\SeleniumFrameworkJava\\javadata.xlsx";
        //String dir = System.getProperty("user.dir");
        filePath = ControlFileName;//dir + getPropertyFile("ControlFileName");

        ArrayList list = null;
        int columns = 0;
        int rowNumber = 1;
        Hashtable<String, String> DataTable = new Hashtable<String, String>();

        try {
            LOGGER.info( "GetExcelDictionary with query:  " + strQuery + ", Started");
            //Import Folio liberary
            Fillo fillo = new Fillo();
            connection = fillo.getConnection(ControlFileName);
            try {
                recordset = connection.executeQuery(strQuery);
            } catch (FilloException fx) {
               LOGGER.error( getStackTrace(fx));
            }

            if (null != recordset) {
                if (recordset.getCount() > 0) {

                    while (recordset.next()) {

                        DataTable.put(recordset.getField(1).value(), recordset.getField(0).value());
                    }

                } else {
                   LOGGER.error( "GetExcelDictionary with query:  " + strQuery + ", No Record found");

                }
            } else {
               LOGGER.error( "GetExcelDictionary with query:  " + strQuery + ", No Record found");

            }
            return DataTable;
        } catch (Exception e) {
            //System.out.println(e.getMessage());
           LOGGER.error( "GetExcelDictionary with query:  \n" + getStackTrace(e) + strQuery);
            return DataTable;
        } finally {
            if (recordset != null) {
                recordset.close();
            }
            if (connection != null)
                try {
                    connection.close();
                } catch (Exception e) {
                   LOGGER.error( "GetExcelDictionary with query:  " + strQuery + "\n" + getStackTrace(e));
                }
        }

    }


}
