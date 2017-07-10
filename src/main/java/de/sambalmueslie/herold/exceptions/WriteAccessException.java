package de.sambalmueslie.herold.exceptions;

/**
 * Exception occur on invalid write access.
 */
public class WriteAccessException extends BaseAccessException {
	/** serial id. */
	private static final long serialVersionUID = -3753853158544762190L;

	public WriteAccessException(String operatorId, Class<?> elementType, String message) {
		super(operatorId, elementType, message);
	}
}
