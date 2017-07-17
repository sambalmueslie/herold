package de.sambalmueslie.herold.model.parse;

import java.util.Optional;

import de.sambalmueslie.herold.DataModelElement;

/**
 * Convert a element to a object and the other way.
 *
 * @param <T>
 *            the object type
 */
public interface ElementConverter<T extends DataModelElement> {

	/**
	 * Convert a {@link Element} to a object.
	 *
	 * @param element
	 *            the element
	 * @return the {@link Optional} of the result
	 */
	Optional<T> convert(Element element);

	/**
	 * Convert object to a {@link Element} .
	 *
	 * @param obj
	 *            the object
	 * @return the {@link Optional} of the result
	 */
	Optional<Element> convert(T obj);

	/**
	 * Merge a {@link Element} into a object.
	 *
	 * @param element
	 *            the element
	 * @param obj
	 *            the object
	 * @return the {@link Optional} of the result
	 */
	Optional<T> merge(Element element, T obj);

}
