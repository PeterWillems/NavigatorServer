package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.util.List;

public class PutSeObject {
	private URI uri;
	private String label;
	private URI assembly;
	private List<URI> parts;
	private String localName;

	public PutSeObject() {
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public URI getAssembly() {
		return assembly;
	}

	public void setAssembly(URI assembly) {
		this.assembly = assembly;
	}

	public List<URI> getParts() {
		return parts;
	}

	public void setParts(List<URI> parts) {
		this.parts = parts;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

}
