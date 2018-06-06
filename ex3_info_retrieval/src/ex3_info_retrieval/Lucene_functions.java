package ex3_info_retrieval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.misc.HighFreqTerms; /// add external jar lucene misc
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser; // import lucene-queries and query_parser jars
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;  // TODO - decide if we want RAMDirectory, or FSDirectory 
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer; //make sure you import into build_path the jar lucene\analysis\common\ jar file

public class Lucene_functions {

	
	
	// choosing between these two gives different results - check why!!
	//fixme - for some reason - changing stopwords list from 20 doesn't make a difference - why?
	static StandardAnalyzer analyzer = new StandardAnalyzer(StopFilter.makeStopSet((Main.stop_words_list))); //converts tokens to lowercase, and removes stop_words
//	static Analyzer analyzer = new StopAnalyzer(EnglishAnalyzer.getDefaultStopSet()); //converts tokens to lowercase, and removes stop_words
//	static Analyzer analyzer = new StopAnalyzer(StopFilter.makeStopSet((Main.stop_words_list))); //converts tokens to lowercase, and removes stop_words

	
	static Directory index_dir = new RAMDirectory();
	

	public static void addDoc(IndexWriter w, String title, String content, String doc_num) throws IOException {
		Document doc = new Document();

		doc.add(new StringField("title", title, Field.Store.YES)); // stringfiled isn't tokenized
		doc.add(new TextField("content", content, Field.Store.YES)); // textfield is tokenized
		doc.add(new StringField("doc_id",doc_num , Field.Store.YES));
		w.addDocument(doc);
		
	}

	
	// get the 20 most frequent words in the collection
	public static void analyze_most_frequent_terms() throws Exception {
		IndexReader reader = DirectoryReader.open(index_dir);
	
        org.apache.lucene.misc.TermStats[] commonTerms = HighFreqTerms.getHighFreqTerms(reader, 20, "content", new HighFreqTerms.TotalTermFreqComparator());
        for (org.apache.lucene.misc.TermStats commonTerm : commonTerms) {
//            System.out.println(commonTerm.termtext.utf8ToString()); 
            Main.stop_words_list.add(commonTerm.termtext.utf8ToString());
        } 
        if (Config.improved_algo) { // add the default english stop words to our stop word list
        	Iterator iter = EnglishAnalyzer.getDefaultStopSet().iterator();
        	while(iter.hasNext()) {
        	    char[] stopWord = (char[]) iter.next();
        	    Main.stop_words_list.add(new String (stopWord));
        	}
        }
        reader.close();
           
	} //analyze_most_frequent_terms
	
	
	// since the documents are given as a single text document, read the document,
	// and for each document read,
	// take it and add it to the Document object
	public static void read_documents() throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = new IndexWriter(index_dir, config);

		File dir = new File(".");
		File document_file = new File(dir.getCanonicalPath() + File.separator + Config.docsFile);

		BufferedReader br = new BufferedReader(new FileReader(document_file));

		String line = null;
		String current_content = null;
		String doc_title = null;
		int doc_num = 1;

		line = br.readLine();
		while (line != null) { // read whole documents file
			doc_title = line;
			line = "";
			current_content = "";
			while (line.startsWith("*TEXT") == false) { // start of document - read one document from file
				line = br.readLine();
				if (line != null) {
					if (line.startsWith("*TEXT") || line.isEmpty()) {
						continue;
					} // skip empty lines and
				}
				current_content += line;
				if (line == null) {
					break;
				}
			}
			addDoc(w, doc_title, current_content, Integer.toString(doc_num));
			doc_num++;
//			System.out.println("doc title:");
//			System.out.println(doc_title);
//			System.out.println("doc content:");
//			System.out.println(current_content);
		} // read whole documents file

		br.close();
		w.close();
	}
	
	//for each of the query terms, fetch the inverted index list from the index 
	public static void submit_query(String current_query_str) throws IOException, ParseException {
		//split the query to terms
//		List<String> query_terms = new ArrayList<String>();
//		query_terms = Arrays.asList(current_query_str.trim().replaceAll("[\\.\\,]","").split("\\s+")); //remove commas, dots and spaces, and split
        IndexReader reader = DirectoryReader.open(index_dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        
        
    	File dir = new File(".");
		File result_file = new File(dir.getCanonicalPath() + File.separator + Config.outputFile);
		result_file.getParentFile().mkdirs(); //creates the dir if doesn't exist

    	FileWriter  fw = new FileWriter(result_file.getAbsolutePath(), true);
    	BufferedWriter   bw = new BufferedWriter(fw);
    	PrintWriter    out = new PrintWriter(bw);
        
        
        
        TFIDFSimilarity similarity = new ClassicSimilarity() 
        {
        	@Override
        	public float tf(float freq) {
        		return (float) (1 + Math.log(freq));
        	}
        	@Override
        	public float idf(long docFreq, long numDocs) {
        		if (docFreq == 0) return 0;
        		return (float) (Math.log(numDocs / docFreq));
        	}
        	@Override
        	public String toString() {
        		return "tf-idf similarity";
        	}
        };
        searcher.setSimilarity(similarity);
        
        

        Query currentQuery = new QueryParser("content", analyzer).parse(QueryParser.escape(current_query_str));
        
        TopScoreDocCollector inputCollector = TopScoreDocCollector.create(10);
        
        searcher.search(currentQuery, inputCollector);
        ScoreDoc[] hits = inputCollector.topDocs().scoreDocs;
        
        System.out.println("current query:" + current_query_str + "  -> results  : ");
        if (hits.length > 0) {
//                int docId = hits[0].doc;
//                Document doc = searcher.doc(docId);
//                System.out.println(doc.get("content"));
//            	System.out.println("list of hits for the query:");
        	
        	out.print(Config.running_output_query_index + "   ");
        	for (ScoreDoc hit : hits) {
        		System.out.print((hit.doc+1) + " "); //fix result offset by 1
        	    out.print((hit.doc+1) + " ");

        	}
        	out.println("");
        	Config.running_output_query_index++;
        	//fixme:
        	Document temp_doc =   reader.document(0);
//            	System.out.println(temp_doc.getField("content").stringValue());
        	
        	System.out.println("");
        }       	
    

   	    out.close();
	    fw.close();
	    bw.close();
        
	}
}


