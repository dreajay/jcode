
package com.jcodes.webservice.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "IMyService", targetNamespace = "http://webservice.jcodes.com/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface IMyService {


    /**
     * 
     * @param b
     * @param a
     * @return
     *     returns int
     */
    @WebMethod
    @WebResult(name = "addResult", targetNamespace = "")
    @RequestWrapper(localName = "add", targetNamespace = "http://webservice.jcodes.com/", className = "com.jcodes.webservice.client.Add")
    @ResponseWrapper(localName = "addResponse", targetNamespace = "http://webservice.jcodes.com/", className = "com.jcodes.webservice.client.AddResponse")
    @Action(input = "http://webservice.jcodes.com/IMyService/addRequest", output = "http://webservice.jcodes.com/IMyService/addResponse")
    public int add(
        @WebParam(name = "a", targetNamespace = "")
        int a,
        @WebParam(name = "b", targetNamespace = "")
        int b);

    /**
     * 
     * @param b
     * @param a
     * @return
     *     returns int
     */
    @WebMethod
    @WebResult(name = "minusResult", targetNamespace = "")
    @RequestWrapper(localName = "minus", targetNamespace = "http://webservice.jcodes.com/", className = "com.jcodes.webservice.client.Minus")
    @ResponseWrapper(localName = "minusResponse", targetNamespace = "http://webservice.jcodes.com/", className = "com.jcodes.webservice.client.MinusResponse")
    @Action(input = "http://webservice.jcodes.com/IMyService/minusRequest", output = "http://webservice.jcodes.com/IMyService/minusResponse")
    public int minus(
        @WebParam(name = "a", targetNamespace = "")
        int a,
        @WebParam(name = "b", targetNamespace = "")
        int b);

    /**
     * 
     * @param username
     * @param password
     * @return
     *     returns com.jcodes.webservice.client.User
     */
    @WebMethod
    @WebResult(name = "loginUser", targetNamespace = "")
    @RequestWrapper(localName = "login", targetNamespace = "http://webservice.jcodes.com/", className = "com.jcodes.webservice.client.Login")
    @ResponseWrapper(localName = "loginResponse", targetNamespace = "http://webservice.jcodes.com/", className = "com.jcodes.webservice.client.LoginResponse")
    @Action(input = "http://webservice.jcodes.com/IMyService/loginRequest", output = "http://webservice.jcodes.com/IMyService/loginResponse")
    public User login(
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "password", targetNamespace = "")
        String password);

}