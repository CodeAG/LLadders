package se.luppii.ladders.enums;

/**
 * Used to determine which side an LadderDispenser should output the ladders from
 * 
 * This enum is used to keep track of which side of the LadderDispenser ladders should be outputed. Ladders will still go up and down, but may start from top/bottom,
 * or one of the sides of the LadderDispenser. 
 * 
 * The possible sides are defined as: 
 * <ul>
 * <li>UPDOWN, ladders will be outputed from the top and bottom of the dispenser. (Represented as int 0)</li>
 * <li>LEFT, ladders will go up and down, but start left of the dispenser (Represented as int 1)</li>
 * <li>RIGHT, same as left but on the right side of the dispenser (Represented as int 2)</li>
 * </ul>
 * 
 * @author Aiquen
 *
 */
public enum OutputSide {
	UPDOWN("Top/Bottom"), LEFT("Left"), RIGHT("Right");
	
	/**
	 * String representation of the side
	 */
	private String outputSide;
	
	/**
	 * Parse a enum from integer. Integer should be an ordinal of the OutputSide enum
	 * @param side integer representation of enum. Expected value is one of OutputSide ordinals.
	 * @return OutputSide enum object
	 */
	public static OutputSide fromInt(int side) {
		if (side >= 0 && side < OutputSide.values().length) {
			return OutputSide.values()[side];
		} else {
			return null;
		}
	}
	
	/**
	 * Default constructor. Sets the string representation of the side. Used for the GUI.
	 * @param side
	 */
	OutputSide(String side) {
		this.outputSide = side;
	}
	
	/**
	 * Used to iterate to the next possible output side. Will loop when it reaches the end of possible enums.
	 * @return The next possible enum of the sides
	 */
	public OutputSide next() {
		return this.ordinal() < OutputSide.values().length - 1 
				? OutputSide.values()[this.ordinal() + 1] 
				: OutputSide.UPDOWN;
	}
	
	/**
	 * Get the string representation of the enum
	 * 
	 * @return the string representation of the enum
	 */
	public String toString() {
		return this.outputSide;
	}
	
	/**
	 * Returns the ordinal as an int representation of enum. Used for control.
	 * @return Integer representation of enum in form of ordinal.
	 */
	public int toInt() {
		return this.ordinal();
	}
	
}
