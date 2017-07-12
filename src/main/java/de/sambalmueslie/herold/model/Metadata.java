package de.sambalmueslie.herold.model;

import java.util.Optional;

import de.sambalmueslie.herold.DataModelElement;
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

	@SuppressWarnings("unchecked")
	private Class<? extends T> getImplementationType() {
		final Optional<ImplementationType> result = AnnotationSpy.findAnnotation(elementType, ImplementationType.class);
		return result.isPresent() ? (Class<T>) result.get().value() : elementType;
	}

	private final Class<T> elementType;
	private Class<? extends T> implType;
}
