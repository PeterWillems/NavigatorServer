package nl.tno.willemsph.coins_navigator.se;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.jena.query.ParameterizedSparqlString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.EmbeddedServer;

@Service
public class SeService {
	@Autowired
	private EmbeddedServer _embeddedServer;

	public List<SystemSlot> getAllSystemSlots(int datasetId) throws IOException, URISyntaxException {
		List<Dataset> datasets = _embeddedServer.getDatasets();
		String datasetUri = datasets.get(datasetId).getUri().toString();
		List<SystemSlot> systemSlots = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.append("SELECT ?system_slot ?label ?assembly ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?system_slot rdf:type se:SystemSlot . ");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?system_slot rdfs:label ?label . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?system_slot . ");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?system_slot . ");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("      ?system_slot coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("      ?system_slot coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			String systemSlotUri = node.get("system_slot").get("value").asText();
			JsonNode labelNode = node.get("label");
			String label = labelNode != null ? labelNode.get("value").asText() : null;
			JsonNode assemblyNode = node.get("assembly");
			String assembly = assemblyNode != null ? node.get("assembly").get("value").asText() : null;
			SystemSlot systemSlot = new SystemSlot(systemSlotUri, label, assembly);
			systemSlot.setFunctions(getFunctionsOfSystemSlot(datasetUri, systemSlotUri));
			systemSlots.add(systemSlot);
		}

		return systemSlots;
	}

	private List<URI> getFunctionsOfSystemSlot(String datasetUri, String systemSlotUri)
			throws IOException, URISyntaxException {
		List<URI> functionUris = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("system_slot", systemSlotUri);
		queryStr.append("SELECT ?function ");
		queryStr.append("WHERE {");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?system_slot se:hasFunction ?function . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		if (responseNodes.size() > 0) {
			functionUris = new ArrayList<>();
		}
		for (JsonNode node : responseNodes) {
			String uri = node.get("function").get("value").asText();
			functionUris.add(new URI(uri));
		}
		return functionUris;
	}

	public SystemSlot getSystemSlot(int datasetId, String systemSlotUri) throws URISyntaxException, IOException {
		SystemSlot systemSlot = new SystemSlot();
		List<Dataset> datasets = _embeddedServer.getDatasets();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasets.get(datasetId).getUri().toString());
		queryStr.setIri("selected_system_slot", systemSlotUri);
		queryStr.append("SELECT ?label ?assembly ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?selected_system_slot rdf:type se:SystemSlot .");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?selected_system_slot rdfs:label ?label .");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?selected_system_slot .");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains .");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?selected_system_slot .");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly .");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode labelNode = node.get("label");
			String label = labelNode != null ? labelNode.get("value").asText() : null;
			JsonNode assemblyNode = node.get("assembly");
			String assembly = assemblyNode != null ? node.get("assembly").get("value").asText() : null;
			systemSlot = new SystemSlot(systemSlotUri, label, assembly);
		}

		return systemSlot;
	}

	public List<Dataset> getAllDatasets() throws URISyntaxException {
		return _embeddedServer.getDatasets();
	}

	public SystemSlot createSystemSlot(int datasetId) throws URISyntaxException, IOException {
		String datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri().toString();
		String localName = "SystemSlot_" + UUID.randomUUID().toString();
		String label = localName.substring(0, "SystemSlot_".length() + 4);
		String systemSlotUri = datasetUri + "#" + localName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("system_slot", systemSlotUri);
		queryStr.setIri("graph", datasetUri);
		queryStr.setLiteral("label", label);
		queryStr.append("INSERT {");
		queryStr.append("  GRAPH ?graph {");
		queryStr.append("    ?system_slot rdf:type se:SystemSlot .");
		queryStr.append("    ?system_slot rdf:type coins2:CoinsContainerObject .");
		queryStr.append("    ?system_slot rdfs:label ?label .");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("WHERE {");
		queryStr.append("}");

		_embeddedServer.update(queryStr);

		// _embeddedServer.saveDataset(datasetId);

		return getSystemSlot(datasetId, systemSlotUri);
	}

	public SystemSlot updateSystemSlot(int datasetId, String localName, SystemSlot systemSlot)
			throws URISyntaxException, IOException {
		URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		updateLabel(datasetUri, systemSlot.getUri(), systemSlot.getLabel());
		updateAssembly(datasetUri, systemSlot.getUri(), systemSlot.getAssembly());

		return getSystemSlot(datasetId, systemSlot.getUri().toString());
	}

	private void updateLabel(URI datasetUri, URI subjectUri, String newLabel) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());
		queryStr.setLiteral("new_label", newLabel);
		queryStr.append("  DELETE { GRAPH ?graph { ?subject rdfs:label ?label }} ");
		queryStr.append("  INSERT { GRAPH ?graph { ?subject rdfs:label ?new_label }} ");
		queryStr.append("WHERE { GRAPH ?graph { OPTIONAL { ?subject rdfs:label ?label }} ");
		queryStr.append("}");

		_embeddedServer.update(queryStr);
	}

	private void updateAssembly(URI datasetUri, URI subjectUri, URI assemblyUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?c ?p1 ?o1 . ");
		queryStr.append("      ?o2 ?p2 ?c . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?c ?p1 ?o1 . ");
		queryStr.append("        OPTIONAL { ?o2 ?p2 ?c . } ");
		queryStr.append("        ?c coins2:hasPart ?subject . ");
		queryStr.append("        ?c coins2:hasAssembly ?assembly . ");
		queryStr.append("      } UNION { ");
		queryStr.append("        ?c ?p1 ?o1 . ");
		queryStr.append("        ?o2 ?p2 ?c . ");
		queryStr.append("        ?subject coins2:partOf ?c . ");
		queryStr.append("        ?c coins2:hasAssembly ?assembly . ");
		queryStr.append("      } UNION { ");
		queryStr.append("        ?c ?p1 ?o1 . ");
		queryStr.append("        ?o2 ?p2 ?c . ");
		queryStr.append("        ?c coins2:hasPart ?subject . ");
		queryStr.append("        ?assembly coins2:hasContainsRelation ?c . ");
		queryStr.append("      } UNION { ");
		queryStr.append("        ?c ?p1 ?o1 . ");
		queryStr.append("        ?o2 ?p2 ?c . ");
		queryStr.append("        ?subject coins2:partOf ?c . ");
		queryStr.append("        ?assembly coins2:hasContainsRelation ?c . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);

		if (assemblyUri != null) {
			String containsRelationUri = datasetUri + "#ContainsRelation_" + UUID.randomUUID().toString();
			queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
			queryStr.setIri("graph", datasetUri.toString());
			queryStr.setIri("subject", subjectUri.toString());
			queryStr.setIri("contains_relation", containsRelationUri);
			queryStr.setIri("assembly", assemblyUri.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?contains_relation rdf:type coins2:ContainsRelation . ");
			queryStr.append("      ?contains_relation rdf:type coins2:CoinsContainerObject . ");
			queryStr.append("      ?contains_relation coins2:hasAssembly ?assembly . ");
			queryStr.append("      ?contains_relation coins2:hasPart ?subject . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			_embeddedServer.update(queryStr);
		}
	}
	
	public List<Function> getAllFunctions(int datasetId) throws IOException, URISyntaxException {
		List<Dataset> datasets = _embeddedServer.getDatasets();
		String datasetUri = datasets.get(datasetId).getUri().toString();
		List<Function> functions = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.append("SELECT ?function ?label ?assembly ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?function rdf:type se:Function . ");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?function rdfs:label ?label . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?function . ");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?function . ");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("      ?function coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("      ?function coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			String functionUri = node.get("function").get("value").asText();
			JsonNode labelNode = node.get("label");
			String label = labelNode != null ? labelNode.get("value").asText() : null;
			JsonNode assemblyNode = node.get("assembly");
			String assembly = assemblyNode != null ? node.get("assembly").get("value").asText() : null;
			Function function = new Function(functionUri, label, assembly);
//			function.setRequirements(getRequirementsOfFunction(datasetUri, functionUri));
			functions.add(function);
		}

		return functions;
	}

}
