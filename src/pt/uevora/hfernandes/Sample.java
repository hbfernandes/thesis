package pt.uevora.hfernandes;

// To use the java client library include in your classpath the file 'mashapeClient.jar'
// make sure to have the json library from http://www.json.org/java/index.html
// import the generated source file
// grab your developer key( you can find it in your dashboard: http://www.mashape.com/account/index )
// and relax!


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.evaluate.Evaluation2;
import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Sentence;

import com.mashape.client.http.MashapeResponse;

public class Sample {

	private static void goldSentSample(String gold, String input) throws Exception{
		Evaluation2 eval = new Evaluation2();
		List<String> correctNer = eval.getCorrectNer(gold, input); // {COMMENT_ID-SENTENCE_ID:ALVO}
				
		Serializer serializer = new Persister();
		File source = new File(gold);

		Corpus goldCorpus = serializer.read(Corpus.class, source);
		Corpus sampleCorpus = new Corpus();
		
		int i = 0;
		String sentKey, nerKey;
		List<String> goldList = new ArrayList<String>();
		Comment sampleComment;
		for (Comment comment : goldCorpus.getComments()) {
			if(i >= 400){
				break;
			}
			
			sampleComment = new Comment(comment.getId());
			sampleCorpus.getComments().add(sampleComment);
			for (Sentence sentence : comment.getSentences()) {
				nerKey = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo();
				sentKey = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo()+"="+sentence.getPolarity();
				
				// entry is in the correct ner and not in the list already
				if(correctNer.contains(nerKey) && !goldList.contains(sentKey)){
					sampleComment.getSentences().add(sentence.getSentenceCopy());
					
					System.out.println(comment.getId()+"-"+sentence.getId());
					i++;
				}
			}
		}
		
		serializer.write(sampleCorpus, new File("sampleGold.xml"));
	}
	
	private static void inputSentSample(String gold, String input) throws Exception{
		Evaluation2 eval = new Evaluation2();
		List<String> correctNer = eval.getCorrectNer(gold, input); // {COMMENT_ID-SENTENCE_ID:ALVO}
		
		Serializer serializer = new Persister();
		File source = new File(input);

		Corpus inputCorpus = serializer.read(Corpus.class, source);
		Corpus sampleCorpus = new Corpus();
		
		String nerKey, sentKey;
		List<String> inputList = new ArrayList<String>();
		Comment sampleComment;
		for (Comment comment : inputCorpus.getComments()) {
			if(comment.getId() > 453){
				break;
			}
			
			sampleComment = new Comment(comment.getId());
			sampleCorpus.getComments().add(sampleComment);
			for (Sentence sentence : comment.getSentences()) {
				nerKey = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo();
				sentKey = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo()+"="+sentence.getPolarity();
				
				// entry is in the correct ner and not in the list already
				if(sentence.getAlvo() != null && correctNer.contains(nerKey) && !inputList.contains(sentKey)){
					sampleComment.getSentences().add(sentence.getSentenceCopy());
					
					System.out.println(comment.getId()+"-"+sentence.getId());
				}
			}
		}
		
		serializer.write(sampleCorpus, new File("sampleInput_classified.xml"));
	}
	
	private static void inputSentSampleUntagged(String gold, String input) throws Exception{
		Evaluation2 eval = new Evaluation2();
		List<String> correctNer = eval.getCorrectNer(gold, input); // {COMMENT_ID-SENTENCE_ID:ALVO}
		
		Serializer serializer = new Persister();
		File source = new File(input);

		Corpus inputCorpus = serializer.read(Corpus.class, source);
		Corpus sampleCorpus = new Corpus();
		
		String nerKey, sentKey;
		List<String> inputList = new ArrayList<String>();
		Comment sampleComment;
		for (Comment comment : inputCorpus.getComments()) {
			if(comment.getId() > 453){
				break;
			}
			
			sampleComment = new Comment(comment.getId());
			sampleCorpus.getComments().add(sampleComment);
			for (Sentence sentence : comment.getSentences()) {
				nerKey = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo();
				sentKey = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo()+"="+sentence.getPolarity();
				
				// entry is in the correct ner and not in the list already
				if(sentence.getAlvo() != null && correctNer.contains(nerKey) && !inputList.contains(sentKey)){
					sampleComment.getSentences().add(sentence.getSentenceCopy());
					
					System.out.println(comment.getId()+"-"+sentence.getId());
				}
			}
		}
		
		serializer.write(sampleCorpus, new File("sampleInput_unclassified.xml"));
	}
	
	public static void main(String[] args) throws Exception {
		String INPUT_CLASSIFIED = "output/SentiCorpus-PT_sentiment_6.xml";
		String INPUT_UNCLASSIFIED = "output/SentiCorpus-PT_ner_3.xml";
		
		goldSentSample(Files.GOLD, INPUT_CLASSIFIED);
		inputSentSample(Files.GOLD, INPUT_CLASSIFIED);
		inputSentSampleUntagged(Files.GOLD, INPUT_UNCLASSIFIED);
		
		// basic instantiation. TODO Put your authentication keys here.
//		SentimentAnalysisFree client = new SentimentAnalysisFree("k39qqgacqktif8dmjmj3fjzghi6xsa", "9wnp1rp2eul3laoshugvmymh7tlhjx");
//
//		// A sample method call. These parameters are not properly filled in.
//		// See SentimentAnalysisFree.java to find all method names and parameters.
//		MashapeResponse<JSONObject> response = client.classifytext("pt", "Manuela Ferreira Leite não presta para líder do PSD.");
//
//		//now you can do something with the response.
//		System.out.println("API Call returned a response code of " + response.getBody().toString());
	}
}
