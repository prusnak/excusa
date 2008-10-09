package sk.gk2.excusa.wsdl;

import java.util.*;
import sk.gk2.excusa.grammar.*;

/**
 * Message
 * @author stick
 */
public class Message {
	private String name, documentation;
	private HashMap<String, MessagePart> parts;
	private Grammar grammar;
	private MessageType type;
	
	/**
	 * Constructor
	 * @param name message name
	 * @param documentation message documentation
	 * @param type message type
	 */
	public Message(String name, String documentation, MessageType type) {
		this.name = name;
		this.documentation = documentation != null ? documentation.trim() : null;
		this.parts = new HashMap<String, MessagePart>();
		this.grammar = null;
		this.type = type;
	}

	/**
	 * Returns message name
	 * @return message name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns message grammar
	 * @return message grammar
	 */
	public Grammar getGrammar() {
		return grammar;
	}
	
	/**
	 * Returns list of message parts
	 * @return list of message parts
	 */
	public Map<String, MessagePart> getParts() {
		return parts;
	}

	/**
	 * Returns message documentation
	 * @return message documentation
	 */
	public String getDocumentation() {
		return documentation;
	}
	
	/**
	 * Adds new message part
	 * @param part message part to be added
	 */
	public void addPart(MessagePart part) {
		parts.put(part.getName(), part);
	}
	
	/**
	 * Returns message type
	 * @return message type
	 */
	public MessageType getType() {
		return type;
	}
	
	/**
	 * Returns string representation of this message
	 */
	@Override
	public String toString() {
		return name + " {" + documentation + "}";
	}

	/**
	 * Creates grammar for this message
	 * @param definition web service definition to use
	 * @throws Exception
	 */
	public void createGrammar(Definition definition) throws Exception {
		GrammarCreator gc = new GrammarCreator(definition);
		grammar = gc.createForMessage(this);
	}

}
