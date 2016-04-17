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

import luceneGUI.IndexGUI;

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

public class IndexFiles {

	PorterStemmer stemmer;
	Stopwords sw;
	
	private String[] indexPath = {
			"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/index/index_00",
			"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/index/index_01",
			"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/index/index_10",
			"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/index/index_11"
	};
	
	private String[] docsPath = {
			"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/docs/docs_00",
			"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/docs/docs_01",
			"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/docs/docs_10",
			"/Users/bcgs/Documents/UFPE-stuff/Web-mining/1st-Task/docs/docs_11",
	};
	
	public void indexFiles() {

		boolean create = true;
		Date start = new Date();;

		try {
			for (int i = 0; i < indexPath.length; i++) {
				
				if(i==1 || i==2) createFiles(docsPath[0],docsPath[i],i);
				if(i==3) createFiles(docsPath[2],docsPath[i],1);

				Path docDir = Paths.get(docsPath[i]);
				if (!Files.isReadable(docDir)) {
					print("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not dable, please check the path");
					System.exit(1);
				}
				
				print("Indexing to directory '" + indexPath[i] + "'...");

				Directory dir = FSDirectory.open(Paths.get(indexPath[i]));
				Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

				if (create) {
					iwc.setOpenMode(OpenMode.CREATE);
				} else {
					iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				}

				IndexWriter writer = new IndexWriter(dir, iwc);
				indexDocs(writer, docDir);

				writer.close();
			}
			
			Date end = new Date();
			print(end.getTime() - start.getTime() + " total milliseconds");
		} catch (IOException e) {
			print(" caught a " + e.getClass() +
					"\n with message: " + e.getMessage());
		}
	}
	
	private void print(String str) {
		IndexGUI.setToContent(str);
	}
	
	
	private void createFiles(String from, String to, int code) {
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
									stemmer = new PorterStemmer();
									stemmer.setCurrent(word);
									stemmer.stem();
									text += stemmer.getCurrent();
									text += ' ';
									break;
								} else if(code == 2) {
									sw = new Stopwords();
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

	
	private void indexDocs(final IndexWriter writer, Path path) throws IOException {
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
	

	private void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			Document doc = new Document();

			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);

			doc.add(new LongField("modified", lastModified, Field.Store.NO));

			doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
//				print("adding " + file);
				writer.addDocument(doc);
			} else {
//				print("updating " + file);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}
}
