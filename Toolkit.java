package uob.oop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Toolkit {
    public static List<String> listVocabulary = null;
    public static List<double[]> listVectors = null;
    private static final String FILENAME_GLOVE = "glove.6B.50d_Reduced.csv";

    public static final String[] STOPWORDS = {"a", "able", "about", "across", "after", "all", "almost", "also", "am", "among", "an", "and", "any", "are", "as", "at", "be", "because", "been", "but", "by", "can", "cannot", "could", "dear", "did", "do", "does", "either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have", "he", "her", "hers", "him", "his", "how", "however", "i", "if", "in", "into", "is", "it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "most", "must", "my", "neither", "no", "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our", "own", "rather", "said", "say", "says", "she", "should", "since", "so", "some", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "twas", "us", "wants", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would", "yet", "you", "your"};

    public void loadGlove() throws IOException {
        BufferedReader myReader = null;
        //TODO Task 4.1 - 5 marks
        listVocabulary = new ArrayList<>();
        listVectors = new ArrayList<>();
        try {
            myReader = new BufferedReader(new FileReader(getFileFromResource(FILENAME_GLOVE)));
            String line = myReader.readLine();
            while (line != null){
                String[] newArray = line.split(",");
                listVocabulary.add(newArray[0]);
                double[] vectorPoints = new double[newArray.length - 1];
                for (int i = 1 ; i < newArray.length; i++){
                    vectorPoints[i-1] = Double.parseDouble(newArray[i]);
                }
                listVectors.add(vectorPoints);
                line = myReader.readLine();
            }
            myReader.close();
        }catch (IOException e){
            e.getMessage();
        }catch (URISyntaxException e) {
            e.getMessage();
        }
    }


    private static File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = Toolkit.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException(fileName);
        } else {
            return new File(resource.toURI());
        }
    }

    public List<NewsArticles> loadNews() {
        List<NewsArticles> listNews = new ArrayList<>();
        //TODO Task 4.2 - 5 Marks

        try {

            File getfolder = getFileFromResource("News");
            File[] files = getfolder.listFiles();
            for (int i = 0; i < files.length - 1; i++) {
                for (int j = 0; j < files.length - i -1; j++) {
                    if (files[j].getName().compareTo(files[j+1].getName()) > 0) {
                        // Swap listOfFiles[j] and listOfFiles[j+1]
                        File temp = files[j];
                        files[j] = files[j+1];
                        files[j+1] = temp;
                    }
                }
            }

            if (files != null){

                for (File file : files){
                    if (file.isFile() && file.getName().endsWith(".htm")) {
                        try{
                            String htmlCode = new String(Files.readAllBytes(file.toPath()));
                            String newTitle = HtmlParser.getNewsTitle(htmlCode);
                            String newContent = HtmlParser.getNewsContent(htmlCode);
                            NewsArticles.DataType dType = HtmlParser.getDataType(htmlCode);
                            String newLabel = HtmlParser.getLabel(htmlCode);
                            NewsArticles news = new NewsArticles(newTitle, newContent, dType, newLabel);
                            listNews.add(news);
                        } catch (IOException e) {
                            e.getMessage();

                        }

                    }

                }
            }
        } catch (URISyntaxException e) {
            e.getMessage();
        }


        return listNews;
    }

    public static List<String> getListVocabulary() {
        return listVocabulary;
    }

    public static List<double[]> getlistVectors() {
        return listVectors;
    }
}
