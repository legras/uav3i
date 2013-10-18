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
 *       &lt;attGroup ref="{}attlist.eight"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "eight")
public class Eight {

    @XmlAttribute(name = "center", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String center;
    @XmlAttribute(name = "turn_around", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String turnAround;
    @XmlAttribute(name = "alt")
    @XmlSchemaType(name = "anySimpleType")
    protected String alt;
    @XmlAttribute(name = "vmode")
    @XmlSchemaType(name = "anySimpleType")
    protected String vmode;
    @XmlAttribute(name = "climb")
    @XmlSchemaType(name = "anySimpleType")
    protected String climb;
    @XmlAttribute(name = "pitch")
    @XmlSchemaType(name = "anySimpleType")
    protected String pitch;
    @XmlAttribute(name = "throttle")
    @XmlSchemaType(name = "anySimpleType")
    protected String throttle;
    @XmlAttribute(name = "until")
    @XmlSchemaType(name = "anySimpleType")
    protected String until;
    @XmlAttribute(name = "radius", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String radius;

    /**
     * Gets the value of the center property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCenter() {
        return center;
    }

    /**
     * Sets the value of the center property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCenter(String value) {
        this.center = value;
    }

    /**
     * Gets the value of the turnAround property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTurnAround() {
        return turnAround;
    }

    /**
     * Sets the value of the turnAround property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTurnAround(String value) {
        this.turnAround = value;
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
     * Gets the value of the pitch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPitch() {
        return pitch;
    }

    /**
     * Sets the value of the pitch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPitch(String value) {
        this.pitch = value;
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
     * Gets the value of the radius property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRadius() {
        return radius;
    }

    /**
     * Sets the value of the radius property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRadius(String value) {
        this.radius = value;
    }

}
