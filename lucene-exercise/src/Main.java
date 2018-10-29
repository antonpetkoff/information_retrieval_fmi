import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field;

public class Main {

    private static final String CONTENT_FIELD_NAME = "content";

    public static void addDocument(IndexWriter writer, File file) throws IOException {
        Document doc = new Document();

        String content = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
        doc.add(new TextField(CONTENT_FIELD_NAME, content, Field.Store.YES));

        writer.addDocument(doc);
    }

    public static void buildIndex(Directory indexDir) throws IOException {
        Analyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer(WhitespaceTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .build();

        IndexWriter writer = new IndexWriter(indexDir, new IndexWriterConfig(analyzer));

        // Add each document of the "20 News Groups” corpus to the index
        //Path documentDir = Paths.get("data", "mini_newsgroups/alt.atheism/");
        File[] listOfFiles = new File("/home/tony/source/information_retrieval_fmi/data/mini_newsgroups/alt.atheism").listFiles();
        for (File file: listOfFiles) {
            addDocument(writer, file);
        }

        // Don't forget to close the writer!
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        Path indexPath = Paths.get("data", "lucene_index");
        Directory indexDir = FSDirectory.open(indexPath);

        buildIndex(indexDir);

        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(indexDir));

        System.out.println("Hello World!");
    }
}
