package pt.uevora.hfernandes.sentiment;

import java.io.File;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Sentence;
import pt.uevora.hfernandes.objects.SentiLexEntry;
import pt.uevora.hfernandes.objects.Token;
import pt.uevora.hfernandes.parsers.SentiLexParser;

/**
 * Adjectives per sentence
 * 
 * @author hfernandes
 *
 */
public class Sentiment0 {
	
	private static void evaluate(String lexicon, String input, String output) throws Exception{
		// get the lexicon ready
		Map<String, SentiLexEntry> lexiconMap = SentiLexParser.parse(lexicon);
		
		// grab the input
		Serializer serializer = new Persister();
		File inputFile = new File(input);
		
		Corpus corpus = serializer.read(Corpus.class, inputFile);
		
		String key;
		int sentencePol;
		for (Comment comment : corpus.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				sentencePol = 0;
				for (Token token : sentence.getTokens()) {
					if(token.getType().equals("ADJ")){
						// assume subjectivity in the presence of adjectives (!)
						sentence.setHasOpinion(true);
						
						key = token.getText()+";"+"ADJ";
						if(lexiconMap.containsKey(key)){
							token.setPolarity(lexiconMap.get(key).getPoln0()); // assume n0
							
							sentencePol += token.getPolarity();
						}
					}	
				}
				if(sentencePol > 0){
					sentence.setPolarity(1);
				}
				if(sentencePol < 0){
					sentence.setPolarity(-1);
				}
			}
		}
		
		// Write to file
		File result = new File(output);
		serializer.write(corpus, result);
		
	}
	
	public static void main(String args[]) throws Exception {
		String LEXICON = "resources/SentiLex-flex-PT02.txt";
		String INPUT = "output/SentiCorpus-PT_ner_0.xml";
		String OUTPUT = "output/SentiCorpus-PT_sentiment_0.xml";
		
		evaluate(LEXICON, INPUT, OUTPUT);
	}

}
