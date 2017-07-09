package de.sambalmueslie.herold;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface DataModel<T extends DataModelElement> {

	/**
	 * Add a element to the model.
	 *
	 * @param element
	 *            the element
	 */
	void add(T element);

	/**
	 * Check if the model contains a element by id.
	 *
	 * @param elementId
	 *            the element id
	 * @return <code>true</code> if so, otherwise <code>false</code>.
	 */
	boolean contains(long elementId);

	/**
	 * Get a element by id.
	 *
	 * @param elementId
	 *            the id
	 * @return the {@link Optional}
	 */
	Optional<T> get(long elementId);

	/**
	 * @return a {@link List} of all elements.
	 */
	List<T> getAll();

	/**
	 * Register a {@link DataModelChangeListener}.
	 *
	 * @param listener
	 *            the listener
	 */
	void register(DataModelChangeListener<T> listener);

	/**
	 * Remove a element by id.
	 *
	 * @param elementId
	 *            the id
	 */
	void remove(long elementId);

	/**
	 * Remove a element by reference.
	 *
	 * @param element
	 *            the element
	 */
	void remove(T element);

	/**
	 * Remove all elements.
	 */
	void removeAll();

	/**
	 * @return create a {@link Stream} of all elements.
	 */
	Stream<T> stream();

	/**
	 * Unregister a {@link DataModelChangeListener}.
	 *
	 * @param listener
	 *            the listener
	 */
	void unregister(DataModelChangeListener<T> listener);

	/**
	 * Update the element.
	 * 
	 * @param element
	 *            the element
	 */
	void update(T element);

}
