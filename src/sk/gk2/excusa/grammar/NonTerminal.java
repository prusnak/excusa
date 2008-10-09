package sk.gk2.excusa.grammar;

/**
 * Nonterminal of XML grammar
 * @author stick
 */
public class NonTerminal extends Symbol implements Comparable<NonTerminal> {
	private static int gen = 0;
	private int id;
	private String idstr;
	
	/**
	 * Constructor
	 */
	public NonTerminal() {
		idstr = "";
		id = gen;
		int i = gen;
		while (i>=0) {
			idstr = (char)(65+i%26) + idstr;
			i /= 26;
			i--;
		}
		gen++;
	}

	/**
	 * Returns string representation of nonterminal (A, B, C, ...)
	 */
	@Override
	public String toString() {
		return idstr;
	}
	
	/**
	 * Resets sequence of nonterminals (next generated nonterminal will be A) 
	 */
	static public void reset() {
		gen = 0;
	}
	
	/**
	 * Compares this nonterminal with another one
	 */
	public int compareTo(NonTerminal nt) {
		return id - nt.getId();
	}

	/**
	 * Gets nonterminal ID
	 * @return nonterminal ID
	 */
	public int getId() {
		return id;
	}
}
