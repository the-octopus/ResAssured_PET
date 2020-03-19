# API Hybrid Test Automation Framework.

API Hybrid is a Hybrid test automation framework, that combines features of (Modular, Keyword Driven and Data driven).

## Concepts Included:

* Data Driven.

* Keyword Driven.

* Page Object pattern and Page Factory.  [POM ](https://www.guru99.com/page-object-model-pom-page-factory-in-selenium-ultimate-guide.html)

* Objects shared repository.

* Rest Assured

## Tools

* Java. 

* TestNG.

* Rest Assured

* Java editor.



## Getting Started.

* Clone the project. [https://github.com/the-octopus/ResAssured_PET.git]

* Open the project in InteliJ Java editor. 

* Project is organized as follow:

### **com.Hybrid.hunger.Utilities**:
Contains shared libraries to handle all interaction in the framework and also as follow.

 * **com.Hybrid.hunger.Utilities.listeners** : used to handle transforming testNG test methods and create extent report.

### **com.Hybrid.hunger.Utilities.core**:

 * **com.Hybrid.hunger.Utilities.core.DataManager** : used to handle reading and updating data from excel sheet.

 * **com.Hybrid.hunger.Utilities.core.Driver**: used to handle the execution of the test by reading test cases from excel sheet, reading test data, invoking the test methods as per test data and reporting.

 * **com.Hybrid.hunger.Utilities.core.Global** : used to create global variables in the project like [Global.Test.Browser].

* **com.Hybrid.hunger.Utilities.core.Common** : Some Common general purpose methods.

### **com.Hybrid.hunger.Pet.Test**:
Contains the test methods.

### **com.Hybrid.hunger.Pet.Scenarios**:
Contains the test scenario methods 

### **com.Hybrid.hunger.Pet.Main**:
This module is the main entry point for the execution.

We will run our project from selecting main method and run it through the InteliJ IDEA.


## PET used in this project:
We are using the PET on line as example [http://petstore.swagger.io/#/] 


### Runing the test:

 1. Clone the Project.
 2. Open it in InteliJ.
 3. Configure the Excel Control file , The Driver sheet with the name of your test scenario method Select which method to execute by flag yes/no.  file [ TestAssets/TestData/TestData.xlsx ]
 4. Define your test data as in Excel Control file , PetServiceDetails sheet 
 5. Save all you work.
 6. Got to Class com.Hybrid.hunger.Pet.Main.Runner.
 8. run the test by using the /main method.
 9. The system will run the test and generate a HTML report with date and time stamp name as per the bellow example 
	Reports/Automation_Result_2020_03_19_103241/Automation_Report.html
 

Please for more details do not hesitate to contact me at [LinkedIn](https://www.linkedin.com/in/abdelghany-abdelaziz)