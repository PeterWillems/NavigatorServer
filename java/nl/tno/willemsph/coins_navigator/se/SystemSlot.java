package nl.tno.willemsph.coins_navigator.se;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class SystemSlot extends SeObject {
	private List<URI> functions;

	public SystemSlot() {
	}

	public SystemSlot(String uri, String label, String assembly) throws URISyntaxException {
		super(uri, label, assembly);
	}

	public List<URI> getFunctions() {
		return functions;
	}

	public void setFunctions(List<URI> functions) {
		this.functions = functions;
	}

}