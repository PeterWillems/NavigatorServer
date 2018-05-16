package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class SystemInterface extends SeObject {
	
	private URI systemSlot0;
	private URI systemSlot1;
	private List<URI> requirements;

	public SystemInterface() {
	}

	public SystemInterface(String uri, String label, String assemblyUri, List<String> partUris)
			throws URISyntaxException {
		super(uri, label, assemblyUri, partUris);
	}

	public URI getSystemSlot0() {
		return systemSlot0;
	}

	public void setSystemSlot0(URI systemSlot0) {
		this.systemSlot0 = systemSlot0;
	}

	public URI getSystemSlot1() {
		return systemSlot1;
	}

	public void setSystemSlot1(URI systemSlot1) {
		this.systemSlot1 = systemSlot1;
	}

	public List<URI> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<URI> requirements) {
		this.requirements = requirements;
	}

}
