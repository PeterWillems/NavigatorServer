package nl.tno.willemsph.coins_navigator.se;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.rdf.model.Model;
import org.springframework.core.io.ClassPathResource;

import nl.tno.willemsph.coins_navigator.EmbeddedServer;

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

	public void save() throws FileNotFoundException, IOException {
		ClassPathResource resource = new ClassPathResource(getFilepath());
		FileOutputStream out = new FileOutputStream(resource.getFile());
		Model namedModel = EmbeddedServer.ds.getNamedModel(getUri().toString());
		namedModel.write(out, "TURTLE", getOntologyUri().toString());
		out.close();
	}

	public File getModel(String filePath) throws FileNotFoundException, IOException {
//		ClassPathResource resource = new ClassPathResource(getFilepath());
//		File output = File.createTempFile(resource.getFilename(),"");
		File output = new File(filePath);
		FileOutputStream out = new FileOutputStream(output);
		Model namedModel = EmbeddedServer.ds.getNamedModel(getUri().toString());
		namedModel.write(out, "TURTLE", getOntologyUri().toString());
		out.close();
		return output;
	}

}
