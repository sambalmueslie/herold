package de.sambalmueslie.herold.model.element;

import de.sambalmueslie.herold.BaseDataModelElement;
import de.sambalmueslie.herold.annotations.AllowedReader;
import de.sambalmueslie.herold.annotations.AllowedWriter;

@AllowedReader("reader")
@AllowedWriter("writer")
public class RestrictedTestElement extends BaseDataModelElement {

	public RestrictedTestElement() {
		// intentionally left empty
	}

	public RestrictedTestElement(long id, String content) {
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
