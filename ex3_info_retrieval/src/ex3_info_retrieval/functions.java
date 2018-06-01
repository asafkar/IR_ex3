package ex3_info_retrieval;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class functions {
	public static void get_all_queries() throws IOException {
		File dir = new File(".");
		File query_file = new File(dir.getCanonicalPath() + File.separator + "/Files/queries.txt");
		BufferedReader br = new BufferedReader(new FileReader(query_file));
		 
		String line = "";
		String current_query= "";
		Boolean eof=false;
		
		br.readLine();
		while (true) {
			while (line.startsWith("*FIND")==false) {
				current_query +=line;
				line = br.readLine();
				if (line==null) {
					eof=true;
					break;
				};
			};
//			System.out.println("current query:");
//			System.out.println(current_query);	
			Main.queries_list.add(current_query);
			current_query="";
			if (line==null) {
				break;
			};
			line="";
		}
		br.close();
	} //get query
	

	// TODO - add parameters file
	
	
}
