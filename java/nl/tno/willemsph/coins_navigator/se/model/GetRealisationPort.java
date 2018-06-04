package nl.tno.willemsph.coins_navigator.se.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.EmbeddedServer;
import nl.tno.willemsph.coins_navigator.se.SeService;

public class GetRealisationPort extends GetSeObject {

	public GetRealisationPort() {
	}

	public GetRealisationPort(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super(seService, datasetId, uri);
	}

	public URI getOwner() throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("port", getUri().toString());
		queryStr.append("SELECT ?owner ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?owner se:hasPort ?port . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		URI ownerUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode ownerNode = node.get("owner");
			ownerUri = ownerNode != null ? new URI(ownerNode.get("value").asText()) : null;
		}
		return ownerUri;
	}

	public void updateOwner(URI owner) throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr1 = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr1.setIri("graph", getDatasetUri());
		queryStr1.setIri("port", getUri().toString());
		queryStr1.append("  DELETE { ");
		queryStr1.append("    GRAPH ?graph { ");
		queryStr1.append("      ?realisation_module se:port ?port . ");
		queryStr1.append("    } ");
		queryStr1.append("  } ");
		queryStr1.append("  WHERE { ");
		queryStr1.append("    GRAPH ?graph { ");
		queryStr1.append("      { ");
		queryStr1.append("        ?realisation_module se:port ?port . ");
		queryStr1.append("      } ");
		queryStr1.append("    }");
		queryStr1.append("  }");

		getEmbeddedServer().update(queryStr1);

		if (owner != null) {
			ParameterizedSparqlString queryStr2 = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
			queryStr2.setIri("graph", getDatasetUri());
			queryStr2.setIri("port", getUri().toString());
			queryStr2.setIri("realisation_module", owner.toString());

			queryStr2.append("  INSERT { ");
			queryStr2.append("    GRAPH ?graph { ");
			queryStr2.append("      ?realisation_module se:port ?port . ");
			queryStr2.append("    } ");
			queryStr2.append("  }");
			queryStr2.append("WHERE { } ");

			getEmbeddedServer().update(queryStr2);
		}
	}
	
	public void updatePerformances(List<URI> performances) throws URISyntaxException, IOException {
		deletePerformances();
		insertPerformances(performances);
	}
	
	public void deletePerformances() throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("realisation_port", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?realisation_port se:hasPerformance ?performance . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?realisation_port se:hasPerformance ?performance . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
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
		queryStr.setIri("realisation_port", getUri().toString());
		queryStr.setIri("performance", performanceUri.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?realisation_port se:hasPerformance ?performance . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		getEmbeddedServer().update(queryStr);
	}
	
	public void update(PutRealisationPort putRealisationPort) throws IOException, URISyntaxException {
		super.update(putRealisationPort);
		updateOwner(putRealisationPort.getOwner());
		updatePerformances(putRealisationPort.getPerformances());
	}

	public static GetRealisationPort create(SeService seService, int datasetId, String uri)
			throws URISyntaxException, IOException {
		GetRealisationPort realisationPort = new GetRealisationPort(seService, datasetId, uri);
		realisationPort.create();
		return realisationPort;
	}
	
	@Override
	public String containsRelation() {
		return EmbeddedServer.SE + "ContainsRealisationPort";
	}
}
