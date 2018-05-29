package nl.tno.willemsph.coins_navigator.se.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.se.SeService;

public class GetRealisationModule extends GetSeObject {

	public GetRealisationModule() {
	}

	public GetRealisationModule(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super(seService, datasetId, uri);
	}

	public List<URI> getPerformances() throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("realisation_module", getUri().toString());
		queryStr.append("SELECT ?performance ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?realisation_module se:hasPerformance ?performance . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		List<URI> performanceUris = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			JsonNode performanceNode = node.get("performance");
			String performanceUri = performanceNode != null ? performanceNode.get("value").asText() : null;
			if (performanceUri != null) {
				performanceUris.add(new URI(performanceUri));
			}
		}
		return performanceUris;
	}

	public void updatePerformances(List<URI> performances) throws URISyntaxException, IOException {
		deletePerformances();
		insertPerformances(performances);
	}

	public void insertPerformances(List<URI> performances) throws IOException, URISyntaxException {
		if (performances != null) {
			for (URI performance : performances) {
				insertPerformance(performance);
			}
		}
	}

	public void insertPerformance(URI performanceUri) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("realisation_module", getUri().toString());
		queryStr.setIri("performance", performanceUri.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?realisation_module se:hasPerformance ?performance . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		getEmbeddedServer().update(queryStr);
	}

	public void deletePerformances() throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("realisation_module", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?realisation_module se:hasPerformance ?performance . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?realisation_module se:hasPerformance ?performance . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public List<URI> getPorts() throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("realisation_module", getUri().toString());
		queryStr.append("SELECT ?port ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?realisation_module se:hasPort ?port . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		List<URI> realisationPortUris = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			JsonNode realisationPortNode = node.get("port");
			String realisationPortUri = realisationPortNode != null ? realisationPortNode.get("value").asText() : null;
			if (realisationPortUri != null) {
				realisationPortUris.add(new URI(realisationPortUri));
			}
		}
		return realisationPortUris;
	}

	public void deletePorts() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("realisation_module", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?realisation_module se:hasPort ?realisation_port . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?realisation_module se:hasPort ?realisation_port . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void updatePorts(List<URI> ports) throws IOException, URISyntaxException {
		deletePorts();
		insertPorts(ports);
	}

	public void insertPorts(List<URI> ports) throws IOException, URISyntaxException {
		if (ports != null) {
			for (URI port : ports) {
				insertPort(port);
			}
		}
	}

	public void insertPort(URI port) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("realisation_module", getUri().toString());
		queryStr.setIri("port", port.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?realisation_module se:hasPort ?port . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		getEmbeddedServer().update(queryStr);
	}

	public void update(PutRealisationModule putRealisationModule) throws IOException, URISyntaxException {
		super.update(putRealisationModule);
		updatePerformances(putRealisationModule.getPerformances());
		updatePorts(putRealisationModule.getPorts());
	}

	public static GetRealisationModule create(SeService seService, int datasetId, String uri)
			throws URISyntaxException, IOException {
		GetRealisationModule realisationModule = new GetRealisationModule(seService, datasetId, uri);
		realisationModule.create();
		return realisationModule;
	}

}
