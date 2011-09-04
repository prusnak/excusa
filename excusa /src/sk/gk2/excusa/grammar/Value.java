package sk.gk2.excusa.grammar;

import sk.gk2.excusa.schema.*;

/**
 * Value of XML grammar
 * @author stick
 */
public class Value extends Terminal {
	private Type type;

	/**
	 * Constructor
	 * @param type type of the value
	 */
	public Value(Type type) {
		this.type = type;
	}

	/**
	 * Returns string representation
	 */
	@Override
	public String toString() {
		return type.isList() ? "[{" + type + "}]" : "{" + type + "}";
	}
	
	/**
	 * Returns type discriminant
	 * @return type discriminant
	 */
	public int getDiscriminant() {
		return type.isList() ? 1000 : 100  + type.ordinal();
	}
}
