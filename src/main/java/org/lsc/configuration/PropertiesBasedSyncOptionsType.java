//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.14 at 12:21:28 AM CEST 
//


package org.lsc.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for propertiesBasedSyncOptionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="propertiesBasedSyncOptionsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://lsc-project.org/XSD/lsc-core-2.0.xsd}syncOptionsType">
 *       &lt;sequence>
 *         &lt;element name="defaultDelimiter" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="defaultPolicy" type="{http://lsc-project.org/XSD/lsc-core-2.0.xsd}policyType"/>
 *         &lt;element name="conditions" type="{http://lsc-project.org/XSD/lsc-core-2.0.xsd}conditionsType" minOccurs="0"/>
 *         &lt;element name="dataset" type="{http://lsc-project.org/XSD/lsc-core-2.0.xsd}datasetType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "propertiesBasedSyncOptionsType", propOrder = {
    "defaultDelimiter",
    "defaultPolicy",
    "conditions",
    "dataset"
})
public class PropertiesBasedSyncOptionsType
    extends SyncOptionsType
{

    @XmlElement(required = true, defaultValue = ";")
    protected String defaultDelimiter = ";";
    @XmlElement(required = true, defaultValue = "FORCE")
    protected PolicyType defaultPolicy = PolicyType.FORCE;
    protected ConditionsType conditions;
    protected List<DatasetType> dataset;

    /**
     * Gets the value of the defaultDelimiter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultDelimiter() {
        return defaultDelimiter;
    }

    /**
     * Sets the value of the defaultDelimiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultDelimiter(String value) {
        this.defaultDelimiter = value;
    }

    /**
     * Gets the value of the defaultPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyType }
     *     
     */
    public PolicyType getDefaultPolicy() {
        return defaultPolicy;
    }

    /**
     * Sets the value of the defaultPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyType }
     *     
     */
    public void setDefaultPolicy(PolicyType value) {
        this.defaultPolicy = value;
    }

    /**
     * Gets the value of the conditions property.
     * 
     * @return
     *     possible object is
     *     {@link ConditionsType }
     *     
     */
    public ConditionsType getConditions() {
        return conditions;
    }

    /**
     * Sets the value of the conditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConditionsType }
     *     
     */
    public void setConditions(ConditionsType value) {
        this.conditions = value;
    }

    /**
     * Gets the value of the dataset property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataset property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataset().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatasetType }
     * 
     * 
     */
    public List<DatasetType> getDataset() {
        if (dataset == null) {
            dataset = new ArrayList<DatasetType>();
        }
        return this.dataset;
    }

}
