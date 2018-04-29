package nl.tno.willemsph.coins_navigator.se;

import java.net.URI;
import java.net.URISyntaxException;

public class SeObject {
	private URI uri;
	private String label;
	private URI assembly;

	public SeObject() {
	}

	public SeObject(String uri, String label, String assembly) throws URISyntaxException {
		this.uri = new URI(uri);
		this.label = label;
		this.assembly = assembly != null ? new URI(assembly) : null;
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

}
