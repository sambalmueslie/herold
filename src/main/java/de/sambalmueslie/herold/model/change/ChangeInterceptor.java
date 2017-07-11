package de.sambalmueslie.herold.model.change;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

public class ChangeInterceptor<T> {
	private static Logger logger = LogManager.getLogger(ChangeInterceptor.class);

	ChangeInterceptor(T element, Class<? extends T> implType) {
		this.element = element;
		this.implType = implType;
	}

	public Map<Field, Object> getChanges() {
		return changes;
	}

	@RuntimeType
	public Object interceptGetter(@Origin Method method) {
		try {
			return method.invoke(element);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

	public void interceptSetter(Object property, @Origin Method method) {
		final String methodName = method.getName();
		final String fieldName = extract(methodName);
		try {
			final Field field = implType.getDeclaredField(fieldName);
			field.setAccessible(true);
			final Object former = field.get(element);
			changes.put(field, former);
			field.set(element, property);

			logger.info("Set value on field {} from '{}' to '{}'", fieldName, former, property);

		} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
			logger.error("Cannot find setter field {} on element {}", method.getName(), this, e);
		}
	}

	private String extract(String methodName) {
		final String rawName = methodName.startsWith("is") ? methodName.substring(2) : methodName.substring(3);
		return Character.toLowerCase(rawName.charAt(0)) + rawName.substring(1);
	}

	private final Map<Field, Object> changes = new HashMap<>();
	private final T element;
	private final Class<? extends T> implType;

}
