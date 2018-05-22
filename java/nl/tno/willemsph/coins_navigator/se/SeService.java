package nl.tno.willemsph.coins_navigator.se;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.jena.query.ParameterizedSparqlString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.EmbeddedServer;
import nl.tno.willemsph.coins_navigator.se.model.Function;
import nl.tno.willemsph.coins_navigator.se.model.Hamburger;
import nl.tno.willemsph.coins_navigator.se.model.NumericProperty;
import nl.tno.willemsph.coins_navigator.se.model.Performance;
import nl.tno.willemsph.coins_navigator.se.model.PortRealisation;
import nl.tno.willemsph.coins_navigator.se.model.RealisationModule;
import nl.tno.willemsph.coins_navigator.se.model.RealisationPort;
import nl.tno.willemsph.coins_navigator.se.model.Requirement;
import nl.tno.willemsph.coins_navigator.se.model.SeObject;
import nl.tno.willemsph.coins_navigator.se.model.SystemInterface;
import nl.tno.willemsph.coins_navigator.se.model.SystemSlot;

@Service
public class SeService {
	private enum SeObjectType {
		SystemSlot, RealisationModule, Function, Performance, Requirement, SystemInterface, Hamburger, PortRealisation, RealisationPort, NumericProperty;

		SeObject create(String uri, String label, String assemblyUri, List<String> partUris) throws URISyntaxException {
			switch (this) {
			case Function:
				return new Function(uri, label, assemblyUri, partUris);
			case Hamburger:
				return new Hamburger(uri, label, assemblyUri, partUris);
			case SystemInterface:
				return new SystemInterface(uri, label, assemblyUri, partUris);
			case RealisationModule:
				return new RealisationModule(uri, label, assemblyUri, partUris);
			case RealisationPort:
				return new RealisationPort(uri, label, assemblyUri, partUris);
			case Performance:
				return new Performance(uri, label, assemblyUri, partUris);
			case PortRealisation:
				return new PortRealisation(uri, label, assemblyUri, partUris);
			case Requirement:
				return new Requirement(uri, label, assemblyUri, partUris);
			case SystemSlot:
				return new SystemSlot(uri, label, assemblyUri, partUris);
			case NumericProperty:
				return new NumericProperty();
			default:
				return null;
			}
		}

		String getUri() {
			switch (this) {
			case NumericProperty:
				return EmbeddedServer.COINS2 + this.name();
			default:
				return EmbeddedServer.SE + this.name();
			}
		}
	}

	@Autowired
	private EmbeddedServer _embeddedServer;

	public List<SystemSlot> getAllSystemSlots(int datasetId) throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.SystemSlot);

		List<SystemSlot> systemSlots = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			SystemSlot systemSlot = (SystemSlot) seObject;
			systemSlot.setRequirements(getRequirements(datasetId, systemSlot.getUri().toString()));
			systemSlot.setFunctions(getFunctionsOfSystemSlot(datasetUri, systemSlot.getUri().toString()));
			systemSlot.setInterfaces(getInterfacesOfSystemSlot(datasetUri, systemSlot.getUri().toString()));
			systemSlots.add(systemSlot);
		}
		return systemSlots;
	}

	private List<URI> getFunctionsOfSystemSlot(String datasetUri, String systemSlotUri)
			throws IOException, URISyntaxException {
		List<URI> functionUris = new ArrayList<>();
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
		for (JsonNode node : responseNodes) {
			String uri = node.get("function").get("value").asText();
			functionUris.add(new URI(uri));
		}
		return functionUris;
	}

	private List<URI> getInterfacesOfSystemSlot(String datasetUri, String systemSlotUri)
			throws IOException, URISyntaxException {
		List<URI> interfaceUris = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("system_slot", systemSlotUri);
		queryStr.append("SELECT ?interface ");
		queryStr.append("WHERE {");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?system_slot se:hasInterfaces ?interface . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			String uri = node.get("interface").get("value").asText();
			interfaceUris.add(new URI(uri));
		}
		return interfaceUris;
	}

	public Function getFunction(int datasetId, String localName) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		Function function = (Function) getSeObject(datasetId, localName, SeObjectType.Function);
		function.setInput(getInputOfFunction(datasetUri, function.getUri().toString()));
		function.setOutput(getOutputOfFunction(datasetUri, function.getUri().toString()));
		function.setRequirements(getRequirements(datasetId, function.getUri().toString()));
		return function;
	}

	public Hamburger getHamburger(int datasetId, String hamburgerLocalName) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		Hamburger hamburger = (Hamburger) getSeObject(datasetId, hamburgerLocalName, SeObjectType.Hamburger);
		hamburger.setFunctionalUnit(getFunctionalUnit(datasetUri, hamburger.getUri().toString()));
		hamburger.setTechnicalSolution(getTechnicalSolution(datasetUri, hamburger.getUri().toString()));
		hamburger.setPortRealisations(getPortRealisations(datasetUri, hamburger.getUri().toString()));
		return hamburger;
	}

	public SystemSlot getSystemSlot(int datasetId, String localName) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		SystemSlot systemSlot = (SystemSlot) getSeObject(datasetId, localName, SeObjectType.SystemSlot);
		systemSlot.setRequirements(getRequirements(datasetId, systemSlot.getUri().toString()));
		systemSlot.setFunctions(getFunctionsOfSystemSlot(datasetUri, systemSlot.getUri().toString()));
		systemSlot.setInterfaces(getInterfacesOfSystemSlot(datasetUri, systemSlot.getUri().toString()));
		return systemSlot;
	}

	public SystemInterface getSystemInterface(int datasetId, String localName) throws URISyntaxException, IOException {
		SystemInterface systemInterface = (SystemInterface) getSeObject(datasetId, localName,
				SeObjectType.SystemInterface);
		List<URI> systemSlots = getSystemSlotsOfSystemInterface(datasetId, systemInterface.getUri().toString());
		systemInterface.setSystemSlot0(systemSlots.size() > 0 ? systemSlots.get(0) : null);
		systemInterface.setSystemSlot1(systemSlots.size() > 1 ? systemSlots.get(1) : null);
		systemInterface.setRequirements(getRequirements(datasetId, systemInterface.getUri().toString()));
		return systemInterface;
	}

	public List<Dataset> getAllDatasets() throws URISyntaxException {
		return _embeddedServer.getDatasets();
	}

	public Function createFunction(int datasetId) throws URISyntaxException, IOException {
		String functionUri = createSeObject(datasetId, SeObjectType.Function);
		return getFunction(datasetId, functionUri);
	}

	public Hamburger createHamburger(int datasetId) throws URISyntaxException, IOException {
		String functionUri = createSeObject(datasetId, SeObjectType.Hamburger);
		return getHamburger(datasetId, functionUri);
	}

	public Function updateFunction(int datasetId, String localName, Function function)
			throws URISyntaxException, IOException {
		URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		updateLabel(datasetUri, function.getUri(), function.getLabel());
		updateAssembly(datasetUri, function.getUri(), function.getAssembly());
		deleteInputOfFunction(datasetUri, function.getUri());
		insertInputOfFunction(datasetUri, function.getUri(), function.getInput());
		deleteOutputOfFunction(datasetUri, function.getUri());
		insertOutputOfFunction(datasetUri, function.getUri(), function.getOutput());
		deleteRequirements(datasetUri, function.getUri());
		for (URI requirementUri : function.getRequirements()) {
			insertRequirement(datasetUri, function.getUri(), requirementUri);
		}
		return getFunction(datasetId, localName);
	}

	public Requirement updateRequirement(int datasetId, String localName, Requirement requirement)
			throws URISyntaxException, IOException {
		URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		updateLabel(datasetUri, requirement.getUri(), requirement.getLabel());
		updateAssembly(datasetUri, requirement.getUri(), requirement.getAssembly());
		deleteParts(datasetUri, requirement.getUri());
		for (URI partUri : requirement.getParts()) {
			insertPart(datasetUri, requirement.getUri(), partUri);
		}
		updateMinValue(datasetUri, requirement.getUri(), requirement.getMinValue());
		updateMaxValue(datasetUri, requirement.getUri(), requirement.getMaxValue());
		return getRequirement(datasetId, localName);
	}

	public Hamburger updateHamburger(int datasetId, String localName, Hamburger hamburger)
			throws URISyntaxException, IOException {
		URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		updateLabel(datasetUri, hamburger.getUri(), hamburger.getLabel());
		updateAssembly(datasetUri, hamburger.getUri(), hamburger.getAssembly());
		deleteParts(datasetUri, hamburger.getUri());
		for (URI partUri : hamburger.getParts()) {
			insertPart(datasetUri, hamburger.getUri(), partUri);
		}
		deleteFunctionalUnit(datasetUri, hamburger.getUri());
		insertFunctionalUnit(datasetUri, hamburger.getUri(), hamburger.getFunctionalUnit());
		deleteTechnicalSolution(datasetUri, hamburger.getUri());
		insertTechnicalSolution(datasetUri, hamburger.getUri(), hamburger.getTechnicalSolution());
		deletePortRealisations(datasetUri, hamburger.getUri());
		for (URI portRealisationUri : hamburger.getPortRealisations()) {
			insertPortRealisation(datasetUri, hamburger.getUri(), portRealisationUri);
		}
		return getHamburger(datasetId, localName);
	}

	public RealisationModule createRealisationModule(int datasetId) throws URISyntaxException, IOException {
		String realisationModuleUri = createSeObject(datasetId, SeObjectType.RealisationModule);
		return getRealisationModule(datasetId, realisationModuleUri);
	}

	public RealisationModule updateRealisationModule(int datasetId, String localName,
			RealisationModule realisationModule) throws URISyntaxException, IOException {
		URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		// update label
		updateLabel(datasetUri, realisationModule.getUri(), realisationModule.getLabel());
		// update assembly
		updateAssembly(datasetUri, realisationModule.getUri(), realisationModule.getAssembly());
		// update parts
		deleteParts(datasetUri, realisationModule.getUri());
		for (URI partUri : realisationModule.getParts()) {
			insertPart(datasetUri, realisationModule.getUri(), partUri);
		}
		// update performances
		deletePerformances(datasetUri, realisationModule.getUri());
		for (URI performanceUri : realisationModule.getPerformances()) {
			insertPerformance(datasetUri, realisationModule.getUri(), performanceUri);
		}
		// update realisation ports
		deleteRealisationPorts(datasetUri, realisationModule.getUri());
		for (URI realisationPortUri : realisationModule.getPorts()) {
			insertRealisationPort(datasetUri, realisationModule.getUri(), realisationPortUri);
		}
		return getRealisationModule(datasetId, localName);
	}

	public RealisationModule getRealisationModule(int datasetId, String realisationModuleUri)
			throws URISyntaxException, IOException {
		RealisationModule realisationModule = (RealisationModule) getSeObject(datasetId, realisationModuleUri,
				SeObjectType.RealisationModule);
		realisationModule.setPerformances(getPerformances(datasetId, realisationModule.getUri().toString()));
		realisationModule.setPorts(getRealisationPorts(datasetId, realisationModule.getUri().toString()));
		return realisationModule;
	}

	public Requirement createRequirement(int datasetId) throws URISyntaxException, IOException {
		String requirementUri = createSeObject(datasetId, SeObjectType.Requirement);
		return getRequirement(datasetId, requirementUri);
	}

	public Performance getPerformance(int datasetId, String functionUri) throws URISyntaxException, IOException {
		return (Performance) getSeObject(datasetId, functionUri, SeObjectType.Performance);
	}

	public Requirement getRequirement(int datasetId, String functionUri) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		Requirement requirement = (Requirement) getSeObject(datasetId, functionUri, SeObjectType.Requirement);
		requirement.setMinValue(getMinValue(datasetUri, requirement.getUri().toString()));
		requirement.setMaxValue(getMaxValue(datasetUri, requirement.getUri().toString()));
		return requirement;
	}

	public SystemSlot createSystemSlot(int datasetId) throws URISyntaxException, IOException {
		String systemSlotUri = createSeObject(datasetId, SeObjectType.SystemSlot);
		return getSystemSlot(datasetId, systemSlotUri);
	}

	public SystemSlot updateSystemSlot(int datasetId, String localName, SystemSlot systemSlot)
			throws URISyntaxException, IOException {
		URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		updateLabel(datasetUri, systemSlot.getUri(), systemSlot.getLabel());
		updateAssembly(datasetUri, systemSlot.getUri(), systemSlot.getAssembly());
		deleteParts(datasetUri, systemSlot.getUri());
		for (URI partUri : systemSlot.getParts()) {
			insertPart(datasetUri, systemSlot.getUri(), partUri);
		}
		deleteRequirements(datasetUri, systemSlot.getUri());
		for (URI requirementUri : systemSlot.getRequirements()) {
			insertRequirement(datasetUri, systemSlot.getUri(), requirementUri);
		}
		deleteFunctions(datasetUri, systemSlot.getUri());
		for (URI functionUri : systemSlot.getFunctions()) {
			insertFunction(datasetUri, systemSlot.getUri(), functionUri);
		}
		deleteInterfaces(datasetUri, systemSlot.getUri());
		for (URI interfaceUri : systemSlot.getInterfaces()) {
			insertInterface(datasetUri, systemSlot.getUri(), interfaceUri);
		}
		return getSystemSlot(datasetId, localName);
	}

	public SystemInterface updateSystemInterface(int datasetId, String localName, SystemInterface systemInterface)
			throws URISyntaxException, IOException {
		URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		updateLabel(datasetUri, systemInterface.getUri(), systemInterface.getLabel());
		updateAssembly(datasetUri, systemInterface.getUri(), systemInterface.getAssembly());
		deleteParts(datasetUri, systemInterface.getUri());
		for (URI partUri : systemInterface.getParts()) {
			insertPart(datasetUri, systemInterface.getUri(), partUri);
		}
		deleteSystemSlots(datasetUri, systemInterface.getUri());
		if (systemInterface.getSystemSlot0() != null) {
			insertSystemSlot(datasetUri, systemInterface.getUri(), systemInterface.getSystemSlot0());
		}
		if (systemInterface.getSystemSlot1() != null) {
			insertSystemSlot(datasetUri, systemInterface.getUri(), systemInterface.getSystemSlot1());
		}
		deleteRequirements(datasetUri, systemInterface.getUri());
		for (URI requirementUri : systemInterface.getRequirements()) {
			insertRequirement(datasetUri, systemInterface.getUri(), requirementUri);
		}
		return getSystemInterface(datasetId, localName);
	}

	private void updateMinValue(URI datasetUri, URI subjectUri, URI minValue) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());
		queryStr.append("  DELETE { GRAPH ?graph { ?subject se:minValue ?value . }} ");
		if (minValue != null) {
			queryStr.setIri("min_value", minValue.toString());
			queryStr.append("  INSERT { GRAPH ?graph { ?subject se:minValue ?min_value . }} ");
		}
		queryStr.append("WHERE { GRAPH ?graph { OPTIONAL { ?subject se:minValue ?value . }} ");
		queryStr.append("}");

		_embeddedServer.update(queryStr);
	}

	private void updateMaxValue(URI datasetUri, URI subjectUri, URI maxValue) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());
		queryStr.append("  DELETE { GRAPH ?graph { ?subject se:maxValue ?value . }} ");
		if (maxValue != null) {
			queryStr.setIri("max_value", maxValue.toString());
			queryStr.append("  INSERT { GRAPH ?graph { ?subject se:maxValue ?max_value . }} ");
		}
		queryStr.append("WHERE { GRAPH ?graph { OPTIONAL { ?subject se:maxValue ?value . }} ");
		queryStr.append("}");

		_embeddedServer.update(queryStr);
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

	private void deleteParts(URI datasetUri, URI subjectUri) throws IOException {
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
		queryStr.append("        ?c coins2:hasPart ?part . ");
		queryStr.append("        ?c coins2:hasAssembly ?subject . ");
		queryStr.append("      } UNION { ");
		queryStr.append("        ?c ?p1 ?o1 . ");
		queryStr.append("        ?o2 ?p2 ?c . ");
		queryStr.append("        ?part coins2:partOf ?c . ");
		queryStr.append("        ?c coins2:hasAssembly ?subject . ");
		queryStr.append("      } UNION { ");
		queryStr.append("        ?c ?p1 ?o1 . ");
		queryStr.append("        ?o2 ?p2 ?c . ");
		queryStr.append("        ?c coins2:hasPart ?part . ");
		queryStr.append("        ?subject coins2:hasContainsRelation ?c . ");
		queryStr.append("      } UNION { ");
		queryStr.append("        ?c ?p1 ?o1 . ");
		queryStr.append("        ?o2 ?p2 ?c . ");
		queryStr.append("        ?part coins2:partOf ?c . ");
		queryStr.append("        ?subject coins2:hasContainsRelation ?c . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);
	}

	private void insertPart(URI datasetUri, URI subjectUri, URI partUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		if (partUri != null) {
			String containsRelationUri = datasetUri + "#ContainsRelation_" + UUID.randomUUID().toString();
			queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
			queryStr.setIri("graph", datasetUri.toString());
			queryStr.setIri("subject", subjectUri.toString());
			queryStr.setIri("contains_relation", containsRelationUri);
			queryStr.setIri("part", partUri.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?contains_relation rdf:type coins2:ContainsRelation . ");
			queryStr.append("      ?contains_relation rdf:type coins2:CoinsContainerObject . ");
			queryStr.append("      ?contains_relation coins2:hasAssembly ?subject . ");
			queryStr.append("      ?contains_relation coins2:hasPart ?part . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			_embeddedServer.update(queryStr);
		}
	}

	// private void updatePart(URI datasetUri, URI subjectUri, URI partUri) throws
	// IOException {
	// ParameterizedSparqlString queryStr = new
	// ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
	// queryStr.setIri("graph", datasetUri.toString());
	// queryStr.setIri("subject", subjectUri.toString());
	//
	// queryStr.append(" DELETE { ");
	// queryStr.append(" GRAPH ?graph { ");
	// queryStr.append(" ?c ?p1 ?o1 . ");
	// queryStr.append(" ?o2 ?p2 ?c . ");
	// queryStr.append(" } ");
	// queryStr.append(" } ");
	// queryStr.append(" WHERE { ");
	// queryStr.append(" GRAPH ?graph { ");
	// queryStr.append(" { ");
	// queryStr.append(" ?c ?p1 ?o1 . ");
	// queryStr.append(" OPTIONAL { ?o2 ?p2 ?c . } ");
	// queryStr.append(" ?c coins2:hasPart ?part . ");
	// queryStr.append(" ?c coins2:hasAssembly ?subject . ");
	// queryStr.append(" } UNION { ");
	// queryStr.append(" ?c ?p1 ?o1 . ");
	// queryStr.append(" ?o2 ?p2 ?c . ");
	// queryStr.append(" ?part coins2:partOf ?c . ");
	// queryStr.append(" ?c coins2:hasAssembly ?subject . ");
	// queryStr.append(" } UNION { ");
	// queryStr.append(" ?c ?p1 ?o1 . ");
	// queryStr.append(" ?o2 ?p2 ?c . ");
	// queryStr.append(" ?c coins2:hasPart ?part . ");
	// queryStr.append(" ?subject coins2:hasContainsRelation ?c . ");
	// queryStr.append(" } UNION { ");
	// queryStr.append(" ?c ?p1 ?o1 . ");
	// queryStr.append(" ?o2 ?p2 ?c . ");
	// queryStr.append(" ?part coins2:partOf ?c . ");
	// queryStr.append(" ?subject coins2:hasContainsRelation ?c . ");
	// queryStr.append(" } ");
	// queryStr.append(" }");
	// queryStr.append(" }");
	//
	// _embeddedServer.update(queryStr);
	//
	// if (partUri != null) {
	// String containsRelationUri = datasetUri + "#ContainsRelation_" +
	// UUID.randomUUID().toString();
	// queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
	// queryStr.setIri("graph", datasetUri.toString());
	// queryStr.setIri("subject", subjectUri.toString());
	// queryStr.setIri("contains_relation", containsRelationUri);
	// queryStr.setIri("part", partUri.toString());
	// queryStr.append(" INSERT { ");
	// queryStr.append(" GRAPH ?graph { ");
	// queryStr.append(" ?contains_relation rdf:type coins2:ContainsRelation . ");
	// queryStr.append(" ?contains_relation rdf:type coins2:CoinsContainerObject .
	// ");
	// queryStr.append(" ?contains_relation coins2:hasAssembly ?subject . ");
	// queryStr.append(" ?contains_relation coins2:hasPart ?part . ");
	// queryStr.append(" } ");
	// queryStr.append(" }");
	// queryStr.append("WHERE { } ");
	//
	// _embeddedServer.update(queryStr);
	// }
	// }

	private void deleteRequirements(URI datasetUri, URI subjectUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasRequirement ?requirement . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?subject se:hasRequirement ?requirement . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);
	}

	private void insertRequirement(URI datasetUri, URI subjectUri, URI requirementUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());
		queryStr.setIri("requirement", requirementUri.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasRequirement ?requirement . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		_embeddedServer.update(queryStr);
	}

	private void deletePerformances(URI datasetUri, URI subjectUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasPerformance ?performance . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?subject se:hasPerformance ?performance . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);
	}

	private void insertPerformance(URI datasetUri, URI subjectUri, URI performanceUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());
		queryStr.setIri("performance", performanceUri.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasPerformance ?performance . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		_embeddedServer.update(queryStr);
	}

	private void deleteRealisationPorts(URI datasetUri, URI subjectUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasPort ?realisation_port . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?subject se:hasPort ?realisation_port . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);
	}

	private void insertRealisationPort(URI datasetUri, URI subjectUri, URI realisationPortUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());
		queryStr.setIri("realisation_port", realisationPortUri.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasPort ?realisation_port . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		_embeddedServer.update(queryStr);
	}

	private void deleteFunctions(URI datasetUri, URI subjectUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasFunction ?function . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?subject se:hasFunction ?function . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);
	}

	private void insertFunction(URI datasetUri, URI subjectUri, URI functionUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());
		queryStr.setIri("function", functionUri.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasFunction ?function . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		_embeddedServer.update(queryStr);
	}

	private void deleteInputOfFunction(URI datasetUri, URI functionUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("function", functionUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?function se:hasInput ?system_interface . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?function se:hasInput ?system_interface . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);
	}

	private void insertInputOfFunction(URI datasetUri, URI functionUri, URI systemInterfaceUri) throws IOException {
		if (systemInterfaceUri != null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
			queryStr.setIri("graph", datasetUri.toString());
			queryStr.setIri("function", functionUri.toString());
			queryStr.setIri("system_interface", systemInterfaceUri.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?function se:hasInput ?system_interface . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			_embeddedServer.update(queryStr);
		}
	}

	private void deleteOutputOfFunction(URI datasetUri, URI functionUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("function", functionUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?function se:hasOutput ?system_interface . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?function se:hasOutput ?system_interface . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);
	}

	private void insertOutputOfFunction(URI datasetUri, URI functionUri, URI systemInterfaceUri) throws IOException {
		if (systemInterfaceUri != null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
			queryStr.setIri("graph", datasetUri.toString());
			queryStr.setIri("function", functionUri.toString());
			queryStr.setIri("system_interface", systemInterfaceUri.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?function se:hasOutput ?system_interface . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			_embeddedServer.update(queryStr);
		}
	}

	private void deleteFunctionalUnit(URI datasetUri, URI subjectUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:functionalUnit ?functional_unit . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?subject se:functionalUnit ?functional_unit . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);
	}

	private void insertFunctionalUnit(URI datasetUri, URI subjectUri, URI functionalUnitUri) throws IOException {
		if (functionalUnitUri != null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
			queryStr.setIri("graph", datasetUri.toString());
			queryStr.setIri("subject", subjectUri.toString());
			queryStr.setIri("functional_unit", functionalUnitUri.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?subject se:functionalUnit ?functional_unit . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			_embeddedServer.update(queryStr);
		}
	}

	private void deleteTechnicalSolution(URI datasetUri, URI subjectUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:technicalSolution ?technical_solution . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?subject se:technicalSolution ?technical_solution  . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);
	}

	private void insertTechnicalSolution(URI datasetUri, URI subjectUri, URI technicalSolutionUri) throws IOException {
		if (technicalSolutionUri != null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
			queryStr.setIri("graph", datasetUri.toString());
			queryStr.setIri("subject", subjectUri.toString());
			queryStr.setIri("technical_solution", technicalSolutionUri.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?subject se:technicalSolution ?technical_solution . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			_embeddedServer.update(queryStr);
		}
	}

	private void deletePortRealisations(URI datasetUri, URI subjectUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasPortRealisation ?port_realisation . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?subject se:hasPortRealisation ?port_realisation  . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);
	}

	private void insertPortRealisation(URI datasetUri, URI subjectUri, URI portRealisationUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());
		queryStr.setIri("port_realisation", portRealisationUri.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasPortRealisation ?port_realisation . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		_embeddedServer.update(queryStr);
	}

	private void deleteSystemSlots(URI datasetUri, URI subjectUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?system_slot se:hasInterfaces ?subject . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?system_slot se:hasInterfaces ?subject . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);
	}

	private void deleteInterfaces(URI datasetUri, URI subjectUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasInterfaces ?interface . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?subject se:hasInterfaces ?interface . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);
	}

	private void insertInterface(URI datasetUri, URI subjectUri, URI interfaceUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());
		queryStr.setIri("interface", interfaceUri.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?subject se:hasInterfaces ?interface . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		_embeddedServer.update(queryStr);
	}

	private void insertSystemSlot(URI datasetUri, URI subjectUri, URI systemSlotUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());
		queryStr.setIri("system_slot", systemSlotUri.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?system_slot se:hasInterfaces ?subject . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		_embeddedServer.update(queryStr);
	}

	public List<Function> getAllFunctions(int datasetId) throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.Function);

		List<Function> functions = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			Function function = (Function) seObject;
			function.setInput(getInputOfFunction(datasetUri, function.getUri().toString()));
			function.setOutput(getOutputOfFunction(datasetUri, function.getUri().toString()));
			function.setRequirements(getRequirements(datasetId, function.getUri().toString()));
			functions.add(function);
		}
		return functions;
	}

	public List<Hamburger> getAllHamburgers(int datasetId) throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.Hamburger);

		List<Hamburger> hamburgers = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			Hamburger hamburger = (Hamburger) seObject;
			hamburger.setFunctionalUnit(getFunctionalUnit(datasetUri, hamburger.getUri().toString()));
			hamburger.setTechnicalSolution(getTechnicalSolution(datasetUri, hamburger.getUri().toString()));
			hamburger.setPortRealisations(getPortRealisations(datasetUri, hamburger.getUri().toString()));
			hamburgers.add(hamburger);
		}
		return hamburgers;
	}

	public NumericProperty getMinValueOfRequirement(int datasetId, String localName)
			throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String requirementUri = getOntologyUri(datasetId) + "#" + localName;
		NumericProperty numericProperty = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("requirement", requirementUri);
		queryStr.append("SELECT ?min_value ?label ?value ?unit ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?requirement se:minValue ?min_value . ");
		queryStr.append("      OPTIONAL { ?min_value rdfs:label ?label . } ");
		queryStr.append("      OPTIONAL { ?min_value coins2:datatypeValue ?value . } ");
		queryStr.append("      OPTIONAL { ?min_value coins2:unit ?unit_rsrc . } ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode minValueNode = node.get("min_value");
			String minValue = minValueNode != null ? minValueNode.get("value").asText() : null;
			URI minValueUri = minValue != null ? new URI(minValue) : null;
			JsonNode labelNode = node.get("label");
			String label = labelNode != null ? labelNode.asText() : null;
			JsonNode valueNode = node.get("value");
			Double datatypeValue = valueNode != null ? valueNode.get("value").asDouble() : null;
			JsonNode unitNode = node.get("unit");
			String unit = unitNode != null ? unitNode.asText() : null;
			// numericProperty = new NumericProperty(minValueUri.toString(), label,
			// datatypeValue, unit);
		}
		return numericProperty;
	}

	public NumericProperty getMaxValueOfRequirement(int datasetId, String localName)
			throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String requirementUri = getOntologyUri(datasetId) + "#" + localName;
		NumericProperty numericProperty = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("requirement", requirementUri);
		queryStr.append("SELECT ?max_value ?label ?value ?unit ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?requirement se:maxValue ?max_value . ");
		queryStr.append("      OPTIONAL { ?max_value rdfs:label ?label . } ");
		queryStr.append("      OPTIONAL { ?max_value coins2:datatypeValue ?value . } ");
		queryStr.append("      OPTIONAL { ?max_value coins2:unit ?unit_rsrc . ");
		queryStr.append("      			  ?unit_rsrc rdfs:label ?unit . } ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode maxValueNode = node.get("max_value");
			String maxValue = maxValueNode != null ? maxValueNode.get("value").asText() : null;
			URI maxValueUri = maxValue != null ? new URI(maxValue) : null;
			JsonNode labelNode = node.get("label");
			String label = labelNode != null ? labelNode.asText() : null;
			JsonNode valueNode = node.get("value");
			Double datatypeValue = valueNode != null ? valueNode.get("value").asDouble() : null;
			JsonNode unitNode = node.get("unit");
			String unit = unitNode != null ? unitNode.asText() : null;
			// numericProperty = new NumericProperty(maxValueUri.toString(), label,
			// datatypeValue, unit);
		}
		return numericProperty;
	}

	private Double getDatatypeValue(String datasetUri, String numericPropertyUri) throws IOException {
		Double datatypeValue = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("numeric_property", numericPropertyUri);
		queryStr.append("SELECT ?datatype_value ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?numeric_property coins2:datatypeValue ?datatype_value . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode datatypeValueNode = node.get("datatype_value");
			datatypeValue = datatypeValueNode != null ? datatypeValueNode.get("value").asDouble() : null;
		}

		return datatypeValue;
	}

	private String getUnit(String datasetUri, String numericPropertyUri) throws IOException {
		String unit = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("numeric_property", numericPropertyUri);
		queryStr.append("SELECT ?unit ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?numeric_property coins2:unit ?unit_value . ");
		queryStr.append("      ?unit_value rdfs:label ?unit . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode unitNode = node.get("unit");
			unit = unitNode != null ? unitNode.get("value").asText() : null;
		}

		return unit;
	}

	private URI getMinValue(String datasetUri, String requirementUri) throws IOException, URISyntaxException {
		URI minValueUri = null;
		// Double value = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("requirement", requirementUri);
		queryStr.append("SELECT ?min_value ?value ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?requirement se:minValue ?min_value . ");
		queryStr.append("      ?min_value coins2:datatypeValue ?value . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode minValueNode = node.get("min_value");
			String minValue = minValueNode != null ? minValueNode.get("value").asText() : null;
			minValueUri = minValue != null ? new URI(minValue) : null;
			// JsonNode valueNode = node.get("value");
			// value = valueNode != null ? valueNode.get("value").asDouble() : null;
		}
		return minValueUri;
	}

	private URI getMaxValue(String datasetUri, String requirementUri) throws IOException, URISyntaxException {
		URI maxValueUri = null;
		// Double value = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("requirement", requirementUri);
		queryStr.append("SELECT ?max_value ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?requirement se:maxValue ?max_value . ");
		queryStr.append("      ?max_value coins2:datatypeValue ?value . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode maxValueNode = node.get("max_value");
			String maxValue = maxValueNode != null ? maxValueNode.get("value").asText() : null;
			maxValueUri = maxValue != null ? new URI(maxValue) : null;
			// JsonNode valueNode = node.get("value");
			// value = valueNode != null ? valueNode.get("value").asDouble() : null;
		}
		return maxValueUri;
	}

	private URI getFunctionalUnit(String datasetUri, String hamburgerUri) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("hamburger", hamburgerUri);
		queryStr.append("SELECT ?functional_unit ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?hamburger se:functionalUnit ?functional_unit . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		URI functionalUnitUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode functionalUnitNode = node.get("functional_unit");
			String functionalUnit = functionalUnitNode != null ? functionalUnitNode.get("value").asText() : null;
			functionalUnitUri = functionalUnit != null ? new URI(functionalUnit) : null;
		}
		return functionalUnitUri;
	}

	private URI getTechnicalSolution(String datasetUri, String hamburgerUri) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("hamburger", hamburgerUri);
		queryStr.append("SELECT ?technical_solution ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?hamburger se:technicalSolution ?technical_solution . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		URI technicalSolutionUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode technicalSolutionNode = node.get("technical_solution");
			String technicalSolution = technicalSolutionNode != null ? technicalSolutionNode.get("value").asText()
					: null;
			technicalSolutionUri = technicalSolution != null ? new URI(technicalSolution) : null;
		}
		return technicalSolutionUri;
	}

	private URI getSystemInterfaceOfPortRealisation(String datasetUri, String portRealisationUri)
			throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("port_realisation", portRealisationUri);
		queryStr.append("SELECT ?system_interface ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?port_realisation se:interface ?system_interface . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		URI systemInterfaceUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode systemInterfaceNode = node.get("system_interface");
			String systemInterface = systemInterfaceNode != null ? systemInterfaceNode.get("value").asText() : null;
			systemInterfaceUri = systemInterface != null ? new URI(systemInterface) : null;
		}
		return systemInterfaceUri;
	}

	private URI getRealisationPortOfPortRealisation(String datasetUri, String portRealisationUri)
			throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("port_realisation", portRealisationUri);
		queryStr.append("SELECT ?realisation_port ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?port_realisation se:port ?realisation_port . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		URI realisationPortUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode realisationPortNode = node.get("realisation_port");
			String realisationPort = realisationPortNode != null ? realisationPortNode.get("value").asText() : null;
			realisationPortUri = realisationPort != null ? new URI(realisationPort) : null;
		}
		return realisationPortUri;
	}

	private List<URI> getPortRealisations(String datasetUri, String hamburgerUri)
			throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("hamburger", hamburgerUri);
		queryStr.append("SELECT ?port_realisation ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?hamburger se:hasPortRealisation ?port_realisation . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
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

	private URI getInputOfFunction(String datasetUri, String functionUri) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("function", functionUri);
		queryStr.append("SELECT ?input ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?function se:input ?input . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		URI inputUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode inputNode = node.get("input");
			String input = inputNode != null ? inputNode.get("value").asText() : null;
			inputUri = input != null ? new URI(input) : null;
		}
		return inputUri;
	}

	private URI getOutputOfFunction(String datasetUri, String functionUri) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("function", functionUri);
		queryStr.append("SELECT ?output ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?function se:output ?output . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		URI outputUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode outputNode = node.get("output");
			String output = outputNode != null ? outputNode.get("value").asText() : null;
			outputUri = output != null ? new URI(output) : null;
		}
		return outputUri;
	}

	private List<URI> getPerformances(int datasetId, String ownerUri) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("owner", ownerUri);
		queryStr.append("SELECT ?performance ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?owner se:hasPerformance ?performance . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
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

	private URI getPortOwner(int datasetId, String portUri) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("port", portUri);
		queryStr.append("SELECT ?owner ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?owner se:hasPort ?port . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		URI ownerUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode ownerNode = node.get("owner");
			ownerUri = ownerNode != null ? new URI(ownerNode.get("value").asText()) : null;
		}
		return ownerUri;
	}

	private List<URI> getRealisationPorts(int datasetId, String ownerUri) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("owner", ownerUri);
		queryStr.append("SELECT ?port ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?owner se:hasPort ?port . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
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

	private List<URI> getRequirements(int datasetId, String ownerUri) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("owner", ownerUri);
		queryStr.append("SELECT ?requirement ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?owner se:hasRequirement ?requirement . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		List<URI> requirementUris = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			JsonNode requirementNode = node.get("requirement");
			String requirementUri = requirementNode != null ? requirementNode.get("value").asText() : null;
			if (requirementUri != null) {
				requirementUris.add(new URI(requirementUri));
			}
		}
		return requirementUris;
	}

	public List<Performance> getAllPerformances(int datasetId) throws IOException, URISyntaxException {
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.Performance);

		List<Performance> performances = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			Performance performance = (Performance) seObject;
			performances.add(performance);
		}
		return performances;
	}

	public Performance createPerformance(int datasetId) throws URISyntaxException, IOException {
		String performanceUri = createSeObject(datasetId, SeObjectType.Performance);
		return getPerformance(datasetId, performanceUri);
	}

	public List<Requirement> getAllRequirements(int datasetId) throws IOException, URISyntaxException {
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.Requirement);

		List<Requirement> requirements = new ArrayList<>();
		String datasetUri = getDatasetUri(datasetId);
		for (SeObject seObject : seObjects) {
			Requirement requirement = (Requirement) seObject;
			requirement.setMinValue(getMinValue(datasetUri, requirement.getUri().toString()));
			requirement.setMaxValue(getMaxValue(datasetUri, requirement.getUri().toString()));
			requirements.add(requirement);
		}
		return requirements;
	}

	public List<NumericProperty> getAllNumericProperties(int datasetId) throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.append("SELECT ?type ?se_object ?label ?datatype_value ?unit ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?type rdfs:subClassOf coins2:FloatProperty . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?type rdfs:subClassOf coins2:IntegerProperty . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?se_object rdf:type ?type . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?se_object rdfs:label ?label . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?se_object coins2:datatypeValue ?datatype_value . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?se_object coins2:unit ?unit . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		List<NumericProperty> numericProperties = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			String seObjectUri = node.get("se_object").get("value").asText();
			JsonNode labelNode = node.get("label");
			String label = labelNode != null ? labelNode.get("value").asText() : null;
			JsonNode typeNode = node.get("type");
			String typeUri = typeNode != null ? typeNode.get("value").asText() : null;
			JsonNode datatypeValueNode = node.get("datatype_value");
			Double datatypeValue = datatypeValueNode != null ? datatypeValueNode.get("value").asDouble() : null;
			JsonNode unitNode = node.get("unit");
			String unitUri = unitNode != null ? unitNode.get("value").asText() : null;
			numericProperties.add(new NumericProperty(seObjectUri, label, typeUri, datatypeValue, unitUri));
		}
		return numericProperties;
	}

	public List<SystemInterface> getAllSystemInterfaces(int datasetId) throws IOException, URISyntaxException {
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.SystemInterface);

		List<SystemInterface> systemInterfaces = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			SystemInterface systemInterface = (SystemInterface) seObject;
			List<URI> systemSlots = getSystemSlotsOfSystemInterface(datasetId, systemInterface.getUri().toString());
			if (systemSlots.size() > 0) {
				systemInterface.setSystemSlot0(systemSlots.get(0));
				if (systemSlots.size() > 1) {
					systemInterface.setSystemSlot1(systemSlots.get(1));
				}
			}
			systemInterfaces.add(systemInterface);
		}
		return systemInterfaces;
	}

	public SystemInterface createSystemInterface(int datasetId) throws URISyntaxException, IOException {
		String systemInterfaceUri = createSeObject(datasetId, SeObjectType.SystemInterface);
		return getSystemInterface(datasetId, systemInterfaceUri);
	}

	private List<URI> getSystemSlotsOfSystemInterface(int datasetId, String systemInterfaceUri)
			throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("system_interface", systemInterfaceUri);
		queryStr.append("SELECT ?system_slot ");
		queryStr.append("WHERE {");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?system_slot se:hasInterfaces ?system_interface . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		List<URI> systemSlotUris = new ArrayList<>();

		for (JsonNode node : responseNodes) {
			String system_slotUri = node.get("system_slot").get("value").asText();
			systemSlotUris.add(new URI(system_slotUri));
		}
		return systemSlotUris;
	}

	public List<RealisationModule> getAllRealisationModules(int datasetId) throws IOException, URISyntaxException {
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.RealisationModule);

		List<RealisationModule> realisationModules = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			RealisationModule realisationModule = (RealisationModule) seObject;
			realisationModule.setPerformances(getPerformances(datasetId, realisationModule.getUri().toString()));
			realisationModule.setPorts(getRealisationPorts(datasetId, realisationModule.getUri().toString()));
			realisationModules.add(realisationModule);
		}
		return realisationModules;
	}

	public List<RealisationPort> getAllRealisationPorts(int datasetId) throws IOException, URISyntaxException {
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.RealisationPort);

		List<RealisationPort> realisationPorts = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			RealisationPort realisationPort = (RealisationPort) seObject;
			realisationPort.setOwner(getPortOwner(datasetId, realisationPort.getUri().toString()));
			realisationPorts.add(realisationPort);
		}
		return realisationPorts;
	}

	public List<Hamburger> getHamburgersForSystemSlot(int datasetId, String systemSlotLocalName)
			throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String ontologyUri = getOntologyUri(datasetId);
		String systemSlotUri = ontologyUri + "#" + systemSlotLocalName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("system_slot", systemSlotUri);
		queryStr.append("SELECT ?hamburger ?technical_solution ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?hamburger se:functionalUnit ?system_slot . ");
		queryStr.append("    OPTIONAL { ");
		queryStr.append("      ?hamburger se:technicalSolution ?technical_solution . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");

		Map<String, String> hamburgerMap = new HashMap<>();
		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			String hamburgerUri = node.get("hamburger").get("value").asText();
			JsonNode technical_solutionNode = node.get("technical_solution");
			String technical_solutionUri = technical_solutionNode != null ? technical_solutionNode.get("value").asText()
					: null;
			hamburgerMap.put(hamburgerUri, technical_solutionUri);
		}

		List<Hamburger> hamburgers = new ArrayList<>();
		for (String hamburgerUri : hamburgerMap.keySet()) {
			String hamburgerLocalName = getLocalName(hamburgerUri);
			Hamburger hamburger = (Hamburger) getSeObject(datasetId, hamburgerLocalName, SeObjectType.Hamburger);
			hamburger.setFunctionalUnit(new URI(systemSlotUri));
			String technicalSolutionUri = hamburgerMap.get(hamburgerUri);
			if (technicalSolutionUri != null)
				hamburger.setTechnicalSolution(new URI(technicalSolutionUri));
			hamburgers.add(hamburger);
		}

		return hamburgers;
	}

	public List<Hamburger> getHamburgersForRealisationModule(int datasetId, String realisationModuleLocalName)
			throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String ontologyUri = getOntologyUri(datasetId);
		String realisationModuleUri = ontologyUri + "#" + realisationModuleLocalName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("realisation_module", realisationModuleUri);
		queryStr.append("SELECT ?hamburger ?functional_unit ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?hamburger se:technicalSolution ?realisation_module . ");
		queryStr.append("    OPTIONAL { ");
		queryStr.append("      ?hamburger se:functionalUnit ?functional_unit . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");

		Map<String, String> hamburgerMap = new HashMap<>();
		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			String hamburgerUri = node.get("hamburger").get("value").asText();
			JsonNode functionalUnitNode = node.get("functional_unit");
			String functionalUnitUri = functionalUnitNode != null ? functionalUnitNode.get("value").asText() : null;
			hamburgerMap.put(hamburgerUri, functionalUnitUri);
		}

		List<Hamburger> hamburgers = new ArrayList<>();
		for (String hamburgerUri : hamburgerMap.keySet()) {
			String hamburgerLocalName = getLocalName(hamburgerUri);
			Hamburger hamburger = (Hamburger) getSeObject(datasetId, hamburgerLocalName, SeObjectType.Hamburger);
			hamburger.setTechnicalSolution(new URI(realisationModuleUri));
			String functionalUnitUri = hamburgerMap.get(hamburgerUri);
			if (functionalUnitUri != null)
				hamburger.setFunctionalUnit(new URI(functionalUnitUri));
			hamburgers.add(hamburger);
		}

		return hamburgers;
	}

	public List<RealisationPort> getPortsForRealisationModule(int datasetId, String realisationModuleLocalName)
			throws URISyntaxException, IOException {
		String ontologyUri = getOntologyUri(datasetId);
		String realisationModuleUri = ontologyUri + "#" + realisationModuleLocalName;
		List<URI> portUris = getRealisationPorts(datasetId, realisationModuleUri);
		List<RealisationPort> ports = new ArrayList<>();
		for (URI portUri : portUris) {
			String localName = getLocalName(portUri.toString());
			RealisationPort port = (RealisationPort) getSeObject(datasetId, localName, SeObjectType.RealisationPort);
			port.setOwner(new URI(realisationModuleUri));
			ports.add(port);
		}
		return ports;
	}

	private List<SeObject> getAllSeObjects(int datasetId, SeObjectType seObjectType)
			throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
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
			List<String> seObjectParts = getSeObjectParts(datasetId, getLocalName(seObjectUri), seObjectType);
			SeObject seObject = seObjectType.create(seObjectUri, label, assembly, seObjectParts);
			seObjects.add(seObject);
		}

		return seObjects;
	}

	public SeObject getSeObject(int datasetId, String localName, SeObjectType seObjectType)
			throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String ontologyUri = getOntologyUri(datasetId);
		String seObjectUri = ontologyUri + "#" + localName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("se_object", seObjectUri);
		queryStr.setIri("SeObject", seObjectType.getUri());
		queryStr.append("SELECT ?label ?assembly ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?se_object rdf:type ?SeObject .");
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

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		SeObject seObject = null;
		for (JsonNode node : responseNodes) {
			JsonNode labelNode = node.get("label");
			String label = labelNode != null ? labelNode.get("value").asText() : null;
			JsonNode assemblyNode = node.get("assembly");
			String assembly = assemblyNode != null ? node.get("assembly").get("value").asText() : null;
			List<String> seObjectParts = getSeObjectParts(datasetId, getLocalName(seObjectUri), seObjectType);
			seObject = seObjectType.create(seObjectUri, label, assembly, seObjectParts);
		}

		return seObject;
	}

	public List<String> getSeObjectParts(int datasetId, String localName, SeObjectType seObjectType)
			throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String ontologyUri = getOntologyUri(datasetId);
		String seObjectUri = ontologyUri + "#" + localName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("assembly", seObjectUri);
		queryStr.setIri("SeObject", seObjectType.getUri());
		queryStr.append("SELECT ?part ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?part . ");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?part . ");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("      ?part coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("      ?part coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		List<String> parts = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			JsonNode partNode = node.get("part");
			if (partNode != null) {
				String partUri = partNode.get("value").asText();
				parts.add(partUri);
			}
		}

		return parts;
	}

	private String createSeObject(int datasetId, SeObjectType seObjectType) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String ontologyUri = getOntologyUri(datasetId);
		String localName = seObjectType.name() + "_" + UUID.randomUUID().toString();
		String label = localName.substring(0, seObjectType.name().length() + 5);
		String seObjectUri = ontologyUri + "#" + localName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("se_object", seObjectUri);
		queryStr.setIri("SeObject", seObjectType.getUri());
		queryStr.setIri("graph", datasetUri);
		queryStr.setLiteral("label", label);
		queryStr.append("INSERT {");
		queryStr.append("  GRAPH ?graph {");
		queryStr.append("    ?se_object rdf:type ?SeObject .");
		queryStr.append("    ?se_object rdf:type coins2:CoinsContainerObject .");
		queryStr.append("    ?se_object rdfs:label ?label .");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("WHERE {");
		queryStr.append("}");

		_embeddedServer.update(queryStr);

		return localName;
	}

	private String getDatasetUri(int datasetId) throws URISyntaxException {
		return _embeddedServer.getDatasets().get(datasetId).getUri().toString();

	}

	private String getOntologyUri(int datasetId) throws URISyntaxException {
		return _embeddedServer.getDatasets().get(datasetId).getOntologyUri().toString();
	}

	private String getLocalName(String uri) {
		int indexOfHashMark = uri.indexOf('#');
		return uri.substring(indexOfHashMark + 1);
	}

	public List<PortRealisation> getPortRealisationsOfHamburger(int datasetId, String hamburgerLocalName)
			throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
		String ontologyUri = getOntologyUri(datasetId);
		List<URI> portRealisationUris = getPortRealisations(datasetUri, ontologyUri + "#" + hamburgerLocalName);
		List<PortRealisation> portRealisations = new ArrayList<>();
		for (URI portRealisationUri : portRealisationUris) {
			PortRealisation portRealisation = (PortRealisation) getSeObject(datasetId,
					getLocalName(portRealisationUri.toString()), SeObjectType.PortRealisation);
			portRealisation
					.setSystemInterface(getSystemInterfaceOfPortRealisation(datasetUri, portRealisationUri.toString()));
			portRealisation
					.setRealisationPort(getRealisationPortOfPortRealisation(datasetUri, portRealisationUri.toString()));
			portRealisations.add(portRealisation);
		}
		return portRealisations;
	}

}
