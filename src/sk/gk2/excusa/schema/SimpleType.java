package sk.gk2.excusa.schema;

/**
 * Schema simple type
 * @author stick
 */
abstract public class SimpleType {
	private String name;
	
	/**
	 * Constructor
	 * @param name simple type name
	 */
	public SimpleType(String name) {
		this.name = name;
	}

	/**
	 * Returns name of the simple type
	 * @return name of the simple type
	 */
	public String getName() {
		return name;
	}
}
