package sk.gk2.excusa.schema;

/**
 * Schema simple type - basic
 * @author stick
 */
public class SimpleTypeBasic extends SimpleType {

	private Type type;
	
	/**
	 * Constructor
	 * @param name simple type name
	 */
	public SimpleTypeBasic(String name) {
		super(name);
		this.type = Type.fromString(name);
	}

	/**
	 * Returns basic type
	 * @return basic type
	 */
	public Type getType() {
		return type;
	}
}
