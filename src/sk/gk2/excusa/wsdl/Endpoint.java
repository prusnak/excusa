package sk.gk2.excusa.wsdl;

import java.util.*;

/**
 * Web service endpoint
 * @author stick
 */
public class Endpoint {
	private Service service;
	private String name, location, documentation;
	private EndpointType type;
	private HashMap<String, Operation> operations;
	
	/**
	 * Constructor
	 * @param name endpoint name
	 * @param type endpoint type
	 * @param location endpoint location
	 * @param documentation endpoint documentation
	 */
	public Endpoint(String name, EndpointType type, String location, String documentation) {
		this.service = null;
		this.name = name;
		this.type = type;
		this.location = location;
		this.documentation = documentation != null ? documentation.trim() : null;
		operations = new HashMap<String, Operation>();
	}
	
	/**
	 * Returns the service this endpoint belongs to
	 * @return the service this endpoint belongs to
	 */
	public Service getService() {
		return service;
	}
	
	/**
	 * Sets the service this endpoint belongs to
	 * @param service service this endpoint belongs to
	 */
	public void setService(Service service) {
		this.service = service;
	}

	/**
	 * Returns endpoint name
	 * @return endpoint name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns endpoint type
	 * @return endpoint type
	 */
	public EndpointType getType() {
		return type;
	}

	/**
	 * Returns endpoint location
	 * @return endpoint location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Returns endpoint documentation
	 * @return endpoint documentation
	 */
	public String getDocumentation() {
		return documentation;
	}

	/**
	 * Returns endpoint operations
	 * @return endpoint operations
	 */
	public Map<String, Operation> getOperations() {
		return operations;
	}
	
	/**
	 * Returns endpoint operation by its name 
	 * @param name name of the operation
	 * @return endpoint operation
	 */
	public Operation getOperation(String name) {
		return operations.get(name);
	}
	
	/**
	 * Adds new operation to this endpoint
	 * @param operation operation to be added
	 */
	void addOperation(Operation operation) {
		operation.setEndpoint(this);
		operations.put(operation.getName() , operation);
	}
	
	/**
	 * Returns string representation of the endpoint
	 */
	@Override
	public String toString() {
		return name + " [" + type + ": " + location + "] {" + documentation + "}";
	}
}
