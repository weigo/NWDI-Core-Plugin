<?xml version="1.0" encoding="utf-8"?>
<!-- ******************************************************************* File: docbook2twiki.xsl Author: Baoqiu Cui <cbaoqiu AT yahoo DOT com> 
  Copyright (C) 2009 Baoqiu Cui This is an XSL stylesheet that converts DocBook documents (http://www.docbook.org/) to TWiki (http://www.twiki.org/) 
  format. docbook2twiki.xsl is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published 
  by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This stylesheet is distributed in the 
  hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
  See the GNU General Public License for more details. A copy of the GNU General Public License can be made from <http://www.gnu.org/licenses/>. 
  Because DocBook contains much more features than TWiki does, this stylesheet can only (and will always) support a subset of DocBook elements. 
  Currently this stylesheet works best for DocBook files exported from Emacs Org-mode files by the DocBook exporter in Org-mode (http://orgmode.org/). 
  Most formatting features supported by Org mode are supported. Some code and ideas are copied from DocBook2Wiki.xsl that is included in the contrib/ 
  directory of DocBook 1.74.0. $Id$ ******************************************************************* -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:d="http://docbook.org/ns/docbook" xmlns:xlink="http://www.w3.org/1999/xlink"
  version="1.0">

  <xsl:output method="text" encoding="utf-8" indent="yes" />
  <xsl:strip-space elements="*" />

  <xsl:template match="/">
    <xsl:text># </xsl:text>
    <xsl:apply-templates />
  </xsl:template>

  <!-- filename -->

  <xsl:template match="d:filename">
    <xsl:text> **</xsl:text>
    <xsl:apply-templates />
    <xsl:text>** </xsl:text>
  </xsl:template>
  <!-- imagedata -->

  <xsl:template match="d:imagedata">
    <xsl:if test="parent::d:imageobject[@role!='fo' or not(@role)]">
      <xsl:text>&lt;img src="%ATTACHURL%/</xsl:text>
      <xsl:call-template name="getfilename">
        <xsl:with-param name="fileref">
          <xsl:value-of select="@fileref" />
        </xsl:with-param>
      </xsl:call-template>
      <xsl:text>"&gt;</xsl:text>
      <xsl:call-template name="newline" />
    </xsl:if>
  </xsl:template>

  <!-- section title -->

  <xsl:template match="d:title[parent::d:section|parent::d:chapter]">
    <xsl:variable name="level" select="count(ancestor::d:chapter|ancestor::d:section|ancestor::d:info) + 1" />
    <xsl:choose>
      <xsl:when test="$level &lt; 7">
        <xsl:call-template name="newline" />
        <xsl:value-of select="substring('#######', 1, $level)" />
        <xsl:text> </xsl:text>
        <xsl:apply-templates />
        <xsl:text>

</xsl:text>
      </xsl:when>
      <!-- Treat section title at 7th level or below as bold text. -->
      <xsl:otherwise>
        <xsl:text>*</xsl:text>
        <xsl:call-template name="remove-trailing-ws">
          <xsl:with-param name="lines" select="." />
        </xsl:call-template>
        <xsl:text>*</xsl:text>
        <xsl:call-template name="newline" />
        <xsl:call-template name="newline" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="d:title">
    <xsl:if test="not(parent::d:info/parent::d:book|parent::d:info/parent::d:article)">
      <xsl:call-template name="newline" />
      <xsl:call-template name="newline" />
      <xsl:text>**</xsl:text>
    </xsl:if>
    <xsl:call-template name="remove-trailing-ws">
      <xsl:with-param name="lines" select="." />
    </xsl:call-template>
    <xsl:if test="not(parent::d:info/parent::d:book|parent::d:info/parent::d:article)">
      <xsl:text>**</xsl:text>
    </xsl:if>
    <xsl:call-template name="newline" />
  </xsl:template>
  <!-- programlisting -->

  <xsl:template match="d:programlisting/text()">
    <xsl:call-template name="indent" />
    <xsl:call-template name="newline" />
    <xsl:text>```</xsl:text>
    <xsl:call-template name="newline" />

    <xsl:variable name="before" select="count(preceding-sibling::*)" />
    <xsl:variable name="after" select="count(following-sibling::*)" />
    <xsl:variable name="proglist" select="." />

    <xsl:variable name="proglist-1">
      <xsl:choose>
        <xsl:when test="$before = 0">
          <xsl:call-template name="remove-leading-newlines">
            <xsl:with-param name="lines" select="$proglist" />
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$proglist" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="proglist-2">
      <xsl:choose>
        <xsl:when test="$after = 0">
          <xsl:call-template name="remove-trailing-ws">
            <xsl:with-param name="lines" select="$proglist-1" />
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$proglist-1" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:value-of select="$proglist-2" />
    <xsl:call-template name="newline" />
    <xsl:text>```</xsl:text>
    <xsl:call-template name="newline" />
  </xsl:template>

  <!-- article/title|info -->

  <xsl:template match="d:copyright|d:authorgroup" />

  <!-- link -->

  <xsl:template match="d:link">
    <xsl:variable name="link-url" select="@xlink:href" />
    <xsl:variable name="desc" select="." />
    <xsl:choose>
      <xsl:when test="starts-with($link-url, $desc) and
		    starts-with($desc, $link-url)">
        <xsl:text>&lt;a href="</xsl:text>
        <xsl:value-of select="$link-url" />
        <xsl:text>"&gt;</xsl:text>
        <xsl:value-of select="$desc" />
        <xsl:text>&lt;/a&gt;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>&lt;a href="</xsl:text>
        <xsl:value-of select="$link-url" />
        <xsl:text>"&gt;</xsl:text>
        <xsl:apply-templates />
        <xsl:text>&lt;/a&gt;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <!-- <xsl:choose> <xsl:when test="starts-with($link-url, $desc) and starts-with($desc, $link-url)"> <xsl:text>[</xsl:text> <xsl:value-of 
      select="$desc" /> <xsl:text>](</xsl:text> <xsl:value-of select="$link-url" /> <xsl:text>)</xsl:text> </xsl:when> <xsl:otherwise> <xsl:text>[</xsl:text> 
      <xsl:value-of select="$desc" /> <xsl:text>](</xsl:text> <xsl:value-of select="$link-url" /> <xsl:text>)</xsl:text> </xsl:otherwise> </xsl:choose> -->
  </xsl:template>

  <xsl:template match="d:envar">
    <xsl:text> _</xsl:text>
    <xsl:apply-templates />
    <xsl:text>_ </xsl:text>
  </xsl:template>

  <!-- itemizedlist -->

  <xsl:template match="d:itemizedlist">
    <xsl:call-template name="newline" />
    <xsl:apply-templates />
  </xsl:template>

  <!-- listitem -->

  <xsl:template match="d:listitem">
    <xsl:choose>
      <xsl:when test="parent::d:itemizedlist">
        <xsl:call-template name="newline" />
        <xsl:text>* </xsl:text>
        <xsl:apply-templates />
        <xsl:call-template name="newline" />
      </xsl:when>
      <xsl:when test="parent::d:orderedlist">
        <xsl:text>1. </xsl:text>
        <xsl:apply-templates />
      </xsl:when>
      <xsl:when test="parent::d:varlistentry">
        <xsl:call-template name="newline" />
        <xsl:text>&lt;td&gt;</xsl:text>
        <xsl:apply-templates />
        <xsl:text>&lt;/td&gt;</xsl:text>
        <xsl:call-template name="newline" />
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- warning -->

  <xsl:template match="d:warning">
    <xsl:call-template name="indent" />
    <xsl:text>*Warning*: </xsl:text>
    <xsl:apply-templates />
    <xsl:call-template name="newline" />
    <xsl:call-template name="newline" />
  </xsl:template>

  <!-- tip -->

  <xsl:template match="d:tip">
    <xsl:call-template name="indent" />
    <xsl:text>*Tip*: </xsl:text>
    <xsl:apply-templates />
    <xsl:call-template name="newline" />
    <xsl:call-template name="newline" />
  </xsl:template>

  <!-- caution -->

  <xsl:template match="d:caution">
    <xsl:call-template name="indent" />
    <xsl:text>*Caution*: </xsl:text>
    <xsl:apply-templates />
    <xsl:call-template name="newline" />
    <xsl:call-template name="newline" />
  </xsl:template>

  <!-- note -->

  <xsl:template match="d:note">
    <xsl:call-template name="indent" />
    <xsl:text>*Note*: </xsl:text>
    <xsl:apply-templates />
    <xsl:call-template name="newline" />
    <xsl:call-template name="newline" />
  </xsl:template>

  <!-- varlistentry/term -->

  <xsl:template match="d:term">
    <!-- xsl:variable name="listlevel" select="count(ancestor-or-self::d:listitem)+1" /> <xsl:value-of select="substring(' ', 1, $listlevel * 
      3)" /> <xsl:text>$ </xsl:text -->
    <xsl:text>&lt;td&gt;</xsl:text>
    <xsl:apply-templates />
    <xsl:text>&lt;/td&gt;</xsl:text>
  </xsl:template>

  <!-- varlistentry -->
  <xsl:template match="d:varlistentry">
    <xsl:call-template name="newline" />
    <xsl:text>&lt;tr&gt;</xsl:text>
    <xsl:apply-templates />
    <xsl:text>&lt;/tr&gt;</xsl:text>
    <xsl:call-template name="newline" />
  </xsl:template>

  <!-- emphasis -->

  <xsl:template match="d:emphasis">
    <!-- Make sure there is a space before the emphasis markup when it is needed. -->
    <xsl:if test="count(preceding-sibling::*) &gt; 0 and
		not(preceding-sibling::node()[1][self::text()])">
      <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="@role = 'underline'">
        <xsl:text>&lt;u&gt;</xsl:text>
        <xsl:apply-templates />
        <xsl:text>&lt;/u&gt;</xsl:text>
      </xsl:when>
      <xsl:when test="@role = 'bold'">
        <xsl:text>*</xsl:text>
        <xsl:apply-templates />
        <xsl:text>*</xsl:text>
      </xsl:when>
      <xsl:when test="@role = 'strikethrough'">
        <xsl:text>&lt;strike&gt;</xsl:text>
        <xsl:apply-templates />
        <xsl:text>&lt;/strike&gt;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>_</xsl:text>
        <xsl:apply-templates />
        <xsl:text>_</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- footnote -->

  <xsl:template match="d:footnote">
    <xsl:text>{{</xsl:text>
    <xsl:apply-templates />
    <xsl:text>}}</xsl:text>
  </xsl:template>

  <!-- footnoteref -->

  <xsl:template match="d:footnoteref">
    <xsl:variable name="footnote-id" select="@linkend" />
    <xsl:apply-templates select="//d:footnote[@xml:id = $footnote-id]" />
  </xsl:template>

  <!-- literal -->

  <xsl:template match="d:literal">
    <xsl:text>=</xsl:text>
    <xsl:apply-templates />
    <xsl:text>=</xsl:text>
  </xsl:template>

  <!-- code -->

  <xsl:template match="d:code">
    <!-- Make sure there is a space before the `=' markup when it is needed. -->
    <xsl:if test="count(preceding-sibling::*) &gt; 0 and
		not(preceding-sibling::node()[1][self::text()])">
      <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:text>`</xsl:text>
    <xsl:apply-templates />
    <xsl:text>`</xsl:text>
  </xsl:template>

  <!-- superscript -->

  <xsl:template match="d:superscript">
    <xsl:text>&lt;sup&gt;</xsl:text>
    <xsl:apply-templates />
    <xsl:text>&lt;/sup&gt;</xsl:text>
  </xsl:template>

  <!-- subscript -->

  <xsl:template match="d:subscript">
    <xsl:text>&lt;sub&gt;</xsl:text>
    <xsl:apply-templates />
    <xsl:text>&lt;/sub&gt;</xsl:text>
  </xsl:template>

  <!-- variablelist -->

  <xsl:template match="d:variablelist">
    <xsl:apply-templates select="d:info/d:title" />
    <xsl:text>&lt;table&gt;</xsl:text>
    <xsl:apply-templates select="d:varlistentry" />
    <xsl:text>&lt;/table&gt;</xsl:text>
  </xsl:template>

  <!-- para -->

  <xsl:template match="d:para">
    <xsl:apply-templates />
    <!-- Add a newline for a para in listitem or section. This newline is always required in these two situations (and we have special handling 
      for listitem too). -->
    <xsl:choose>
      <xsl:when test="parent::d:section">
        <xsl:call-template name="newline" />
      </xsl:when>
    </xsl:choose>
    <!-- Add a second newline if this para is in a section but itself is NOT the last para in that section. -->
    <xsl:choose>
      <xsl:when test="parent::d:section and
		    following-sibling::*">
        <xsl:call-template name="newline" />
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="d:para" mode="centering">
    <xsl:apply-templates />
    <xsl:call-template name="newline" />
    <xsl:call-template name="newline" />
  </xsl:template>

  <!-- getfilename -->

  <xsl:template name="getfilename">
    <xsl:param name="fileref" />
    <xsl:choose>
      <xsl:when test="contains($fileref,'\')">
        <xsl:call-template name="getfilename">
          <xsl:with-param name="fileref">
            <xsl:value-of select="substring-after($fileref,'\')" />
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="contains($fileref,'/')">
        <xsl:call-template name="getfilename">
          <xsl:with-param name="fileref">
            <xsl:value-of select="substring-after($fileref,'/')" />
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$fileref" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <!-- HTML table -->
  <!-- Here, only <tr> needs to worry about indentation. -->

  <xsl:template match="d:table/d:caption" />

  <xsl:template match="d:tr">
    <xsl:call-template name="indent" />
    <xsl:text>| </xsl:text>
    <xsl:apply-templates />
    <xsl:call-template name="newline" />
  </xsl:template>

  <xsl:template match="d:th">
    <xsl:text> *</xsl:text>
    <xsl:apply-templates />
    <xsl:text>*  | </xsl:text>
  </xsl:template>

  <xsl:template match="d:td">
    <xsl:apply-templates />
    <xsl:text> | </xsl:text>
  </xsl:template>

  <!-- Tables with left- and right- alignment in columns -->
  <xsl:template match="d:informaltable[d:colgroup]/d:tbody/d:tr/d:td">
    <xsl:param name="pos" select="position()" />
    <xsl:param name="align" select="../../../d:colgroup/d:col[$pos]/@align" />
    <xsl:if test="$align='right'">
      <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:apply-templates />
    <xsl:if test="$align='left'">
      <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:text> | </xsl:text>
  </xsl:template>

  <!-- CLAS table -->

  <xsl:template match="d:table/d:title">
    <xsl:call-template name="indent" />
    <xsl:apply-templates />
    <xsl:call-template name="newline" />
  </xsl:template>

  <xsl:template match="d:tgroup">
    <xsl:choose>
      <!-- Special informaltable for text centering -->
      <xsl:when test="count(./*) = 1 and
		    @align='center' and
		    @cols='1'">
        <xsl:text>&lt;div style="text-align: center"&gt;
</xsl:text>
        <xsl:apply-templates select="d:tbody/d:row/d:entry" mode="centering" />
        <xsl:text>&lt;/div&gt;
</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="d:thead" />
        <xsl:apply-templates select="d:tbody" />
        <xsl:apply-templates select="d:tfoot" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="d:thead/d:row|d:tbody/d:row|d:tfoot/d:row">
    <xsl:call-template name="indent" />
    <xsl:text>| </xsl:text>
    <xsl:apply-templates />
    <xsl:call-template name="newline" />
  </xsl:template>

  <xsl:template match="d:thead/d:row/d:entry|d:tfoot/d:row/d:entry">
    <xsl:text>*</xsl:text>
    <xsl:apply-templates />
    <xsl:text>* | </xsl:text>
  </xsl:template>

  <xsl:template match="d:tbody/d:row/d:entry">
    <xsl:apply-templates />
    <xsl:text> | </xsl:text>
  </xsl:template>

  <xsl:template match="d:tbody/d:row/d:entry" mode="centering">
    <xsl:apply-templates mode="centering" />
  </xsl:template>

  <xsl:template match="text()|@*">
    <xsl:variable name="oneline">
      <xsl:call-template name="entity-replace">
        <xsl:with-param name="input-str" select="translate(., '&#xA;&#xD;', ' ')" />
      </xsl:call-template>
    </xsl:variable>

    <xsl:value-of select="normalize-space($oneline)" />
  </xsl:template>

  <xsl:template name="remove-leading-spaces">
    <xsl:param name="text" />
    <xsl:if test="starts-with($text, ' ')">
      <xsl:call-template name="remove-leading-spaces">
        <xsl:with-param name="text">
          <xsl:value-of select="substring($text, 2)" />
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="not(starts-with($text, ' '))">
      <xsl:value-of select="$text" />
    </xsl:if>
  </xsl:template>

  <xsl:template name="indent">
    <xsl:variable name="listlevel" select="count(ancestor-or-self::d:listitem)" />
    <xsl:variable name="ind" select="substring('                        ', 1, $listlevel * 3)" />
    <!-- Additional indent for listitem bullet/number -->
    <xsl:variable name="delta-ind">
      <xsl:choose>
        <xsl:when test="$listlevel &gt; 0">
          <xsl:variable name="latest-listitem" select="ancestor-or-self::d:listitem[1]" />
          <xsl:choose>
            <xsl:when test="$latest-listitem/parent::d:orderedlist">
              <xsl:text>   </xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>  </xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text></xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:if test="(self::d:para and position() != 1) or
		parent::d:programlisting or
		self::d:tr">
      <xsl:value-of select="$ind" />
      <xsl:value-of select="$delta-ind" />
      <xsl:if
        test="self::d:para and
		  $listlevel &gt; 0 and
		  count(preceding-sibling::*) &gt; 0 and
		  not(preceding-sibling::node()[1][self::d:programlisting])">
        <xsl:text>%BR%%BR%
</xsl:text>
        <xsl:value-of select="$ind" />
        <xsl:value-of select="$delta-ind" />
      </xsl:if>
    </xsl:if>
  </xsl:template>

  <!-- Insert a newline -->

  <xsl:template name="newline">
    <xsl:text>
</xsl:text>
  </xsl:template>

  <!-- Replace characters `&', `<', and `>' with `&amp;', `&lt;' and `&gt;' respectively. -->

  <xsl:template name="entity-replace">
    <xsl:param name="input-str" />
    <xsl:call-template name="string-replace">
      <xsl:with-param name="to" select="'&amp;gt;'" />
      <xsl:with-param name="from" select="'&gt;'" />
      <xsl:with-param name="string">
        <xsl:call-template name="string-replace">
          <xsl:with-param name="to" select="'&amp;lt;'" />
          <xsl:with-param name="from" select="'&lt;'" />
          <xsl:with-param name="string">
            <xsl:call-template name="string-replace">
              <xsl:with-param name="to" select="'&amp;amp;'" />
              <xsl:with-param name="from" select="'&amp;'" />
              <xsl:with-param name="string" select="$input-str" />
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- Replace all occurences of the character(s) `from' in the string `string' with the string `to'. -->

  <xsl:template name="string-replace">
    <xsl:param name="string" />
    <xsl:param name="from" />
    <xsl:param name="to" />
    <xsl:choose>
      <xsl:when test="contains($string,$from)">
        <xsl:value-of select="substring-before($string,$from)" />
        <xsl:value-of select="$to" />
        <xsl:call-template name="string-replace">
          <xsl:with-param name="string" select="substring-after($string,$from)" />
          <xsl:with-param name="from" select="$from" />
          <xsl:with-param name="to" select="$to" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$string" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Remove leading newlines -->

  <xsl:template name="remove-leading-newlines">
    <xsl:param name="lines" />
    <xsl:choose>
      <xsl:when test="starts-with($lines,'&#xA;') or
                    starts-with($lines,'&#xD;')">
        <xsl:call-template name="remove-leading-newlines">
          <xsl:with-param name="lines" select="substring($lines, 2)" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$lines" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Remove trailing whitespaces -->

  <xsl:template name="remove-trailing-ws">
    <xsl:param name="lines" />
    <xsl:variable name="last-char">
      <xsl:value-of select="substring($lines, string-length($lines), 1)" />
    </xsl:variable>
    <xsl:choose>
      <xsl:when
        test="($last-char = '&#xA;') or
                    ($last-char = '&#xD;') or
                    ($last-char = '&#x20;') or
                    ($last-char = '&#x9;')">
        <xsl:call-template name="remove-trailing-ws">
          <xsl:with-param name="lines" select="substring($lines, 1,
				string-length($lines) - 1)" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$lines" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="create-title">
    <xsl:param name="lines" />
    <xsl:choose>
      <xsl:when test="string-length($lines) &gt; 0">
        <xsl:text>=</xsl:text>
        <xsl:call-template name="create-title">
          <xsl:with-param name="lines" select="substring($lines, 1,
                string-length($lines) - 1)" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$lines" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
