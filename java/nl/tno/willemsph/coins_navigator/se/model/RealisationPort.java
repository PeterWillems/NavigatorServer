package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class RealisationPort extends SeObject {
	private URI owner;

	public RealisationPort() {
	}

	public RealisationPort(String uri, String label, String assemblyUri, List<String> partUris)
			throws URISyntaxException {
		super(uri, label, assemblyUri, partUris);
	}

	public URI getOwner() {
		return owner;
	}

	public void setOwner(URI owner) {
		this.owner = owner;
	}

}
