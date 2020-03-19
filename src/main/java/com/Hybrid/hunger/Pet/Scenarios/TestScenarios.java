package com.Hybrid.hunger.Pet.Scenarios;


import com.Hybrid.hunger.Utilities.core.Global;
import com.Hybrid.hunger.Pet.Test.PetAPI;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static com.Hybrid.hunger.Utilities.core.Common.getStackTrace;

public class TestScenarios {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestScenarios.class);

    @Test
    public void validateGetPetByID() {
        try {
            Response response= PetAPI.ExecuteRequest(Global.Test.TestDataRow);
            PetAPI.verifyResponseExpectedResult(response,Global.Test.TestDataRow.get("ExpectedResultJsonPAth".toUpperCase()),Global.Test.TestDataRow.get("ExpectedResult".toUpperCase()));
        } catch (Exception ex) {
            LOGGER.error(getStackTrace(ex));
        }
    }


    @Test
    public void validateGetPetByStatus() {
        try {
            Response response= PetAPI.ExecuteRequest(Global.Test.TestDataRow);
            PetAPI.verifyResponseExpectedResult(response,Global.Test.TestDataRow.get("ExpectedResultJsonPAth".toUpperCase()),Global.Test.TestDataRow.get("ExpectedResult".toUpperCase()));
        } catch (Exception ex) {
            LOGGER.error(getStackTrace(ex));
        }
    }


    @Test
    public void validateDeletePetByID() {
        try {
            Response response= PetAPI.ExecuteRequest(Global.Test.TestDataRow);
            PetAPI.verifyResponseStatus(response,Global.Test.TestDataRow.get("ExpectedStatus".toUpperCase()));
        } catch (Exception ex) {
            LOGGER.error(getStackTrace(ex));
        }
    }


    @Test
    public void validateAddNewPet() {
        try {
            Response response= PetAPI.ExecuteRequest(Global.Test.TestDataRow);
            PetAPI.verifyResponseExpectedResult(response,Global.Test.TestDataRow.get("ExpectedResultJsonPAth".toUpperCase()),Global.Test.TestDataRow.get("ExpectedResult".toUpperCase()));
        } catch (Exception ex) {
            LOGGER.error(getStackTrace(ex));
        }
    }


    @Test
    public void validateUpdateExistingPET() {
        try {
            Response response= PetAPI.ExecuteRequest(Global.Test.TestDataRow);
            PetAPI.verifyResponseExpectedResult(response,Global.Test.TestDataRow.get("ExpectedResultJsonPAth".toUpperCase()),Global.Test.TestDataRow.get("ExpectedResult".toUpperCase()));
        } catch (Exception ex) {
            LOGGER.error(getStackTrace(ex));
        }
    }


    @Test
    public void validateUpdateExistingPETByFormData() {
        try {
            Response response= PetAPI.ExecuteRequest(Global.Test.TestDataRow);
            PetAPI.verifyResponseExpectedResult(response,Global.Test.TestDataRow.get("ExpectedResultJsonPAth".toUpperCase()),Global.Test.TestDataRow.get("ExpectedResult".toUpperCase()));
        } catch (Exception ex) {
            LOGGER.error(getStackTrace(ex));
        }
    }

}
