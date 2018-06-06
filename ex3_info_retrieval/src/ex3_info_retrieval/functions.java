package ex3_info_retrieval;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class functions {
	
	public static void read_params_file(String param_file_name) throws IOException {
		File dir = new File(".");
		File params_file = new File(dir.getCanonicalPath() + File.separator + param_file_name);
		BufferedReader br = new BufferedReader(new FileReader(params_file));
		
		Config.queryFile = br.readLine().split("=")[1];
		Config.docsFile= br.readLine().split("=")[1];
		Config.outputFile= br.readLine().split("=")[1];
		Config.retrievalAlgorithm= br.readLine().split("=")[1];
		
		br.close();
		
		if (Config.retrievalAlgorithm.startsWith("improved")) {
			Config.improved_algo=true;
			 System.out.println("running imporved algo");
		};
		
		//delete old output file
		File output_file = new File(dir.getCanonicalPath() + File.separator + Config.outputFile);
		output_file.delete();
	}
	
	
	public static void get_all_queries() throws IOException {
		File dir = new File(".");
		File query_file = new File(dir.getCanonicalPath() + File.separator + Config.queryFile);
		BufferedReader br = new BufferedReader(new FileReader(query_file));
		 
		String line = "";
		String current_query= "";
		
		br.readLine();
		while (true) {
			while (line.startsWith("*FIND")==false) {
				current_query +=line;
				line = br.readLine();
				if (line==null) {
					break;
				};
			};
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
