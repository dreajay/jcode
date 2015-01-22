
package com.jcodes.webservice.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for minusResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="minusResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="minusResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "minusResponse", propOrder = {
    "minusResult"
})
public class MinusResponse {

    protected int minusResult;

    /**
     * Gets the value of the minusResult property.
     * 
     */
    public int getMinusResult() {
        return minusResult;
    }

    /**
     * Sets the value of the minusResult property.
     * 
     */
    public void setMinusResult(int value) {
        this.minusResult = value;
    }

}
