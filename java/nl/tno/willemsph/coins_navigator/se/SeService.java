package nl.tno.willemsph.coins_navigator.se;

import java.io.File;
import java.io.FileNotFoundException;
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
import nl.tno.willemsph.coins_navigator.se.model.GetFunction;
import nl.tno.willemsph.coins_navigator.se.model.GetHamburger;
import nl.tno.willemsph.coins_navigator.se.model.NumericProperty;
import nl.tno.willemsph.coins_navigator.se.model.GetPerformance;
import nl.tno.willemsph.coins_navigator.se.model.PortRealisation;
import nl.tno.willemsph.coins_navigator.se.model.PutFunction;
import nl.tno.willemsph.coins_navigator.se.model.PutHamburger;
import nl.tno.willemsph.coins_navigator.se.model.PutPerformance;
import nl.tno.willemsph.coins_navigator.se.model.PutRealisationModule;
import nl.tno.willemsph.coins_navigator.se.model.PutRequirement;
import nl.tno.willemsph.coins_navigator.se.model.PutSystemInterface;
import nl.tno.willemsph.coins_navigator.se.model.PutSystemSlot;
import nl.tno.willemsph.coins_navigator.se.model.GetRealisationModule;
import nl.tno.willemsph.coins_navigator.se.model.RealisationPort;
import nl.tno.willemsph.coins_navigator.se.model.GetRequirement;
import nl.tno.willemsph.coins_navigator.se.model.GetSeObject;
import nl.tno.willemsph.coins_navigator.se.model.GetSystemInterface;
import nl.tno.willemsph.coins_navigator.se.model.GetSystemSlot;

@Service
public class SeService {
	public enum SeObjectType {
		SystemSlot, RealisationModule, Function, Performance, Requirement, SystemInterface, Hamburger, PortRealisation, RealisationPort, NumericProperty;

		GetSeObject create(SeService seService, int datasetId, String uri) throws URISyntaxException {
			switch (this) {
			case Function:
				return new GetFunction(seService, datasetId, uri);
			case Hamburger:
				return new GetHamburger(seService, datasetId, uri);
			case NumericProperty:
				return new NumericProperty(seService, datasetId, uri);
			case Performance:
				return new GetPerformance(seService, datasetId, uri);
			case PortRealisation:
				return new PortRealisation(seService, datasetId, uri);
			case RealisationModule:
				return new GetRealisationModule(seService, datasetId, uri);
			case RealisationPort:
				return new RealisationPort(seService, datasetId, uri);
			case Requirement:
				return new GetRequirement(seService, datasetId, uri);
			case SystemInterface:
				return new GetSystemInterface(seService, datasetId, uri);
			case SystemSlot:
				return new GetSystemSlot(seService, datasetId, uri);
			default:
				return null;
			}
		}

		public String getUri() {
			switch (this) {
			case NumericProperty:
				return EmbeddedServer.COINS2 + this.name();
			default:
				return EmbeddedServer.SE + this.name();
			}
		}
	}

	@Autowired
	public EmbeddedServer _embeddedServer;

	public List<GetSystemSlot> getAllSystemSlots(int datasetId) throws IOException, URISyntaxException {
		List<GetSeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.SystemSlot);

		List<GetSystemSlot> systemSlots = new ArrayList<>();
		for (GetSeObject seObject : seObjects) {
			systemSlots.add((GetSystemSlot) seObject);
		}
		return systemSlots;
	}

	public GetFunction getFunction(int datasetId, String localName) throws URISyntaxException, IOException {
		return (GetFunction) getSeObject(datasetId, localName, SeObjectType.Function);
	}

	public GetHamburger getHamburger(int datasetId, String hamburgerLocalName) throws URISyntaxException, IOException {
		return (GetHamburger) getSeObject(datasetId, hamburgerLocalName, SeObjectType.Hamburger);
	}

	public GetSystemSlot getSystemSlot(int datasetId, String localName) throws URISyntaxException, IOException {
		return (GetSystemSlot) getSeObject(datasetId, localName, SeObjectType.SystemSlot);
	}

	public GetSystemInterface getSystemInterface(int datasetId, String localName) throws URISyntaxException, IOException {
		return (GetSystemInterface) getSeObject(datasetId, localName, SeObjectType.SystemInterface);
	}

	public List<Dataset> getAllDatasets() throws URISyntaxException {
		return _embeddedServer.getDatasets();
	}

	public Dataset getDataset(int datasetId) throws URISyntaxException {
		return _embeddedServer.getDatasets().get(datasetId);
	}

	public File getDatasetFile(int datasetId, String filePath)
			throws URISyntaxException, FileNotFoundException, IOException {
		return _embeddedServer.getDatasets().get(datasetId).getModel(filePath);
	}

	public Dataset saveDataset(int datasetId) throws URISyntaxException, FileNotFoundException, IOException {
		Dataset dataset = _embeddedServer.getDatasets().get(datasetId);
		dataset.save();
		return dataset;
	}

	public GetFunction createFunction(int datasetId) throws URISyntaxException, IOException {
		return GetFunction.create(this, datasetId, generateUri(datasetId, SeObjectType.Function));
	}

	public GetHamburger createHamburger(int datasetId) throws URISyntaxException, IOException {
		return GetHamburger.create(this, datasetId, generateUri(datasetId, SeObjectType.Hamburger));
	}

	public GetFunction updateFunction(int datasetId, String functionLocalName, PutFunction putFunction)
			throws URISyntaxException, IOException {
		GetFunction getFunction = getFunction(datasetId, functionLocalName);
		getFunction.update(putFunction);
		return getFunction;
	}

	public GetRequirement updateRequirement(int datasetId, String localName, PutRequirement requirement)
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

	public GetPerformance updatePerformance(int datasetId, String performanceLocalName, PutPerformance putPerformance)
			throws URISyntaxException, IOException {
		GetPerformance getPerformance = getPerformance(datasetId, performanceLocalName);
		getPerformance.update(putPerformance);
		return getPerformance;
	}

	public GetHamburger updateHamburger(int datasetId, String hamburgerLocalName, PutHamburger putHamburger)
			throws URISyntaxException, IOException {
		GetHamburger getHamburger = getHamburger(datasetId, hamburgerLocalName);
		getHamburger.update(putHamburger);
		return getHamburger;
	}

	public GetRealisationModule createRealisationModule(int datasetId) throws URISyntaxException, IOException {
		return GetRealisationModule.create(this, datasetId, generateUri(datasetId, SeObjectType.RealisationModule));
	}

	public GetRealisationModule updateRealisationModule(int datasetId, String realisationModuleLocalName,
			PutRealisationModule putRealisationModule) throws URISyntaxException, IOException {
		GetRealisationModule realisationModule = getRealisationModule(datasetId, realisationModuleLocalName);
		realisationModule.update(putRealisationModule);
		return realisationModule;

		// URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		// update label
		// updateLabel(datasetUri, putRealisationModule.getUri(),
		// putRealisationModule.getLabel());
		// update assembly
		// updateAssembly(datasetUri, putRealisationModule.getUri(),
		// putRealisationModule.getAssembly());
		// update parts
		// deleteParts(datasetUri, putRealisationModule.getUri());
		// for (URI partUri : putRealisationModule.getParts()) {
		// insertPart(datasetUri, putRealisationModule.getUri(), partUri);
		// }
		// update performances
		// deletePerformances(datasetUri, putRealisationModule.getUri());
		// for (URI performanceUri : putRealisationModule.getPerformances()) {
		// insertPerformance(datasetUri, putRealisationModule.getUri(), performanceUri);
		// }
		// update realisation ports
		// deleteRealisationPorts(datasetUri, putRealisationModule.getUri());
		// for (URI realisationPortUri : putRealisationModule.getPorts()) {
		// insertRealisationPort(datasetUri, putRealisationModule.getUri(),
		// realisationPortUri);
		// }
		// return getRealisationModule(datasetId, realisationModuleLocalName);
	}

	public GetRealisationModule getRealisationModule(int datasetId, String realisationModuleUri)
			throws URISyntaxException, IOException {
		return (GetRealisationModule) getSeObject(datasetId, realisationModuleUri, SeObjectType.RealisationModule);
	}

	public GetRequirement createRequirement(int datasetId) throws URISyntaxException, IOException {
		return GetRequirement.create(this, datasetId, generateUri(datasetId, SeObjectType.Requirement));
	}

	public GetPerformance getPerformance(int datasetId, String functionUri) throws URISyntaxException, IOException {
		return (GetPerformance) getSeObject(datasetId, functionUri, SeObjectType.Performance);
	}

	public GetRequirement getRequirement(int datasetId, String functionUri) throws URISyntaxException, IOException {
		return (GetRequirement) getSeObject(datasetId, functionUri, SeObjectType.Requirement);
	}

	public GetSystemSlot createSystemSlot(int datasetId) throws URISyntaxException, IOException {
		return GetSystemSlot.create(this, datasetId, generateUri(datasetId, SeObjectType.SystemSlot));
	}

	public GetSystemSlot updateSystemSlot(int datasetId, String systemSlotLocalName, PutSystemSlot putSystemSlot)
			throws URISyntaxException, IOException {
		GetSystemSlot getSystemSlot = getSystemSlot(datasetId, systemSlotLocalName);
		
		URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		updateLabel(datasetUri, putSystemSlot.getUri(), putSystemSlot.getLabel());
		updateAssembly(datasetUri, putSystemSlot.getUri(), putSystemSlot.getAssembly());
		deleteParts(datasetUri, putSystemSlot.getUri());
		for (URI partUri : putSystemSlot.getParts()) {
			insertPart(datasetUri, putSystemSlot.getUri(), partUri);
		}
		deleteRequirements(datasetUri, putSystemSlot.getUri());
		for (URI requirementUri : putSystemSlot.getRequirements()) {
			insertRequirement(datasetUri, putSystemSlot.getUri(), requirementUri);
		}
		deleteFunctions(datasetUri, putSystemSlot.getUri());
		for (URI functionUri : putSystemSlot.getFunctions()) {
			insertFunction(datasetUri, putSystemSlot.getUri(), functionUri);
		}
		deleteInterfaces(datasetUri, putSystemSlot.getUri());
		for (URI interfaceUri : putSystemSlot.getInterfaces()) {
			insertInterface(datasetUri, putSystemSlot.getUri(), interfaceUri);
		}
		return getSystemSlot;
	}

	public GetSystemInterface updateSystemInterface(int datasetId, String systemInterfaceLocalName,
			PutSystemInterface putSystemInterface) throws URISyntaxException, IOException {
		GetSystemInterface getSystemInterface = getSystemInterface(datasetId, systemInterfaceLocalName);
		getSystemInterface.update(putSystemInterface);

		//
		// URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		// updateLabel(datasetUri, putSystemInterface.getUri(),
		// putSystemInterface.getLabel());
		// updateAssembly(datasetUri, putSystemInterface.getUri(),
		// putSystemInterface.getAssembly());
		// deleteParts(datasetUri, putSystemInterface.getUri());
		// for (URI partUri : putSystemInterface.getParts()) {
		// insertPart(datasetUri, putSystemInterface.getUri(), partUri);
		// }
		// deleteSystemSlots(datasetUri, putSystemInterface.getUri());
		// if (putSystemInterface.getSystemSlot0() != null) {
		// insertSystemSlot(datasetUri, putSystemInterface.getUri(),
		// putSystemInterface.getSystemSlot0());
		// }
		// if (putSystemInterface.getSystemSlot1() != null) {
		// insertSystemSlot(datasetUri, putSystemInterface.getUri(),
		// putSystemInterface.getSystemSlot1());
		// }
		// deleteRequirements(datasetUri, putSystemInterface.getUri());
		// for (URI requirementUri : putSystemInterface.getRequirements()) {
		// insertRequirement(datasetUri, putSystemInterface.getUri(), requirementUri);
		// }
		return getSystemInterface;
	}

	private void updateValue(URI datasetUri, URI subjectUri, URI value) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());
		queryStr.append("  DELETE { GRAPH ?graph { ?subject se:value ?value . }} ");
		if (value != null) {
			queryStr.setIri("the_value", value.toString());
			queryStr.append("  INSERT { GRAPH ?graph { ?subject se:value ?the_value . }} ");
		}
		queryStr.append("WHERE { GRAPH ?graph { OPTIONAL { ?subject se:value ?value . }} ");
		queryStr.append("}");

		_embeddedServer.update(queryStr);
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

	private void insertInputOfFunction(URI datasetUri, URI functionUri, URI systemInterfaceUri) throws IOException {
		if (systemInterfaceUri != null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
			queryStr.setIri("graph", datasetUri.toString());
			queryStr.setIri("function", functionUri.toString());
			queryStr.setIri("system_interface", systemInterfaceUri.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?function se:input ?system_interface . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			_embeddedServer.update(queryStr);
		}
	}

	private void insertOutputOfFunction(URI datasetUri, URI functionUri, URI systemInterfaceUri) throws IOException {
		if (systemInterfaceUri != null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
			queryStr.setIri("graph", datasetUri.toString());
			queryStr.setIri("function", functionUri.toString());
			queryStr.setIri("system_interface", systemInterfaceUri.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?function se:output ?system_interface . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			_embeddedServer.update(queryStr);
		}
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

	public List<GetFunction> getAllFunctions(int datasetId) throws IOException, URISyntaxException {
		List<GetSeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.Function);

		List<GetFunction> functions = new ArrayList<>();
		for (GetSeObject seObject : seObjects) {
			functions.add((GetFunction) seObject);
		}
		return functions;
	}

	public List<GetHamburger> getAllHamburgers(int datasetId) throws IOException, URISyntaxException {
		List<GetSeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.Hamburger);

		List<GetHamburger> hamburgers = new ArrayList<>();
		for (GetSeObject seObject : seObjects) {
			hamburgers.add((GetHamburger) seObject);
		}
		return hamburgers;
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

	public List<GetPerformance> getAllPerformances(int datasetId) throws IOException, URISyntaxException {
		List<GetSeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.Performance);

		List<GetPerformance> performances = new ArrayList<>();
		for (GetSeObject seObject : seObjects) {
			performances.add((GetPerformance) seObject);
		}
		return performances;
	}

	public GetPerformance createPerformance(int datasetId) throws URISyntaxException, IOException {
		return GetPerformance.create(this, datasetId, generateUri(datasetId, SeObjectType.Performance));
	}

	public List<GetRequirement> getAllRequirements(int datasetId) throws IOException, URISyntaxException {
		List<GetSeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.Requirement);

		List<GetRequirement> requirements = new ArrayList<>();
		for (GetSeObject seObject : seObjects) {
			requirements.add((GetRequirement) seObject);
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
			// NumericProperty numericProperty = new NumericProperty(seObjectUri, label,
			// typeUri, datatypeValue, unitUri);
			NumericProperty numericProperty = new NumericProperty(this, datasetId, seObjectUri);
			// numericProperty.setSeService(this);
			// numericProperty.setDatasetId(datasetId);
			numericProperties.add(numericProperty);
		}
		return numericProperties;
	}

	public List<GetSystemInterface> getAllSystemInterfaces(int datasetId) throws IOException, URISyntaxException {
		List<GetSeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.SystemInterface);

		List<GetSystemInterface> systemInterfaces = new ArrayList<>();
		for (GetSeObject seObject : seObjects) {
			systemInterfaces.add((GetSystemInterface) seObject);
		}
		return systemInterfaces;
	}

	public GetSystemInterface createSystemInterface(int datasetId) throws URISyntaxException, IOException {
		return GetSystemInterface.create(this, datasetId, generateUri(datasetId, SeObjectType.SystemInterface));
	}

	public List<GetRealisationModule> getAllRealisationModules(int datasetId) throws IOException, URISyntaxException {
		List<GetSeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.RealisationModule);

		List<GetRealisationModule> realisationModules = new ArrayList<>();
		for (GetSeObject seObject : seObjects) {
			realisationModules.add((GetRealisationModule) seObject);
		}
		return realisationModules;
	}

	public List<RealisationPort> getAllRealisationPorts(int datasetId) throws IOException, URISyntaxException {
		List<GetSeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.RealisationPort);

		List<RealisationPort> realisationPorts = new ArrayList<>();
		for (GetSeObject seObject : seObjects) {
			realisationPorts.add((RealisationPort) seObject);
		}
		return realisationPorts;
	}

	public List<GetHamburger> getHamburgersForSystemSlot(int datasetId, String systemSlotLocalName)
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

		List<GetHamburger> hamburgers = new ArrayList<>();
		for (String hamburgerUri : hamburgerMap.keySet()) {
			String hamburgerLocalName = getLocalName(hamburgerUri);
			GetHamburger hamburger = (GetHamburger) getSeObject(datasetId, hamburgerLocalName, SeObjectType.Hamburger);
			// hamburger.setFunctionalUnit(new URI(systemSlotUri));
			// String technicalSolutionUri = hamburgerMap.get(hamburgerUri);
			// if (technicalSolutionUri != null)
			// hamburger.setTechnicalSolution(new URI(technicalSolutionUri));
			hamburgers.add(hamburger);
		}

		return hamburgers;
	}

	public List<GetHamburger> getHamburgersForRealisationModule(int datasetId, String realisationModuleLocalName)
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

		List<GetHamburger> hamburgers = new ArrayList<>();
		for (String hamburgerUri : hamburgerMap.keySet()) {
			String hamburgerLocalName = getLocalName(hamburgerUri);
			GetHamburger hamburger = (GetHamburger) getSeObject(datasetId, hamburgerLocalName, SeObjectType.Hamburger);
			// hamburger.setTechnicalSolution(new URI(realisationModuleUri));
			// String functionalUnitUri = hamburgerMap.get(hamburgerUri);
			// if (functionalUnitUri != null)
			// hamburger.setFunctionalUnit(new URI(functionalUnitUri));
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
			ports.add(port);
		}
		return ports;
	}

	private List<GetSeObject> getAllSeObjects(int datasetId, SeObjectType seObjectType)
			throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("SeObject", seObjectType.getUri());
		queryStr.append("SELECT ?se_object ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?se_object rdf:type ?SeObject . ");
		queryStr.append("  }");
		queryStr.append("}");

		List<GetSeObject> seObjects = new ArrayList<>();
		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			String seObjectUri = node.get("se_object").get("value").asText();
			GetSeObject seObject = seObjectType.create(this, datasetId, seObjectUri);
			seObjects.add(seObject);
		}

		return seObjects;
	}

	public GetSeObject getSeObject(int datasetId, String localName, SeObjectType seObjectType)
			throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String ontologyUri = getOntologyUri(datasetId);
		String seObjectUri = ontologyUri + "#" + localName;
		// ParameterizedSparqlString queryStr = new
		// ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		// queryStr.setIri("graph", datasetUri);
		// queryStr.setIri("se_object", seObjectUri);
		// queryStr.setIri("SeObject", seObjectType.getUri());
		// queryStr.append("SELECT ?label ?assembly ");
		// queryStr.append("{");
		// queryStr.append(" GRAPH ?graph { ");
		// queryStr.append(" ?se_object rdf:type ?SeObject .");
		// queryStr.append(" OPTIONAL {");
		// queryStr.append(" ?se_object rdfs:label ?label . ");
		// queryStr.append(" }");
		// queryStr.append(" OPTIONAL {");
		// queryStr.append(" ?contains coins2:hasPart ?se_object . ");
		// queryStr.append(" ?assembly coins2:hasContainsRelation ?contains . ");
		// queryStr.append(" }");
		// queryStr.append(" OPTIONAL {");
		// queryStr.append(" ?contains coins2:hasPart ?se_object . ");
		// queryStr.append(" ?contains coins2:hasAssembly ?assembly . ");
		// queryStr.append(" }");
		// queryStr.append(" OPTIONAL {");
		// queryStr.append(" ?assembly coins2:hasContainsRelation ?contains . ");
		// queryStr.append(" ?se_object coins2:partOf ?contains . ");
		// queryStr.append(" }");
		// queryStr.append(" OPTIONAL {");
		// queryStr.append(" ?contains coins2:hasAssembly ?assembly . ");
		// queryStr.append(" ?se_object coins2:partOf ?contains . ");
		// queryStr.append(" }");
		// queryStr.append(" }");
		// queryStr.append("}");
		//
		// JsonNode responseNodes = _embeddedServer.query(queryStr);
		GetSeObject seObject = seObjectType.create(this, datasetId, seObjectUri);
		;
		// for (JsonNode node : responseNodes) {
		// JsonNode labelNode = node.get("label");
		// String label = labelNode != null ? labelNode.get("value").asText() : null;
		// JsonNode assemblyNode = node.get("assembly");
		// String assembly = assemblyNode != null ?
		// node.get("assembly").get("value").asText() : null;
		// List<String> seObjectParts = getSeObjectParts(datasetId,
		// getLocalName(seObjectUri), seObjectType);
		// // seObject = seObjectType.create(seObjectUri, label, assembly,
		// seObjectParts);
		// seObject = seObjectType.create(this, datasetId, seObjectUri);
		// // seObject.setSeService(this);
		// // seObject.setDatasetId(datasetId);
		// }

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

	private String generateUri(int datasetId, SeObjectType seObjectType) throws URISyntaxException {
		String localName = seObjectType.name() + "_" + UUID.randomUUID().toString();
		return getOntologyUri(datasetId) + "#" + localName;
	}

	public String getOntologyUri(int datasetId) throws URISyntaxException {
		return _embeddedServer.getDatasets().get(datasetId).getOntologyUri().toString();
	}

	public String getLocalName(String uri) {
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
			portRealisations.add(portRealisation);
		}
		return portRealisations;
	}

}
