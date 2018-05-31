package ex3_info_retrieval;

import ex3_info_retrieval.functions;
import ex3_info_retrieval.Lucene_functions;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.*;


public class Main {
	public static List<String> stop_words_list = new ArrayList<String>();
	public static List<String> queries_list = new ArrayList<String>();
	public static void main(String[] args) throws Exception {
        System.out.println("starting program");
        
        functions.get_all_queries(); // get query
        Lucene_functions.read_documents(); //read collection, 
        Lucene_functions.analyze_most_frequent_terms(); //find top 20 words that appear in it
        
        //fetch the inverted list for each query
        for (String query : queries_list) {
//        	System.out.println(query);
        	Lucene_functions.submit_query(query);
        }
        

    } // main
} //Main
