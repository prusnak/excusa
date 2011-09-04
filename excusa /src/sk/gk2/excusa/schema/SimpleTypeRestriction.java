package sk.gk2.excusa.schema;

import java.util.*;

/**
 * Schema simple type - restriction 
 * @author stick
 */
public class SimpleTypeRestriction extends SimpleType {

	private String baseType;
	private Integer minExclusive;
	private Integer minInclusive;
	private Integer maxExclusive;
	private Integer maxInclusive;
	private Integer totalDigits;
	private Integer fractionDigits;
	private Integer length;
	private Integer minLength;
	private Integer maxLength;
	private ArrayList<String> enumerations;
	private ArrayList<String> patterns;

	/**
	 * Constructor
	 * @param name simple type name
	 * @param baseType base type
	 */
	public SimpleTypeRestriction(String name, String baseType) {
		super(name);
		this.baseType = baseType;
		this.minExclusive = null;
		this.minInclusive = null;
		this.maxExclusive = null;
		this.maxInclusive = null;
		this.totalDigits = null;
		this.fractionDigits = null;
		this.length = null;
		this.minLength = null;
		this.maxLength = null;
		this.enumerations = new ArrayList<String>();
		this.patterns = new ArrayList<String>();
	}

	/**
	 * Returns base type
	 * @return base type
	 */
	public String getBaseType() {
		return baseType;
	}	
	
	/**
	 * Returns min exclusive attribute
	 * @return min exclusive attribute
	 */
	public int getMinExclusive() {
		return minExclusive;
	}

	/**
	 * Sets min exclusive attribute
	 * @param minExclusive min exclusive attribute
	 */
	public void setMinExclusive(int minExclusive) {
		this.minExclusive = minExclusive;
	}

	/**
	 * Returns min inclusive attribute
	 * @return min inclusive attribute
	 */
	public int getMinInclusive() {
		return minInclusive;
	}

	/**
	 * Sets min inclusive attribute
	 * @param minInclusive min inclusive attribute
	 */
	public void setMinInclusive(int minInclusive) {
		this.minInclusive = minInclusive;
	}

	/**
	 * Returns max exclusive attribute
	 * @return max exclusive attribute
	 */
	public int getMaxExclusive() {
		return maxExclusive;
	}

	/**
	 * Sets max exclusive attribute
	 * @param maxExclusive max exclusive attribute
	 */
	public void setMaxExclusive(int maxExclusive) {
		this.maxExclusive = maxExclusive;
	}

	/**
	 * Returns max inclusive attribute
	 * @return max inclusive attribute
	 */
	public int getMaxInclusive() {
		return maxInclusive;
	}

	/**
	 * Sets max inclusive attribute
	 * @param maxInclusive max inclusive attribute
	 */
	public void setMaxInclusive(int maxInclusive) {
		this.maxInclusive = maxInclusive;
	}

	/**
	 * Returns total digits attribute
	 * @return total digits attribute
	 */
	public int getTotalDigits() {
		return totalDigits;
	}

	/**
	 * Sets total digits attribute
	 * @param totalDigits total digits attribute
	 */
	public void setTotalDigits(int totalDigits) {
		this.totalDigits = totalDigits;
	}

	/**
	 * Returns fraction digits attribute
	 * @return fraction digits attribute
	 */
	public int getFractionDigits() {
		return fractionDigits;
	}

	/**
	 * Sets fraction digits attribute 
	 * @param fractionDigits fraction digits attribute
	 */
	public void setFractionDigits(int fractionDigits) {
		this.fractionDigits = fractionDigits;
	}

	/**
	 * Returns length attribute
	 * @return length attribute
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Sets length attribute
	 * @param length length attribute
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Returns minimum length attribute
	 * @return minimum length attribute
	 */
	public int getMinLength() {
		return minLength;
	}

	/**
	 * Sets minimum length attribute
	 * @param minLength minimum length attribute
	 */
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	/**
	 * Returns maximum length attribute
	 * @return maximum length attribute
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * Sets maximum length attribute
	 * @param maxLength maximum length attribute
	 */	
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * Returns enumerations of the restriction
	 * @return enumerations of the restriction
	 */
	public List<String> getEnumerations() {
		return enumerations;
	}

	/**
	 * Adds new enumeration item to the restriction
	 * @param enumeration new enumeration item
	 */
	public void addEnumeration(String enumeration) {
		enumerations.add(enumeration);
	}

	/**
	 * Returns patterns of the restriction
	 * @return patterns of the restriction
	 */
	public List<String> getPatterns() {
		return patterns;
	}

	/**
	 * Adds new pattern to the restriction
	 * @param pattern new pattern to the restriction
	 */
	public void addPattern(String pattern) {
		patterns.add(pattern);
	}
}
