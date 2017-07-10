package de.sambalmueslie.herold.model.element;

import de.sambalmueslie.herold.BaseDataModelElement;

public class TestElement extends BaseDataModelElement {
	public TestElement(long id, String content) {
		setId(id);
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private String content;
}
