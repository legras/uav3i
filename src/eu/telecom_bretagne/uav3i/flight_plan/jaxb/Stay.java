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
 *       &lt;attGroup ref="{}attlist.stay"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "stay")
public class Stay {

    @XmlAttribute(name = "wp", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String wp;
    @XmlAttribute(name = "vmode")
    @XmlSchemaType(name = "anySimpleType")
    protected String vmode;
    @XmlAttribute(name = "throttle")
    @XmlSchemaType(name = "anySimpleType")
    protected String throttle;
    @XmlAttribute(name = "climb")
    @XmlSchemaType(name = "anySimpleType")
    protected String climb;
    @XmlAttribute(name = "alt")
    @XmlSchemaType(name = "anySimpleType")
    protected String alt;
    @XmlAttribute(name = "until")
    @XmlSchemaType(name = "anySimpleType")
    protected String until;
    @XmlAttribute(name = "height")
    @XmlSchemaType(name = "anySimpleType")
    protected String height;

    /**
     * Gets the value of the wp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWp() {
        return wp;
    }

    /**
     * Sets the value of the wp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWp(String value) {
        this.wp = value;
    }

    /**
     * Gets the value of the vmode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVmode() {
        return vmode;
    }

    /**
     * Sets the value of the vmode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVmode(String value) {
        this.vmode = value;
    }

    /**
     * Gets the value of the throttle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThrottle() {
        return throttle;
    }

    /**
     * Sets the value of the throttle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThrottle(String value) {
        this.throttle = value;
    }

    /**
     * Gets the value of the climb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClimb() {
        return climb;
    }

    /**
     * Sets the value of the climb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClimb(String value) {
        this.climb = value;
    }

    /**
     * Gets the value of the alt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlt() {
        return alt;
    }

    /**
     * Sets the value of the alt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlt(String value) {
        this.alt = value;
    }

    /**
     * Gets the value of the until property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUntil() {
        return until;
    }

    /**
     * Sets the value of the until property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUntil(String value) {
        this.until = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeight(String value) {
        this.height = value;
    }

}
