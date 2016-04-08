package lucene;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.tartarus.snowball.ext.PorterStemmer;

/** Simple command-line based search demo. */
public class SearchFiles {

	private SearchFiles() {}

	/** Simple command-line based search demo. */
	public static void main(String[] args) throws Exception {

		String[] index = {
				"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/index-folders/index_00",
				"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/index-folders/index_01",
				"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/index-folders/index_10",
				"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/index-folders/index_11"
		};
		String field = "contents";
		String queries = null;
		int repeat = 0;
		boolean raw = false;
		String queryString = null;
		int hitsPerPage = 10;

		PorterStemmer stemmer = new PorterStemmer();
		Stopwords sw = new Stopwords();
		
		BufferedReader in = null;
		in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		
		System.out.println("Choose:\n(0) - regular search;\n(1) - stemmed search;\n"
				+ "(2) - stopword search;\n(3) - stem and stopword search");
		String code = in.readLine();

		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index[Integer.parseInt(code)])));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);

		QueryParser parser = new QueryParser(field, analyzer);
		while (true) {
			
//			System.out.println("LogReset (y/n)?");
//			String line = in.readLine();
//			if(line.charAt(0) == 'y') {
//				File log = new File("log.txt");
//				try {
//					PrintWriter out = new PrintWriter(log);
//					out.print("");
//					out.close();
//				} catch (IOException e) {
//					System.out.println("File not read.");
//				}
//				System.out.println("Log reseted!");
//				line = "";
//			}

			if (queries == null && queryString == null) {
				System.out.println("Enter query: ");
			}

			String line = queryString != null ? queryString : in.readLine();

			if (line == null || line.length() == -1) {
				break;
			}

			line = line.trim();
			if (line.length() == 0) {
				break;
			}

			String[] splitLine = line.split("\\s");
			
			if(Integer.parseInt(code) == 1)	{ 					// If stemmed search
				line = "";
				for (int i = 0; i < splitLine.length; i++) {
					stemmer.setCurrent(splitLine[i]);
					stemmer.stem();
					line += stemmer.getCurrent();
					line += ' ';
				}
			}
			if(Integer.parseInt(code) == 2) {					// If stopword search
				line = "";
				for (int i = 0; i < splitLine.length; i++) {
					if(!sw.isStopword(splitLine[i])) {
						line += splitLine[i];
						line += ' ';
					}
				} if(line.length() == 0) {
					System.out.println("No matching documents, try again...");
					continue;
				}
			}
			if(Integer.parseInt(code) == 3)	{					// If stem and stopword search
				line = "";
				for (int i = 0; i < splitLine.length; i++) {
					if(!sw.isStopword(splitLine[i])) {
						stemmer.setCurrent(splitLine[i]);
						stemmer.stem();
						line += stemmer.getCurrent();
						line += ' ';
					}
				} if(line.length() == 0) {
					System.out.println("No matching documents, try again...");
					continue;
				}
			}
			line = line.trim();
				
			Query query = parser.parse(line);
			System.out.println("Searching for: " + query.toString(field));

			if (repeat > 0) {                           		// repeat & time as benchmark
				Date start = new Date();
				for (int i = 0; i < repeat; i++) {
					searcher.search(query, 100);
				}
				Date end = new Date();
				System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
			}

			doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null);

			if (queryString != null) {
				break;
			}
		}
		reader.close();
	}
	
	public static void log(ScoreDoc[] hits, int[] matrix) {
		File log = new File("log.txt");
		try {
			if(log.exists()==false) {
				System.out.println("New log created!");
				log.createNewFile();
			}
			PrintWriter out = new PrintWriter(new FileWriter(log, true));
			
			for (int i = 0; i < hits.length; i++)
				matrix[hits[i].doc] = 1;
			
			for (int i = 0; i < matrix.length; i++)
				out.append(matrix[i]+" ");
	
			out.append('\n');
			out.close();
		} catch (IOException e) {
			System.out.println("Error. No log created!");
		}
	}
	
	/**
	 * This demonstrates a typical paging search scenario, where the search engine presents 
	 * pages of size n to the user. The user can then go to the next page if interested in
	 * the next hits.
	 * 
	 * When the query is executed for the first time, then only enough results are collected
	 * to fill 5 result pages. If the user wants to page beyond this limit, then the query
	 * is executed another time and all hits are collected.
	 * 
	 */
	public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, 
			int hitsPerPage, boolean raw, boolean interactive) throws IOException {
		
		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		// Create log
		// (hits.doc+1) <= doc hit by query
//		int[] matrix = new int[200];
//		log(hits, matrix);
		
		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + " total matching documents");

		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);

		while (true) {
			if (end > hits.length) {
				System.out.println("Only results 1 - " + hits.length +" of " + numTotalHits + " total matching documents collected.");
				System.out.println("Collect more (y/n) ?");
				String line = in.readLine();
				if (line.length() == 0 || line.charAt(0) == 'n') {
					break;
				}

				hits = searcher.search(query, numTotalHits).scoreDocs;
			}

			end = Math.min(hits.length, start + hitsPerPage);

			for (int i = start; i < end; i++) {
				if (raw) {                              // output raw format
					System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
					continue;
				}

				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get("path");
				if (path != null) {
					System.out.println((i+1) + ". " + path);
					String title = doc.get("title");
					if (title != null) {
						System.out.println("   Title: " + doc.get("title"));
					}
				} else {
					System.out.println((i+1) + ". " + "No path for this document");
				}

			}

			if (!interactive || end == 0) {
				break;
			}

			if (numTotalHits >= end) {
				boolean quit = false;
				while (true) {
					System.out.print("Press ");
					if (start - hitsPerPage >= 0) {
						System.out.print("(p)revious page, ");  
					}
					if (start + hitsPerPage < numTotalHits) {
						System.out.print("(n)ext page, ");
					}
					System.out.println("(q)uit or enter number to jump to a page.");

					String line = in.readLine();
					if (line.length() == 0 || line.charAt(0)=='q') {
						quit = true;
						break;
					}
					if (line.charAt(0) == 'p') {
						start = Math.max(0, start - hitsPerPage);
						break;
					} else if (line.charAt(0) == 'n') {
						if (start + hitsPerPage < numTotalHits) {
							start+=hitsPerPage;
						}
						break;
					} else {
						int page = Integer.parseInt(line);
						if ((page - 1) * hitsPerPage < numTotalHits) {
							start = (page - 1) * hitsPerPage;
							break;
						} else {
							System.out.println("No such page");
						}
					}
				}
				if (quit) break;
				end = Math.min(numTotalHits, start + hitsPerPage);
			}
		}
	}
}

