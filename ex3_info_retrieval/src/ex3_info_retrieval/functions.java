package ex3_info_retrieval;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.document.Document;

public class functions {

	
	public static void get_query() throws IOException {
		File dir = new File(".");
		File query_file = new File(dir.getCanonicalPath() + File.separator + "/Files/queries.txt");
		 
		BufferedReader br = new BufferedReader(new FileReader(query_file));
		 
		String line = null;
		String current_query= null;
		
		line = br.readLine();
		if (line!=null) {
			if (line.startsWith("*FIND")) {
				br.readLine();
				current_query = br.readLine();
				br.readLine();
			};
			System.out.println("current query:");
			System.out.println(current_query);
		};
		
		br.close();
	} //get query
	
	public static void read_documents() {
		  Document doc = new Document();
		  doc.add(new TextField("title", title, Field.Store.YES));
		  doc.add(new StringField("isbn", isbn, Field.Store.YES));
		  w.addDocument(doc);
		  
	}
	
}
