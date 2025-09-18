package uob.oop;

import org.apache.commons.lang3.time.StopWatch;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdvancedNewsClassifier {
    public Toolkit myTK = null;
    public static List<NewsArticles> listNews = null;
    public static List<Glove> listGlove = null;
    public List<ArticlesEmbedding> listEmbedding = null;
    public MultiLayerNetwork myNeuralNetwork = null;

    public final int BATCHSIZE = 10;

    public int embeddingSize = 0;
    private static StopWatch mySW = new StopWatch();

    public AdvancedNewsClassifier() throws IOException {
        myTK = new Toolkit();
        myTK.loadGlove();
        listNews = myTK.loadNews();
        listGlove = createGloveList();
        listEmbedding = loadData();
    }

    public static void main(String[] args) throws Exception {
        mySW.start();
        AdvancedNewsClassifier myANC = new AdvancedNewsClassifier();

        myANC.embeddingSize = myANC.calculateEmbeddingSize(myANC.listEmbedding);
        myANC.populateEmbedding();
        myANC.myNeuralNetwork = myANC.buildNeuralNetwork(2);
        myANC.predictResult(myANC.listEmbedding);
        myANC.printResults();
        mySW.stop();
        System.out.println("Total elapsed time: " + mySW.getTime());
    }

    public List<Glove> createGloveList() {
        List<Glove> listResult = new ArrayList<>();
        //TODO Task 6.1 - 5 Marks

        String[] stopWords = Toolkit.STOPWORDS;

        for (int i = 0; i < Toolkit.listVocabulary.size(); i++) {
            String word = Toolkit.listVocabulary.get(i);
            boolean isStopWord = false;
            for (String stopWord : stopWords) {
                if (stopWord.equalsIgnoreCase(word)) {
                    isStopWord = true;
                    break;
                }
            }
            if (isStopWord) {
                continue;
            }
            Vector vector = new Vector(Toolkit.listVectors.get(i));
            Glove glove = new Glove(word, vector);
            listResult.add(glove);
        }

        return listResult;
    }


    public static List<ArticlesEmbedding> loadData() {
        List<ArticlesEmbedding> listEmbedding = new ArrayList<>();
        for (NewsArticles news : listNews) {
            ArticlesEmbedding myAE = new ArticlesEmbedding(news.getNewsTitle(), news.getNewsContent(), news.getNewsType(), news.getNewsLabel());
            listEmbedding.add(myAE);
        }
        return listEmbedding;
    }

    public int calculateEmbeddingSize(List<ArticlesEmbedding> _listEmbedding) {
        int intMedian = -1;
        //TODO Task 6.2 - 5 Marks

        List<Integer> docLengths = new ArrayList<>();
        for(ArticlesEmbedding aEmbedding : _listEmbedding){
            String content = aEmbedding.getNewsContent();
            if (content == null || content.isEmpty()){
                continue;
            }
            String[] words = content.split(" ");
            int docLength = 0;
            for (String word : words){
                for (Glove glove : AdvancedNewsClassifier.listGlove) {
                    if (glove.getVocabulary().equals(word)) {
                        docLength++;
                        break;
                    }
                }
            }
            docLengths.add(docLength);
        }
        docLengths.sort(null);
        int listSize = docLengths.size();
        if (listSize % 2 == 0){
            intMedian = (int) (((double)docLengths.get(listSize/2) + docLengths.get((listSize/2) + 1))/2);
        } else {
            intMedian = (int) ((double)docLengths.get((listSize + 1)/2 ));
        }


        return intMedian;
    }

    public void populateEmbedding() {
        //TODO Task 6.3 - 10 Marks

        for (ArticlesEmbedding aEmbedding : listEmbedding) {


            try {

                INDArray embedding = aEmbedding.getEmbedding();


            } catch (InvalidSizeException e) {


                aEmbedding.setEmbeddingSize(embeddingSize);

            } catch (InvalidTextException e) {

                aEmbedding.getNewsContent();


            } catch (Exception e) {
                e.getMessage();
            }
        }

    }

    public DataSetIterator populateRecordReaders(int _numberOfClasses) throws Exception {
        ListDataSetIterator myDataIterator = null;
        List<DataSet> listDS = new ArrayList<>();
        INDArray inputNDArray = null;
        INDArray outputNDArray = null;

        //TODO Task 6.4 - 8 Marks

        for (ArticlesEmbedding aEmbedding : listEmbedding) {
            if (aEmbedding.getNewsType() != NewsArticles.DataType.Training) {
                continue;
            }
            inputNDArray = aEmbedding.getEmbedding();
            outputNDArray = Nd4j.zeros(1, _numberOfClasses);

            int labelIndex = Integer.parseInt(aEmbedding.getNewsLabel()) - 1;
            outputNDArray.putScalar(new int[]{0, labelIndex}, 1);

            DataSet myDataSet = new DataSet(inputNDArray, outputNDArray);
            listDS.add(myDataSet);
        }

        return new ListDataSetIterator(listDS, BATCHSIZE);
    }

    public MultiLayerNetwork buildNeuralNetwork(int _numOfClasses) throws Exception {
        DataSetIterator trainIter = populateRecordReaders(_numOfClasses);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(42)
                .trainingWorkspaceMode(WorkspaceMode.ENABLED)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .updater(Adam.builder().learningRate(0.02).beta1(0.9).beta2(0.999).build())
                .l2(1e-4)
                .list()
                .layer(new DenseLayer.Builder().nIn(embeddingSize).nOut(15)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.HINGE)
                        .activation(Activation.SOFTMAX)
                        .nIn(15).nOut(_numOfClasses).build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        for (int n = 0; n < 100; n++) {
            model.fit(trainIter);
            trainIter.reset();
        }
        return model;
    }

    public List<Integer> predictResult(List<ArticlesEmbedding> _listEmbedding) throws Exception {
        List<Integer> listResult = new ArrayList<>();
        //TODO Task 6.5 - 8 Marks
        for (ArticlesEmbedding embedding : _listEmbedding) {
            if (embedding.getNewsType() == NewsArticles.DataType.Testing) {
                INDArray predictNDArray = embedding.getEmbedding();
                int[] prediction = myNeuralNetwork.predict(predictNDArray);
                int predictedLabel = prediction[0];
                listResult.add(predictedLabel);

                embedding.setNewsLabel(Integer.toString(predictedLabel));
            }
        }

        return listResult;
    }

    public void printResults() {
        //TODO Task 6.6 - 6.5 Marks
        List<String> uniqueClasses = new ArrayList<>();
        for (ArticlesEmbedding aEmbedding : listEmbedding) {
            if (aEmbedding.getNewsType() == NewsArticles.DataType.Testing) {
                String label = aEmbedding.getNewsLabel();
                if (!uniqueClasses.contains(label)) {
                    uniqueClasses.add(label);
                }
            }
        }

        setNumOfClasses(uniqueClasses.size());


        List<List<ArticlesEmbedding>> groups = new ArrayList<>();
        for (int i = 0; i < _numberOfClasses; i++) {
            groups.add(new ArrayList<>());
        }


        for (ArticlesEmbedding aEmbedding : listEmbedding) {
            if (aEmbedding.getNewsType() == NewsArticles.DataType.Testing) {
                int labelIndex = Integer.parseInt(aEmbedding.getNewsLabel());
                if (labelIndex < _numberOfClasses) {
                    groups.get(labelIndex).add(aEmbedding);
                }
            }
        }


        for (int i = 0; i < _numberOfClasses; i++) {
            System.out.print("Group " + (i + 1) + "\r\n");
            for (ArticlesEmbedding aEmbedding : groups.get(i)) {
                System.out.print(aEmbedding.getNewsTitle() + "\r\n");
            }

        }
    }

    private int _numberOfClasses;

    public void setNumOfClasses(int numberOfClasses) {
        this._numberOfClasses = numberOfClasses;

    }
        
    }

