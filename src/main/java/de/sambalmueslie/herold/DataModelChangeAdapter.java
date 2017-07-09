package de.sambalmueslie.herold;

/**
 * Simple adapter for the change listener.
 * 
 * @param <T>
 *            the element type
 */
public class DataModelChangeAdapter<T extends DataModelElement> implements DataModelChangeListener<T> {

	@Override
	public void handleElementAdded(T element) {
		// intentionally left empty
	}

	@Override
	public void handleElementChanged(T element) {
		// intentionally left empty
	}

	@Override
	public void handleElementRemoved(T element) {
		// intentionally left empty
	}

}
