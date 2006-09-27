package org.riotfamily.common.markup.markdown;

/**
 * @link http://en.wikipedia.org/wiki/Quotation_mark
 */
public class QuoteStyle {

	public static final String LEFT_DOUBLE = "&ldquo;"; // 66
	public static final String RIGHT_DOUBLE = "&rdquo;"; // 99
	public static final String BOTTOM_DOUBLE = "&bdquo;"; // 99 (bottom);
		
	public static final String LEFT_SINGLE = "&lsquo;"; // 6
	public static final String RIGHT_SINGLE = "&rsquo;"; // 9
	public static final String BOTTOM_SINGLE = "&sbquo;"; // 9 (bottom)
	
	public static final String LEFT_DOUBLE_ANGLE = "&laquo;"; // <<
	public static final String RIGHT_DOUBLE_ANGLE = "&raquo;"; // >>
	
	public static final String LEFT_SINGLE_ANGLE = "&lsaquo;"; // <
	public static final String RIGHT_SINGLE_ANGLE = "&rsaquo;"; // >
	
	
	public static final QuoteStyle EN_US = new QuoteStyle(
			LEFT_DOUBLE, RIGHT_DOUBLE,
			LEFT_SINGLE, RIGHT_SINGLE);
	
	public static final QuoteStyle EN_UK = new QuoteStyle(
			LEFT_SINGLE, RIGHT_SINGLE,
			LEFT_DOUBLE, RIGHT_DOUBLE);
	
	public static final QuoteStyle FR = new QuoteStyle(
			LEFT_DOUBLE_ANGLE, RIGHT_DOUBLE_ANGLE,
			LEFT_SINGLE_ANGLE, RIGHT_SINGLE_ANGLE);
	
	public static final QuoteStyle DE = new QuoteStyle(
			BOTTOM_DOUBLE, LEFT_DOUBLE,
			BOTTOM_SINGLE, LEFT_SINGLE);
	
	public static final QuoteStyle DE_CH = FR;
	
	
	private String left;
	
	private String right;

	private String secondaryLeft;
	
	private String secondaryRight;
	
	

	public QuoteStyle(String left, String right, 
			String secoundaryLeft, String secondaryRight) {
		
		this.left = left;
		this.right = right;
		this.secondaryLeft = secoundaryLeft;
		this.secondaryRight = secondaryRight;
	}

	public String getLeft() {
		return this.left;
	}

	public String getRight() {
		return this.right;
	}

	public String getSecondaryRight() {
		return this.secondaryRight;
	}

	public String getSecondaryLeft() {
		return this.secondaryLeft;
	}
	
}
