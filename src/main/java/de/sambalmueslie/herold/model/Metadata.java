package de.sambalmueslie.herold.model;

import java.util.Optional;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.annotations.ChangeListener;
import de.sambalmueslie.herold.annotations.ImplementationType;
import de.sambalmueslie.herold.util.AnnotationSpy;

public class Metadata<T extends DataModelElement> {

	@SuppressWarnings("unchecked")
	Metadata(Class<T> elementType) {
		this.elementType = elementType;

		genericListenerType = (Class<DataModelChangeListener<T>>) (Object) DataModelChangeListener.class;
	}

	public Class<? extends T> getElementImplType() {
		if (implType == null) {
			implType = getImplementationType();
		}
		return implType;
	}

	public Class<T> getElementType() {
		return elementType;
	}

	public Class<? extends DataModelChangeListener<? extends T>> getSpecificListenerType() {
		if (specificListenerType == null) {
			specificListenerType = getListenerType();
		}
		return specificListenerType;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends T> getImplementationType() {
		final Optional<ImplementationType> result = AnnotationSpy.findAnnotation(elementType, ImplementationType.class);
		return result.isPresent() ? (Class<T>) result.get().value() : elementType;
	}

	@SuppressWarnings({ "unchecked" })
	private Class<? extends DataModelChangeListener<? extends T>> getListenerType() {
		final Optional<ChangeListener> result = AnnotationSpy.findAnnotation(elementType, ChangeListener.class);

		return result.isPresent() ? (Class<DataModelChangeListener<T>>) result.get().value() : genericListenerType;
	}

	private final Class<T> elementType;
	private final Class<DataModelChangeListener<T>> genericListenerType;
	private Class<? extends T> implType;
	private Class<? extends DataModelChangeListener<? extends T>> specificListenerType;
}
