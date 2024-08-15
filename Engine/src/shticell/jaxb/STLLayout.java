//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.5 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package shticell.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element ref="{}STL-Size"/>
 *       </sequence>
 *       <attribute name="rows" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       <attribute name="columns" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "stlSize"
})
@XmlRootElement(name = "STL-Layout")
public class STLLayout {

    @XmlElement(name = "STL-Size", required = true)
    protected STLSize stlSize;
    @XmlAttribute(name = "rows", required = true)
    protected int rows;
    @XmlAttribute(name = "columns", required = true)
    protected int columns;

    /**
     * Gets the value of the stlSize property.
     * 
     * @return
     *     possible object is
     *     {@link STLSize }
     *     
     */
    public STLSize getSTLSize() {
        return stlSize;
    }

    /**
     * Sets the value of the stlSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link STLSize }
     *     
     */
    public void setSTLSize(STLSize value) {
        this.stlSize = value;
    }

    /**
     * Gets the value of the rows property.
     * 
     */
    public int getRows() {
        return rows;
    }

    /**
     * Sets the value of the rows property.
     * 
     */
    public void setRows(int value) {
        this.rows = value;
    }

    /**
     * Gets the value of the columns property.
     * 
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Sets the value of the columns property.
     * 
     */
    public void setColumns(int value) {
        this.columns = value;
    }

}
