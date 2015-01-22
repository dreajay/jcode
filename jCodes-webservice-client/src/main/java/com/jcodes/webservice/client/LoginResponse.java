
package com.jcodes.webservice.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for loginResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="loginResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="loginUser" type="{http://webservice.jcodes.com/}user" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "loginResponse", propOrder = {
    "loginUser"
})
public class LoginResponse {

    protected User loginUser;

    /**
     * Gets the value of the loginUser property.
     * 
     * @return
     *     possible object is
     *     {@link User }
     *     
     */
    public User getLoginUser() {
        return loginUser;
    }

    /**
     * Sets the value of the loginUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link User }
     *     
     */
    public void setLoginUser(User value) {
        this.loginUser = value;
    }

}
