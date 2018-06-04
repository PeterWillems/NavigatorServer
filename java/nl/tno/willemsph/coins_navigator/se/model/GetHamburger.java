package nl.tno.willemsph.coins_navigator.se.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.EmbeddedServer;
import nl.tno.willemsph.coins_navigator.se.SeService;

public class GetHamburger extends GetSeObject {

	public GetHamburger() {
	}

	public GetHamburger(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super(seService, datasetId, uri);
	}

	public URI getFunctionalUnit() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("hamburger", getUri().toString());
		queryStr.append("SELECT ?functional_unit ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?hamburger se:functionalUnit ?functional_unit . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		URI functionalUnitUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode functionalUnitNode = node.get("functional_unit");
			String functionalUnit = functionalUnitNode != null ? functionalUnitNode.get("value").asText() : null;
			functionalUnitUri = functionalUnit != null ? new URI(functionalUnit) : null;
		}
		return functionalUnitUri;
	}

	public void deleteFunctionalUnit() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("hamburger", getUri().toString());
		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?hamburger se:functionalUnit ?functional_unit . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?hamburger se:functionalUnit ?functional_unit . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void insertFunctionalUnit(URI functionalUnit) throws IOException, URISyntaxException {
		if (functionalUnit != null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
			queryStr.setIri("graph", getDatasetUri());
			queryStr.setIri("hamburger", getUri().toString());
			queryStr.setIri("functional_unit", functionalUnit.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?hamburger se:functionalUnit ?functional_unit . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			getEmbeddedServer().update(queryStr);
		}
	}

	public void updateFunctionalUnit(URI functionalUnit) throws IOException, URISyntaxException {
		deleteFunctionalUnit();
		insertFunctionalUnit(functionalUnit);
	}

	public URI getTechnicalSolution() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("hamburger", getUri().toString());

		queryStr.append("SELECT ?technical_solution ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?hamburger se:technicalSolution ?technical_solution . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		URI technicalSolutionUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode technicalSolutionNode = node.get("technical_solution");
			String technicalSolution = technicalSolutionNode != null ? technicalSolutionNode.get("value").asText()
					: null;
			technicalSolutionUri = technicalSolution != null ? new URI(technicalSolution) : null;
		}
		return technicalSolutionUri;
	}

	public void deleteTechnicalSolution() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("hamburger", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?hamburger se:technicalSolution ?technical_solution . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?hamburger se:technicalSolution ?technical_solution  . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void insertTechnicalSolution(URI technicalSolution) throws IOException, URISyntaxException {
		if (technicalSolution != null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
			queryStr.setIri("graph", getDatasetUri());
			queryStr.setIri("hamburger", getUri().toString());
			queryStr.setIri("technical_solution", technicalSolution.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?hamburger se:technicalSolution ?technical_solution . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			getEmbeddedServer().update(queryStr);
		}
	}

	public void updateTechnicalSolution(URI technicalSolution) throws IOException, URISyntaxException {
		deleteTechnicalSolution();
		insertTechnicalSolution(technicalSolution);
	}

	public List<URI> getPortRealisations() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("hamburger", getUri().toString());
		queryStr.append("SELECT ?port_realisation ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?hamburger se:hasPortRealisation ?port_realisation . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		List<URI> portRealisationUris = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			JsonNode portRealisationNode = node.get("port_realisation");
			String portRealisation = portRealisationNode != null ? portRealisationNode.get("value").asText() : null;
			URI portRealisationUri = portRealisation != null ? new URI(portRealisation) : null;
			if (portRealisationUri != null)
				portRealisationUris.add(portRealisationUri);
		}
		return portRealisationUris;
	}

	public void deletePortRealisations() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("hamburger", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?hamburger se:hasPortRealisation ?port_realisation . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?hamburger se:hasPortRealisation ?port_realisation  . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void insertPortRealisations(List<URI> portRealisations) throws IOException, URISyntaxException {
		if (portRealisations != null) {
			for (URI portRealisation : portRealisations) {
				insertPortRealisation(portRealisation);
			}
		}
	}

	private void insertPortRealisation(URI portRealisation) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("hamburger", getUri().toString());
		queryStr.setIri("port_realisation", portRealisation.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasPortRealisation ?port_realisation . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		getEmbeddedServer().update(queryStr);
	}

	public void updatePortRealisations(List<URI> portRealisations) throws IOException, URISyntaxException {
		deletePortRealisations();
		insertPortRealisations(portRealisations);
	}

	public void update(PutHamburger putHamburger) throws IOException, URISyntaxException {
		super.update(putHamburger);
		updateFunctionalUnit(putHamburger.getFunctionalUnit());
		updateTechnicalSolution(putHamburger.getTechnicalSolution());
		updatePortRealisations(putHamburger.getPortRealisations());
	}

	public static GetHamburger create(SeService seService, int datasetId, String uri)
			throws URISyntaxException, IOException {
		GetHamburger hamburger = new GetHamburger(seService, datasetId, uri);
		hamburger.create();
		return hamburger;
	}
	
	@Override
	public String containsRelation() {
		return EmbeddedServer.SE + "ContainsHamburger";
	}
}
