package cmiller.interview.common;

public interface Tool {

    /**
     * @return the tool's unique identifier code. Guaranteed to not be null or
     *         empty.
     */
    String getCode();

    /**
     * @return the tool's unique identifier code. Guaranteed to not be null.
     */
    Type getType();

    /**
     * @return the tool's brand. Guaranteed to not be null or empty.
     */
    String getBrand();

    public enum Type {
	CHAINSAW("Chainsaw"), LADDER("Ladder"), JACKHAMMER("Jackhammer"), OTHER("Other");

	private final String typeString;

	private Type(String string) {
	    this.typeString = string;
	}

	public String asString() {
	    return typeString;
	}

	public static Type fromString(String string) {
	    for (Type type : values()) {
		if (type.typeString.equalsIgnoreCase(string)) {
		    return type;
		}
	    }
	    return Type.OTHER;
	}
    }
}