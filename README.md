#  Advanced News Classifier

This project implements an **Advanced News Classifier** in **Java**, leveraging **GloVe embeddings** and machine learning techniques for document-level classification of news articles.  

The system processes raw news data (HTML format), extracts titles, content, data types, and labels, builds embeddings from pre-trained **GloVe vectors**, and classifies news into groups using a neural network (via **Deeplearning4J** and **ND4J**).  

---

##  Features

- **News Data Handling**  
  - Parse news articles from `.htm` files.  
  - Extract metadata: title, content, type (Training/Testing), and labels.  

- **GloVe Embeddings**  
  - Load reduced GloVe embeddings (`glove.6B.50d_Reduced.csv`).  
  - Convert words into fixed-length document embeddings.  
  - Support preprocessing: cleaning, lemmatization (via Stanford CoreNLP), stopword removal.  

- **Machine Learning Pipeline**  
  - Construct embeddings with ND4J arrays.  
  - Prepare training and testing datasets.  
  - Train and evaluate neural network models with Deeplearning4J.  
  - Predict labels for unseen (Testing) data.  

- **Results Visualization**  
  - Print grouped results for classified news articles.  
  - Flexible support for multiple newsgroups.  

---

##  Tech Stack

- **Language:** Java  
- **Libraries/Frameworks:**  
  - [Deeplearning4J](https://github.com/eclipse/deeplearning4j) – Neural network training.  
  - [ND4J](https://github.com/eclipse/deeplearning4j) – N-dimensional arrays.  
  - [Stanford CoreNLP](https://stanfordnlp.github.io/CoreNLP/) – Text lemmatization.  
- **Embeddings:** [GloVe (50d Reduced)](https://nlp.stanford.edu/projects/glove/)  

---

##  Project Structure

Advanced-NewsClassifier/
│── src/
│ ├── Glove.java # Represents GloVe word embeddings
│ ├── NewsArticles.java # Stores news metadata (title, content, type, label)
│ ├── HtmlParser.java # Extracts title, content, labels from HTML
│ ├── Toolkit.java # Utility functions for loading data
│ ├── ArticlesEmbedding.java # Converts articles into document embeddings
│ ├── AdvancedNewsClassifier.java # Main classifier logic
│ └── Main.java # Entry point
│── resources/
│ ├── glove.6B.50d_Reduced.csv # Pre-trained embeddings
│ └── News/ # Training & Testing HTML articles
│── tests/ # jUnit test files
│── README.md # Documentation

## Setup 
Clone the Repository: 
git clone https://github.com/abukar916/Advanced-NewsClassifier.git
cd Advanced-NewsClassifier

#Run
javac -d bin src/*.java
java -cp bin Main
