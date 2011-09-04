package sk.gk2.excusa.grammar;

/**
 * Closing XML tag
 * @author stick
 */
public class EndTag extends Tag {
	
	/**
	 * Constructor
	 * @param namespace namespace
	 * @param name name
	 */
	public EndTag(String namespace, String name) {
		super(namespace, name);
	}

	/**
	 * Constructor
	 * @param name name
	 */
	public EndTag(String name) {
		super(name);
	}
	
	/**
	 * Prints tag's string representation
	 */
	@Override
	public String toString() {
		return "</" + super.getQName() + ">";
	}
}
