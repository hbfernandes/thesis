package pt.uevora.hfernandes.sentiment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.inductiva.semantic.client.SearchClient;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.objects.Clause;
import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Sentence;
import pt.uevora.hfernandes.objects.SentiLexEntry;
import pt.uevora.hfernandes.objects.Token;
import pt.uevora.hfernandes.parsers.SentiLexParser;


/**
 * Semantic relations for unknown items
 * 
 * @author hfernandes
 *
 */
public class Sentiment7 {
	private Map<String, SentiLexEntry> LEXICON;
	PropertiesConfiguration THESAURUS;
	private double adj = 0, verb = 0, name = 0;
	private double madj = 0, mverb = 0, mname = 0;
	private double msadj = 0, msverb = 0, msname = 0;
	
	SearchClient client = new SearchClient("http://rose.xdi.uevora.pt:8080/semantic/");
	
	private void evaluate(String lexicon, String input, String output, String results) throws Exception{
		// get the lexicon ready
		LEXICON = SentiLexParser.parse(lexicon);
		THESAURUS = new PropertiesConfiguration("resources/OpenThesaurusPT.properties");
		
		String[] negatives = new String[]{"NÃO", "NAO"}; // Adverbios de negação
		String[] decreasers = new String[]{"POUCO", "MENOS"};
		String[] increasers = new String[]{"MUITO", "MAIS"};
		
		// grab the input
		Serializer serializer = new Persister();
		File inputFile = new File(input);
		
		Corpus corpus = serializer.read(Corpus.class, inputFile);
		
		Clause referedClause;
		boolean invertPolarity, increasePolarity, decreasePolarity;
		int invertToken = 0, increaseToken = 0, decreaseToken = 0;
		double polarity;
		for (Comment comment : corpus.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				polarity = 0;
				invertPolarity = false;
				increasePolarity = false;
				decreasePolarity = false;
				referedClause = sentence.getReferedClause();
				
				if(referedClause != null){
					for (Token token : referedClause.getGramaticalCategories()) {
						
						if(token == null || token.getText() == null || token.getText().isEmpty()){
							continue;
						}
						
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
						// F1: 0.44657534
						if(token.getType().equals("PUNCT")){
							invertPolarity = false;
							continue;
						}
						
						// F1: 0.4454
//						if(invertToken > 2){ 
//							invertPolarity = false;
//						}
						
						if(increaseToken > 2){
							increasePolarity = false;
						}
						
						if(decreaseToken > 2){
							decreasePolarity = false;
						}
						/*************************************/
						
						polarity += getPolarity(token, invertPolarity, increasePolarity, decreasePolarity);
						
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
		
		FileOutputStream out = new FileOutputStream(results);
		List<String> outputLines = new ArrayList<String>();
		
		System.out.println("Before synonym check:");
		outputLines.add("Before synonym check:");
		System.out.println("Verbs matched: "+mverb+"/"+verb+" : "+mverb/verb);
		outputLines.add("Verbs matched: "+mverb+"/"+verb+" : "+mverb/verb);
		System.out.println("Names matched: "+mname+"/"+name+" : "+mname/name);
		outputLines.add("Names matched: "+mname+"/"+name+" : "+mname/name);
		System.out.println("Adjectives matched: "+madj+"/"+adj+" : "+madj/adj);
		outputLines.add("Adjectives matched: "+madj+"/"+adj+" : "+madj/adj);
		
		System.out.println("");
		outputLines.add("\n");
		
		System.out.println("After synonym check:");
		outputLines.add("After synonym check:");
		System.out.println("Verbs matched: "+(mverb+msverb)+"/"+verb+" : "+(mverb+msverb)/verb);
		outputLines.add("Verbs matched: "+(mverb+msverb)+"/"+verb+" : "+(mverb+msverb)/verb);
		System.out.println("Names matched: "+(mname+msname)+"/"+name+" : "+(mname+msname)/name);
		outputLines.add("Names matched: "+(mname+msname)+"/"+name+" : "+(mname+msname)/name);
		System.out.println("Adjectives matched: "+(madj+msadj)+"/"+adj+" : "+(madj+msadj)/adj);
		outputLines.add("Adjectives matched: "+(madj+msadj)+"/"+adj+" : "+(madj+msadj)/adj);
		
		IOUtils.writeLines(outputLines, "\n", out);
		
		// Write to file
		File result = new File(output);
		serializer.write(corpus, result);
		
	}
	
	private double getPolarity(Token token, boolean invertPolarity, boolean increasePolarity, boolean decreasePolarity) throws Exception{
		String key;
		double polarity;
		int lexiconPol;
		List<String> synonyms;// 1:0.4197 5/2:0.4465 6/2:0.44931507
		double weightAdj=1, weightNam = 0.6, weightVerb = 0.2;
		
		if(token.getType().equals("ADJ")){
			adj++;
			key = token.getText().toLowerCase()+";"+"ADJ";
			if(LEXICON.containsKey(key)){
				madj++;
				polarity = LEXICON.get(key).getPoln0();
				
			}
			else{
				synonyms = getSynonym(token);
				polarity = 0;
				for (String synonym : synonyms) {
					key = synonym+";"+"ADJ";
					
					// search lexicon for each synonym
					if(LEXICON.containsKey(key)){
						lexiconPol = LEXICON.get(key).getPoln0();
						polarity += lexiconPol;
					}
				}
								
				//check if for any hits
				if(polarity != 0){
					msadj++;
					
					//normalize
					if(polarity > 0){
						polarity = 1;
					}
					if(polarity < 0){
						polarity = -1;
					}
					
				}
			}
			
			if(invertPolarity){
				token.setPolarity(polarity*-1); // assume n0
				token.setInverted(true);
			}
			else{
				token.setPolarity(polarity); // assume n0
			}
			
			if(increasePolarity){
				token.setPolarity(token.getPolarity()*2);
			}
			
			if(decreasePolarity){
				token.setPolarity(token.getPolarity()/2);
			}
			
			return token.getPolarity()*weightAdj;
		}
		
		if(token.getType().equals("N")){
			name++;
			key = token.getText().toLowerCase()+";"+"N";
			if(LEXICON.containsKey(key)){
				mname++;
				polarity = LEXICON.get(key).getPoln0();
			}
			else{
				synonyms = getSynonym(token);
				polarity = 0;
				for (String synonym : synonyms) {
					key = synonym+";"+"N";
					
					// search lexicon for each synonym
					if(LEXICON.containsKey(key)){
						lexiconPol = LEXICON.get(key).getPoln0();
						polarity += lexiconPol;
					}
				}
								
				//check if for any hits
				if(polarity != 0){
					msname++;
					
					//normalize
					if(polarity > 0){
						polarity = 1;
					}
					if(polarity < 0){
						polarity = -1;
					}
				
				}
			}
			
			// assign
			if(invertPolarity){
				token.setPolarity(polarity*-1); // assume n0
				token.setInverted(true);
			}
			else{
				token.setPolarity(polarity); // assume n0
			}
			
			if(increasePolarity){
				token.setPolarity(token.getPolarity()*2);
			}
			
			if(decreasePolarity){
				token.setPolarity(token.getPolarity()/2);
			}
			
			return token.getPolarity()*weightNam;
		}	
		
		if(token.getType().startsWith("V-")){
			verb++;
			key = token.getText().toLowerCase()+";"+"V";
			if(LEXICON.containsKey(key)){
				mverb++;
				polarity = LEXICON.get(key).getPoln0();
			}
			else{
				synonyms = getSynonym(token);
				polarity = 0;
				for (String synonym : synonyms) {
					key = synonym+";"+"V";
					
					// search lexicon for each synonym
					if(LEXICON.containsKey(key)){
						lexiconPol = LEXICON.get(key).getPoln0();
						polarity += lexiconPol;
					}
				}
								
				//check if for any hits
				if(polarity != 0){
					msverb++;
					
					//normalize
					if(polarity > 0){
						polarity = 1;
					}
					if(polarity < 0){
						polarity = -1;
					}
					
				}
			}
			
			if(invertPolarity){
				token.setPolarity(polarity*-1); // assume n0
				token.setInverted(true);
			}
			else{
				token.setPolarity(polarity); // assume n0
			}
			
			if(increasePolarity){
				token.setPolarity(token.getPolarity()*2);
			}
			
			if(decreasePolarity){
				token.setPolarity(token.getPolarity()/2);
			}
			
			return token.getPolarity()*weightVerb;
		}	
		
		return 0.0;
	}
	
	private List getSynonym(Token token) throws Exception{
		
//		return THESAURUS.getList(token.getType().startsWith("V-") ? token.getInfinitive() : token.getText().toLowerCase());
		if(token.getText().startsWith("http"))
			return new ArrayList<String>();
		
		return client.getSynonymList(token.getType().startsWith("V-") ? token.getInfinitive() : token.getText().toLowerCase());
	}
	
	public static void main(String args[]) throws Exception {
		String LEXICON = "resources/SentiLex-flex-PT02.txt";
//		String INPUT = "output/SentiCorpus-PT_ner_4.xml";
//		String OUTPUT = "output/SentiCorpus-PT_sentiment_7.xml";
//		String eval = "output/SentiCorpus-PT_sentiment_match_7.txt";
		
		String INPUT = "output/Sentituites_ner_4.xml";
		String OUTPUT = "output/Sentituites_sentiment_7.xml";
		String eval = "output/Sentituites_sentiment_match_7.txt";
		
		Sentiment7 sent = new Sentiment7();
		
		sent.evaluate(LEXICON, INPUT, OUTPUT, eval);
//		new Evaluation2().evaluate8();
	}
}
