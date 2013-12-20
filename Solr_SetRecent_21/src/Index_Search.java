
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

@SuppressWarnings("unused")
public class Index_Search {
	//static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	//static String date = sdf.format(new java.util.Date());
    public static void main(String args[]) throws Exception {
    	String date="2013-12-17";
       // testDeleteData();
        testQueryData();
       // testDeleteData(date);
//         String s="~`!@#$%^&*()_-+=";
//         String s1="Œ";
//          
//         for (int x = 0; x < s.length(); x++) { 
//              System.out.print( (int) s.charAt(x) + ""); 
//         }         System.out.println();
//
//         for (int x = 0; x < s1.length(); x++) { 
//             System.out.print( (int) s1.charAt(x) + " "); 
//        } 
//         System.out.println();
    }

    public static void testDeleteData() throws Exception {

        // logger.info("Delete records for ID = 1");
        SolrDataDAO dataDao = new SolrDataDAO();
        dataDao.deleteAllData();
        System.out.println("All Indexing Deleted..");

    }

    public static void testDeleteData(String d) throws Exception {

		SolrDataDAO dataDao = new SolrDataDAO();
		dataDao.deleteData_date(d);

		// deleteAllData function will delete all indexed data
		// dataDao.deleteAllData();
		System.out.println("All Indexing Deleted..");

	}
    public static void testQueryData() throws Exception {

        SolrDataDAO dataDao = new SolrDataDAO();
        QueryResponse resp = dataDao.queryDisplay(0, 10, "*:*");
        SolrDocumentList data = resp.getResults();
        System.out.println("lenth " + data.size());
        // Iterate through solr response
        for (Iterator<SolrDocument> iterator = data.iterator(); iterator
                .hasNext();) {
            SolrDocument solrDocument = (SolrDocument) iterator.next();
            System.out.println("Id " + solrDocument.getFieldValue("id"));
        	String id=(String) solrDocument.getFieldValue("id");
            System.out.println("ID in String : "+id);

            System.out.println("ASCII : " + solrDocument.getFieldValue("ascii_value"));
            System.out.println("Subject : " + solrDocument.getFieldValue("subject"));

            System.out.println("Date : " + solrDocument.getFieldValue("download_date"));

        }
      //  dataDao.server.commit();
       // dataDao.server.optimize();
        System.out.println("Commited");
        // logger.info("Exiting Query Test data.");

    }
}
