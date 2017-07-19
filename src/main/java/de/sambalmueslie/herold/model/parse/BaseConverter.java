package de.sambalmueslie.herold.model.parse;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.annotations.Key;
import de.sambalmueslie.herold.annotations.Value;
import de.sambalmueslie.herold.util.ReflectionUtils;

public abstract class BaseConverter<T extends DataModelElement> implements ElementConverter<T> {

	private static Logger logger = LogManager.getLogger(BaseConverter.class);

	protected BaseConverter(Class<? extends T> elementType) {
		this.elementType = elementType;

		final Set<Field> elementFields = ReflectionUtils.getFields(elementType);

		key = elementFields.stream().filter(f -> f.isAnnotationPresent(Key.class)).findAny().get();
		key.setAccessible(true);
		fields = elementFields.stream().filter(f -> f.isAnnotationPresent(Value.class)).collect(Collectors.toMap(Field::getName, Function.identity()));
		fields.values().forEach(f -> f.setAccessible(true));
	}

	@Override
	public final Optional<T> convert(Element element) {
		if (element == null) return Optional.empty();

		try {
			final long id = element.getId();
			final T obj = elementType.newInstance();
			key.set(obj, id);

			return merge(element, obj);

		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Cannot create object for type " + elementType, e);
		}

		return Optional.empty();
	}

	@Override
	public final Optional<Element> convert(T obj) {
		if (obj == null) return Optional.empty();

		final long key = obj.getId();
		final Element element = new Element(key);

		for (final Field f : fields.values()) {
			try {
				final Object fieldVal = f.get(obj);
				final String value = objToString(fieldVal);
				element.set(f.getName(), value);

			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error("cannot convert " + obj, e);
			}
		}

		return Optional.of(element);
	}

	@Override
	public final Optional<T> merge(Element element, T obj) {
		if (obj.getId() != element.getId()) {
			logger.error("Cannot merge element with id {} and obj with id {}", element.getId(), obj.getId());
			return Optional.empty();
		}

		try {
			for (final Entry<String, String> val : element.getValues().entrySet()) {
				final String name = val.getKey();
				final Field f = fields.get(name);
				if (f == null) {
					logger.warn("Ignoring value {} for type {} cause no suitable field was found", name, elementType);
					continue;
				}

				final String value = val.getValue();
				final Object fieldVal = stringToObj(value, f.getType());
				f.set(obj, fieldVal);

			}
		} catch (final IllegalAccessException | IllegalArgumentException e) {
			logger.error("Cannot merge object for type " + elementType, e);
		}
		return Optional.of(obj);
	}

	/**
	 * Convert a {@link Object} to {@link String}.
	 *
	 * @param value
	 *            the {@link Object}
	 * @return the converted {@link String}
	 */
	protected abstract String objToString(Object value);

	/**
	 * Convert a {@link String} to {@link Object}.
	 *
	 * @param value
	 *            the {@link String}
	 * @return the converted {@link Object}.
	 */
	protected abstract Object stringToObj(String value, Class<?> type);

	private final Class<? extends T> elementType;
	private final Map<String, Field> fields;
	private final Field key;

}
