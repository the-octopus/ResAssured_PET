package com.theoctopus.java.HybridRestAssured.PetTest.Test;

import com.theoctopus.java.HybridRestAssured.Utilities.core.Global;
import com.theoctopus.java.HybridRestAssured.Utilities.core.Common;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.Hashtable;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;

public class PetAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(PetAPI.class);

    public static Response ExecuteRequest(Hashtable<String, String> ServiceData) {
        RequestSpecBuilder builder;
        Response response = null;

        try {
            //forming the request body and filling it from the excel sheet

            //setting the request headers
            if (null != ServiceData) {
                String[] specDet;
                builder = new RequestSpecBuilder();

                builder.setBody(ServiceData.get("RequestBodyData".toUpperCase()));

                if (ServiceData.get("FormData".toUpperCase()) != null) {
                    if (!ServiceData.get("FormData".toUpperCase()).equalsIgnoreCase("")) {
                        for (String s : ServiceData.get("FormData".toUpperCase()).split("\n")) {
                            specDet = s.split("&");
                            builder.addFormParam(specDet[0], specDet[1]);
                        }
                    }
                }

                for (String s : ServiceData.get("Header".toUpperCase()).split("\n")) {
                    specDet = s.split(":", 2);
                    builder.addHeader(specDet[0], specDet[1]);
                }

                //running the request and (you can choose the HTTP method type ie. post , get,..etc )

                String finalURL = ServiceData.get("Uri".toUpperCase()) + ServiceData.get("ServiceName".toUpperCase()) + ServiceData.get("RequestParams".toUpperCase());


                RequestSpecification requestSpec = builder.build();
                if (ServiceData.get("RequestType".toUpperCase()).trim().equalsIgnoreCase("post"))
                    response = given().spec(requestSpec).when().post(finalURL);
                else if (ServiceData.get("RequestType".toUpperCase()).trim().equalsIgnoreCase("put"))
                    response = given().spec(requestSpec).when().put(finalURL);
                else if (ServiceData.get("RequestType".toUpperCase()).trim().equalsIgnoreCase("get"))
                    response = given().spec(requestSpec).when().get(finalURL);
                else if (ServiceData.get("RequestType".toUpperCase()).trim().equalsIgnoreCase("delete"))
                    response = given().spec(requestSpec).when().delete(finalURL);

                Headers allDetailedHeaders = response.getHeaders();

                if (response.asString().equalsIgnoreCase("")) {
                    LOGGER.info("Response Status : " + response.getStatusLine());
                    Global.Reporter.extentTestReporter.info("Response Status : " + response.getStatusLine());

                    LOGGER.info("Response Headers : " + allDetailedHeaders.toString());
                    Global.Reporter.extentTestReporter.info("Response Headers: " + allDetailedHeaders.toString());
                } else {
                    LOGGER.info("Response : " + response.asString());
                    Global.Reporter.extentTestReporter.info("Response : " + response.asString());
                }
            }
            return response;

        } catch (Exception e) {
            LOGGER.error(Common.getStackTrace(e));
            return null;
        }
    }

    public static void verifyResponseExpectedResult(Response response, String jsonPath, String expectedResult) {
        try {

            if (response.getStatusCode() == 200) {
                String returnVal = from(response.asString()).getString(jsonPath);

                Assert.assertEquals(returnVal, expectedResult);

                Global.Reporter.extentTestReporter.info("Json Path ( " + jsonPath + " ) returned the Expected Result [ " + expectedResult + " ]. ");
            } else {
                Global.Reporter.extentTestReporter.fail("Response Status is [ " + response.getStatusCode() + " ]. ");
            }
        } catch (Exception e) {

            LOGGER.error(Common.getStackTrace(e));

        }

    }

    public static void verifyResponseStatus(Response response, String expectedStatus) {
        try {

            if (response.getStatusCode() == 200) {

                Assert.assertEquals(String.valueOf(response.getStatusCode()), expectedStatus);

                Global.Reporter.extentTestReporter.info("Reponse returned the Expected Status [ " + expectedStatus + " ]. ");
            } else {
                Global.Reporter.extentTestReporter.fail("Response Status is [ " + response.getStatusCode() + " ]. ");
            }
        } catch (Exception e) {

            LOGGER.error(Common.getStackTrace(e));

        }

    }


}
