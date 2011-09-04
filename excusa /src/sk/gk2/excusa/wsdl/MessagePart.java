package sk.gk2.excusa.wsdl;

/**
 * Message part
 * @author stick
 */
public class MessagePart {
	private String name, element, type;
	
	/**
	 * Constructor
	 * @param name message part name
	 * @param element message part element
	 * @param type message part type
	 */
	public MessagePart(String name, String element, String type) {
		this.name = name;
		this.element = element;
		this.type = type;
	}

	/**
	 * Returns message part name
	 * @return message part name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns message part element
	 * @return message part element
	 */
	public String getElement() {
		return element;
	}

	/**
	 * Returns message part type
	 * @return message part type
	 */
	public String getType() {
		return type;
	}
}
