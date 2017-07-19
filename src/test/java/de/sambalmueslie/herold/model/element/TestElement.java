package de.sambalmueslie.herold.model.element;

import de.sambalmueslie.herold.BaseDataModelElement;
import de.sambalmueslie.herold.annotations.Value;

public class TestElement extends BaseDataModelElement implements TestElementInterface {
	public TestElement() {
		// intentionally left empty
	}

	public TestElement(long id, String content) {
		setId(id);
		this.content = content;
	}

	@Override
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Value
	private String content;
}
