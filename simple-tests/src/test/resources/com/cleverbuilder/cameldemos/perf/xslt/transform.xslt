<?xml version="1.0" ?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ws="urn:com.workday/workersync"
                xmlns:out="http://www.example.com/OutputFormat">

  <xsl:strip-space elements="*"/>
  <xsl:output indent="yes" method="xml"/>

  <xsl:template match="/">
    <out:RootElement>
      <xsl:apply-templates select="ws:Worker_Sync/ws:Worker" />
    </out:RootElement>
  </xsl:template>

  <xsl:template match="ws:Worker">
    <out:OutputRecord>
      <out:FirstName>
        <xsl:value-of select="ws:Personal/ws:Name_Data/ws:First_Name"/>
      </out:FirstName>
      <out:LastName>
        <xsl:value-of select="ws:Personal/ws:Name_Data/ws:Last_Name"/>
      </out:LastName>
      <out:EmailAddress>
        <xsl:value-of select="ws:Personal/ws:Email_Data[ws:Is_Primary = 'true']/ws:Email_Address"/>
      </out:EmailAddress>
      <out:PostCode>
        <xsl:value-of select="ws:Personal/ws:Address_Data[ws:Address_Type = 'Work']/ws:Postal_Code"/>
      </out:PostCode>
      <out:EmployeeID>
        <xsl:value-of select="ws:Summary/ws:Employee_ID"/>
      </out:EmployeeID>
    </out:OutputRecord>
  </xsl:template>


</xsl:stylesheet>