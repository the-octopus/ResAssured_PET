package com.Hybrid.hunger.Pet.Main;


import com.Hybrid.hunger.Utilities.core.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.Hybrid.hunger.Utilities.core.Common.getStackTrace;

public class Runner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) {
        try {

            Driver.ExecuteTestSuite();
        } catch (Exception e) {
            LOGGER.error(getStackTrace(e));
        }
    }

}
