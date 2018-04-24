<?xml version="1.0" ?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output indent="yes" method="xml"/>

  <xsl:template match="/">
    <People>
      <xsl:apply-templates select="//staff/person" />
    </People>
  </xsl:template>

  <xsl:template match="person">
    <Person>
      <xsl:attribute name="name">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </Person>
  </xsl:template>

</xsl:stylesheet>