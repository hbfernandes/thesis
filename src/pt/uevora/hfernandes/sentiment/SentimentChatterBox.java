package pt.uevora.hfernandes.sentiment;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.mashape.client.http.MashapeResponse;

import pt.uevora.hfernandes.SentimentAnalysisFree;
import pt.uevora.hfernandes.evaluate.Evaluation;
import pt.uevora.hfernandes.objects.Clause;
import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Sentence;
import pt.uevora.hfernandes.objects.SentiLexEntry;
import pt.uevora.hfernandes.objects.Token;
import pt.uevora.hfernandes.parsers.SentiLexParser;


/**
 * Using chatterbox
 * 
 * @author hfernandes
 *
 */
public class SentimentChatterBox {
	private static void evaluate(String input, String output) throws Exception{
		// grab the input
		Serializer serializer = new Persister();
		File inputFile = new File(input);
		
		Corpus corpus = serializer.read(Corpus.class, inputFile);
		
		Clause referedClause;
		SentimentAnalysisFree client = new SentimentAnalysisFree("k39qqgacqktif8dmjmj3fjzghi6xsa", "9wnp1rp2eul3laoshugvmymh7tlhjx");
		int polarity, assigned;
		double confidence;
		for (Comment comment : corpus.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				referedClause = sentence.getReferedClause();
				
				if(referedClause != null){
					// A sample method call. These parameters are not properly filled in.
					// See SentimentAnalysisFree.java to find all method names and parameters.
					MashapeResponse<JSONObject> response = client.classifytext("pt", referedClause.getTokenText());
					
					polarity = response.getBody().getInt("sent");
					confidence = response.getBody().getDouble("value");
					assigned = 0;
					
					if(polarity > 0 && confidence > 0.15){
						assigned = 1;
					}
					else if(polarity < 0 && confidence < -0.15){
						assigned = -1;
						
					}
					
					sentence.setPolarity(assigned);
					referedClause.setPolarity(assigned);
					System.out.println(comment.getId()+"-"+sentence.getId()+": "+response.getBody().toString()+" -> "+ assigned);
				}
			}
		}
		
		// Write to file
		File result = new File(output);
		serializer.write(corpus, result);
		
	}
	
	public static void main(String args[]) throws Exception {
		String INPUT = "sampleInput_unclassified.xml";
		String OUTPUT = "sampleInput_chatter.xml";
		
		evaluate(INPUT, OUTPUT);
	}
}
