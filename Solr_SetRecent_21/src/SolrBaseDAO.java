import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.core.CoreContainer;

public class SolrBaseDAO {

	public HttpSolrServer getSolrConnection() throws Exception {
		HttpSolrServer solrServer = null;
		try {
			if (solrServer == null) {

				solrServer = new HttpSolrServer("http://10.10.1.72:8080/solr");
				// solrServer = new HttpSolrServer(
				// "http://10.10.1.21:8080/solr");
				solrServer.setParser(new XMLResponseParser());
				//solrServer.setSoTimeout(5000000);
			//	solrServer.setConnectionTimeout(5000000);

				// Other commonly used properties
				// solrServer.setDefaultMaxConnectionsPerHost(100);
				// solrServer.setMaxTotalConnections(100);
				// solrServer.setFollowRedirects(false); // defaults to false
				// // allowCompression defaults to false.
				// // Server side must support gzip or deflate for this to have
				// any effect.
				// solrServer.setAllowCompression(true);
				// solrServer.setMaxRetries(1); // defaults to 0. > 1 not
				// recommended.
			}

		} catch (Exception exc) {
			System.out.println(" Exception in getting Solr Connection: "
					+ exc.getMessage());

			exc.printStackTrace();
		}
		return solrServer;
	}

	public EmbeddedSolrServer getEmbeddedSolrConnection() {
		EmbeddedSolrServer server = null;
		try {

			if (server == null) {
				// If not already set in Tomcat Catalina.sh or eclipse JVM
				// arguments
				// explicilty set this property
				System.setProperty("solr.solr.home",
						"/home/developer/solr/example/solr");
				CoreContainer.Initializer initializer = new CoreContainer.Initializer();
				CoreContainer coreContainer = initializer.initialize();
				server = new EmbeddedSolrServer(coreContainer, "");
			}

		} catch (Exception ex) {
			System.out.println(" Exception in getting Solr Connection: "
					+ ex.getMessage());

			ex.printStackTrace();
		}

		return server;
	}
}