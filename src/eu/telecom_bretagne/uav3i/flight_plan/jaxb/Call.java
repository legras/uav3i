//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.18 at 04:07:31 PM CEST 
//


package eu.telecom_bretagne.uav3i.flight_plan.jaxb;

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
 *       &lt;attGroup ref="{}attlist.call"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "call")
public class Call {

    @XmlAttribute(name = "fun", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String fun;

    /**
     * Gets the value of the fun property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFun() {
        return fun;
    }

    /**
     * Sets the value of the fun property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFun(String value) {
        this.fun = value;
    }

}
