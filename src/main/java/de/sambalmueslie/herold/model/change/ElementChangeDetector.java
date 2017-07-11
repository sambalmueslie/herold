package de.sambalmueslie.herold.model.change;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.sambalmueslie.herold.DataModelElement;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class ElementChangeDetector<T extends DataModelElement> {
	private static Logger logger = LogManager.getLogger(ElementChangeDetector.class);

	public ElementChangeDetector(Class<? extends T> implType) {
		this.implType = implType;
	}

	public Map<Field, Object> getChangedFields(T element) {
		final ChangeInterceptor<T> interceptor = inteceptors.get(element.getId());
		return interceptor == null ? new HashMap<>() : interceptor.getChanges();
	}

	@SuppressWarnings("unchecked")
	public Optional<T> setup(T element) {
		final long elementId = element.getId();
		if (inteceptors.containsKey(elementId)) return (Optional<T>) Optional.of(inteceptors.get(elementId));

		try {
			final ChangeInterceptor<T> interceptor = new ChangeInterceptor<T>(element, implType);
			inteceptors.put(elementId, interceptor);
			final T result = new ByteBuddy()
					.subclass(implType)
					.method(ElementMatchers.isSetter())
					.intercept(MethodDelegation.to(interceptor))
					.make()
					.load(ChangeInterceptor.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
					.getLoaded()
					.newInstance();

			return Optional.of(result);
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Cannot setup change detectinon on {}", implType, e);
			return Optional.empty();
		}
	}

	/** the implementation type. */
	private final Class<? extends T> implType;
	private final Map<Long, ChangeInterceptor<T>> inteceptors = new HashMap<>();

}
