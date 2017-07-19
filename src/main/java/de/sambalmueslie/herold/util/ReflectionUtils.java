package de.sambalmueslie.herold.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ReflectionUtils {
	public static Stream<Field> getAllFields(Class<?> type) {
		final Set<Field> fields = getFields(type);
		return fields.stream();
	}

	public static Set<Field> getFields(Class<?> type) {
		final Set<Field> fields = new HashSet<>();
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		final Class<?> superclass = type.getSuperclass();
		if (superclass != null) {
			fields.addAll(getFields(superclass));
		}

		return fields;

	}
}
