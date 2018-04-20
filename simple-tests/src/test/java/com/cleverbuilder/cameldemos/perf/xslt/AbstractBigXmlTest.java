package com.cleverbuilder.cameldemos.perf.xslt;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;

/**
 * Created by tdonohue on 20/02/2018.
 */
public abstract class AbstractBigXmlTest extends CamelTestSupport {

    private int RECORDS = 100000;

    private static String STREAM_ENDPOINT = "stream:file?fileName=target/bigfiles/outputfile.xml";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        String fileOpen = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ws:Worker_Sync xmlns:ws=\"urn:com.workday/workersync\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
        String fileClose = "</ws:Worker_Sync>";

        String worker = "    <ws:Worker>\n" +
                "        <ws:Summary>\n" +
                "            <ws:Employee_ID>21001</ws:Employee_ID>\n" +
                "            <ws:Name>Lutricia McNeil</ws:Name>\n" +
                "        </ws:Summary>\n" +
                "        <ws:Eligibility>true</ws:Eligibility>\n" +
                "        <ws:Personal>\n" +
                "            <ws:Name_Data>\n" +
                "                <ws:Name_Type>Legal</ws:Name_Type>\n" +
                "                <ws:First_Name>Lutricia</ws:First_Name>\n" +
                "                <ws:Last_Name>Sylvester</ws:Last_Name>\n" +
                "                <ws:Formatted_Name>Lutricia Sylvester</ws:Formatted_Name>\n" +
                "                <ws:Reporting_Name>Sylvester, Lutricia</ws:Reporting_Name>\n" +
                "            </ws:Name_Data>\n" +
                "            <ws:Gender>Female</ws:Gender>\n" +
                "            <ws:Birth_Date>1971-05-25</ws:Birth_Date>\n" +
                "            <ws:Marital_Status>Married_USA</ws:Marital_Status>\n" +
                "            <ws:Ethnicity>White</ws:Ethnicity>\n" +
                "            <ws:Hispanic_or_Latino>false</ws:Hispanic_or_Latino>\n" +
                "            <ws:Citizenship_Status>USA_Citizen</ws:Citizenship_Status>\n" +
                "            <ws:Address_Data>\n" +
                "                <ws:Address_Type>WORK</ws:Address_Type>\n" +
                "                <ws:Address_Is_Public>true</ws:Address_Is_Public>\n" +
                "                <ws:Is_Primary>true</ws:Is_Primary>\n" +
                "                <ws:Address_Line_Data ws:Type=\"ADDRESS_LINE_1\" ws:Label=\"Address Line 1\">3939 The Embarcadero</ws:Address_Line_Data>\n" +
                "                <ws:Municipality>San Francisco</ws:Municipality>\n" +
                "                <ws:Region>California</ws:Region>\n" +
                "                <ws:Postal_Code>94111</ws:Postal_Code>\n" +
                "                <ws:Country>GB</ws:Country>\n" +
                "            </ws:Address_Data>\n" +
                "            <ws:Phone_Data>\n" +
                "                <ws:Phone_Type>WORK</ws:Phone_Type>\n" +
                "                <ws:Phone_Device_Type>Mobile</ws:Phone_Device_Type>\n" +
                "                <ws:Phone_Is_Public>true</ws:Phone_Is_Public>\n" +
                "                <ws:Is_Primary>true</ws:Is_Primary>\n" +
                "                <ws:Formatted_Phone_Number>+1 (415) 789-8904</ws:Formatted_Phone_Number>\n" +
                "                <ws:International_Phone_Code>1</ws:International_Phone_Code>\n" +
                "                <ws:Phone_Area_Code>415</ws:Phone_Area_Code>\n" +
                "                <ws:Phone_Number>789-8904</ws:Phone_Number>\n" +
                "            </ws:Phone_Data>\n" +
                "            <ws:Email_Data>\n" +
                "                <ws:Email_Type>WORK</ws:Email_Type>\n" +
                "                <ws:Email_Is_Public>true</ws:Email_Is_Public>\n" +
                "                <ws:Is_Primary>true</ws:Is_Primary>\n" +
                "                <ws:Email_Address>lSylvester@example.com</ws:Email_Address>\n" +
                "            </ws:Email_Data>\n" +
                "            <ws:Tobacco_Use>false</ws:Tobacco_Use>\n" +
                "        </ws:Personal>\n" +
                "        <ws:Status>\n" +
                "            <ws:Employee_Status>Active</ws:Employee_Status>\n" +
                "            <ws:Active>true</ws:Active>\n" +
                "            <ws:Active_Status_Date>2000-01-01</ws:Active_Status_Date>\n" +
                "            <ws:Hire_Date>2000-01-01</ws:Hire_Date>\n" +
                "            <ws:Original_Hire_Date>2000-01-01</ws:Original_Hire_Date>\n" +
                "            <ws:Hire_Reason>Hire_Employee_New_Hire_New_Position</ws:Hire_Reason>\n" +
                "            <ws:Continuous_Service_Date>2000-01-01</ws:Continuous_Service_Date>\n" +
                "            <ws:First_Day_of_Work>2000-01-01</ws:First_Day_of_Work>\n" +
                "            <ws:Retired>false</ws:Retired>\n" +
                "            <ws:Seniority_Date>2000-01-01</ws:Seniority_Date>\n" +
                "            <ws:Terminated>false</ws:Terminated>\n" +
                "            <ws:Not_Eligible_for_Hire>false</ws:Not_Eligible_for_Hire>\n" +
                "            <ws:Regrettable_Termination>false</ws:Regrettable_Termination>\n" +
                "            <ws:Not_Returning>false</ws:Not_Returning>\n" +
                "            <ws:Return_Unknown>false</ws:Return_Unknown>\n" +
                "            <ws:Has_International_Assignment>false</ws:Has_International_Assignment>\n" +
                "            <ws:Home_Country>US</ws:Home_Country>\n" +
                "            <ws:Rehire>false</ws:Rehire>\n" +
                "        </ws:Status>\n" +
                "        <ws:Compensation>\n" +
                "            <ws:Position_ID>P-00004</ws:Position_ID>\n" +
                "            <ws:Effective_Date>2016-04-01</ws:Effective_Date>\n" +
                "            <ws:Compensation_Change_Reason>Merit _Performance_Annual_Focal_Review</ws:Compensation_Change_Reason>\n" +
                "            <ws:Total_Annual_Base_Pay>212676.46</ws:Total_Annual_Base_Pay>\n" +
                "            <ws:Total_Base_Pay>17723.04</ws:Total_Base_Pay>\n" +
                "            <ws:Base_Pay_Currency>USD</ws:Base_Pay_Currency>\n" +
                "            <ws:Base_Pay_Frequency>Monthly</ws:Base_Pay_Frequency>\n" +
                "        </ws:Compensation>\n" +
                "        <ws:Identification_Data>\n" +
                "            <ws:Identification>Custom</ws:Identification>\n" +
                "            <ws:ID>123456</ws:ID>\n" +
                "            <ws:ID_Type>GPID</ws:ID_Type>\n" +
                "        </ws:Identification_Data>\n" +
                "    </ws:Worker>";

        deleteDirectory("target/bigfiles");

        template.sendBody(STREAM_ENDPOINT, fileOpen);

        for (int i = 0; i < RECORDS; i++) {
            template.sendBody(STREAM_ENDPOINT, worker);
        }

        template.sendBody(STREAM_ENDPOINT, fileClose);
    }


}
