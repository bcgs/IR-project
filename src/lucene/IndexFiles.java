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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

/** Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing.
 * Run it with no command-line arguments for usage information.
 */
public class IndexFiles {

	private IndexFiles() {}

	static PorterStemmer stemmer = new PorterStemmer();
	static Stopwords sw = new Stopwords();
	
	
	/** Index all text files under a directory. */
	public static void main(String[] args) {

		String[] indexPath = {
				"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/index-folders/index_00",
				"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/index-folders/index_01",
				"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/index-folders/index_10",
				"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/index-folders/index_11"
		};

		String[] docsPath = {
				"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/docs/docs_00",
				"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/docs/docs_01",
				"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/docs/docs_10",
				"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/docs/docs_11",
		};

		boolean create = true;
		//renameFile(docsPath[0], docsPath[0]);

		try {
			for (int i = 0; i < indexPath.length; i++) {
				
				// i = 1 : stemming
				// i = 2 : stopword
				// i = 3 : (stopword -> stemming)

				if(i==1 || i==2) createFiles(docsPath[0],docsPath[i],i);
				if(i==3) createFiles(docsPath[2],docsPath[i],1);

				Path docDir = Paths.get(docsPath[i]);
				if (!Files.isReadable(docDir)) {
					System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not dable, please check the path");
					System.exit(1);
				}

				Date start = new Date();

				System.out.println("Indexing to directory '" + indexPath[i] + "'...");

				Directory dir = FSDirectory.open(Paths.get(indexPath[i]));
				Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

				if (create) {
					// Create a new index in the directory, removing any
					// previously indexed documents:
					iwc.setOpenMode(OpenMode.CREATE);
				} else {
					// Add new documents to an existing index:
					iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				}

				// Optional: for better indexing performance, if you
				// are indexing many documents, increase the RAM
				// buffer.  But if you do this, increase the max heap
				// size to the JVM (eg add -Xmx512m or -Xmx1g):
				//
				// iwc.setRAMBufferSizeMB(256.0);

				IndexWriter writer = new IndexWriter(dir, iwc);
				indexDocs(writer, docDir);

				// NOTE: if you want to maximize search performance,
				// you can optionally call forceMerge here.  This can be
				// a terribly costly operation, so generally it's only
				// worth it when your index is relatively static (ie
				// you're done adding documents to it):
				//
				// writer.forceMerge(1);

				writer.close();

				Date end = new Date();
				System.out.println(end.getTime() - start.getTime() + " total milliseconds");
			}
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() +
					"\n with message: " + e.getMessage());
		}
	}
	
	static void renameFile(String from, String to) {
		File dir = new File(from);
		int i = 1;
		for (File file : dir.listFiles()) {
			File newName = new File(to + "/" + String.format("%03d", i));
			file.renameTo(newName);
			i++;
		}
	}
	
	static void createFiles(String from, String to, int code) {
		String word, text;
		char[] w = new char[501];
		int i = 1;

		File dir = new File(from);
		for (File file : dir.listFiles())
			try {
				text = "";
				FileInputStream in = new FileInputStream(file);
				while(true) {
					word = ""; 
					int ch = in.read();
					if (Character.isLetter((char) ch)) {
						int j = 0;

						while(true) {
							ch = Character.toLowerCase((char) ch);
							w[j] = (char) ch;
							if (j < 500) j++;
							ch = in.read();

							if (!Character.isLetter((char) ch)) { 
								for (int c = 0; c < j; c++) {
									word += w[c];
								}
								if(code == 1) {
									stemmer.setCurrent(word);
									stemmer.stem();
									text += stemmer.getCurrent();
									text += ' ';
									break;
								} else if(code == 2) {
									if(!sw.isStopword(word)) {
										text += word;
										text += ' ';
									}
									break;
								}
							}
						}
					}
					if (ch < 0) break;
				}

				FileOutputStream out = new FileOutputStream(to +"/"+ String.format("%03d", i));
				new PrintStream(out).println(text);
				i++;
				out.close();
				in.close();

			} catch(FileNotFoundException ie) {
				System.out.println("error reading " + file);
				break;
			} catch (IOException e) {
				System.out.println("file " + file + " not found");
				break;
			}
	}

	/**
	 * Indexes the given file using the given writer, or if a directory is given,
	 * recurses over files and directories found under the given directory.
	 * 
	 * NOTE: This method indexes one document per input file.  This is slow.  For good
	 * throughput, put multiple documents into your input file(s).  An example of this is
	 * in the benchmark module, which can create "line doc" files, one document per line,
	 * using the
	 * <a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.l"
	 * >WriteLineDocTask</a>.
	 *  
	 * @param writer Writer to the index where the given file/dir info will be stored
	 * @param path The file to index, or the directory to recurse into to find files to index
	 * @throws IOException If there is a low-level I/O error
	 */
	static void indexDocs(final IndexWriter writer, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try {
						indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
					} catch (IOException ignore) {
						// don't index files that can't be read.
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
		}
	}

	/** Indexes a single document */
	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			// make a new, empty document
			Document doc = new Document();

			// Add the path of the file as a field named "path".  Use a
			// field that is indexed (i.e. searchable), but don't tokenize 
			// the field into separate words and don't index term frequency
			// or positional information:
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);

			// Add the last modified date of the file a field named "modified".
			// Use a LongField that is indexed (i.e. efficiently filterable with
			// NumericRangeFilter).  This indexes to milli-second resolution, which
			// is often too fine.  You could instead create a number based on
			// year/month/day/hour/minutes/seconds, down the resolution you require.
			// For example the long value 2011021714 would mean
			// February 17, 2011, 2-3 PM.
			doc.add(new LongField("modified", lastModified, Field.Store.NO));

			// Add the contents of the file to a field named "contents".  Specify a Reader,
			// so that the text of the file is tokenized and indexed, but not stored.
			// Note that FileReader expects the file to be in UTF-8 encoding.
			// If that's not the case searching for special characters will fail.
			doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				// New index, so we just add the document (no old document can be there):
				System.out.println("adding " + file);
				writer.addDocument(doc);
			} else {
				// Existing index (an old copy of this document may have been indexed) so 
				// we use updateDocument instead to replace the old one matching the exact 
				// path, if present:
				System.out.println("updating " + file);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}
}
