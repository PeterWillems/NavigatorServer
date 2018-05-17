package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class PortRealisation extends SeObject {
	private URI systemInterface;
	private URI realisationPort;

	public PortRealisation() {
	}

	public PortRealisation(String uri, String label, String assemblyUri, List<String> partUris) throws URISyntaxException {
		super(uri, label, assemblyUri, partUris);
	}

	public URI getSystemInterface() {
		return systemInterface;
	}

	public void setSystemInterface(URI systemInterface) {
		this.systemInterface = systemInterface;
	}

	public URI getRealisationPort() {
		return realisationPort;
	}

	public void setRealisationPort(URI realisationPort) {
		this.realisationPort = realisationPort;
	}

}
