//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.19 at 10:08:36 AM CEST 
//


package com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *       &lt;sequence>
 *         &lt;element ref="{}waypoint" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{}attlist.waypoints"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "waypoint"
})
@XmlRootElement(name = "waypoints")
public class Waypoints {

    @XmlElement(required = true)
    protected List<Waypoint> waypoint;
    @XmlAttribute(name = "utm_x0")
    @XmlSchemaType(name = "anySimpleType")
    protected String utmX0;
    @XmlAttribute(name = "utm_y0")
    @XmlSchemaType(name = "anySimpleType")
    protected String utmY0;

    /**
     * Gets the value of the waypoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the waypoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWaypoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Waypoint }
     * 
     * 
     */
    public List<Waypoint> getWaypoint() {
        if (waypoint == null) {
            waypoint = new ArrayList<Waypoint>();
        }
        return this.waypoint;
    }

    /**
     * Gets the value of the utmX0 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUtmX0() {
        return utmX0;
    }

    /**
     * Sets the value of the utmX0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUtmX0(String value) {
        this.utmX0 = value;
    }

    /**
     * Gets the value of the utmY0 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUtmY0() {
        return utmY0;
    }

    /**
     * Sets the value of the utmY0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUtmY0(String value) {
        this.utmY0 = value;
    }

}