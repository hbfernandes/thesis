package pt.uevora.hfernandes.evaluate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.Files;
import pt.uevora.hfernandes.objects.Clause;
import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Sentence;

@Deprecated
public class Evaluation {

	private int GOLD_NER_SENTENCES = 0;
	private int GOLD_NER = 0;

	private int INPUT_NER_SENTENCES = 0;
	private int INPUT_NER = 0;

	private int GOLD_SENT_FULL_SENTENCES = 0;
	private int GOLD_SENT_FULL = 0;
	private int GOLD_POS_FULL = 0;
	private int GOLD_NEG_FULL = 0;
	private int GOLD_NEUT_FULL = 0;

	private int GOLD_SENT_SENTENCES = 0;
	private int GOLD_SENT = 0;
	private int GOLD_POS = 0;
	private int GOLD_NEG = 0;
	private int GOLD_NEUT = 0;

	private int INPUT_SENT_FULL_SENTENCES = 0;
	private int INPUT_SENT_FULL = 0;
	private int INPUT_POS_FULL = 0;
	private int INPUT_NEG_FULL = 0;
	private int INPUT_NEUT_FULL = 0;

	private int INPUT_SENT_SENTENCES = 0;
	private int INPUT_SENT = 0;
	private int INPUT_POS = 0;
	private int INPUT_NEG = 0;
	private int INPUT_NEUT = 0;

	/**
	 * 
	 * LOAD into a map the gold file
	 * 
	 * FORMAT
	 * 
	 * NER EVALUATION
	 * COMMENT_ID-SENTENCE_ID -> {ALVO}
	 */
	private Map<String, List<String>> loadGoldNER(String GOLD) throws Exception{
		Serializer serializer = new Persister();
		File source = new File(GOLD);

		Corpus gold = serializer.read(Corpus.class, source);
		
		int lastSentId = -1;
		String key;
		Map<String, List<String>> goldList = new HashMap<String, List<String>>();
		for (Comment comment : gold.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				key = comment.getId()+"-"+sentence.getId();
				
				if(!goldList.containsKey(key)){
					goldList.put(key, new ArrayList<String>());
				}
				
				// DONT ADD DUPLICATES FOR NOW
				if(goldList.get(key).contains(sentence.getAlvo())){
					continue;
				}
				
				goldList.get(key).add(sentence.getAlvo());
				
				GOLD_NER++;
				if(sentence.getId() != lastSentId){ // ignore repeated sentences
					GOLD_NER_SENTENCES++;
					lastSentId = sentence.getId();
				}
			}
		}
		
		return goldList;
	}
	
	/**
	 * 
	 * LOAD into a map the full sentiment gold file (full system evaluation)
	 * 
	 * FORMAT
	 * 
	 * SENTIMENT EVALUATION ALL
	 * COMMENT_ID-SENTENCE_ID -> {POLARITY:ALVO}
	 */
	private Map<String, List<String>> loadGoldSentFull(String GOLD) throws Exception{
		Serializer serializer = new Persister();
		File source = new File(GOLD);

		Corpus gold = serializer.read(Corpus.class, source);
		
		int lastSentId = -1;
		String key;
		Map<String, List<String>> goldList = new HashMap<String, List<String>>();
		for (Comment comment : gold.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				key = comment.getId()+"-"+sentence.getId();
				
				if(!goldList.containsKey(key)){
					goldList.put(key, new ArrayList<String>());
				}
				
				// IGNORE DUPLICATES FOR NOW
				if(goldList.get(key).contains(sentence.getPolarity()+":"+sentence.getAlvo())){
					continue;
				}
				
				goldList.get(key).add(sentence.getPolarity()+":"+sentence.getAlvo());
				
				switch(sentence.getPolarity()){
					case 1: GOLD_POS_FULL++; break;
					case -1: GOLD_NEG_FULL++; break;
					case 0: GOLD_NEUT_FULL++; break;
					
					default: break;
				}
				
				GOLD_SENT_FULL++; // all opinion
				
				if(sentence.getId() != lastSentId){ // ignore repeated sentences
					GOLD_SENT_FULL_SENTENCES++;
					lastSentId = sentence.getId();
				}
				
			}
		}
		
		return goldList;
	}
	
	/**
	 * 
	 * LOAD into a map the sentiment gold file (sentiment evaluation only)
	 * 
	 * FORMAT
	 * 
	 * SENTIMENT EVALUATION RESTRICTED TO CORRECT ENTITIES
	 * COMMENT_ID-SENTENCE_ID -> {POLARITY:ALVO}
	 */
	private Map<String, List<String>> loadGoldSent(String gold, String input) throws Exception{
		Map<String, List<String>> ner = getCorrectNer(gold, input); // Entities to consider
				
		Serializer serializer = new Persister();
		File source = new File(gold);

		Corpus goldCorpus = serializer.read(Corpus.class, source);
		
		int lastSentId = -1;
		String key;
		Map<String, List<String>> goldList = new HashMap<String, List<String>>();
		for (Comment comment : goldCorpus.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				key = comment.getId()+"-"+sentence.getId();
				
				// skip this entry if its not in the NER hits
				if(!ner.containsKey(key) || !ner.get(key).contains(sentence.getAlvo())){
					continue;
				}
				
				// initialize if needed
				if(!goldList.containsKey(key)){
					goldList.put(key, new ArrayList<String>());
				}
				
				// IGNORE DUPLICATES, MAKES NO SENSE IN INCLUDING
				if(goldList.get(key).contains(sentence.getPolarity()+":"+sentence.getAlvo())){
					continue;
				} 
								
				goldList.get(key).add(sentence.getPolarity()+":"+sentence.getAlvo());
				
				switch(sentence.getPolarity()){
					case 1: GOLD_POS++; break;
					case -1: GOLD_NEG++; break;
					case 0: GOLD_NEUT++; break;
					
					default: break;
				}
				
				GOLD_SENT++; // all opinion
				
				if(sentence.getId() != lastSentId){ // ignore repeated sentences
					GOLD_SENT_SENTENCES++;
					lastSentId = sentence.getId();
				}
				
			}
		}
		
		return goldList;
	}
	
	
	private void goldSentSample(String gold, String input) throws Exception{
		Map<String, List<String>> ner = getCorrectNer(gold, input); // Entities to consider
				
		Serializer serializer = new Persister();
		File source = new File(gold);

		Corpus goldCorpus = serializer.read(Corpus.class, source);
		Corpus sampleCorpus = new Corpus();
		
		int i=0;
		String key;
		Map<String, List<String>> goldList = new HashMap<String, List<String>>();
		for (Comment comment : goldCorpus.getComments()) {
			if(i > 400){
				break;
			}
			
			Comment sampleComment = new Comment();
			sampleComment.setId(comment.getId());
			sampleCorpus.getComments().add(sampleComment);
			for (Sentence sentence : comment.getSentences()) {
				key = comment.getId()+"-"+sentence.getId();
				
				// skip this entry if its not in the NER hits
				if(!ner.containsKey(key) || !ner.get(key).contains(sentence.getAlvo())){
					continue;
				}
				
				// initialize if needed
				if(!goldList.containsKey(key)){
					goldList.put(key, new ArrayList<String>());
				}
				
				// IGNORE DUPLICATES, MAKES NO SENSE IN INCLUDING
				if(goldList.get(key).contains(sentence.getPolarity()+":"+sentence.getAlvo())){
					continue;
				} 
				
				sampleComment.getSentences().add(sentence.getSentenceCopy());
				i++;
			}
		}
		System.out.println("sentences: "+i);
		serializer.write(sampleCorpus, new File("sampleGold.xml"));
	}
	
	private void inputSentSample(String gold, String input) throws Exception{
		Map<String, List<String>> ner = getCorrectNer(gold, input); // Entities to consider
		
		Serializer serializer = new Persister();
		File source = new File(input);

		Corpus inputCorpus = serializer.read(Corpus.class, source);
		Corpus sampleCorpus = new Corpus();
		
		int i=0;
		String key;
		Map<String, List<String>> inputList = new HashMap<String, List<String>>();
		for (Comment comment : inputCorpus.getComments()) {
			if(i > 400){
				break;
			}
			
			Comment sampleComment = new Comment();
			sampleComment.setId(comment.getId());
			sampleCorpus.getComments().add(sampleComment);
			
			
			for (Sentence sentence : comment.getSentences()) {
				key = comment.getId()+"-"+sentence.getId();
				
				// skip this entry if its not in the NER hits
				if(!ner.containsKey(key) || !ner.get(key).contains(sentence.getAlvo())){
					continue;
				}
				
				// initialize 
				if(!inputList.containsKey(key)){
					inputList.put(key, new ArrayList<String>());
				}
				
				if(sentence.getAlvo() != null){
					inputList.get(key).add(sentence.getPolarity()+":"+sentence.getAlvo());
					
					sampleComment.getSentences().add(sentence.getSentenceCopy());
					i++;
				}
				
			}
		}
		
		System.out.println("sentences: "+i);
		serializer.write(sampleCorpus, new File("sampleInput.xml"));
	}
	
	
	private Map<String, List<String>> loadInputNER(String input) throws Exception{
		Serializer serializer = new Persister();
		File source = new File(input);

		Corpus inputCorpus = serializer.read(Corpus.class, source);
		
		int lastSentId = -1;
		Map<String, List<String>> inputList = new HashMap<String, List<String>>();
		for (Comment comment : inputCorpus.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				if(!inputList.containsKey(comment.getId()+"-"+sentence.getId())){
					inputList.put(comment.getId()+"-"+sentence.getId(), new ArrayList<String>());
				}
				
				if(sentence.getAlvo() != null){
					inputList.get(comment.getId()+"-"+sentence.getId()).add(sentence.getAlvo());
					INPUT_NER++;
				}
				
				if(sentence.getId() != lastSentId){ // ignore repeated sentences
					INPUT_NER_SENTENCES++;
					lastSentId = sentence.getId();
				}
			}
		}
		
		return inputList;
	}
	
	private Map<String, List<String>> loadInputSentFull(String input) throws Exception{
		Serializer serializer = new Persister();
		File source = new File(input);

		Corpus inputCorpus = serializer.read(Corpus.class, source);
		
		int lastSentId = -1;
		Map<String, List<String>> inputList = new HashMap<String, List<String>>();
		for (Comment comment : inputCorpus.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				if(!inputList.containsKey(comment.getId()+"-"+sentence.getId())){
					inputList.put(comment.getId()+"-"+sentence.getId(), new ArrayList<String>());
				}
				
				if(sentence.getAlvo() != null){
					inputList.get(comment.getId()+"-"+sentence.getId()).add(sentence.getPolarity()+":"+sentence.getAlvo());
					
					switch(sentence.getPolarity()){
						case 1: INPUT_POS_FULL++; break;
						case -1: INPUT_NEG_FULL++; break;
						case 0: INPUT_NEUT_FULL++; break;
						
						default: break;
					}
					
					INPUT_SENT_FULL++; // all opinions
					
				}
				
				if(sentence.getId() != lastSentId){ // ignore repeated sentences
					INPUT_SENT_FULL_SENTENCES++;
					lastSentId = sentence.getId();
				}
			}
		}
		
		return inputList;
	}
	
	private Map<String, List<String>> loadInputSent(String gold, String input) throws Exception{
		Map<String, List<String>> ner = getCorrectNer(gold, input); // Entities to consider
		
		Serializer serializer = new Persister();
		File source = new File(input);

		Corpus inputCorpus = serializer.read(Corpus.class, source);
		
		int lastSentId = -1;
		String key;
		Map<String, List<String>> inputList = new HashMap<String, List<String>>();
		for (Comment comment : inputCorpus.getComments()) {
			List<String> cenas = new ArrayList<String>();
			for (Sentence sentence : comment.getSentences()) {
				key = comment.getId()+"-"+sentence.getId();
				
				// skip this entry if its not in the NER hits
				if(!ner.containsKey(key) || !ner.get(key).contains(sentence.getAlvo())){
					continue;
				}
				
				// initialize 
				if(!inputList.containsKey(key)){
					inputList.put(key, new ArrayList<String>());
				}
				
//				if(cenas.contains(sentence.getId()+":"+sentence.getAlvo())){
//					System.out.println(comment.getId()+":"+sentence.getId()+":"+sentence.getAlvo());
//				}
				
				if(sentence.getAlvo() != null){
					inputList.get(key).add(sentence.getPolarity()+":"+sentence.getAlvo());
					cenas.add(sentence.getId()+":"+sentence.getAlvo());
					
					switch(sentence.getPolarity()){
						case 1: INPUT_POS++; break;
						case -1: INPUT_NEG++; break;
						case 0: INPUT_NEUT++; break;
						
						default: break;
					}
					
					INPUT_SENT++; // all opinions
					
				}
				
				if(sentence.getId() != lastSentId){ // ignore repeated sentences
					INPUT_SENT_SENTENCES++;
					lastSentId = sentence.getId();
				}
			}
		}
		
		return inputList;
	}
	
	
	/**
	 * AUXILIAR: Get the list of entities that were hits by the NER system
	 * Needed to evaluate the sentiment module only.
	 * 
	 * NER HITS, USED FOR SOLO SENT EVALUATION
	 * COMMENT_ID-SENTENCE_ID -> {ALVO}
	 */
	private Map<String, List<String>> getCorrectNer(String gold, String input) throws Exception{
		Map<String, List<String>> goldNER = loadGoldNER(gold);
		Map<String, List<String>> inputNER = loadInputNER(input);
		
		//get NER hits only
		Map<String, List<String>> goldInputNER = new HashMap<String, List<String>>();
		for (String key : goldNER.keySet()) {
			for (String goldEntry : goldNER.get(key)) {
				// gold hit, input is present on gold ner
				if(inputNER.get(key).contains(goldEntry)){
					// initialize if not
					if(!goldInputNER.containsKey(key)){
						goldInputNER.put(key, new ArrayList<String>());
					}
					goldInputNER.get(key).add(goldEntry);
				}
			}
		}
		
		return goldInputNER;
	}
	
	/****
	 * 
	 * EVALUATION METHODS
	 * 
	 * 
	 * P=TP/(TP+FP) 
	 * R=TP/(TP+FN)
	 * F1=2PR/(P+R)
	 * 
	 * **/
	
	public void evaluateNer(String gold, String input, String output) throws Exception{
		Map<String, List<String>> goldNER = loadGoldNER(gold);
		Map<String, List<String>> inputNER = loadInputNER(input);
		FileOutputStream out = new FileOutputStream(output);
		
		float tp = 0, fp = 0, fn = 0;
		for (String key : goldNER.keySet()) {
			for (String goldEntry : goldNER.get(key)) {
				// selected correct
				if(inputNER.get(key).contains(goldEntry)){
					tp++;
				}
				// not selected correct
				else{
					fn++;
				}
			}
			
			for (String inputEntry : inputNER.get(key)) {
				// selected not correct
				if(!goldNER.get(key).contains(inputEntry)){
					fp++;
				}
			}
		}
		
		List<String> outputLines = new ArrayList<String>();
		
		outputLines.add("### NER Evaluation ###");
		outputLines.add("\n# Gold #");
		outputLines.add("Sentences: " + GOLD_NER_SENTENCES + " Entities: " + GOLD_NER);
		
		outputLines.add("\n# Input #");
		outputLines.add("Sentences: " + INPUT_NER_SENTENCES + " Entities: " + INPUT_NER);
		
		outputLines.add("\nTP: "+tp+"   FP: "+fp);
		outputLines.add("FN: "+fn);
		
		float p = tp/(tp+fp);
		float r = tp/(tp+fn);
		float f1 = (2*(p*r))/(p+r);
		
		outputLines.add("\nP: "+p);
		outputLines.add("R: "+r);
		outputLines.add("F1: "+f1);
		
		System.out.println(StringUtils.join(outputLines, "\n"));
		IOUtils.writeLines(outputLines, "\n", out);
	}
	
	public void evaluateSent(String gold, String input, String output) throws Exception{
		Map<String, List<String>> goldSent = loadGoldSent(gold, input);
		Map<String, List<String>> inputSent = loadInputSent(gold, input);
		FileOutputStream out = new FileOutputStream(output);
		
		Serializer serializer = new Persister();
		File source = new File(input);
		Corpus corpus = serializer.read(Corpus.class, source);
		
		int polarity;
		float tpPos = 0, fpPos = 0, fnPos = 0;
		float tpNeg = 0, fpNeg = 0, fnNeg = 0;
		float tpNeut = 0, fpNeut = 0, fnNeut = 0;
		float tp = 0, fp = 0, fn = 0;
		for (String key : goldSent.keySet()) { // for each sentence
			for (String goldEntry : goldSent.get(key)) { // for each polarity of this sentence
				polarity = Integer.parseInt(goldEntry.split(":")[0]); // is "-1:Socrates" in the input for this sentence?

				// if the input has the gold value its a hit
				if(inputSent.get(key).contains(goldEntry)){
					List<String> list = inputSent.get(key);
					switch(polarity){
					case 1: tpPos++; break;
					case -1: tpNeg++; break;
					case 0: tpNeut++; break;
					default: break;
					}

					tp++;
				}
				// not selected correct
				else{
					switch(polarity){
					case 1: fnPos++;
								Sentence sentence = corpus.getSentence(Integer.parseInt(key.split("-")[0]), Integer.parseInt(key.split("-")[1]));
								Clause sentClause = sentence.getReferedClause();
								
								System.out.println(key + " : " + goldEntry + " : "+ sentClause.getTokenText());
								break;
								
					case -1: 	fnNeg++; 
								
//								Sentence sentence = corpus.getSentence(Integer.parseInt(key.split("-")[0]), Integer.parseInt(key.split("-")[1]));
//								Clause sentClause = sentence.getReferedClause();
//								
//								System.out.println(key + " : " + goldEntry + " : "+ sentClause.getTokenText());
								
								break;
					case 0: fnNeut++; break;
					default: break;
					}

					fn++;
					
					
				}

			}
			
			for (String inputEntry : inputSent.get(key)) { // for each input sentence of the current key
				// selected not correct
				if(!goldSent.get(key).contains(inputEntry)){ // is it contained in the gold?
					switch(Integer.parseInt(inputEntry.split(":")[0])){
						case 1: fpPos++; break;
						case -1: fpNeg++; break;
						case 0: fpNeut++; break;
						default: break;
					}

					fp++;
				}
			}
		}

		List<String> outputLines = new ArrayList<String>();
		
		outputLines.add("### Sentiment Evaluation ###\n");
		
		outputLines.add("# Gold #");
		outputLines.add("Sentences: " + GOLD_SENT_SENTENCES + " Entities: " + GOLD_SENT);
		outputLines.add("Positive: " + GOLD_POS + " Negative: " + GOLD_NEG + " Neutral: " + GOLD_NEUT);
		
		outputLines.add("\n# Input #");
		outputLines.add("Sentences: " + INPUT_SENT_SENTENCES + " Entities (Might higher than gold if several polarities were found for the same entity): " + INPUT_SENT);
		outputLines.add("Positive: " + INPUT_POS + " Negative: " + INPUT_NEG + " Neutral: " + INPUT_NEUT);
		
		// POSITIVE
		outputLines.add("\nPositive");
		outputLines.add("TP: "+tpPos+"   FP: "+fpPos);
		outputLines.add("FN: "+fnPos);
		
		float pPos = tpPos/(tpPos+fpPos);
		float rPos = tpPos/(tpPos+fnPos);
		float f1Pos = (2*(pPos*rPos))/(pPos+rPos);
		
		outputLines.add("P: "+pPos);
		outputLines.add("R: "+rPos);
		outputLines.add("F1: "+f1Pos);
		
		//NEGATIVE
		outputLines.add("\nNegative");
		outputLines.add("TP: "+tpNeg+"   FP: "+fpNeg);
		outputLines.add("FN: "+fnNeg);
		
		float pNeg = tpNeg/(tpNeg+fpNeg);
		float rNeg = tpNeg/(tpNeg+fnNeg);
		float f1Neg = (2*(pNeg*rNeg))/(pNeg+rNeg);
		
		outputLines.add("P: "+pNeg);
		outputLines.add("R: "+rNeg);
		outputLines.add("F1: "+f1Neg);
		
		//NEUTRAL
		outputLines.add("\nNeutral");
		outputLines.add("TP: "+tpNeut+"   FP: "+fpNeut);
		outputLines.add("FN: "+fnNeut);
		
		float pNeut = tpNeut/(tpNeut+fpNeut);
		float rNeut = tpNeut/(tpNeut+fnNeut);
		float f1Neut = (2*(pNeut*rNeut))/(pNeut+rNeut);
		
		outputLines.add("P: "+pNeut);
		outputLines.add("R: "+rNeut);
		outputLines.add("F1: "+f1Neut);
		
		//OVERALL
		outputLines.add("\nOverall");
		outputLines.add("TP: "+tp+"   FP: "+fp);
		outputLines.add("FN: "+fn);
		
		float p = tp/(tp+fp);
		float r = tp/(tp+fn);
		float f1 = (2*(p*r))/(p+r);
		
		outputLines.add("P: "+p);
		outputLines.add("R: "+r);
		outputLines.add("F1: "+f1);
		
		System.out.println(StringUtils.join(outputLines, "\n"));
		IOUtils.writeLines(outputLines, "\n", out);
	}
	
	public void evaluateSystem(String gold, String input, String output) throws Exception{
		Map<String, List<String>> goldSent = loadGoldSentFull(gold);
		Map<String, List<String>> inputSent = loadInputSentFull(input);
		FileOutputStream out = new FileOutputStream(output);
		
		int polarity;
		float tpPos = 0, fpPos = 0, fnPos = 0;
		float tpNeg = 0, fpNeg = 0, fnNeg = 0;
		float tpNeut = 0, fpNeut = 0, fnNeut = 0;
		float tp = 0, fp = 0, fn = 0;
		for (String key : goldSent.keySet()) { // for each sentence
			for (String goldEntry : goldSent.get(key)) { // for each polarity of this sentence
				polarity = Integer.parseInt(goldEntry.split(":")[0]); // is "-1:Socrates" in the input for this sentence?

				// if the input has the gold value its a hit
				if(inputSent.get(key).contains(goldEntry)){
					switch(polarity){
						case 1: tpPos++; break;
						case -1: tpNeg++; break;
						case 0: tpNeut++; break;
						default: break;
					}

					tp++;
				}
				// not selected correct
				else{
					switch(polarity){
						case 1: fnPos++; break;
						case -1: fnNeg++; break;
						case 0: fnNeut++; break;
						default: break;
					}

					fn++;
				}

			}
			
			for (String inputEntry : inputSent.get(key)) { // for each input sentence of the current key
				// selected not correct
				if(!goldSent.get(key).contains(inputEntry)){ // is it contained in the gold?
					switch(Integer.parseInt(inputEntry.split(":")[0])){
						case 1: fpPos++; break;
						case -1: fpNeg++; break;
						case 0: fpNeut++; break;
						default: break;
					}

					fp++;
				}
			}
		}

		List<String> outputLines = new ArrayList<String>();
		
		outputLines.add("### Full System Evaluation ###\n");
		
		outputLines.add("# Gold #");
		outputLines.add("Sentences: " + GOLD_SENT_FULL_SENTENCES + " Entities: " + GOLD_SENT_FULL);
		outputLines.add("Positive: " + GOLD_POS_FULL + " Negative: " + GOLD_NEG_FULL + " Neutral: " + GOLD_NEUT_FULL);
		
		outputLines.add("\n# Input #");
		outputLines.add("Sentences: " + INPUT_SENT_FULL_SENTENCES + " Entities: " + INPUT_SENT_FULL);
		outputLines.add("Positive: " + INPUT_POS_FULL + " Negative: " + INPUT_NEG_FULL + " Neutral: " + INPUT_NEUT_FULL);
		
		// POSITIVE
		outputLines.add("\nPositive");
		outputLines.add("TP: "+tpPos+"   FP: "+fpPos);
		outputLines.add("FN: "+fnPos);
		
		float pPos = tpPos/(tpPos+fpPos);
		float rPos = tpPos/(tpPos+fnPos);
		float f1Pos = (2*(pPos*rPos))/(pPos+rPos);
		
		outputLines.add("P: "+pPos);
		outputLines.add("R: "+rPos);
		outputLines.add("F1: "+f1Pos);
		
		//NEGATIVE
		outputLines.add("\nNegative");
		outputLines.add("TP: "+tpNeg+"   FP: "+fpNeg);
		outputLines.add("FN: "+fnNeg);
		
		float pNeg = tpNeg/(tpNeg+fpNeg);
		float rNeg = tpNeg/(tpNeg+fnNeg);
		float f1Neg = (2*(pNeg*rNeg))/(pNeg+rNeg);
		
		outputLines.add("P: "+pNeg);
		outputLines.add("R: "+rNeg);
		outputLines.add("F1: "+f1Neg);
		
		//NEUTRAL
		outputLines.add("\nNeutral");
		outputLines.add("TP: "+tpNeut+"   FP: "+fpNeut);
		outputLines.add("FN: "+fnNeut);
		
		float pNeut = tpNeut/(tpNeut+fpNeut);
		float rNeut = tpNeut/(tpNeut+fnNeut);
		float f1Neut = (2*(pNeut*rNeut))/(pNeut+rNeut);
		
		outputLines.add("P: "+pNeut);
		outputLines.add("R: "+rNeut);
		outputLines.add("F1: "+f1Neut);
		
		//OVERALL
		outputLines.add("\nOverall");
		outputLines.add("TP: "+tp+"   FP: "+fp);
		outputLines.add("FN: "+fn);
		
		float p = tp/(tp+fp);
		float r = tp/(tp+fn);
		float f1 = (2*(p*r))/(p+r);
		
		outputLines.add("P: "+p);
		outputLines.add("R: "+r);
		outputLines.add("F1: "+f1);
		
		System.out.println(StringUtils.join(outputLines, "\n"));
		IOUtils.writeLines(outputLines, "\n", out);
	}
	
	public static void evaluate0() throws Exception{
		Evaluation eval = new Evaluation();

		String INPUT_0 = "output/SentiCorpus-PT_sentiment_0.xml";
		String OUTPUT_NER_0 = "output/evalNER_N0_P0.txt";
		String OUTPUT_SENT_0 = "output/evalSENT_S0_N0_P0.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S0_N0_P0.txt";
		
		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate1() throws Exception{
		Evaluation eval = new Evaluation();
		
		// Only NER was altered here
		String INPUT_0 = "output/SentiCorpus-PT_ner_1.xml";
		String OUTPUT_NER_0 = "output/evalNER_N1_P1.txt"; //names per NP
//		String OUTPUT_SENT_0 = "output/evalSENT_0.txt";
//		String OUTPUT_OVERALL_0 = "output/evalFULL_0.txt";
		
		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
//		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
//		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate2() throws Exception{
		Evaluation eval = new Evaluation();
		
		String INPUT_0 = "output/SentiCorpus-PT_sentiment_1.xml"; // polarity per fcl -> result of ner change
		String OUTPUT_NER_0 = "output/evalNER_N2_P3.txt";
		String OUTPUT_SENT_0 = "output/evalSENT_S1_N2_P3.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S1_N2_P3.txt";
		
		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate3() throws Exception{
		Evaluation eval = new Evaluation();

		String INPUT_0 = "output/SentiCorpus-PT_sentiment_2.xml"; // included names polarity
		String OUTPUT_NER_0 = "output/evalNER_N2_P3.txt";
		String OUTPUT_SENT_0 = "output/evalSENT_S2_N2_P3.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S2_N2_P3.txt";
		
		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	

	public static void evaluate4() throws Exception{
		Evaluation eval = new Evaluation();

		String INPUT_0 = "output/SentiCorpus-PT_sentiment_3.xml"; // included verbs polarity
//		String OUTPUT_NER_0 = "output/evalNER_N2_P3.txt"; 
		String OUTPUT_SENT_0 = "output/evalSENT_S3_N2_P3.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S3_N2_P3.txt";
		
//		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate5() throws Exception{
		Evaluation eval = new Evaluation();
		
		String INPUT_0 = "output/SentiCorpus-PT_sentiment_4.xml"; // Negation 
		String OUTPUT_NER_0 = "output/evalNER_N2_P3.txt";
		String OUTPUT_SENT_0 = "output/evalSENT_S4_N2_P3.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S4_N2_P3.txt";
		
		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate6() throws Exception{
		Evaluation eval = new Evaluation();
		
		String INPUT_0 = "output/SentiCorpus-PT_sentiment_5.xml"; // Ner pre process, no change sentiment
//		String INPUT_0 = "output/SentiCorpus-PT_ner_3.xml"; // Ner pre process, no change sentiment
		String OUTPUT_NER_0 = "output/evalNER_N3_P4.txt";
		String OUTPUT_SENT_0 = "output/evalSENT_S5_N3_P4.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S5_N3_P4.txt";
		
		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate7() throws Exception{
		Evaluation eval = new Evaluation();
		
		String INPUT_0 = "output/SentiCorpus-PT_sentiment_6.xml";  
//		String OUTPUT_NER_0 = "output/evalNER_N3_P4.txt";
		String OUTPUT_SENT_0 = "output/evalSENT_S6_N3_P4.txt";
//		String OUTPUT_OVERALL_0 = "output/evalFULL_S5_N3_P4.txt";
		
//		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
//		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void main(String args[]) throws Exception{
//		evaluate0();
//		evaluate1();
//		evaluate2();
//		evaluate3();
//		evaluate4();
//		evaluate5();
//		evaluate6();
//		evaluate7();
		
		new Evaluation().goldSentSample(Files.GOLD, "output/SentiCorpus-PT_sentiment_6.xml");
		new Evaluation().inputSentSample(Files.GOLD, "output/SentiCorpus-PT_sentiment_6.xml");
	}
	
}
