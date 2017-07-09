package de.sambalmueslie.herold;

/**
 * The change listener for the data model.
 * 
 * @param <T>
 *            the element type
 */
public interface DataModelChangeListener<T extends DataModelElement> {

	/**
	 * Handle the addition of a new element.
	 *
	 * @param element
	 *            the element
	 */
	void handleElementAdded(T element);

	/**
	 * Handle the change of a existing element.
	 *
	 * @param element
	 *            the element.
	 */
	void handleElementChanged(T element);

	/**
	 * Handle the removal of a element.
	 *
	 * @param element
	 *            the removed element
	 */
	void handleElementRemoved(T element);

}
