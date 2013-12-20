import java.util.UUID;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;

public class SolrDataDAO extends SolrBaseDAO {

	public HttpSolrServer server = null;

	public SolrDataDAO() throws Exception {
		server = getSolrConnection();
	}

	public QueryResponse queryData(int start, int rows, String query)
			throws Exception {
		ModifiableSolrParams solrParams = new ModifiableSolrParams();
		query = Clean1(query);
		String q="\"";
		query=q+query+q;
		System.out.println("Checking "+query);
		solrParams.set("q", query);
		solrParams.set("start", start);
		solrParams.set("rows", rows);

		// solrParams.set("facet", facet); // Facets if required
		return server.query(solrParams);
	}

	public QueryResponse queryData1(int start, int rows, String query)
			throws Exception {
		ModifiableSolrParams solrParams = new ModifiableSolrParams();
		solrParams.set("q", query);
		// solrParams.set("q", query);
		solrParams.set("start", start);
		solrParams.set("rows", rows);

		// solrParams.set("facet", facet); // Facets if required
		return server.query(solrParams);
	}

	public QueryResponse queryDisplay(int start, int rows, String query)
			throws Exception {

		ModifiableSolrParams solrParams = new ModifiableSolrParams();
		// solrParams.set("q", escaping(query));
		solrParams.set("q", query);
		solrParams.set("start", start);
		solrParams.set("rows", rows);
		// solrParams.set("defType", "edismax");
		// solrParams.set("facet", facet); // Facets if required
		return server.query(solrParams);
	}

	public void addData(int id, int uid, String name, String date)
			throws Exception {

		System.out.println("Add to index: OID = " + id + " Subject = " + name);
		// name = Clean1(name);
		// System.out.println("ASCII " + name);

		// Populate solr document
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", id);
		doc.addField("subject", name);
		name = Clean1(name);
		// System.out.println("ASCII " + name);
		doc.addField("ascii_value", name);
		doc.addField("download_date", date);
		// doc.addField("url_id", uid);

		server.add(doc);
		// server.commit();
		// server.optimize();
		// System.out.println("Data committed Successfully!");

	}

	public void addData1(int id, int uid, String name, String date)
			throws Exception {

		System.out.println("Add to index: OID = " + id + " Subject = " + name);
		// name = Clean1(name);
		// System.out.println("ASCII " + name);

		// Populate solr document
		UUID uuid = UUID.randomUUID();
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", uuid);
		doc.addField("subject", name);
		name = Clean1(name);
		System.out.println("ASCII " + name);
		doc.addField("ascii_value", name);
		doc.addField("download_date", date);
		// doc.addField("url_id", uid);

		server.add(doc);
		//server.commit();
		// server.optimize();
		// System.out.println("Data committed Successfully!");

	}

	public static String Clean1(String s) {

		// for (int i = 0; i < s.length(); i++) {
		// String text = s.substring(i, i + 1);
		// String decomposed = Normalizer.normalize(text, Form.NFD);
		// s = decomposed.replaceAll(
		// "\\p{InCombiningDiacriticalMarks}+", "");
		// }
		String ascii = "";
		for (int x = 0; x < s.length(); x++) {
			ascii += ((int) s.charAt(x) + "=");
		}
		return ascii;

	}

	// public static String escaping(String s) {
	// StringBuilder sb = new StringBuilder();
	// for (int i = 0; i < s.length(); i++) {
	// char c = s.charAt(i);
	// // These characters are part of the query syntax and must be escaped
	// if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '('
	// || c == ')' || c == ':' || c == '^' || c == '[' || c == ']'
	// || c == '\"' || c == '{' || c == '}' || c == '~'
	// || c == '*' || c == '?' || c == '|' || c == '&' || c == ';') {
	// sb.append('\\');
	// }
	// /*
	// * if(Character.isWhitespace(c)) { sb.append(" \\ "); }
	// */
	// sb.append(c);
	// }
	// return sb.toString();
	// }

	public void deleteAllData() throws Exception {
		// server.deleteByQuery("id:" + id);
		server.deleteByQuery("*:*");
		// server.deleteByQuery("comments:15-10-2013");
		server.commit();
		server.optimize();
		System.out.println("Data committed Successfully!");

	}

	public void deleteData_date(String date) throws Exception {
		// server.deleteByQuery("id:" + id);
		// server.deleteByQuery("*:*");
		server.deleteByQuery("download_date:" + date);
		server.commit();
		server.optimize();
		System.out.println("Data committed Successfully!");

	}
}