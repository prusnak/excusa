package sk.gk2.excusa.code;

import java.util.*;
import sk.gk2.excusa.grammar.*;

/**
 * Class for generating grammar structs
 * @author stick
 */
public class GrammarStructsGenerator {
	
	private ArrayList<String> tags;
	
	/**
	 * Constructor
	 */
	public GrammarStructsGenerator() {
		tags = new ArrayList<String>();
	}
	
	/**
	 * Gets tag ID (or add new entry if the tag is new)
	 * 
	 * @param tag tag name
	 * @return tag ID
	 */
	private int getTagId(String tag) {
		int i = tags.indexOf(tag);
		if (i == -1) {
			tags.add(tag);
			return tags.size()-1;
		} else {
			return i;
		}
	}
	
	/**
	 * Converts grammar into C representation
	 * 
	 * @param name grammar name
	 * @param grammar grammar
	 * @return {header code, source code}
	 */
	public String[] convert(String name, Grammar grammar) {
		String header = "/*\n", source = "";
		RewriteRule[] r = grammar.getRules().toArray(new RewriteRule[grammar.getRules().size()]);
		int[] cnt = new int[r.length], idx = new int[r.length];
		for (int i = 0; i<r.length; i++) {
			cnt[i] = 0; idx[i] = 0;
		}
		for (int i = 0; i<r.length; i++) {
			cnt[r[i].getLeft().getId()]++;
		}
		for (int i = 0; i<r.length; i++) {
			for (int j = 0; j<i; j++)
				idx[i] += cnt[j];
		}
		for (int i=0; i<r.length; i++) {
			header += " " + r[i].toString() + " \n";
			Symbol[]rs = r[i].getRight();
			switch (r[i].getType()) {
				case NonTerminals:
					// A -> B C D
					source += "GUNIT " + name+i + "[] = {0, ";
					if (rs!=null) {
						for (NonTerminal nt: (NonTerminal[])rs) {
							int id = nt.getId();
							if (cnt[id] > 1) {
								source += idx[id] + "+(" + (cnt[id]-1) + "<<GSHIFT), ";
							} else {
								source += idx[id] + ", ";
							}
						}
					}
					source += "-1};\n";
					break;
				case Tag:
					// A -> <tag> B </tag>
					String n = ((StartTag)rs[0]).getQName();
					getTagId(n);
					source += "GUNIT " + name+i + "[] = {1 , tag_" + n + ", ";
					int id = ((NonTerminal)rs[1]).getId();
					if (cnt[id] > 1) {
						source += idx[id] + "+(" + (cnt[id]-1) + "<<GSHIFT)";
					} else {
						source += idx[id];
					}
					source += "};\n";
					break;
				case Value:
					// A -> {value}
					int d = ((Value)rs[0]).getDiscriminant();
					source += "GUNIT " + name+i + "[] = {" + d + "};\n";
					break;
			}
		}
		String line = "";
		header += "*/\nextern GUNIT *" + name + "_grammar[];\n\n";
		line += "GUNIT *" + name + "_grammar[] = {";
		for (int i = 0; i<r.length-1; i++) {
			line += name + i + ", ";
		}
		line += name + (r.length-1) + "};\n\n";
		source += line;
		return new String[]{header, source};
	}
	
	/**
	 * Gets list of tags (to be used in C representation of grammar)
	 * 
	 * @return list of tags (C {declaration, definition}
	 */
	public String[] getTags() {
		String h = "char *grammar_tag[]";
		String s = h + " = {\n";
		h = "extern " + h + ";\n\nenum {\n";
		for (int i = 0; i<tags.size(); i++) {
			String tag = tags.get(i);
			s += "\"" + tag + "\"";
			h += "tag_" + tag + " = " + i;
			if (i<tags.size()-1) {
				s += ",\n";
				h += ",\n";
			} else {
				s += "\n";
				h += "\n";
			}
		}
		s += "};\n";
		h += "};\n";
		return new String[]{h, s};
	}
}
