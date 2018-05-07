package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SeObject {
	private URI uri;
	private String label;
	private URI assembly;
	private List<URI> parts;

	public SeObject() {
	}

	public SeObject(String uri, String label, String assemblyUri, List<String> partUris) throws URISyntaxException {
		this.uri = new URI(uri);
		this.label = label;
		this.assembly = assemblyUri != null ? new URI(assemblyUri) : null;
		if (partUris != null) {
			parts = new ArrayList<>();
			for (String partUri : partUris) {
				parts.add(new URI(partUri));
			}
		}
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

}
