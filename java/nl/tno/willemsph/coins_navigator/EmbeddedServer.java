package nl.tno.willemsph.coins_navigator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmbeddedServer {
	public static final String QUERY_URL = "http://localhost:3330/rdf/query";
	public static final String UPDATE_URL = "http://localhost:3330/rdf/update";
	public static final String SE_EXAMPLE_URI = "http://www.coinsweb.nl/coins2/reffrm/se/";
	private static final String COINS2 = "http://www.coinsweb.nl/cbim-2.0.rdf#";
	private static final String CBIM2_RESOURCE = "cbim-2.0.rdf";
	public static final String SE = "http://www.coinsweb.nl/coins2/reffrm/se#";
	private static final String SE_RESOURCE = "se.ttl";
	private static final String[] EXAMPLES = { "parkbank", "parkbank2", "roadjunction", "wissel" };
	private static Dataset ds;
	private static FusekiServer fuseki;
	private PrefixMapping prefixMapping;

	public EmbeddedServer() throws IOException, URISyntaxException {
		startServer();
	}

	public void startServer() throws IOException, URISyntaxException {
		Resource cbim2 = new ClassPathResource(CBIM2_RESOURCE);
		Resource se = new ClassPathResource(SE_RESOURCE);

		Model defaultModel = ModelFactory.createDefaultModel();
		defaultModel.read(cbim2.getInputStream(), null);
		defaultModel.read(se.getInputStream(), null, "TURTLE");
		ds = DatasetFactory.create(defaultModel);

		for (nl.tno.willemsph.coins_navigator.se.Dataset dataset : getDatasets()) {
			Model namedModel = ModelFactory.createDefaultModel();
			Resource model = new ClassPathResource(dataset.getFilepath());
			namedModel.read(model.getInputStream(), null, "TURTLE");
			ds.addNamedModel(dataset.getUri().toString(), namedModel);
		}

		fuseki = FusekiServer.create().add("/rdf", ds).build();
		fuseki.start();
	}

	public PrefixMapping getPrefixMapping() {
		if (prefixMapping == null) {
			prefixMapping = PrefixMapping.Factory.create();
			prefixMapping.setNsPrefix("rdf", RDF.uri);
			prefixMapping.setNsPrefix("rdfs", RDFS.uri);
			prefixMapping.setNsPrefix("owl", OWL.NS);
			prefixMapping.setNsPrefix("xml", "http://www.w3.org/XML/1998/namespace/");
			prefixMapping.setNsPrefix("coins2", COINS2);
			prefixMapping.setNsPrefix("se", SE);
		}
		return prefixMapping;
	}

	public JsonNode query(ParameterizedSparqlString queryStr) throws IOException {
		String query = queryStr.toString();
		HttpURLConnection con = getQueryConnection();
		JsonNode bindings = sendQuery(con, query);
		return bindings;
	}

	public void update(ParameterizedSparqlString queryStr) throws IOException {
		String query = queryStr.toString();
		HttpURLConnection con = getUpdateConnection();
		sendUpdate(con, query);
	}

	private HttpURLConnection getQueryConnection() throws IOException {
		URL obj = new URL(QUERY_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/sparql-query");
		return con;
	}

	private HttpURLConnection getUpdateConnection() throws IOException {
		URL obj = new URL(UPDATE_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/sparql-update");
		return con;
	}

	private JsonNode sendQuery(HttpURLConnection con, String query) throws IOException {
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(query);
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + EmbeddedServer.class);
		System.out.println("Post parameters : " + query);
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jTree = mapper.readTree(in);
		JsonNode bindings = jTree.findValue("bindings");
		return bindings;
	}

	private void sendUpdate(HttpURLConnection con, String query) throws IOException {
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(query);
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + EmbeddedServer.class);
		System.out.println("Post parameters : " + query);
		System.out.println("Response Code : " + responseCode);
	}

	private List<nl.tno.willemsph.coins_navigator.se.Dataset> datasets;

	public List<nl.tno.willemsph.coins_navigator.se.Dataset> getDatasets() throws URISyntaxException {
		if (datasets == null) {
			datasets = new ArrayList<>();
			int id = 0;
			for (String example : EXAMPLES) {
				datasets.add(new nl.tno.willemsph.coins_navigator.se.Dataset(id++, "examples/" + example + ".ttl",
						SE_EXAMPLE_URI + example));
			}
		}
		return datasets;
	}

	public void saveDataset(int datasetId) throws URISyntaxException {
		DatasetAccessor datasetAccessor = DatasetAccessorFactory.create(ds);
		String graphUri = getDatasets().get(datasetId).getUri().toString();
		Model model = datasetAccessor.getModel(graphUri);
		RDFWriter writer = model.getWriter("TURTLE");
		writer.write(model, System.out, null);
	}

}
