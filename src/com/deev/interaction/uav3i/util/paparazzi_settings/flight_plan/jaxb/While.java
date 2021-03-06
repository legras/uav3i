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
import javax.xml.bind.annotation.XmlElements;
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
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{}exception"/>
 *         &lt;element ref="{}while"/>
 *         &lt;element ref="{}heading"/>
 *         &lt;element ref="{}attitude"/>
 *         &lt;element ref="{}go"/>
 *         &lt;element ref="{}xyz"/>
 *         &lt;element ref="{}set"/>
 *         &lt;element ref="{}call"/>
 *         &lt;element ref="{}circle"/>
 *         &lt;element ref="{}deroute"/>
 *         &lt;element ref="{}stay"/>
 *         &lt;element ref="{}follow"/>
 *         &lt;element ref="{}survey_rectangle"/>
 *         &lt;element ref="{}for"/>
 *         &lt;element ref="{}return"/>
 *         &lt;element ref="{}eight"/>
 *         &lt;element ref="{}oval"/>
 *         &lt;element ref="{}path"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{}attlist.while"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "exceptionOrWhileOrHeading"
})
@XmlRootElement(name = "while")
public class While {

    @XmlElements({
        @XmlElement(name = "exception", type = Exception.class),
        @XmlElement(name = "while", type = While.class),
        @XmlElement(name = "heading", type = Heading.class),
        @XmlElement(name = "attitude", type = Attitude.class),
        @XmlElement(name = "go", type = Go.class),
        @XmlElement(name = "xyz", type = Xyz.class),
        @XmlElement(name = "set", type = Set.class),
        @XmlElement(name = "call", type = Call.class),
        @XmlElement(name = "circle", type = Circle.class),
        @XmlElement(name = "deroute", type = Deroute.class),
        @XmlElement(name = "stay", type = Stay.class),
        @XmlElement(name = "follow", type = Follow.class),
        @XmlElement(name = "survey_rectangle", type = SurveyRectangle.class),
        @XmlElement(name = "for", type = For.class),
        @XmlElement(name = "return", type = Return.class),
        @XmlElement(name = "eight", type = Eight.class),
        @XmlElement(name = "oval", type = Oval.class),
        @XmlElement(name = "path", type = Path.class)
    })
    protected List<Object> exceptionOrWhileOrHeading;
    @XmlAttribute(name = "cond")
    @XmlSchemaType(name = "anySimpleType")
    protected String cond;

    /**
     * Gets the value of the exceptionOrWhileOrHeading property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the exceptionOrWhileOrHeading property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExceptionOrWhileOrHeading().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Exception }
     * {@link While }
     * {@link Heading }
     * {@link Attitude }
     * {@link Go }
     * {@link Xyz }
     * {@link Set }
     * {@link Call }
     * {@link Circle }
     * {@link Deroute }
     * {@link Stay }
     * {@link Follow }
     * {@link SurveyRectangle }
     * {@link For }
     * {@link Return }
     * {@link Eight }
     * {@link Oval }
     * {@link Path }
     * 
     * 
     */
    public List<Object> getExceptionOrWhileOrHeading() {
        if (exceptionOrWhileOrHeading == null) {
            exceptionOrWhileOrHeading = new ArrayList<Object>();
        }
        return this.exceptionOrWhileOrHeading;
    }

    /**
     * Gets the value of the cond property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCond() {
        return cond;
    }

    /**
     * Sets the value of the cond property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCond(String value) {
        this.cond = value;
    }

}
