package pt.uevora.hfernandes.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Sentence;
import pt.uevora.hfernandes.objects.Token;


public class LXPOSTagParser {

	/**
	 * Lost usage when after using LXPOSTagger2, only processSentence is needed
	 */
	@Deprecated
	public static Corpus parse(File input) throws IOException{
		BufferedReader breader = new BufferedReader(new FileReader(input));
		
		// Objects
		int commentID = 1;
		int sentenceID = 0;
		Corpus text = new Corpus();
		Comment comment = new Comment(commentID);
		Sentence sentence;
		
		text.getComments().add(comment); // initial
		
		String line;
		while ((line = breader.readLine()) != null) { // Read the file line by line
			if(line.isEmpty()){ // Blank line means end of paragraph, create new one
				if(!comment.getSentences().isEmpty()){
					commentID++;
					sentenceID = 0;
					comment = new Comment(commentID);
					text.getComments().add(comment);
				}
					continue;
			}
			
			sentenceID++;
			sentence = new Sentence(sentenceID);
			sentence.setPosValue(StringUtils.trim(line));
			
			// parse sentence tokens
			processSentence(sentence);
			
			comment.getSentences().add(sentence);
		}
		
		if(comment.getSentences().isEmpty()){
			text.getComments().remove(comment);
		}
		return text;
	}

    public static void processSentence(Sentence sentence) {
    	Token token;
    	
    	String[] splitSentence = sentence.getPosValue().split(" ");
    	for (int i = 0; i < splitSentence.length; i++) {
    		token = new Token();
    		token.setTextAndType(splitSentence[i]); //Store raw token
    		token.setIndex(i);
    		
    		// special case for / symbol
    		if(splitSentence[i].equals("//SYB") || splitSentence[i].equals("//PNT")){
    			token.setText("/");
    			token.setType(splitSentence[i].substring(2));
    		}
    		else if(splitSentence[i].contains("*//")){
    			if(splitSentence[i].startsWith("\\*")){
    				token.setText(splitSentence[i].split("\\*//")[0].replace("\\*", ""));
    			}
    			else{
    				token.setText(splitSentence[i].split("\\*//")[0]);
    			}
    			token.setType(splitSentence[i].split("\\*//")[1]);
    		}
    		else{
    			token.setText(splitSentence[i].split("/")[0]);
    			token.setType(splitSentence[i].split("/")[1]);
    		}
    		sentence.getTokens().add(token);
		}
    	
    	if(sentence.getValue() == null){
    		sentence.setValue(sentence.getRawSentence());
    	}
    }

    public static void main(String[] args) throws IOException {
		File corpusFile = new File("resources/SentiCorpus-PT_clean2_pos-tagged.txt");
	        Corpus parse = parse(corpusFile);
			
	        parse.printSubjective();
			
    }


}