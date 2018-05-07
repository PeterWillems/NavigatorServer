package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class RealisationModule extends SeObject {
	List<URI> perfomances;
	List<URI> ports;

	public RealisationModule() {
	}

	public RealisationModule(String uri, String label, String assemblyUri, List<String> partUris)
			throws URISyntaxException {
		super(uri, label, assemblyUri, partUris);
	}

	public List<URI> getPerfomances() {
		return perfomances;
	}

	public void setPerfomances(List<URI> perfomances) {
		this.perfomances = perfomances;
	}

	public List<URI> getPorts() {
		return ports;
	}

	public void setPorts(List<URI> ports) {
		this.ports = ports;
	}

}
