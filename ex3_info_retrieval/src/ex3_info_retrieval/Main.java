package ex3_info_retrieval;

import ex3_info_retrieval.functions;
import ex3_info_retrieval.Lucene_functions;

import org.apache.lucene.*;


public class Main {
	public static void main(String[] args) throws Exception {
        System.out.println("starting program");
        
        
        //read collection, find top 20 words that appear in it
//        functions.find_stop_words();
        
        // get query - 
        functions.get_query();
        Lucene_functions.read_documents();
        Lucene_functions.analyze_most_frequent_terms();

    } // main
} //Main
