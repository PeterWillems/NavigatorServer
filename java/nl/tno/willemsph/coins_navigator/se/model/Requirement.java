package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URISyntaxException;
import java.util.List;

public class Requirement extends SeObject {

	public Requirement() {
	}

	public Requirement(String uri, String label, String assemblyUri, List<String> partUris) throws URISyntaxException {
		super(uri, label, assemblyUri, partUris);
	}

}
