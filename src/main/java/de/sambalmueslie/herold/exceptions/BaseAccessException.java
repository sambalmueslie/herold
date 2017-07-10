package de.sambalmueslie.herold.exceptions;

/**
 * The base access exception.
 */
public abstract class BaseAccessException extends RuntimeException {

	/** the serial id. */
	private static final long serialVersionUID = 6894730212051163332L;

	public BaseAccessException(String operatorId, Class<?> elementType, String message) {
		super(message);
		this.operatorId = operatorId;
		this.elementType = elementType;
	}

	public Class<?> getElementType() {
		return elementType;
	}

	public String getOperatorId() {
		return operatorId;
	}

	private final Class<?> elementType;
	private final String operatorId;

}
