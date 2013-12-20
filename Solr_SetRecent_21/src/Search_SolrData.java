import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.ConnectException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Properties;
import org.apache.solr.client.solrj.SolrQuery;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;

@SuppressWarnings("unused")
public class Search_SolrData {

	public static String dbip = "", dbname = "", dbpass = "", dbuser = "",
			table1 = "", table2 = "", table3 = "", udate = "", rows, status1,
			sub, index, delete, start, end;
	static Connection conn = null;
	static PreparedStatement pstmt = null, pstmt1 = null;
	static String subject, link, published_date, created_by, comment, body,
			is_mapped, is_searched, tagged_body, tagged_subject, content;
	static int hits = 1, url_id, oid, status;
	static Date downloaded_on;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static String date = sdf.format(new java.util.Date());
	// static String date = "11-11-2013";
	static SolrDataDAO dataDao;

	public static void main(String[] args) throws FileNotFoundException {

		try {
			dataDao = new SolrDataDAO();
			long startTime = System.currentTimeMillis();

			getProperties();

			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection("jdbc:sqlserver://" + dbip
					+ ";user=" + dbuser + ";password=" + dbpass
					+ ";DatabaseName=" + dbname + ";");

			String oid_qry = "select max(id) from " + table2;

			Statement stmt = conn.createStatement();

			ResultSet rss = stmt.executeQuery(oid_qry);

			try {
				if (rss.next()) {
					oid = rss.getInt(1);
				}
			} catch (Exception excp) {
				excp.printStackTrace();
			}
			oid++;
			// testAddData(conn);

			if (index.equals("true")) {
				Statement s = conn.createStatement();
				String creat_table = "create table "
						+ table3
						+ " ([url_id] [int] NULL,[subject] [varchar](700) NULL,[link] [nvarchar](max) NULL,[published_date] [varchar](200) NULL,[created_by] [varchar](max) NULL,[comment] [nvarchar](100) NULL,[body] [nvarchar](max) NULL,[downloaded_on] [datetime] NULL,[is_mapped] [bit] NULL,[is_searched] [bit] NULL,[tagged_body] [nvarchar](max) NULL,[tagged_subject] [nvarchar](max) NULL,[Content] [varchar](max) NULL,[Status] [int] NULL,[oid_temp] [bigint] IDENTITY("
						+ oid + ",1) NOT NULL,[hash] [int] NULL) ON [PRIMARY]";

				Statement st = conn.createStatement();
				try {
					s.executeUpdate(creat_table);
					System.out.println("Table Created...");

				} catch (Exception e) {
					//e.printStackTrace();
					System.out.println("Table Exists...");
				}

				ResultSet rs = st.executeQuery("select * from " + table1
						+ " where id between " + start + " and " + end
						+ " and subject!=''");

				while (rs.next()) {

					url_id = rs.getInt(2);
					subject = rs.getString(3).toString();
					link = rs.getString(4);
					published_date = rs.getString(5);
					created_by = rs.getString(6);
					comment = rs.getString(7);
					body = rs.getString(8);
					downloaded_on = rs.getDate(9);
					is_mapped = rs.getString(10);
					is_searched = rs.getString(11);
					tagged_body = rs.getString(12);
					tagged_subject = rs.getString(13);
					status = rs.getInt(15);

					testQueryData(url_id, subject, link, published_date,
							created_by, comment, body, downloaded_on,
							is_mapped, is_searched, tagged_body,
							tagged_subject, status, conn);
				}
			}

			if ((delete.equals("true"))) {
				if (!(udate.equals(null)) && !(udate.equals(""))) {
					testDeleteData(udate);
				} else {
					System.out.println("Check the date format...\n");
				}
			}

			if (status1.equals("true")) {
				if (!(rows.equals(null)) && !(rows.equals(""))
						|| !(sub.equals(null)) && !(sub.equals(""))) {
					displayAllData(rows, sub);
				} else {
					System.out.println("Row and Subject is Empty...");
				}
			}

			// testQueryData();
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			long mn = (totalTime / 1000);
			SolrQuery q = new SolrQuery("*:*");
			q.setRows(0);
			System.out.println(" Total Files : "
					+ dataDao.server.query(q).getResults().getNumFound());
			System.out.println("Total time taken : " + mn + " seconds");

		} catch (SolrException solrEx) {

			System.out.println("Exception :" + solrEx.getMessage());
			solrEx.printStackTrace();

		} catch (ConnectException cexp) {
			System.out
					.println("Solr Connection Failed (Make sure solr is up and running): "
							+ cexp.getMessage());

			cexp.printStackTrace();
		} catch (Exception ex) {
			System.out.println("Generic Exception : " + ex.getMessage());

			ex.printStackTrace();
		}
	}

	public static void getProperties() throws FileNotFoundException {

		Properties props = new Properties();
		Reader read = (Reader) new FileReader(new File(
				"solr_searching.properties"));
		try {
			props.load(read);
			dbip = props.getProperty("databeseServer");
			dbuser = props.getProperty("user");
			dbpass = props.getProperty("password");
			dbname = props.getProperty("dataBaseName");
			table1 = props.getProperty("in_table");
			table2 = props.getProperty("main_table");
			table3 = props.getProperty("out_table");
			udate = props.getProperty("date");
			rows = props.getProperty("rows");
			status1 = props.getProperty("search");
			sub = props.getProperty("subject");
			index = props.getProperty("index");
			delete = props.getProperty("delete");
			start = props.getProperty("start");
			end = props.getProperty("end");

			read.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public static void testAddData(Connection conn) throws Exception {
	//
	// String sql = "select id,subject from " + table1
	// + " where id between 1 and 500 and subject!=''";// +table1;
	//
	// // String sql =
	// //"select id,subject from recent_feedsnew_oct28 where id between 10000 and 11000 order by id";
	// // // where id between
	// // 23877748 and
	// String oid_qry1 = "select max(id) from " + table2; // 23977748";
	//
	// Statement stmt1 = conn.createStatement();
	//
	// ResultSet rs1 = stmt1.executeQuery(oid_qry1);
	//
	// try {
	// if (rs1.next()) {
	// oid = rs1.getInt(1);
	// }
	// } catch (Exception excp) {
	// excp.printStackTrace();
	// }
	//
	// Statement stmt = conn.createStatement();
	// ResultSet rs = stmt.executeQuery(sql);
	//
	// try {
	// System.out.println("\nIndexing Started... ");
	//
	// while (rs.next()) {
	// // int uid = rs.getInt("id");
	// // System.out.println(oid);
	// // Thread.sleep(500);
	// int i = rs.getInt(1);
	// String s = rs.getString(2);
	// //String download = rs.getString(3);
	// System.out.println("id :" + i);
	// System.out.println("Subject :" + rs.getString(2).toString());
	// dataDao.addData(oid,i,s,date);
	// // dataDao.addData(oid, i, s, download);
	// oid++;
	// }
	//
	// dataDao.server.commit();
	// dataDao.server.optimize();
	// System.out.println("Data Indexed Successfully");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// rs.close();
	// stmt.close();
	// }
	// }

	public static void testQueryData(int url_id, String subject, String link,
			String published_date, String created_by, String comment,
			String body, Date downloaded_on, String is_mapped,
			String is_searched, String tagged_body, String tagged_subject,
			int status, Connection co) throws Exception {

		try {

			SolrDataDAO dataDao = new SolrDataDAO();
			QueryResponse resp = dataDao.queryData(0, 1, subject);
			SolrDocumentList data = resp.getResults();
			System.out.println("len " + data.size());
			System.out.println();

			if (data.size() > 0) {
				for (Iterator<SolrDocument> iterator = data.iterator(); iterator
						.hasNext();) {
					SolrDocument solrDocument = (SolrDocument) iterator.next();
					String solrsub = (String) solrDocument
							.getFieldValue("subject");
					System.out.println("Searching :" + solrsub);

					if (solrsub.equals(subject)) {
						System.out.println("Matched :" + subject);
						// String s = "delete from " + table2 +
						// " where subject=?";
						// pstmt = co.prepareStatement(s);
						// pstmt.setString(1, subject);
						// pstmt.execute();
						// }
						//
						// }

					} else {

						try {

							try {
								oid++;

								dataDao.addData1(oid, url_id, subject, date);
								// dataDao.server.commit();
								// dataDao.server.optimize();
							} catch (Exception add) {
								add.printStackTrace();
							}

							String rss_insert = "INSERT INTO "
									+ table3
									+ " (url_id, subject,link,published_date,created_by,comment,body,downloaded_on,is_mapped,is_searched,tagged_body,tagged_subject,status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
							pstmt = co.prepareStatement(rss_insert);
							// pstmt1 = co.prepareStatement(insert_oid);
							// pstmt.setString(1, body);
							// pstmt.setString(2, comment);
							// pstmt.setString(3, content);
							// pstmt.setString(4, created_by);
							// pstmt.setDate(5, (java.sql.Date) downloaded_on);
							// pstmt.setString(6, is_mapped);
							// pstmt.setString(7, is_searched);
							// pstmt.setString(8, link);
							// pstmt.setString(9, published_date);
							// pstmt.setInt(10, status);
							// pstmt.setString(11, subject);
							// pstmt.setString(12, tagged_body);
							// pstmt.setString(13, tagged_subject);
							// pstmt.setInt(14, url_id);

							// pstmt1 = co.prepareStatement(insert_oid);

							// url_id = rs.getInt(1);
							// subject = rs.getString(2).toString();
							// link = rs.getString(4);
							// published_date = rs.getString(5);
							// created_by = rs.getString(6);
							// comment = rs.getString(7);
							// body = rs.getString(8);
							// downloaded_on = rs.getDate(9);
							// is_mapped = rs.getString(10);
							// is_searched = rs.getString(11);
							// tagged_body = rs.getString(12);
							// tagged_subject = rs.getString(13);
							// status = rs.getInt(15);

							pstmt.setInt(1, url_id);
							pstmt.setString(2, subject);
							// pstmt.setString(3, content);
							pstmt.setString(3, link);
							pstmt.setString(4, published_date);
							pstmt.setString(5, created_by);
							pstmt.setString(6, comment);
							pstmt.setString(7, body);
							pstmt.setDate(8, (java.sql.Date) downloaded_on);
							pstmt.setString(9, is_mapped);
							pstmt.setString(10, is_searched);
							pstmt.setString(11, tagged_body);
							pstmt.setString(12, tagged_subject);
							pstmt.setInt(13, status);

							pstmt.execute();

						} catch (Exception s) {
							s.printStackTrace();
						}

						pstmt.close();
						// pstmt1.close();
					}
				}
			} else {

				try {

					try {
						oid++;

						dataDao.addData1(oid, url_id, subject, date);

					} catch (Exception add) {
						add.printStackTrace();
					}

					String rss_insert = "INSERT INTO "
							+ table3
							+ " (url_id, subject,link,published_date,created_by,comment,body,downloaded_on,is_mapped,is_searched,tagged_body,tagged_subject,status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
					pstmt = co.prepareStatement(rss_insert);

					pstmt.setInt(1, url_id);
					pstmt.setString(2, subject);
					// pstmt.setString(3, content);
					pstmt.setString(3, link);
					pstmt.setString(4, published_date);
					pstmt.setString(5, created_by);
					pstmt.setString(6, comment);
					pstmt.setString(7, body);
					pstmt.setDate(8, (java.sql.Date) downloaded_on);
					pstmt.setString(9, is_mapped);
					pstmt.setString(10, is_searched);
					pstmt.setString(11, tagged_body);
					pstmt.setString(12, tagged_subject);
					pstmt.setInt(13, status);

					pstmt.execute();

				} catch (Exception s) {
					s.printStackTrace();
				}

				pstmt.close();
				// pstmt1.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testDeleteData(String d) throws Exception {

		SolrDataDAO dataDao = new SolrDataDAO();
		dataDao.deleteData_date(d);

		System.out.println("All Indexing Deleted..");

	}

	public static void displayAllData(String rows1, String sub1)
			throws Exception {
		if (!(rows.equals(null)) && !(rows.equals(""))) {
			int row = Integer.parseInt(rows1);
			SolrDataDAO dataDao = new SolrDataDAO();
			QueryResponse resp = dataDao.queryDisplay(0, row, "*:*");
			SolrDocumentList data = resp.getResults();
			// System.out.println("lenth " + data.size());
			// Iterate through solr response
			System.out.println("\nDisplaying indexed data.. \n");

			for (Iterator<SolrDocument> iterator = data.iterator(); iterator
					.hasNext();) {
				SolrDocument solrDocument = (SolrDocument) iterator.next();
				System.out.println("Id " + solrDocument.getFieldValue("id"));
				System.out.println("Subject : "
						+ solrDocument.getFieldValue("name"));
				System.out.println("Date : "
						+ solrDocument.getFieldValue("comments"));
				System.out.println("URL_ID : "
						+ solrDocument.getFieldValue("links") + "\n");

			}
		} else {
			int row = 100;
			// SolrDataDAO dataDao = new SolrDataDAO();
			QueryResponse resp = dataDao.queryDisplay(0, row, sub1);
			SolrDocumentList data = resp.getResults();
			// System.out.println("lenth " + data.size());
			// Iterate through solr response
			System.out.println("\nDisplaying indexed data.. \n");

			for (Iterator<SolrDocument> iterator = data.iterator(); iterator
					.hasNext();) {
				SolrDocument solrDocument = (SolrDocument) iterator.next();
				System.out.println("Id " + solrDocument.getFieldValue("id"));
				System.out.println("Subject : "
						+ solrDocument.getFieldValue("name"));
				System.out.println("Date : "
						+ solrDocument.getFieldValue("comments"));
				System.out.println("URL_ID : "
						+ solrDocument.getFieldValue("links") + "\n");

			}
		}
	}
}