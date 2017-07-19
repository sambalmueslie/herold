package de.sambalmueslie.herold.exceptions;

/**
 * Exception occur on invalid read access.
 */
public class ReadAccessException extends BaseAccessException {
	/** serial id. */
	private static final long serialVersionUID = -3753853158544762190L;

	public ReadAccessException(String operatorId, Class<?> elementType, String message) {
		super(operatorId, elementType, message);
	}
}
