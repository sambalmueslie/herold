package de.sambalmueslie.herold.util;

import java.lang.annotation.Annotation;
import java.util.Optional;

public class AnnotationSpy {

	public static <A extends Annotation> Optional<A> findAnnotation(Class<?> type, Class<A> annotation) {
		if (type == null || annotation == null) return Optional.empty();

		final A result = type.getAnnotation(annotation);
		if (result != null) return Optional.of(result);

		final Optional<A> sResult = findAnnotation(type.getSuperclass(), annotation);
		if (sResult.isPresent()) return sResult;

		for (final Class<?> i : type.getInterfaces()) {
			final Optional<A> ifResult = findAnnotation(i, annotation);
			if (ifResult.isPresent()) return ifResult;
		}

		return Optional.empty();
	}

	/**
	 * Constructor.
	 */
	private AnnotationSpy() {
		// intentionally left empty
	}

}
