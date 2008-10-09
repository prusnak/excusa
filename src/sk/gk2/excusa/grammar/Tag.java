package sk.gk2.excusa.grammar;

/**
 * Tag of XML grammar
 * @author stick
 */
abstract public class Tag extends Terminal {
	protected String name;
	protected String namespace;
	
	/**
	 * Constructor
	 * @param namespace tag namespace
	 * @param name tag name
	 */
	public Tag(String namespace, String name) {
		this.namespace = namespace;
		this.name = name;
	}
	
	/**
	 * Constructor
	 * @param name tag name
	 */
	public Tag(String name) {
		this(null, name);
	}
	
	/**
	 * Returns tag name
	 * @return tag name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns tag namespace
	 * @return tag namespace
	 */
	public String getNamespace() {
		return namespace;
	}
	
	/**
	 * Returns qualified name (namespace:name)
	 * @return qualified name (namespace:name)
	 */
	public String getQName() {
		return (namespace != null && !namespace.isEmpty()) ? namespace + ":" + name : name;
	}
}
