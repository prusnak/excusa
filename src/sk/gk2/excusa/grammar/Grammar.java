package sk.gk2.excusa.grammar;

import java.util.*;

/**
 * XML grammar
 * @author stick
 */
public class Grammar {
	private NonTerminal root;
	private Vector<RewriteRule> rules;
	
	/**
	 * Constructor
	 * @param root the start symbol of this grammar
	 */
	public Grammar(NonTerminal root) {
		this.root = root;
		rules = new Vector<RewriteRule>();
	}
	
	/**
	 * Returns the start symbol of this grammar
	 * @return the start symbol of this grammar
	 */
	public NonTerminal getRoot() {
		return root;
	}
	
	/**
	 * Returns the list of rewriting rules
	 * @return the list of rewriting rules
	 */
	public List<RewriteRule> getRules() {
		Collections.sort(rules);
		return rules;
	}
	
	/**
	 * Add new rewriting rule
	 * @param rule the new rewriting rule
	 */
	public void addRule(RewriteRule rule) {
		rules.add(rule);
	}

	/**
	 * Returns string representation of this grammar (rewriting rules separated by newline)
	 */
	@Override
	public String toString() {
		String ret = "";
		for (RewriteRule r: getRules()) {
			ret += r + "\n";
		}
		return ret;
	}
}
