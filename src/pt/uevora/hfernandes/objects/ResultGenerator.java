package pt.uevora.hfernandes.objects;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import org.apache.commons.io.FileUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.cortex.sentiment.parsers.ResourceNormalizer;


public class ResultGenerator {

	
	private static void generateResults(Corpus corpus, File outputDir) throws Exception{
		File outFile;  
		String filename;
		String result;
		List<String> lines;
		
		for (Comment paragraph : corpus.getComments()) {
			filename = paragraph.getId() + ".txt";
			outFile = new File(outputDir, filename);
			lines = new ArrayList<>();
			
			for (Sentence sentence : paragraph.getSentences()) {
				if(sentence.getAlvo() != null){
						if(sentence.getPolarity() > 0){
							result = "positive";
						}
						else if(sentence.getPolarity() < 0){
							result = "negative";
						}
						else{
							result = "neutral";
						}
						
						lines.add(sentence.getAlvo()+":"+result);
					}
					
					
				}
			
			FileUtils.writeLines(outFile, lines);
		}
	}
	
	private static void generateResultsSTuites(Corpus corpus, File outputDir) throws Exception{
		File outFile;  
		String filename;
		String result;
		List<String> lines;
		
		for (Comment paragraph : corpus.getComments()) {
			for (Sentence sentence : paragraph.getSentences()) {
				filename = sentence.getId() + ".txt";
				outFile = new File(outputDir, filename);
				lines = new ArrayList<>();
				if(sentence.getAlvo() != null){
						if(sentence.getPolarity() > 0){
							result = "positive";
						}
						else if(sentence.getPolarity() < 0){
							result = "negative";
						}
						else{
							result = "neutral";
						}
						
						lines.add(sentence.getAlvo()+":"+result);
					}
					
					
				FileUtils.writeLines(outFile, lines);
				}
			
		}
	}
	
	private static void generateInput(Corpus corpus, File outputDir) throws Exception{
		File outFile;  
		String filename;
		String result;
		List<String> lines;
		
		for (Comment paragraph : corpus.getComments()) {
			filename = paragraph.getId() + ".txt";
			outFile = new File(outputDir, filename);
			lines = new ArrayList<>();
			
			for (Sentence sentence : paragraph.getSentences()) {
				lines.add(sentence.getValue());
			}
			
			FileUtils.writeLines(outFile, lines);
		}
	}
	
	private static void generateInputSTuites(Corpus corpus, File outputDir) throws Exception{
		File outFile;  
		String filename;
		String result;
		List<String> lines;
		
		InputStream modelIn = ResourceNormalizer.class.getResourceAsStream("/pt-sent.bin");
		SentenceModel model = new SentenceModel(modelIn);
	  
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		String newSentences[];
		for (Comment paragraph : corpus.getComments()) {
			
			for (Sentence sentence : paragraph.getSentences()) {
				filename = sentence.getId() + ".txt";
				outFile = new File(outputDir, filename);
				lines = new ArrayList<>();

				newSentences = sentenceDetector.sentDetect(sentence.getValue());
				
				for (String string : newSentences) {
					lines.add(string);
				}
				
				FileUtils.writeLines(outFile, lines);
			}
			
		}
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
//		File outputDir = new File("/home/hfernandes/git/sentiment/sentiment-resources/corpus/Sentituites-PT/results");
//		Serializer serializer = new Persister();
//		Corpus text = serializer.read(Corpus.class, 
//				new File("/home/hfernandes/git/sentiment/sentiment-resources/hf-format/Sentituites_gold.xml")); 
//		generateResultsSTuites(text, outputDir);
		
		
		File outputDir = new File("/home/hfernandes/git/sentiment/sentiment-resources/corpus/Sentituites-PT/input");
		Serializer serializer = new Persister();
		Corpus text = serializer.read(Corpus.class, 
				new File("/home/hfernandes/git/sentiment/sentiment-resources/hf-format/Sentituites_gold.xml")); 
		generateInputSTuites(text, outputDir);

		
		
	}

}
