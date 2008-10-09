package sk.gk2.excusa.schema;

/**
 * Schema simple type - list
 * @author stick
 */
public class SimpleTypeList extends SimpleType {

	private String itemType;
	
	/**
	 * Constructor
	 * @param name simple type name
	 * @param itemType type of the list items
	 */
	public SimpleTypeList(String name, String itemType) {
		super(name);
		this.itemType = itemType;
	}

	/**
	 * Returns type of the list items
	 * @return type of the list items
	 */
	public String getItemType() {
		return itemType;
	}	
}
