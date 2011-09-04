package sk.gk2.excusa.schema;

import java.util.*;

/**
 * Schema simple type - union
 * @author stick
 */
public class SimpleTypeUnion extends SimpleType {

	private ArrayList<String> baseTypes; 

	/**
	 * Constructor
	 * @param name simple type name
	 * @param baseTypes base types
	 */
	public SimpleTypeUnion(String name, List<String> baseTypes) {
		super(name);
		this.baseTypes = new ArrayList<String>(baseTypes);
	}

	/**
	 * Returns base types
	 * @return base types
	 */
	public List<String> getBaseTypes() {
		return baseTypes;
	}
}
