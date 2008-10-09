package sk.gk2.excusa.grammar;

import java.util.*;
import sk.gk2.excusa.schema.*;
import sk.gk2.excusa.wsdl.*;

/**
 * Class for generating grammars from messages
 * @author stick
 */
public class GrammarCreator {
	
	private Definition definition;

	/**
	 * Constructor
	 * @param definition definition
	 */
	public GrammarCreator(Definition definition) {
		this.definition = definition;
	}
	
	/**
	 * Creates grammar for the message
	 * @param msg message to create message for
	 * @return generated grammar for the message
	 * @throws Exception
	 */
	public Grammar createForMessage(Message msg) throws Exception {
		NonTerminal.reset();
		NonTerminal mainNT = new NonTerminal();
		ArrayList<NonTerminal> mainright = new ArrayList<NonTerminal>();
		Grammar g = new Grammar(mainNT);
		if (msg.getType() == MessageType.Input || msg.getType() == MessageType.Output) {
			NonTerminal NT = new NonTerminal();
			g.addRule(new RewriteRule(mainNT, new StartTag("Envelope"), NT, new EndTag("Envelope")));
			mainNT = new NonTerminal();
			g.addRule(new RewriteRule(NT, new StartTag("Body"), mainNT, new EndTag("Body")));
		} else 
		if (msg.getType() == MessageType.Fault) {
			NonTerminal NT = new NonTerminal();
			g.addRule(new RewriteRule(mainNT, new StartTag("Fault"), NT, new EndTag("Fault")));
			mainNT = NT;
		}	
		
		for (MessagePart p: msg.getParts().values()) {
			// create two new non-terminals (left, right)
			NonTerminal lnt = new NonTerminal();
			mainright.add(lnt);
			if (p.getElement() != null) {	// part is element
				// add rewrite rule for element
				String name = p.getElement();
				if (definition.getElements().containsKey(name)) {
					createRulesForElement(g, lnt, definition.getElements().get(name));
				} else {
					throw new Exception("Unknown Element: " + name);
				}				
			} else
			if (p.getType() != null) {	// part is type
				// add new rewrite rule for part name: LNT -> <part> RNT </part>
				NonTerminal rnt = new NonTerminal();
				g.addRule(new RewriteRule(lnt, new StartTag(p.getName()), rnt, new EndTag(p.getName())));
				// add rewrite rule for type
				createRulesForType(g, rnt, p.getType());
			}
		}
		g.addRule(new RewriteRule(mainNT, mainright));
		return g;
	}

	/**
	 * Creates rewriting rules for type
	 * @param grammar grammar to add rewriting rules to
	 * @param left nonterminal to start from
	 * @param name name of the type
	 * @throws Exception
	 */
	private void createRulesForType(Grammar grammar, NonTerminal left, String name) throws Exception {
		// simpleType
		if (definition.getSimpleTypes().containsKey(name)) {
			createRulesForSimpleType(grammar, left, definition.getSimpleTypes().get(name));
		} else
		// complexType
		if (definition.getComplexTypes().containsKey(name)) {
			createRulesForComplexType(grammar, left, definition.getComplexTypes().get(name));
		} else {
			throw new Exception("Unknown Type: "  + name);
		}
	}
	
	/**
	 * Creates rewriting rules for simple type
	 * @param grammar grammar to add rewriting rules to
	 * @param left nonterminal to start from 
	 * @param st simple type to generate grammar for
	 */
	private void createRulesForSimpleType(Grammar grammar, NonTerminal left, SimpleType st) {
		Type t = Type.fromSimpleType(definition, st);
		Value v = new Value(t);
		grammar.addRule(new RewriteRule(left, v));
	}
	

	/**
	 * Returns next permutation of array
	 * @param <T> type of array
	 * @param array array to generate next permutation from
	 * @return next permutation of the array
	 */
	private <T extends Comparable<T>>boolean nextPermutation(T[] array) {
        int i, j;
        T tmp;
        for (i = array.length-1; --i >= 0;)
        	if (array[i].compareTo(array[i+1]) < 0)
                 break;
        if (i < 0) return false;
        for (j = array.length; --j > i;)
        	if (array[i].compareTo(array[j]) < 0)
                break;
        tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
        for (j = array.length; ++i < --j;) {
            tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
        return true;
	}

	/**
	 * Creates rewriting rules for complex type
	 * @param grammar grammar to add rewriting rules to
	 * @param left nonterminal to start from
	 * @param ct complex type to generate grammar for
	 */
	private void createRulesForComplexType(Grammar grammar, NonTerminal left, ComplexType ct) {
		ComplexTypeGroupType type = ct.getType();
		
		// change All to Sequence for element size > 5
		if (type == ComplexTypeGroupType.All && ct.getElements().size() > 5) {
			type = ComplexTypeGroupType.Sequence;
		}

		if (type == ComplexTypeGroupType.Sequence) {
			ArrayList<NonTerminal> list = new ArrayList<NonTerminal>(ct.getElements().size());
			for (Element e: ct.getElements()) {
				NonTerminal newleft = new NonTerminal();
				list.add(newleft);
				createRulesForElement(grammar, newleft, e);
			}
			grammar.addRule(new RewriteRule(left, list));
		} else
		if (type == ComplexTypeGroupType.Choice) {
			for (Element e: ct.getElements()) {
				createRulesForElement(grammar, left, e);
			}
		} else
		if (type == ComplexTypeGroupType.All) {
			ArrayList<NonTerminal> list = new ArrayList<NonTerminal>(ct.getElements().size());
			for (Element e: ct.getElements()) {
				NonTerminal newleft = new NonTerminal();
				list.add(newleft);				
				createRulesForElement(grammar, newleft, e);
			}
			// insert all permutations - nasty
			NonTerminal[] array = new NonTerminal[list.size()];
			list.toArray(array);
			do {
				grammar.addRule(new RewriteRule(left, array));
			} while (nextPermutation(array));
		}
	}
	
	/**
	 * Creates rewriting rules for element
	 * @param grammar grammar to add rewriting rules to
	 * @param left nonterminal to start from
	 * @param e element to generate grammar for
	 */
	private void createRulesForElement(Grammar grammar, NonTerminal left, Element e) {
		NonTerminal newleft;

		if (e.getMaxOccurs()!=-1)
		{
			// case (x,y)
			NonTerminal templeft = new NonTerminal();
			newleft = new NonTerminal();
			ArrayList<NonTerminal> list = new ArrayList<NonTerminal>(e.getMaxOccurs());
			for (int i = 0; i < e.getMaxOccurs(); i++) {
				list.add(templeft);
			}
			for (int i = e.getMaxOccurs(); i > e.getMinOccurs(); i--) {
				grammar.addRule(new RewriteRule(left, list));
				list.remove(list.size()-1);
			}
			grammar.addRule(new RewriteRule(left, list));
			grammar.addRule(new RewriteRule(templeft, new StartTag(e.getName()), newleft, new EndTag(e.getName())));
		} else
		{
			// case (x,unbounded)
			NonTerminal templeft = new NonTerminal();
			NonTerminal templeft2 = new NonTerminal();
			newleft = new NonTerminal();
			ArrayList<NonTerminal> list = new ArrayList<NonTerminal>();
			for (int i = 0; i < e.getMinOccurs(); i++) {
				list.add(templeft);
			}
			list.add(templeft2);
			grammar.addRule(new RewriteRule(left, list));
			grammar.addRule(new RewriteRule(templeft, new StartTag(e.getName()), newleft, new EndTag(e.getName())));
			grammar.addRule(new RewriteRule(templeft2, new NonTerminal[]{templeft, templeft2}));
			grammar.addRule(new RewriteRule(templeft2));			
		}
		if (e.getMaxOccurs() != 0) {
			if (e.getSimpleType() != null) {
				createRulesForSimpleType(grammar, newleft, e.getSimpleType());
			} else
			if (e.getComplexType() != null) {
				createRulesForComplexType(grammar, newleft, e.getComplexType());
			}
		}
	}

}
