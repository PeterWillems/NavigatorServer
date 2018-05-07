package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URISyntaxException;
import java.util.List;

public class NetworkConnection extends SeObject {

	public NetworkConnection() {
	}

	public NetworkConnection(String uri, String label, String assemblyUri, List<String> partUris)
			throws URISyntaxException {
		super(uri, label, assemblyUri, partUris);
	}

}
