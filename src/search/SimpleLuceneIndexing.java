package search;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.wikipedia.WikipediaTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import sun.misc.Regexp;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Lucene Demo: basic similarity based content indexing 
 * @author Sharonpova
 * Current sample files fragments of wikibooks and stackoverflow. 
 */


public class SimpleLuceneIndexing {
	
	private static void indexDirectory(IndexWriter writer, File dir) throws IOException {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				indexDirectory(writer, f); // recurse
			} else if (f.getName().endsWith(".txt")) {
				// call indexFile to add the title of the txt file to your index (you can also index html)
				indexFile(writer, f);
			}
		}
	}
	private static void indexFile(IndexWriter writer, File f) throws IOException {
		//System.out.println("Indexing " + f.getName());
		Document doc = new Document();
		doc.add(new TextField("filename", f.getName(), TextField.Store.YES));
		
		
		//open each file to index the content
		try{
			
				FileInputStream is = new FileInputStream(f);
		        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		        StringBuffer stringBuffer = new StringBuffer();
		        String line = null;
		        while((line = reader.readLine())!=null){
		          stringBuffer.append(line).append("\n");
		        }
		        reader.close();
				String content = stringBuffer.toString();
				content = content.replace("\n","<br />");
				doc.add(new TextField("contents", content, TextField.Store.YES));

		}catch (Exception e) {
            
			System.out.println("something wrong with indexing content of the files");
        }    
		
          
        
		writer.addDocument(doc);
		
	}	
	
	 public static ArrayList runIndex(String searchText) throws IOException, ParseException {
		 
		File dataDir = new File("assign2files"); //my sample file folder path
		// Check whether the directory to be indexed exists
		if (!dataDir.exists() || !dataDir.isDirectory()) {
			throw new IOException(
					dataDir + " does not exist or is not a directory");
		}
		Directory indexDir = new RAMDirectory();
		
		// Specify the analyzer for tokenizing text.
		//StandardAnalyzer analyzer = new StandardAnalyzer();
		 String[] stopWords = {
				 "without", "see", "unless", "due", "also", "must", "might", "like", "will", "may", "can", "much", "every", "the", "in", "other", "this", "the", "many", "any", "an", "or", "for", "in", "an", "an ", "is", "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren’t", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can’t", "cannot", "could",
				 "couldn’t", "did", "didn’t", "do", "does", "doesn’t", "doing", "don’t", "down", "during", "each", "few", "for", "from", "further", "had", "hadn’t", "has", "hasn’t", "have", "haven’t", "having",
				 "he", "he’d", "he’ll", "he’s", "her", "here", "here’s", "hers", "herself", "him", "himself", "his", "how", "how’s", "i ", " i", "i’d", "i’ll", "i’m", "i’ve", "if", "in", "into", "is",
				 "isn’t", "it", "it’s", "its", "itself", "let’s", "me", "more", "most", "mustn’t", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "ought", "our", "ours", "ourselves",
				 "out", "over", "own", "same", "shan’t", "she", "she’d", "she’ll", "she’s", "should", "shouldn’t", "so", "some", "such", "than",
				 "that", "that’s", "their", "theirs", "them", "themselves", "then", "there", "there’s", "these", "they", "they’d", "they’ll", "they’re", "they’ve",
				 "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn’t", "we", "we’d", "we’ll", "we’re", "we’ve",
				 "were", "weren’t", "what", "what’s", "when", "when’s", "where", "where’s", "which", "while", "who", "who’s", "whom",
				 "why", "why’s", "with", "won’t", "would", "wouldn’t", "you", "you’d", "you’ll", "you’re", "you’ve", "your", "yours", "yourself", "yourselves",
				 "without", "see", "unless", "due", "also", "must", "might", "like", "will", "may", "can", "much", "every", "the", "in", "other", "this", "the", "many", "any", "an", "or", "for", "in", "an", "an ", "is", "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren’t", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can’t", "cannot", "could",
				 "couldn’t", "did", "didn’t", "do", "does", "doesn’t", "doing", "don’t", "down", "during", "each", "few", "for", "from", "further", "had", "hadn’t", "has", "hasn’t", "have", "haven’t", "having",
				 "he", "he’d", "he’ll", "he’s", "her", "here", "here’s", "hers", "herself", "him", "himself", "his", "how", "how’s", "i ", " i", "i’d", "i’ll", "i’m", "i’ve", "if", "in", "into", "is",
				 "isn’t", "it", "it’s", "its", "itself", "let’s", "me", "more", "most", "mustn’t", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "ought", "our", "ours", "ourselves",
				 "out", "over", "own", "same", "shan’t", "she", "she’d", "she’ll", "she’s", "should", "shouldn’t", "so", "some", "such", "than",
				 "that", "that’s", "their", "theirs", "them", "themselves", "then", "there", "there’s", "these", "they", "they’d", "they’ll", "they’re", "they’ve",
				 "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn’t", "we", "we’d", "we’ll", "we’re", "we’ve",
				 "were", "weren’t", "what", "what’s", "when", "when’s", "where", "where’s", "which", "while", "who", "who’s", "whom",
				 "why", "why’s", "with", "won’t", "would", "wouldn’t", "you", "you’d", "you’ll", "you’re", "you’ve", "your", "yours", "yourself", "yourselves", "java"
		 };

		 CharArraySet stops = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		 for (String i:stopWords) stops.add(i);
		 Analyzer analyzer = new Analyzer() {
			 @Override
			 protected TokenStreamComponents createComponents(String s) {
				 Tokenizer wiki = new WikipediaTokenizer();
				 TokenStream filter = new LowerCaseFilter(wiki);
				 filter = new StopFilter(filter, stops);
				 filter = new PatternReplaceFilter(filter,Pattern.compile("[^A-Za-z0-9 ]"),"",true);
				 filter = new LengthFilter(filter,3,30);
				 filter = new TrimFilter(filter);
				 filter = new PorterStemFilter(filter);
				 filter = new NGramTokenFilter(filter,2,3);
				 return new TokenStreamComponents(wiki,filter);
			 }
		 };



		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(indexDir, config);
		
		// call indexDirectory to add to your index
		// the names of the txt files in dataDir
		indexDirectory(writer, dataDir);
		writer.close();
		 
		//Query string!  
		//String querystr = "contents:Null Pointer" ;
		//String querystr = "contents:One way to implement deep copy is to add copy constructors to each associated class. A copy constructor takes an instance of 'this' as its single argument and copies all the values from it. Quite some work, but pretty straightforward and safe. EDIT: note that you don't need to use accessor methods to read fields. You can access all fields directly because the source instance is always of the same type as the instance with the copy constructor. Obvious but might be overlooked. Example Edit Note that when using copy constructors you need to know the runtime type of the object you are copying. With the above approach you cannot easily copy a mixed list (you might be able to do it with some reflection code). \n";
		//Pattern chars = Pattern.compile("[^A-Za-z0-9 ]");

		 System.out.println(searchText);
		searchText = searchText.replaceAll("[^A-Za-z0-9 ]","");


		HashMap<String, Integer> duplicates = new HashMap<String, Integer>();
		StringBuilder newQuery = new StringBuilder();
		for(String s:searchText.split(" ")){

			if(!stops.contains(s) && s.length()>2) {
				if (Character.isUpperCase(s.charAt(0))){
					duplicates.put(s.toLowerCase(), 3);
					continue;
				}
				s = s.toLowerCase();

				if(duplicates.isEmpty()) {
					if (Character.isUpperCase(s.charAt(0))){
						duplicates.put(s.toLowerCase(), 3);
						continue;
					}
					else {
						duplicates.put(s, 2);
						continue;
					}
				}

				if (duplicates.containsKey(s))
					duplicates.put(s, duplicates.get(s) + 1);
				else
					duplicates.put(s, 1);
			}
		}
		 for(String s:duplicates.keySet())
					newQuery.append(s+'^'+duplicates.get(s).toString()+' ');
		 System.out.println(newQuery);
		 String querystr = "contents:"+ newQuery.toString();

		/*//This is going to be your selected posts.
		Scanner console = new Scanner(System.in);
		String querystr = "contents:"+console.nextLine();
		System.out.println(querystr);
		*/
		
		Query q = new QueryParser( "contents", analyzer).parse(querystr);
		int hitsPerPage = 10;
		IndexReader reader = null;
		 
		
		 
		 TopScoreDocCollector collector = null;
		 IndexSearcher searcher = null;
		 reader = DirectoryReader.open(indexDir);
		 searcher = new IndexSearcher(reader);
		 collector = TopScoreDocCollector.create(hitsPerPage);
		 searcher.search(q, collector);
		 
		 
		 
		 ScoreDoc[] hits = collector.topDocs().scoreDocs;
		 System.out.println("Found " + hits.length + " hits.");
		 System.out.println();

		 ArrayList result = new ArrayList();
		 
		 for (int i = 0; i < hits.length; ++i) {
			 int docId = hits[i].doc;
			 Document d;
			 d = searcher.doc(docId);
			
			 //System.out.println((i + 1) + ". " + d.get("filename"));
			// String contents =
			 result.add(d.get("contents"));
		 }
		 reader.close();

		 return result;
	 }

}