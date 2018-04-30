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
	private enum SeObjectType {
		SystemSlot, Function;

		SeObject create(String uri, String label, String assembly) throws URISyntaxException {
			switch (this) {
			case Function:
				return new Function(uri, label, assembly);
			case SystemSlot:
				return new SystemSlot(uri, label, assembly);
			default:
				return null;
			}
		}

		String getUri() {
			return EmbeddedServer.SE + this.name();
		}
	}

	@Autowired
	private EmbeddedServer _embeddedServer;

	public List<SystemSlot> getAllSystemSlots(int datasetId) throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
		List<SeObject> seObjects = getAllSeObjects(datasetUri, SeObjectType.SystemSlot);

		List<SystemSlot> systemSlots = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			SystemSlot systemSlot = (SystemSlot) seObject;
			systemSlot.setFunctions(getFunctionsOfSystemSlot(datasetUri, systemSlot.getUri().toString()));
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
		String datasetUri = getDatasetUri(datasetId);
		List<SeObject> seObjects = getAllSeObjects(datasetUri, SeObjectType.Function);

		List<Function> functions = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			functions.add((Function) seObject);
		}
		return functions;
	}

	private List<SeObject> getAllSeObjects(String datasetUri, SeObjectType seObjectType)
			throws IOException, URISyntaxException {
		List<SeObject> seObjects = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("SeObject", seObjectType.getUri());
		queryStr.append("SELECT ?se_object ?label ?assembly ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?se_object rdf:type ?SeObject . ");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?se_object rdfs:label ?label . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?se_object . ");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?se_object . ");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("      ?se_object coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("      ?se_object coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			String seObjectUri = node.get("se_object").get("value").asText();
			JsonNode labelNode = node.get("label");
			String label = labelNode != null ? labelNode.get("value").asText() : null;
			JsonNode assemblyNode = node.get("assembly");
			String assembly = assemblyNode != null ? node.get("assembly").get("value").asText() : null;
			SeObject seObject = seObjectType.create(seObjectUri, label, assembly);
			seObjects.add(seObject);
		}

		return seObjects;
	}

	private String getDatasetUri(int datasetId) throws URISyntaxException {
		return _embeddedServer.getDatasets().get(datasetId).getUri().toString();

	}

}
