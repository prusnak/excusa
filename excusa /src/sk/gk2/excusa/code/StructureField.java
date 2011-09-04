package sk.gk2.excusa.code;

import sk.gk2.excusa.schema.*;

/**
 * Field of the structure
 * @author stick
 */
public class StructureField {
	private String structure = null;
	private Type type = null;
	private boolean isList = false;
	private boolean isOptional = false;
	private String name = null;
	private String comment = null;
	
	/**
	 * Constructor
	 * 
	 * @param comment comment
	 */
	public StructureField(String comment) {
		this.comment = comment;
	}
	
	/**
	 * Constructor
	 * 
	 * @param structure structure type to point at
	 * @param name field name
	 * @param comment comment 
	 * @param isList is field list?
	 * @param isOptional is field optional?
	 */
	public StructureField(String structure, String name, String comment, boolean isList, boolean isOptional) {
		this.structure = structure;
		this.name = name;
		this.comment = comment;
		this.isList = isList;
		this.isOptional = isOptional;
	}
	
	/**
	 * Constructor
	 * 
	 * @param type field type
	 * @param name field name
	 */
	public StructureField(Type type, String name) {
		this(type, name, null, false, false);
	}

	/**
	 * Constructor
	 * 
	 * @param type field type
	 * @param name field name
	 * @param comment comment 
	 * @param isList is field list?
	 * @param isOptional is field optional?
	 */
	public StructureField(Type type, String name, String comment, boolean isList, boolean isOptional) {
		this.type = type;
		this.name = name;
		this.comment = comment;
		this.isList = isList;
		this.isOptional = isOptional;
	}

	/**
	 * Prints structure field
	 */
	@Override
	public String toString() {
		String r = "";
		if (name != null)
		{
			if (structure != null) {
				r += "struct " + structure +" *";
			}
			if (type != null) {
				r += type.toString() + " ";
			}
			if (isList) {
				r += "*";
			}
			r += name + ";";
		}
		if (name != null && comment != null) {
			r += " ";
		}		
		if (comment != null) {
			r += "/* " + comment + " */";
		}
		return r;
	}

	/**
	 * Gets structure
	 * @return structure type
	 */
	public String getStructure() {
		return structure;
	}

	/**
	 * Gets type
	 * 
	 * @return field type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Is structure field a list of values?
	 * @return true if the field is a list of values, false otherwise
	 */
	public boolean isList() {
		return isList;
	}

	/**
	 * Is structure field optional?
	 * @return true if the field is optional, false otherwise
	 */
	public boolean isOptional() {
		return isOptional;
	}

	/**
	 * Gets name of the field
	 * 
	 * @return field name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets comment
	 * 
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}
}
