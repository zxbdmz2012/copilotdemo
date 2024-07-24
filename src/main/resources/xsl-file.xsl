<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:output method="xml" indent="yes"/>

    <!-- Root template -->
    <xsl:template match="/">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="simple" page-height="29.7cm" page-width="21cm" margin="2cm">
                    <fo:region-body margin="2cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="simple">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block>
                        <xsl:apply-templates/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <!-- Template for paragraphs -->
    <xsl:template match="p">
        <fo:block font-family="serif" font-size="12pt" line-height="1.5">
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>

    <!-- Template for headers -->
    <xsl:template match="h1">
        <fo:block font-family="serif" font-size="24pt" font-weight="bold" space-after="12pt">
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>

    <xsl:template match="h2">
        <fo:block font-family="serif" font-size="18pt" font-weight="bold" space-after="10pt">
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>

    <!-- Template for tables -->
    <xsl:template match="table">
        <fo:table table-layout="fixed" width="100%" border="1pt solid black">
            <xsl:apply-templates/>
        </fo:table>
    </xsl:template>

    <xsl:template match="tr">
        <fo:table-row>
            <xsl:apply-templates/>
        </fo:table-row>
    </xsl:template>

    <xsl:template match="td">
        <fo:table-cell border="1pt solid black" padding="2pt">
            <fo:block>
                <xsl:apply-templates/>
            </fo:block>
        </fo:table-cell>
    </xsl:template>

    <!-- Add more templates for other HTML elements as needed -->
</xsl:stylesheet>