package de.sambalmueslie.herold.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.sambalmueslie.herold.DataModelChangeListener;

/**
 * Its possible to add a specific change listener for each field. Therefore you have to consider the method signature.
 * <code>handle[FieldName]Change(ElementReference, FormerValue) </code>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ChangeListener {
	Class<? extends DataModelChangeListener<?>> value();
}
