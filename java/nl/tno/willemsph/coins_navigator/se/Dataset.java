package nl.tno.willemsph.coins_navigator.se;

import java.net.URI;
import java.net.URISyntaxException;

public class Dataset {
	private int id;
	private String filepath;
	private URI uri;
	private URI ontologyUri;

	public Dataset() {
	}

	public Dataset(int id, String filePath, String uri) throws URISyntaxException {
		this.id = id;
		this.filepath = filePath;
		this.uri = uri != null ? new URI(uri) : null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public URI getOntologyUri() {
		return ontologyUri;
	}

	public void setOntologyUri(URI ontologyUri) {
		this.ontologyUri = ontologyUri;
	}

}
