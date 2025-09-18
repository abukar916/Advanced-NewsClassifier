package uob.oop;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.util.Properties;


public class ArticlesEmbedding extends NewsArticles {
    private int intSize = -1;
    private String processedText = "";

    private INDArray newsEmbedding = Nd4j.create(0);

    public ArticlesEmbedding(String _title, String _content, NewsArticles.DataType _type, String _label) {
        //TODO Task 5.1 - 1 Mark
        super(_title, _content, _type,_label);
    }

    public void setEmbeddingSize(int _size) {
        //TODO Task 5.2 - 0.5 Marks
        intSize = _size;

    }

    public int getEmbeddingSize(){
        return intSize;
    }

    private static final StanfordCoreNLP pipeline;
    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        pipeline = new StanfordCoreNLP(props);
    }
    private boolean isProcessed = false;
    @Override
    public String getNewsContent() {
        //TODO Task 5.3 - 10 Marks
        if (isProcessed) {
            return processedText;
        }
        processedText = textCleaning(super.getNewsContent());
        Annotation document = new Annotation(processedText);
        pipeline.annotate(document);
        CoreDocument doc2 = new CoreDocument(document);


        StringBuilder lemmatizedText = new StringBuilder(processedText.length() * 2);
        String[] stopWords = Toolkit.STOPWORDS;
        for (CoreLabel token : doc2.tokens()) {
            String word = token.word();
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                boolean isStopWord = false;
                for (String stopWord : stopWords) {
                    if (stopWord.equalsIgnoreCase(lemma)) {
                        isStopWord = true;
                        break;
                    }
                }
                if (!isStopWord) {
                    lemmatizedText.append(lemma).append(' ');
                }
            }


        processedText = lemmatizedText.toString().trim().toLowerCase();
        isProcessed = true;
        return processedText;
    }

    private boolean isEmbedded = false;
    public INDArray getEmbedding() throws Exception {
        //TODO Task 5.4 - 20 Marks
        if (isEmbedded) {
            return newsEmbedding;
        }
        if (intSize == -1) {
            throw new InvalidSizeException("Invalid size");
        }
        if (processedText.isEmpty()) {
            throw new InvalidTextException("Invalid Text.");
        }

        String[] words = processedText.split(" ");
        newsEmbedding = Nd4j.zeros(intSize, Toolkit.listVectors.get(0).length);

        int intIndex = 0;
        for (String word : words) {
            int intWordIndex = -1;
            for (int i = 0; i < AdvancedNewsClassifier.listGlove.size(); i++) {
                if (AdvancedNewsClassifier.listGlove.get(i).getVocabulary().equals(word)) {
                    intWordIndex = i;
                    break;

                }
            }
            if (intWordIndex != -1 && intIndex < intSize){
                Glove glove = AdvancedNewsClassifier.listGlove.get(intWordIndex);
                Vector vector = glove.getVector();
                double[] gloveVector = vector.getAllElements();
                INDArray wordEmbedding = Nd4j.create(gloveVector);

                newsEmbedding.putRow(intIndex, wordEmbedding);
                intIndex++;
            }
        }

        // System.out.println(newsEmbedding);
        while (intIndex < intSize) {
            newsEmbedding.putRow(intIndex, Nd4j.zeros(newsEmbedding.columns()));
            intIndex++;
        }
        isEmbedded = true;

        return Nd4j.vstack(newsEmbedding.mean(1));
    }

    /***
     * Clean the given (_content) text by removing all the characters that are not 'a'-'z', '0'-'9' and white space.
     * @param _content Text that need to be cleaned.
     * @return The cleaned text.
     */
    private static String textCleaning(String _content) {
        StringBuilder sbContent = new StringBuilder();

        for (char c : _content.toLowerCase().toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || Character.isWhitespace(c)) {
                sbContent.append(c);
            }
        }

        return sbContent.toString().trim();
    }
}
