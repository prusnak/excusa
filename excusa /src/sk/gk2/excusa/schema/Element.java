package sk.gk2.excusa.schema;

/**
 * Schema element
 * @author stick
 */
public class Element {
	private String name;
	private int minOccurs, maxOccurs;
	private SimpleType simpleType;
	private ComplexType complexType;

	/**
	 * Constructor
	 * @param name name of the element
	 * @param minOccurs minimal occurence
	 * @param maxOccurs maximal occurence
	 */
	public Element(String name, int minOccurs, int maxOccurs) {
		this.name = name;
		this.simpleType = null;
		this.complexType = null;
		this.minOccurs = minOccurs;
		this.maxOccurs = maxOccurs;
	}
	
	/**
	 * Returns the name of the element
	 * @return the name of the element
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns minimal occurence of the element
	 * @return minimal occurence of the element
	 */
	public int getMinOccurs() {
		return minOccurs;
	}

	/**
	 * Returns maximal occurence of the element
	 * @return maximal occurence of the element
	 */
	public int getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * Returns simple type contained in the element
	 * @return simple type contained in the element
	 */
	public SimpleType getSimpleType() {
		return simpleType;
	}

	/**
	 * Returns complex type contained in the element
	 * @return complex type contained in the element
	 */
	public ComplexType getComplexType() {
		return complexType;
	}

	/**
	 * Sets simple type contained in the element
	 * @param simpleType simple type to be contained in the element
	 */
	public void setSimpleType(SimpleType simpleType) {
		this.simpleType = simpleType;
	}

	/**
	 * Sets complex type contained in the element
	 * @param complexType complex type to be contained in the element
	 */
	public void setComplexType(ComplexType complexType) {
		this.complexType = complexType;
	}
}
