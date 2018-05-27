package ex3_info_retrieval;

import ex3_info_retrieval.functions;

import org.apache.lucene.*;


public class Main {
	public static void main(String[] args) throws Exception {
        System.out.println("starting program");
        
        
        //read collection, find top 20 words that appear in it
        functions.find_stop_words();
        
        // get query - 
        functions.get_query();
        

    } // main
} //Main
