package sk.gk2.excusa.wsdl;

import java.util.*;

/**
 * Web service
 * @author stick
 */
public class Service {
	private String name, documentation;
	private HashMap<String, Endpoint> endpoints;
	private Definition definition;
	
	/**
	 * Constructor
	 * @param name web service name
	 * @param documentation web service name
	 */
	public Service(String name, String documentation) {
		this.definition = null;
		this.name = name;
		this.documentation = documentation != null ? documentation.trim() : null;
		endpoints = new HashMap<String, Endpoint>();
	}
	
	/**
	 * Returns definition this service belongs to 
	 * @return definition this service belongs to
	 */
	public Definition getDefinition() {
		return definition;
	}
	
	/**
	 * Sets definition this service belongs to
	 * @param definition definition
	 */
	public void setDefinition(Definition definition) {
		this.definition = definition;
	}

	/**
	 * Returns service name
	 * @return service name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns service documentation
	 * @return service documentation
	 */
	public String getDocumentation() {
		return documentation;
	}
	
	/**
	 * Returns list of service endpoints
	 * @return list of service endpoints
	 */
	public Map<String, Endpoint> getEndpoints() {
		return endpoints;
	}
	
	/**
	 * Gets service endpoint by its name
	 * @param name name of the endpoint
	 * @return endpoint
	 */
	public Endpoint getEndpoint(String name) {
		return endpoints.get(name);
	}
	
	/**
	 * Add new endpoint to this service
	 * @param endpoint endpoint to be added
	 */
	void addEndpoint(Endpoint endpoint) {
		endpoint.setService(this);
		endpoints.put( endpoint.getName() , endpoint);
	}
	
	/**
	 * Returns string representation of this service
	 */
	@Override
	public String toString() {
		return name + " {" + documentation + "}";
	}
}
