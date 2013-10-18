//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.18 at 04:07:31 PM CEST 
//


package eu.telecom_bretagne.uav3i.flight_plan.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.telecom_bretagne.uav3i.flight_plan.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Header_QNAME = new QName("", "header");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.telecom_bretagne.uav3i.flight_plan.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Sector }
     * 
     */
    public Sector createSector() {
        return new Sector();
    }

    /**
     * Create an instance of {@link Corner }
     * 
     */
    public Corner createCorner() {
        return new Corner();
    }

    /**
     * Create an instance of {@link Home }
     * 
     */
    public Home createHome() {
        return new Home();
    }

    /**
     * Create an instance of {@link Call }
     * 
     */
    public Call createCall() {
        return new Call();
    }

    /**
     * Create an instance of {@link Param }
     * 
     */
    public Param createParam() {
        return new Param();
    }

    /**
     * Create an instance of {@link For }
     * 
     */
    public For createFor() {
        return new For();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link While }
     * 
     */
    public While createWhile() {
        return new While();
    }

    /**
     * Create an instance of {@link Heading }
     * 
     */
    public Heading createHeading() {
        return new Heading();
    }

    /**
     * Create an instance of {@link Attitude }
     * 
     */
    public Attitude createAttitude() {
        return new Attitude();
    }

    /**
     * Create an instance of {@link Go }
     * 
     */
    public Go createGo() {
        return new Go();
    }

    /**
     * Create an instance of {@link Xyz }
     * 
     */
    public Xyz createXyz() {
        return new Xyz();
    }

    /**
     * Create an instance of {@link Set }
     * 
     */
    public Set createSet() {
        return new Set();
    }

    /**
     * Create an instance of {@link Circle }
     * 
     */
    public Circle createCircle() {
        return new Circle();
    }

    /**
     * Create an instance of {@link Deroute }
     * 
     */
    public Deroute createDeroute() {
        return new Deroute();
    }

    /**
     * Create an instance of {@link Stay }
     * 
     */
    public Stay createStay() {
        return new Stay();
    }

    /**
     * Create an instance of {@link Follow }
     * 
     */
    public Follow createFollow() {
        return new Follow();
    }

    /**
     * Create an instance of {@link SurveyRectangle }
     * 
     */
    public SurveyRectangle createSurveyRectangle() {
        return new SurveyRectangle();
    }

    /**
     * Create an instance of {@link Return }
     * 
     */
    public Return createReturn() {
        return new Return();
    }

    /**
     * Create an instance of {@link Eight }
     * 
     */
    public Eight createEight() {
        return new Eight();
    }

    /**
     * Create an instance of {@link Oval }
     * 
     */
    public Oval createOval() {
        return new Oval();
    }

    /**
     * Create an instance of {@link Path }
     * 
     */
    public Path createPath() {
        return new Path();
    }

    /**
     * Create an instance of {@link Waypoint }
     * 
     */
    public Waypoint createWaypoint() {
        return new Waypoint();
    }

    /**
     * Create an instance of {@link Block }
     * 
     */
    public Block createBlock() {
        return new Block();
    }

    /**
     * Create an instance of {@link Includes }
     * 
     */
    public Includes createIncludes() {
        return new Includes();
    }

    /**
     * Create an instance of {@link Include }
     * 
     */
    public Include createInclude() {
        return new Include();
    }

    /**
     * Create an instance of {@link Arg }
     * 
     */
    public Arg createArg() {
        return new Arg();
    }

    /**
     * Create an instance of {@link With }
     * 
     */
    public With createWith() {
        return new With();
    }

    /**
     * Create an instance of {@link Exceptions }
     * 
     */
    public Exceptions createExceptions() {
        return new Exceptions();
    }

    /**
     * Create an instance of {@link Kml }
     * 
     */
    public Kml createKml() {
        return new Kml();
    }

    /**
     * Create an instance of {@link Procedure }
     * 
     */
    public Procedure createProcedure() {
        return new Procedure();
    }

    /**
     * Create an instance of {@link Waypoints }
     * 
     */
    public Waypoints createWaypoints() {
        return new Waypoints();
    }

    /**
     * Create an instance of {@link Sectors }
     * 
     */
    public Sectors createSectors() {
        return new Sectors();
    }

    /**
     * Create an instance of {@link Blocks }
     * 
     */
    public Blocks createBlocks() {
        return new Blocks();
    }

    /**
     * Create an instance of {@link FlightPlan }
     * 
     */
    public FlightPlan createFlightPlan() {
        return new FlightPlan();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "header")
    public JAXBElement<String> createHeader(String value) {
        return new JAXBElement<String>(_Header_QNAME, String.class, null, value);
    }

}
