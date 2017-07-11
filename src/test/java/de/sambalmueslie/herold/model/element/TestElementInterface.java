package de.sambalmueslie.herold.model.element;

import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.annotations.ImplementationType;

@ImplementationType(TestElement.class)
public interface TestElementInterface extends DataModelElement {
	String getContent();
}
