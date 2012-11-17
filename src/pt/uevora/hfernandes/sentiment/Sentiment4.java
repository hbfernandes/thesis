package pt.uevora.hfernandes.sentiment;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.evaluate.Evaluation2;
import pt.uevora.hfernandes.objects.Clause;
import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Sentence;
import pt.uevora.hfernandes.objects.SentiLexEntry;
import pt.uevora.hfernandes.objects.Token;
import pt.uevora.hfernandes.parsers.SentiLexParser;


/**
 * Implement Negation, increasers/decreasers
 * Polarity of the clause is inverted from the point of the negative symbol until the next punctuation
 * 
 * @author hfernandes
 *
 */
public class Sentiment4 {
	private static void evaluate(String lexicon, String input, String output) throws Exception{
		// get the lexicon ready
		Map<String, SentiLexEntry> lexiconMap = SentiLexParser.parse(lexicon);
		
		String[] negatives = new String[]{"NÃO", "NAO"};
		String[] decreasers = new String[]{"POUCO", "MENOS"};
		String[] increasers = new String[]{"MUITO", "MAIS"};
		
		// grab the input
		Serializer serializer = new Persister();
		File inputFile = new File(input);
		
		Corpus corpus = serializer.read(Corpus.class, inputFile);
		
		String key;
		Clause referedClause;
		boolean invertPolarity, increasePolarity, decreasePolarity;
		int polarity;
		int invertToken = 0, increaseToken = 0, decreaseToken = 0;
		int lexiconPol;
		for (Comment comment : corpus.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				polarity = 0;
				invertPolarity = false;
				increasePolarity = false;
				decreasePolarity = false;
				referedClause = sentence.getReferedClause();
				
				if(referedClause != null){
					for (Token token : referedClause.getGramaticalCategories()) {
						
						/*********** Negation *****************/
						invertToken++;
						increaseToken++;
						decreaseToken++;
						
						// negative reference check
						if(ArrayUtils.contains(negatives, token.getText().toUpperCase())){
							invertPolarity = true;
							invertToken = 0;
							continue;
						}
						
						if(ArrayUtils.contains(increasers, token.getText().toUpperCase())){
							increasePolarity = true;
							increaseToken = 0;
							continue;
						}
						
						if(ArrayUtils.contains(decreasers, token.getText().toUpperCase())){
							decreasePolarity = true;
							decreaseToken = 0;
							continue;
						}
						
						// Stop negation if we hit punctuation
//						if(token.getType().equals("PUNCT")){
//							invertPolarity = false;
//							continue;
//						}
						
						if(invertToken > 2){
							invertPolarity = false;
						}
						
						if(increaseToken > 2){
							increasePolarity = false;
						}
						
						if(decreaseToken > 2){
							decreasePolarity = false;
						}
						
						/*************************************/
						
						if(token.getType().equals("ADJ")){
							key = token.getText()+";"+"ADJ";
							if(lexiconMap.containsKey(key)){
								lexiconPol = lexiconMap.get(key).getPoln0();
								
								if(invertPolarity){
									token.setPolarity(lexiconPol*-1); // assume n0
									token.setInverted(true);
								}
								else{
									token.setPolarity(lexiconPol); // assume n0
								}
								
								if(increasePolarity){
									token.setPolarity(token.getPolarity()*2);
								}
								
								if(decreasePolarity){
									token.setPolarity(token.getPolarity()/2);
								}
								
								polarity += token.getPolarity();
							}
						}
						
						if(token.getType().equals("N")){
							key = token.getText()+";"+"N";
							if(lexiconMap.containsKey(key)){
								lexiconPol = lexiconMap.get(key).getPoln0();
								
								if(invertPolarity){
									token.setPolarity(lexiconPol*-1); // assume n0
									token.setInverted(true);
								}
								else{
									token.setPolarity(lexiconPol); // assume n0
								}
								
								if(increasePolarity){
									token.setPolarity(token.getPolarity()*2);
								}
								
								if(decreasePolarity){
									token.setPolarity(token.getPolarity()/2);
								}
								
								polarity += token.getPolarity();
							}
						}	
						
						if(token.getType().equals("V-FIN")){
							key = token.getText()+";"+"V";
							if(lexiconMap.containsKey(key)){
								lexiconPol = lexiconMap.get(key).getPoln0();
								
								if(invertPolarity){
									token.setPolarity(lexiconPol*-1); // assume n0
									token.setInverted(true);
								}
								else{
									token.setPolarity(lexiconPol); // assume n0
								}
								
								if(increasePolarity){
									token.setPolarity(token.getPolarity()*2);
								}
								
								if(decreasePolarity){
									token.setPolarity(token.getPolarity()/2);
								}
								
								polarity += token.getPolarity();
							}
						}	
						
					}
					if(polarity > 0){
						sentence.setPolarity(1);
						referedClause.setPolarity(1);
					}
					if(polarity < 0){
						sentence.setPolarity(-1);
						referedClause.setPolarity(-1);
					}
				}
			}
		}
		
		// Write to file
		File result = new File(output);
		serializer.write(corpus, result);
		
	}
	
	public static void main(String args[]) throws Exception {
		String LEXICON = "resources/SentiLex-flex-PT02.txt";
		String INPUT = "output/SentiCorpus-PT_ner_2.xml";
		String OUTPUT = "output/SentiCorpus-PT_sentiment_4.xml";
		
		evaluate(LEXICON, INPUT, OUTPUT);
		new Evaluation2().evaluate5();
	}
}
