package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URISyntaxException;
import java.util.List;

public class Performance extends SeObject {

	public Performance() {
	}

	public Performance(String uri, String label, String assemblyUri, List<String> partUris) throws URISyntaxException {
		super(uri, label, assemblyUri, partUris);
	}

}
