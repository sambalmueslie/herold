package de.sambalmueslie.herold.model;

import java.util.Optional;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.annotations.ChangeListener;
import de.sambalmueslie.herold.annotations.ImplementationType;
import de.sambalmueslie.herold.util.AnnotationSpy;

public class Metadata<T extends DataModelElement> {

	Metadata(Class<T> elementType) {
		this.elementType = elementType;
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

	@SuppressWarnings("unchecked")
	private <S extends DataModelChangeListener<? extends T>> Class<S> getListenerType() {
		final Optional<ChangeListener> result = AnnotationSpy.findAnnotation(elementType, ChangeListener.class);
		return result.isPresent() ? (Class<S>) result.get().value() : (Class<S>) DataModelChangeListener.class;
	}

	private final Class<T> elementType;
	private Class<? extends T> implType;
	private Class<? extends DataModelChangeListener<? extends T>> specificListenerType;
}
