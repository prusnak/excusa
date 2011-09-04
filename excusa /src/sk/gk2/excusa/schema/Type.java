package sk.gk2.excusa.schema;

import sk.gk2.excusa.wsdl.*;

/**
 * Enumerates schema types
 * @author stick
 */
public enum Type {
	String,
	Boolean,
	Base64Binary,
	HexBinary,
	Float,
	Double,
	Integer,
	Long,
	Int,
	Short,
	Byte,
	UnsignedLong,
	UnsignedInt,
	UnsignedShort,
	UnsignedByte;
	
	private boolean isList = false;
	
	/**
	 * Creates type from string
	 * @param str string to create type from
	 * @return type created from string
	 */
	static public Type fromString(String str) {
		return fromString(str, false);
	}
	
	/**
	 * Creates type from string
	 * @param str string to create type from
	 * @param isList is desired type a list? 
	 * @return type create from string
	 */
	static public Type fromString(String str, boolean isList) {
		for(Type v: Type.values()) {
			if (str.equalsIgnoreCase(v.toString())) {
				v.isList = isList;
				return v;
			}
		}
		return null;
	}
	
	/**
	 * Determine type from simple type
	 * @param definition definition to use types from
	 * @param st simple type
	 * @return basic type
	 */
	static public Type fromSimpleType(Definition definition, SimpleType st) {
		if (st instanceof SimpleTypeBasic) {
			SimpleTypeBasic stb = (SimpleTypeBasic)st;
			return stb.getType();
		}
		if (st instanceof SimpleTypeList) {
			SimpleTypeList stl = (SimpleTypeList)st;
			SimpleTypeBasic stb = (SimpleTypeBasic)(definition.getSimpleType(stl.getItemType()));
			Type t = stb.getType();
			t.isList = true;
			return t;
		}
		if (st instanceof SimpleTypeRestriction) {
			SimpleTypeRestriction str = (SimpleTypeRestriction)st;
			SimpleTypeBasic stb = (SimpleTypeBasic)(definition.getSimpleType(str.getBaseType()));
			return stb.getType();
		}
		return null;
	}

	/**
	 * Is this type a list?
	 * @return true if type is a list, false otherwise
	 */
	public boolean isList() {
		return isList;
	}
	
	/**
	 * Returns string representation of this type
	 */
	@Override
	public String toString() {
		return isList ? super.toString()+"List" : super.toString(); 
	}
	
}
