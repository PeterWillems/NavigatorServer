package nl.tno.willemsph.coins_navigator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class CoinsService {
	/**
	 * Get a HTTP connection and initialize with a POST request and sparql-query
	 * content type.
	 * 
	 * @return HttpURLConnection
	 * @throws IOException
	 */
	protected HttpURLConnection getConnection() throws IOException {
		URL obj = new URL(EmbeddedServer.QUERY_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/sparql-query");
		return con;
	}
	
	/**
	 * Send sparql query to the embedded fuseki server and return the result.
	 * 
	 * @param con
	 *            HttpURLConnection
	 * @param query
	 *            Sparql query text
	 * @return Result JSON node
	 * @throws IOException
	 */
	protected JsonNode send(HttpURLConnection con, String query) throws IOException {
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
}
