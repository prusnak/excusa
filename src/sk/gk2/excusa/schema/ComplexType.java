package sk.gk2.excusa.schema;

import java.util.*;

/**
 * Schema complex type
 * @author stick
 */
public class ComplexType {
	private String name;
	private ComplexTypeGroupType type;
	private ArrayList<Element> elements;
	
	/**
	 * Constructor
	 * @param name complex type name
	 * @param minOccurs minimal occurence
	 * @param maxOccurs maximal occurence
	 * @param type complex type group type
	 */
	public ComplexType(String name, int minOccurs, int maxOccurs, ComplexTypeGroupType type) {
		this.name = name;
		this.elements = new ArrayList<Element>();
		this.type = type;
	}

	/**
	 * Returns complex type name
	 * @return complex type name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns complex type group type
	 * @return complex type group type
	 */
	public ComplexTypeGroupType getType() {
		return type;
	}

	/**
	 * Returns the list of elements
	 * @return the list of elements
	 */
	public List<Element> getElements() {
		return elements;
	}

	/**
	 * Adds new element to complex type
	 * @param e element
	 */
	public void addElement(Element e) {
		elements.add(e);
	}
}
