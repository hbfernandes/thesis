package pt.uevora.hfernandes;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.evaluate.Evaluation2;
import pt.uevora.hfernandes.objects.Alvo;
import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Sentence;

public class OneEntityExtract {
	
	private static void goldSentSample(String gold, String input) throws Exception{
		Evaluation2 eval = new Evaluation2();
		List<String> correctNer = eval.getCorrectNer(gold, input); // {COMMENT_ID-SENTENCE_ID:ALVO}
				
		Serializer serializer = new Persister();
		File source = new File(gold);

		Corpus corpus = serializer.read(Corpus.class, source);
		Corpus sampleCorpus = new Corpus();
		
		String nerKey;
		Comment sampleComment;
		
		
		// count sentence entity occurrence
		Map<String, Integer> counts = new HashMap<String, Integer>();
		for (Comment comment : corpus.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				nerKey = comment.getId()+"-"+sentence.getId();
				if(nerKey.equals("142-5") || nerKey.equals("357-5")){
					counts.put(nerKey, 2);
				}
				
				if(!counts.containsKey(nerKey)){
					counts.put(nerKey, 0);
				}
				
				counts.put(nerKey, counts.get(nerKey)+1);
			}
		}
		
		
		for (Comment comment : corpus.getComments()) {
			sampleComment = new Comment(comment.getId());
			sampleCorpus.getComments().add(sampleComment);
			for (Sentence sentence : comment.getSentences()) {
				nerKey = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo();
				
				// entry is in the correct ner and not in the list already
				if(correctNer.contains(nerKey) && counts.get(comment.getId()+"-"+sentence.getId()).equals(1)){
					sampleComment.getSentences().add(sentence.getSentenceCopy());
					
					System.out.println(comment.getId()+"-"+sentence.getId());
				}
			}
		}
		
		serializer.write(sampleCorpus, new File("CorrectOneEntitySentences_Gold.xml"));
	}
	
	private static void inputSentSample(String gold, String input) throws Exception{
		Evaluation2 eval = new Evaluation2();
		List<String> correctNer = eval.getCorrectNer(gold, input); // {COMMENT_ID-SENTENCE_ID:ALVO}
				
		Serializer serializer = new Persister();
		File source = new File(input);

		Corpus goldCorpus = serializer.read(Corpus.class, new File(gold));
		Corpus corpus = serializer.read(Corpus.class, source);
		Corpus sampleCorpus = new Corpus();
		
		String nerKey;
		Comment sampleComment;
		
		
		// count sentence entity occurrence
		Map<String, Integer> counts = new HashMap<String, Integer>();
		for (Comment comment : goldCorpus.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				nerKey = comment.getId()+"-"+sentence.getId();
				if(nerKey.equals("142-5") || nerKey.equals("357-5")){
					counts.put(nerKey, 2);
				}
				
				if(!counts.containsKey(nerKey)){
					counts.put(nerKey, 0);
				}
				
				counts.put(nerKey, counts.get(nerKey)+1);
			}
		}
		
		
		for (Comment comment : corpus.getComments()) {
			sampleComment = new Comment(comment.getId());
			sampleCorpus.getComments().add(sampleComment);
			for (Sentence sentence : comment.getSentences()) {
				nerKey = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo();
				
				// entry is in the correct ner and not in the list already
				if(correctNer.contains(nerKey) && counts.get(comment.getId()+"-"+sentence.getId()).equals(1)){
					sampleComment.getSentences().add(sentence.getSentenceCopy());
					
					System.out.println(comment.getId()+"-"+sentence.getId());
				}
			}
		}
		
		serializer.write(sampleCorpus, new File("CorrectOneEntitySentences_input.xml"));
	}
	
	
	private static void generateTxt(String file) throws Exception{
		Serializer serializer = new Persister();
		File source = new File(file);
		File posDir = new File("weka/positive");
		File negDir = new File("weka/negative");
		File neutDir = new File("weka/neutral");
		
		FileOutputStream fos;

		Corpus goldCorpus = serializer.read(Corpus.class, source);
		
		int i = 0;
		for (Comment comment : goldCorpus.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				for (Alvo alvo : sentence.getAlvos()) {
					sentence.setValue(sentence.getValue().replace(alvo.getValue(), "XYZ"));
				}
				
				i++;
				switch (sentence.getPolarity()) {
					case 1:
							fos = new FileOutputStream(new File(posDir, i+".txt"));
							IOUtils.write(sentence.getValue(), fos);
							IOUtils.closeQuietly(fos);
							break;
					case -1:
							fos = new FileOutputStream(new File(negDir, i+".txt"));
							IOUtils.write(sentence.getValue(), fos);
							IOUtils.closeQuietly(fos);
							break;
					case 0:
							fos = new FileOutputStream(new File(neutDir, i+".txt"));
							IOUtils.write(sentence.getValue(), fos);
							IOUtils.closeQuietly(fos);
							break;

				default:
					break;
				}
				
			}
		}
	}
	
	

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String INPUT_CLASSIFIED = "output/SentiCorpus-PT_sentiment_7.xml";
		
//		goldSentSample(Files.GOLD, INPUT_CLASSIFIED);
//		inputSentSample(Files.GOLD, INPUT_CLASSIFIED);
//		generateTxt("output/CorrectOneEntitySentences_Gold.xml");
		generateTxt("resources/Sentituites.xml");
	}

}
