package ex3_info_retrieval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.misc.HighFreqTerms; /// add external jar lucene misc
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser; // import lucene-queries and query_parser jars
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.standard.StandardAnalyzer; //make sure you import into build_path the jar lucene\analysis\common\ jar file
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.codecs.TermStats;

public class Lucene_functions {

	static StandardAnalyzer analyzer = new StandardAnalyzer();
	static Directory index_dir = new RAMDirectory();

	public static void addDoc(IndexWriter w, String title, String content) throws IOException {
		Document doc = new Document();
		doc.add(new StringField("title", title, Field.Store.YES)); // stringfiled isn't tokenized
		doc.add(new TextField("content", content, Field.Store.YES)); // textfield is tokenized
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
        reader.close();
           
	} //analyze_most_frequent_terms
	
	
	// since the documents are given as a single text document, read the document,
	// and for each document read,
	// take it and add it to the Document object
	public static void read_documents() throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = new IndexWriter(index_dir, config);

		File dir = new File(".");
		File document_file = new File(dir.getCanonicalPath() + File.separator + "/Files/docs.txt");

		BufferedReader br = new BufferedReader(new FileReader(document_file));

		String line = null;
		String current_content = null;
		String doc_title = null;

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
			addDoc(w, doc_title, current_content);
//			System.out.println("doc title:");
//			System.out.println(doc_title);
//			System.out.println("doc content:");
//			System.out.println(current_content);
		} // read whole documents file

		br.close();
		w.close();
	}
	
	//for each of the query terms, fetch the inverted index list from the index 
	public static void submit_query(String current_query) throws IOException {
		
		//split the query to terms
		List<String> query_terms = new ArrayList<String>();
		query_terms = Arrays.asList(current_query.trim().replaceAll("[\\.\\,]","").split("\\s+")); //remove commas, dots and spaces, and split
//		System.out.println(query_terms);
        IndexReader reader = DirectoryReader.open(index_dir);
        
        
        
        // read this : https://www.tutorialspoint.com/lucene/lucene_analysis.htm and pages around it
        
        //some example i found, not sure it's relevant
//        Terms terms = MultiFields.getTerms(reader, "content");
//        final TermsEnum it = terms.iterator();
//        BytesRef term = it.next();
//        while (term != null) {
//            String termString = term.utf8ToString();
//            System.out.print(termString + ": ");
//            for (LeafReaderContext lrc : reader.leaves()) {
//                LeafReader lr = lrc.reader();
//                PostingsEnum pe = lr.postings(new Term("content", termString));
//                int docId = pe.nextDoc();
//                while (docId != PostingsEnum.NO_MORE_DOCS) {
//                    Document doc = lr.document(docId);
//                    System.out.println(doc);
//                    docId = pe.nextDoc();
//                }
//            }
//            term = it.next();
//        }
	}
}
