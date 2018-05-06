package nl.tno.willemsph.coins_navigator.se;

import java.net.URISyntaxException;

public class NetworkConnection extends SeObject {

	public NetworkConnection() {
	}

	public NetworkConnection(String uri, String label, String assembly) throws URISyntaxException {
		super(uri, label, assembly);
	}

}
