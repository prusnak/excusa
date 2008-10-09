package sk.gk2.excusa.grammar;

import java.util.*;

/**
 * Rewrite rule of XML grammar
 * @author stick
 */
public class RewriteRule implements Comparable<RewriteRule> {
	private NonTerminal left;
	private Symbol[] right;
	private RewriteRuleType type;
	
	/**
	 * Constructor for rule A ->
	 * @param left nonterminal on the left side of the rule
	 */ 
	public RewriteRule(NonTerminal left) {
		this.left = left;
		this.right = null;
		this.type = RewriteRuleType.NonTerminals;
	}
	
	/**
	 * Constructor for rule A -> B C D ...
	 * @param left nonterminal on the left side of the rule
	 * @param right nonterminals on the right side of the rule
	 */
	public RewriteRule(NonTerminal left, NonTerminal right[]) {
		this.left = left;
		this.right = right.clone();
		this.type = RewriteRuleType.NonTerminals;
	}
	
	/**
	 * Constructor for rule A -> B C D ...
	 * @param left nonterminal on the left side of the rule
	 * @param right nonterminals on the right side of the rule
	 */
	public RewriteRule(NonTerminal left, List<NonTerminal> right) {
		this.left = left;
		this.right = right.toArray(new NonTerminal[right.size()]);
		this.type = RewriteRuleType.NonTerminals;
	}

	/**
	 * Constructor for rule A -> <tag> B </tag> 
	 * @param left nonterminal on the left side of the rule
	 * @param st opening tag
	 * @param right nonterminal between tags
	 * @param et closing tag
	 */
	public RewriteRule(NonTerminal left, StartTag st, NonTerminal right, EndTag et) {
		this.left = left;
		this.right = new Symbol[]{st, right, et};
		this.type = RewriteRuleType.Tag;
	}

	/**
	 * Constructor for rule A -> value
	 * @param left nonterminal on the left side of the rule
	 * @param right value on the right side of the rule
	 */
	public RewriteRule(NonTerminal left, Value right) {
		this.left = left;
		this.right = new Symbol[]{right};
		this.type = RewriteRuleType.Value;
	}

	/**
	 * Returns nonterminal on the left side of the rule
	 * @return nonterminal on the left side of the rule
	 */
	public NonTerminal getLeft() {
		return left;
	}

	/**
	 * Returns symbols on the right side of the rule
	 * @return symbols on the right side of the rule
	 */
	public Symbol[] getRight() {
		return right;
	}

	/**
	 * Returns rewrite rule type
	 * @return rewrite rule type
	 */
	public RewriteRuleType getType() {
		return type;
	}
	
	/**
	 * Returns string representation of this rewriting rule
	 */
	@Override
	public String toString() {
		String ret = left.toString() + " ->";
		if (right != null) {
			for (Symbol s: right) {
				ret = ret + " " + s;
			}
		}
		return ret;
	}
	
	/**
	 * Compare this rewriting rule to another one
	 */
	public int compareTo(RewriteRule r) {
		return this.left.compareTo( r.left );
	}
}
