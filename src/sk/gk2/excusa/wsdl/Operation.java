package sk.gk2.excusa.wsdl;

import java.util.*;

/**
 * Web service operation
 * @author stick
 */
public class Operation {
	private Endpoint endpoint;
	private String name, documentation;
	private String location, action;
	private Message input, output;
	private ArrayList<Message> faults;
	
	/**
	 * Constructor
	 * @param name operation name
	 * @param location operation location
	 * @param action operation action
	 * @param documentation operation documentation
	 */
	public Operation(String name, String location, String action, String documentation) {
		this.endpoint = null;
		this.name = name;
		this.documentation = documentation != null ? documentation.trim() : null;
		this.location = location;
		this.action = action;
		this.input = null;
		this.output = null;
		faults = new ArrayList<Message>();
	}
	
	/**
	 * Returns endpoint this operation belongs to
	 * @return endpoint
	 */
	public Endpoint getEndpoint() {
		return endpoint;
	}
	
	/**
	 * Sets endpoint this operation belongs to
	 * @param endpoint endpoint
	 */
	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * Returns operation name
	 * @return operation name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns operation location
	 * @return operation location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Returns operation action
	 * @return operation action
	 */
	public String getAction() {
		return action;
	}
	
	/**
	 * Returns operation documentation
	 * @return operation documentation
	 */
	public String getDocumentation() {
		return documentation;
	}

	/**
	 * Returns operation input message
	 * @return operation input message
	 */
	public Message getInput() {
		return input;
	}

	/**
	 * Returns operation output message
	 * @return operation output message
	 */
	public Message getOutput() {
		return output;
	}
	
	/**
	 * Returns list of operation fault messages
	 * @return list of operation fault messages
	 */
	public List<Message> getFaults() {
		return faults;
	}

	/**
	 * Sets operation input message
	 * @param input operation input message
	 */
	void setInput(Message input) {
		this.input = input;
	}

	/**
	 * Sets operation output message
	 * @param output operation output message
	 */
	void setOutput(Message output) {
		this.output = output;
	}
	
	/**
	 * Adds new operation fault message
	 * @param fault operation fault message to be added
	 */
	void addFault(Message fault) {
		faults.add(fault);
	}
	
	/**
	 * Returns string representation of this operation
	 */
	@Override
	public String toString() {
		return name + " [" + location + "|" + action + "] {" + documentation + "}";
	}
}
