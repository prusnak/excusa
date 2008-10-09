package sk.gk2.excusa.grammar;

/**
 * Opening XML tag
 * @author stick
 *
 */
public class StartTag extends Tag {
	
	/**
	 * Constructor
	 * @param namespace namespace
	 * @param name name
	 */
	public StartTag(String namespace, String name) {
		super(namespace, name);
	}

	/**
	 * Constructor
	 * @param name name
	 */
	public StartTag(String name) {
		super(name);
	}
	
	/**
	 * Prints tag's string representation
	 */
	@Override
	public String toString() {
		return "<" + super.getQName() + ">";
	}
}
