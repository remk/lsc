//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.17 at 10:30:30 PM CEST 
//


package org.lsc.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ldapSourceServiceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ldapSourceServiceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://lsc-project.org/XSD/lsc-core-2.0.xsd}ldapServiceType">
 *       &lt;sequence>
 *         &lt;element name="cleanFilter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="filterAsync" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="interval" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ldapSourceServiceType", propOrder = {
    "cleanFilter",
    "filterAsync",
    "dateFormat",
    "interval"
})
@XmlSeeAlso({
    AsyncLdapSourceServiceType.class
})
public class LdapSourceServiceType
    extends LdapServiceType
{

    protected String cleanFilter;
    protected String filterAsync;
    protected String dateFormat;
    @XmlElement(defaultValue = "5")
    protected Integer interval = 5;

    /**
     * Gets the value of the cleanFilter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCleanFilter() {
        return cleanFilter;
    }

    /**
     * Sets the value of the cleanFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCleanFilter(String value) {
        this.cleanFilter = value;
    }

    /**
     * Gets the value of the filterAsync property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilterAsync() {
        return filterAsync;
    }

    /**
     * Sets the value of the filterAsync property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilterAsync(String value) {
        this.filterAsync = value;
    }

    /**
     * Gets the value of the dateFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets the value of the dateFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateFormat(String value) {
        this.dateFormat = value;
    }

    /**
     * Gets the value of the interval property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getInterval() {
        return interval;
    }

    /**
     * Sets the value of the interval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setInterval(Integer value) {
        this.interval = value;
    }

}
