//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.24 at 11:07:35 AM CEST 
//


package com.deev.interaction.uav3i.util.paparazzi_settings.airframe.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{}attlist.linear"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "linear")
public class Linear {

    @XmlAttribute(name = "name", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String name;
    @XmlAttribute(name = "arity", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String arity;
    @XmlAttribute(name = "coeff1", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String coeff1;
    @XmlAttribute(name = "coeff2")
    @XmlSchemaType(name = "anySimpleType")
    protected String coeff2;
    @XmlAttribute(name = "coeff3")
    @XmlSchemaType(name = "anySimpleType")
    protected String coeff3;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the arity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArity() {
        return arity;
    }

    /**
     * Sets the value of the arity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArity(String value) {
        this.arity = value;
    }

    /**
     * Gets the value of the coeff1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoeff1() {
        return coeff1;
    }

    /**
     * Sets the value of the coeff1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoeff1(String value) {
        this.coeff1 = value;
    }

    /**
     * Gets the value of the coeff2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoeff2() {
        return coeff2;
    }

    /**
     * Sets the value of the coeff2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoeff2(String value) {
        this.coeff2 = value;
    }

    /**
     * Gets the value of the coeff3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoeff3() {
        return coeff3;
    }

    /**
     * Sets the value of the coeff3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoeff3(String value) {
        this.coeff3 = value;
    }

}
