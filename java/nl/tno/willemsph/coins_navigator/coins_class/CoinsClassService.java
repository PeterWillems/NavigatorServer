package nl.tno.willemsph.coins_navigator.coins_class;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.CoinsService;

@Service
public class CoinsClassService extends CoinsService {

	private PrefixMapping prefixMapping;

	private PrefixMapping getPrefixMapping() {
		if (prefixMapping == null) {
			prefixMapping = PrefixMapping.Factory.create();
			prefixMapping.setNsPrefix("rdf", RDF.uri);
			prefixMapping.setNsPrefix("rdfs",RDFS.uri);
			prefixMapping.setNsPrefix("owl", OWL.NS);
			prefixMapping.setNsPrefix("xml", "http://www.w3.org/XML/1998/namespace/");
			prefixMapping.setNsPrefix("cbim2", "http://www.coinsweb.nl/cbim-2.0.rdf#");
		}
		return prefixMapping;
	}

	public List<CoinsClass> getAllCoinsClasses() throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getPrefixMapping());
		queryStr.append("SELECT DISTINCT ?class ?classname ?is_abstract");
		queryStr.append("{");
		queryStr.append("  ?class rdf:type owl:Class ;");
		queryStr.append("  cbim2:isClassAbstract ?is_abstract ;");
		queryStr.append("  rdfs:label ?classname .");
		queryStr.append("  FILTER ( lang(?classname) = \"en-GB\" ) .");
		queryStr.append("}");
		queryStr.append("ORDER BY ?classname");
		String query = queryStr.toString();

		List<CoinsClass> allCoinsClassesList = new ArrayList<>();
		HttpURLConnection con = getConnection();
		JsonNode bindings = send(con, query);
		Iterator<JsonNode> elements = bindings.elements();
		while (elements.hasNext()) {
			JsonNode nextElement = elements.next();
			JsonNode classNameValue = nextElement.get("classname").get("value");
			JsonNode isAbstractValue = nextElement.get("is_abstract").get("value");
			allCoinsClassesList.add(new CoinsClass(classNameValue.asText(), isAbstractValue.asBoolean()));
			System.out.println(classNameValue.asText());
		}
		return allCoinsClassesList;
	}

	public List<CoinsProperty> getCoinsClassProperties(String name) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getPrefixMapping());
		queryStr.setLiteral("classname", name, "en-GB");
		queryStr.append("SELECT DISTINCT ?property ?property_name ?range_uri");
		queryStr.append("{");
		queryStr.append("  ?class rdf:type owl:Class ;");
		queryStr.append("  rdfs:subClassOf+ ?superclass ;");
		queryStr.append("  rdfs:label ?classname .");
		queryStr.append("  ?property rdfs:label ?property_name FILTER ( lang(?property_name) = \"en-GB\" ) .");
		queryStr.append("  ?property rdfs:range ?range_uri .");
		queryStr.append("  {?property rdfs:domain ?class} UNION {?property rdfs:domain ?superclass} .");
		queryStr.append("}");
		queryStr.append("ORDER BY ?property_name");
		String query = queryStr.toString();

		List<CoinsProperty> allCoinsPropertiesList = new ArrayList<>();
		HttpURLConnection con = getConnection();
		JsonNode bindings = send(con, query);
		Iterator<JsonNode> elements = bindings.elements();
		while (elements.hasNext()) {
			JsonNode next = elements.next();
			JsonNode propertyNameValue = next.get("property_name").get("value");
			JsonNode propertyRangeUri = next.get("range_uri").get("value");
			allCoinsPropertiesList.add(new CoinsProperty(propertyNameValue.asText(), propertyRangeUri.asText()));
			System.out.println(propertyNameValue.asText());
		}
		return allCoinsPropertiesList;
	}

}
