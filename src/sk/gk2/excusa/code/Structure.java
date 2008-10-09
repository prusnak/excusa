package sk.gk2.excusa.code;

import java.util.*;
import sk.gk2.excusa.schema.*;

/**
 * Class for representing structures 
 * @author stick
 */
public class Structure {
	private String name;
	private ArrayList<StructureField> fields;
	private boolean isMessage;

	/**
	 * Constructor
	 * 
	 * @param name structure name
	 */
	public Structure(String name) {
		this(name, false);
	}
	
	/**
	 * Constructor
	 * 
	 * @param name structure name
	 * @param isMessage true if structure represents message, false otherwise
	 */
	public Structure(String name, boolean isMessage) {
		this.name = name;
		this.fields = new ArrayList<StructureField>();
		this.isMessage = isMessage;
	}
	
	/**
	 * Adds new field
	 * 
	 * @param field structure field
	 */
	public void addField(StructureField field) {
		fields.add(field);
	}

	/**
	 * Gets name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Prints structure
	 */
	@Override
	public String toString() {
		String r = "struct";
		r += " " + name + "\n{\n";
		for (StructureField sf: fields) {
			r += "\t" + sf + "\n";
			if (sf.isList() || (sf.getType()!=null && sf.getType().isList())) {
				r += "\tint " + sf.getName() + "_count;\n";
			}
		}
		r += "};\n\n";
		return r;
	}

	/**
	 * Gets prototype for parse function
	 * 
	 * @return C prototype
	 */
	public String getParseFuncPrototype() {
		return isMessage ? "struct " + name + " *parse" + name + "(char *buf, int len);\n" : "";
	}
	
	/**
	 * Gets prototype for free function
	 * 
	 * @return C prototype
	 */
	public String getFreeFuncPrototype() {
		return "void free" + name + "(struct " + name + " *ptr);\n";
	}

	/**
	 * Gets prototype for construct function
	 * 
	 * @return C prototype
	 */
	public String getConstructFuncPrototype() {
		return "int construct" + name + "(char *buf, int maxlen, struct " + name + " *ptr);\n";
	}
	
	/**
	 * Gets parse function
	 * 
	 * @return C definition
	 */
	public String getParseFunc() {
		String r = "struct " + name + " *parse" + name + "(" + (isMessage ? "char *buf, int len" : "int start") + ") {\n";
		String start;
		start = isMessage ? "0" : "start";
		r += "\tint idx;\n";
		r += "\tstruct " + name + " *ret;\n";
		r += "\tint i, cnt;\n\n";
		if (isMessage) {
			r += "\tParser_Buf = buf;\n";
			r += "\tParser_BufLen = len;\n";
			r += "\tParser_Grammar = " + name + "_grammar;\n";
			r += "\tParser_StackCount = 0;\n\n";
			r += "\tif (eatRule(0, 0, 0) != len) return NULL;\n\n";
		}
		r += "\tret = alloc" + name + "();\n";
		
		for (StructureField sf: fields) {
			if (sf.getStructure() != null) {
				if (sf.isList()) {
					r +="\tcnt = countTags(" + start + ", tag_" + sf.getName() + ");\n";
					r +="\tret->" + sf.getName() + "_count = cnt;\n";
					r +="\tif (cnt > 0) {\n";
					r +="\t\tret->" + sf.getName() + " = alloc" + sf.getStructure() + "List(cnt);\n";
					r +="\t\tidx = " + start + ";\n";
					r +="\t\tfor (i=0;i<cnt;i++) {\n";
					r +="\t\t\tidx = findNextTagSame(idx, tag_" + sf.getStructure() + ");\n";
					r +="\t\t\tret->" + sf.getName() + "[i] = parse" + sf.getStructure() + "(idx);\n";
					r +="\t\t}\n";
					r +="\t} else {\n";
					r +="\t\tret->" + sf.getName() + " = NULL;\n";
					r +="\t}\n";
				} else {
					if (isMessage) {
						r += "\tidx = findTagSame(0, tag_Envelope);\n";
						r += "\tif (idx >= 0) idx = findNextTag(idx, tag_Body);\n";
						r += "\tif (idx >= 0) idx = findNextTag(idx, tag_" + sf.getStructure() + ");\n";
					} else {
						r += "\tidx = findNextTag(" + start + ", tag_" + sf.getName() + ");\n";	
					}					
					r += "\tif (idx >= 0) {\n";
					r += "\t\tret->" + sf.getName() + " = parse" + sf.getStructure() + "(idx);\n";
					r += "\t} else {\n";
					r += "\t\tret->" + sf.getName() + " = NULL;\n";
					r += "\t}\n";
				}
			}
			if (sf.getType() != null) {
				if (sf.isList()) {
					r += "\tidx = findNextTag(" + start + ", tag_" + sf.getName() + ");\n";
					r += "\tif (idx <= 0) {\n";
					r += "\t\tret->" + sf.getName() + " = NULL;\n";
					r += "\t\tret->" + sf.getName() + "_count = 0;\n";
					r += "\t}\n";
					r += "\ti = 1;\n";
					r += "\twhile (Parser_Grammar[Parser_Stack[idx+i*2].rule][0] == 1 && Parser_Grammar[Parser_Stack[idx+i*2].rule][1] == tag_" + sf.getName() + ") i++;\n";
					r += "\tret->" + sf.getName() + "_count = i-1;\n";
					r += "\tret->" + sf.getName() + " = (" + sf.getType() + "List)malloc(i*sizeof(" + sf.getType() + "));\n";
					r += "\tfor (i=0; i<ret->" + sf.getName() + "_count; i++) {\n";
					r += "\t\tret->" + sf.getName() + "[i] = parse" + sf.getType() + "( getValue(idx+i*2+1) );\n";
					r += "\t}\n";
				} else 
				if (sf.getType().isList()) {
					r += "\tidx = findNextTag(" + start + ", tag_" + sf.getName() + ");\n";
					r += "\tret->" + sf.getName() + " = parse" + sf.getType() + "List( (idx>=0) ? getValue(idx+1) : NULL, ret->" + sf.getName() + "_count);\n";
				} else {
					r += "\tidx = findNextTag(" + start + ", tag_" + sf.getName() + ");\n";
					r += "\tret->" + sf.getName() + " = parse" + sf.getType() + "( (idx>=0) ? getValue(idx+1) : NULL);\n";
				}
			}
		}
		r += "\treturn ret;\n}\n\n";
		return r;
	}

	/**
	 * Gets free function
	 * 
	 * @return C definition
	 */
	public String getFreeFunc() {
		String r = "";
		r += "void free" + name + "(struct " + name + " *ptr)\n{\n";
		r += "\tint i;\n";
		r += "\tif (!ptr) return;\n";
		for (StructureField sf: fields) {
			// free pointers
			if (sf.getStructure() != null) {
				if (sf.isList()) {
					r += "\tfor (i=0; i<(ptr->" + sf.getName()+ "_count); i++) {\n";
					r += "\t\tfree" + sf.getStructure() + "(ptr->" + sf.getName() + "[i]);\n";
					r += "\t}\n";
					r += "\tfree(ptr->" + sf.getName() + ");\n";
				} else {
					r += "\tfree" + sf.getStructure() + "(ptr->" + sf.getName() + ");\n";
				}
			} else
			if (sf.getType() == Type.String || sf.getType() == Type.Base64Binary || sf.getType() == Type.HexBinary) {
				if (sf.isList() || sf.getType().isList()) {
					r += "\tfor (i=0; i<(ptr->" + sf.getName()+ "_count); i++) {\n";
					r += "\t\tfree(ptr->" + sf.getName() + "[i]);\n";
					r += "\t}\n";
				}	
				r += "\tfree(ptr->" + sf.getName() + ");\n";
			} else
			if (sf.getType() != null && sf.isList()) {
				r += "\tfree(ptr->" + sf.getName() + ");\n";
			}
		}
		r += "\tfree(ptr);\n";
		r += "}\n\n";
		return r;
	}
	
	/**
	 * Gets construct function
	 * 
	 * @return C definition
	 */
	public String getConstructFunc() {
		String r = "";
		r += "int construct" + name + "(char *buf, int maxlen, struct " + name + " *ptr)\n{\n";
		r += "\tint i, pos = 0;\n";
		if (!isMessage) {
			r += "\tpos += snprintf(buf, maxlen, \"<%s>\", \"" + name + "\");\n";
		} else {
			r += "\tif (!ptr) return 0;\n";
		}
		for (StructureField sf: fields) {
			if (sf.getName() == null) {
				continue;
			}
			if (sf.getStructure() != null) {
				if (sf.isList()) {
					r += "\tfor (i=0; i<(ptr->" + sf.getName() + "_count); i++) { /* " + sf.getComment() + " */\n";
					r += "\t\tpos += construct" + sf.getStructure() + "(buf, maxlen-pos, ptr->" + sf.getName() + "[i]);\n";
					r += "\t}\n";
				} else
				if (sf.isOptional()) {
					r += "\tif (ptr->" + sf.getName() + ") { /* " + sf.getComment() + " */\n";
					r += "\t\tpos += construct" + sf.getStructure() + "(buf, maxlen-pos, ptr->" + sf.getName() + ");\n";
					r += "\t}\n";
				} else {
					r += "\tpos += construct" + sf.getStructure() + "(buf, maxlen-pos, ptr->" + sf.getName() + ");\n";
				}
			}
			if (sf.getType() != null) {
				String func = "construct" + sf.getType();
				if ((sf.isList() || sf.getType().isList())) {
					r += "\tfor (i=0; i<(ptr->" + sf.getName() + "_count); i++) { /* " + sf.getComment() + " */\n";
					r += "\t\tpos += snprintf(buf+pos, maxlen-pos, \"<%s>\", \"" + sf.getName() + "\");\n";
					r += "\t\tpos += " + func + "(buf+pos, maxlen-pos, ptr->" + sf.getName()+ "[i]);\n";
					r += "\t\tpos += snprintf(buf+pos, maxlen-pos, \"</%s>\", \"" + sf.getName() + "\");\n";
					r += "\t}\n";
				} else
				if (sf.isOptional()) {
					r += "\tif (ptr->" + sf.getName() + ") { /* " + sf.getComment() + " */\n";
					r += "\t\tpos += snprintf(buf+pos, maxlen-pos, \"<%s>\", \"" + sf.getName() + "\");\n";
					r += "\t\tpos += " + func + "(buf+pos, maxlen-pos, ptr->" + sf.getName()+ ");\n";
					r += "\t\tpos += snprintf(buf+pos, maxlen-pos, \"</%s>\", \"" + sf.getName() + "\");\n";
					r += "\t}\n";
				} else {
					r += "\tpos += snprintf(buf+pos, maxlen-pos, \"<%s>\", \"" + sf.getName() + "\");\n";
					r += "\tpos += " + func + "(buf+pos, maxlen-pos, ptr->" + sf.getName()+ ");\n";
					r += "\tpos += snprintf(buf+pos, maxlen-pos, \"</%s>\", \"" + sf.getName() + "\");\n";
				}
			}
		}
		if (!isMessage) {
			r += "\tpos += snprintf(buf+pos, maxlen-pos, \"</%s>\", \"" + name + "\");\n";
		}
		r += "\treturn pos;\n";
		r += "}\n\n";
		return r;
	}

	/**
	 * Does structure represent message?
	 * @return true if message represents a structure, false otherwise
	 */
	public boolean isMessage() {
		return isMessage;
	}	
}
