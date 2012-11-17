package pt.uevora.hfernandes.evaluate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.Files;
import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Sentence;

public class Evaluation2 {

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
	 * LOAD into a list the gold file for NER
	 * 
	 * FORMAT
	 * 
	 * NER EVALUATION
	 * {COMMENT_ID-SENTENCE_ID:ALVO}
	 */
	private List<String> loadGoldNER(String GOLD) throws Exception{
		Serializer serializer = new Persister();
		File source = new File(GOLD);

		Corpus gold = serializer.read(Corpus.class, source);
		
		int lastSentId = -1;
		String key;
		List<String> goldList = new ArrayList<String>();
		for (Comment comment : gold.getComments()) {
			lastSentId = -1;
			for (Sentence sentence : comment.getSentences()) {
				key = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo();
				
				// There are duplicates, but they seem safe to ignore since 
				// apparently they are corpus errors
				if(!goldList.contains(key)){
					goldList.add(key);

					if(sentence.getId() != lastSentId){ // ignore repeated sentences
						GOLD_NER_SENTENCES++; // number of sentences
						lastSentId = sentence.getId();
					}
				}
								
			}
		}
		
		GOLD_NER = goldList.size(); // number of entries
		
		return goldList;
	}
	
	/**
	 * 
	 * LOAD into a list the full sentiment gold file (full system evaluation)
	 * 
	 * FORMAT
	 * 
	 * SENTIMENT EVALUATION ALL
	 * {COMMENT_ID-SENTENCE_ID:ALVO=POLARITY}
	 */
	private List<String> loadGoldSentFull(String GOLD) throws Exception{
		Serializer serializer = new Persister();
		File source = new File(GOLD);

		Corpus gold = serializer.read(Corpus.class, source);
		
		int lastSentId = -1;
		String key;
		List<String> goldList = new ArrayList<String>();
		for (Comment comment : gold.getComments()) {
			lastSentId = -1;
			for (Sentence sentence : comment.getSentences()) {
				key = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo()+"="+sentence.getPolarity();
				
				// There are duplicates, but they seem safe to ignore since 
				// apparently they are corpus errors
				if(!goldList.contains(key)){
					goldList.add(key);

					switch(sentence.getPolarity()){
						case 1: GOLD_POS_FULL++; break;
						case -1: GOLD_NEG_FULL++; break;
						case 0: GOLD_NEUT_FULL++; break;
						
						default: break;
					}

					if(sentence.getId() != lastSentId){ // ignore repeated sentences
						GOLD_SENT_FULL_SENTENCES++; // number of sentences
						lastSentId = sentence.getId();
					}
				}
			}
		}
		GOLD_SENT_FULL = goldList.size(); // all opinions entries
		
		return goldList;
	}
	
	/**
	 * 
	 * LOAD into a list the input file for NER
	 * 
	 * FORMAT
	 * 
	 * NER EVALUATION
	 * {COMMENT_ID-SENTENCE_ID:ALVO}
	 */
	private List<String> loadInputNER(String input) throws Exception{
		Serializer serializer = new Persister();
		File source = new File(input);

		Corpus inputCorpus = serializer.read(Corpus.class, source);
		
		int lastSentId = -1;
		String key;
		List<String> inputList = new ArrayList<String>();
		for (Comment comment : inputCorpus.getComments()) {
			lastSentId = -1;
			for (Sentence sentence : comment.getSentences()) {
				key = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo();
				
				if(sentence.getAlvo() != null && !inputList.contains(key)){
					inputList.add(key);

					if(sentence.getId() != lastSentId){
						INPUT_NER_SENTENCES++; // number of sentences
						lastSentId = sentence.getId();
					}
				}
			}
		}
		
		INPUT_NER = inputList.size();
		return inputList;
	}
	
	/**
	 * AUXILIAR: Get the list of entities that were hits by the NER system
	 * Needed to evaluate the sentiment module only.
	 * 
	 * NER HITS, USED FOR SOLO SENT EVALUATION
	 * {COMMENT_ID-SENTENCE_ID:ALVO}
	 */
	public List<String> getCorrectNer(String gold, String input) throws Exception{
		List<String> goldNER = loadGoldNER(gold); //{COMMENT_ID-SENTENCE_ID:ALVO}
		List<String> inputNER = loadInputNER(input); //{COMMENT_ID-SENTENCE_ID:ALVO}
		
		//get NER hits only
		List<String> goldInputNER = new ArrayList<String>();
		for (String goldEntry : goldNER) {
			// gold hit, input is present on gold ner
			if(inputNER.contains(goldEntry)){
				goldInputNER.add(goldEntry);
			}
		}
		
		return goldInputNER;
	}
			
	/**
	 * 
	 * LOAD into a list the sentiment gold file (sentiment evaluation only)
	 * 
	 * FORMAT
	 * 
	 * SENTIMENT EVALUATION RESTRICTED TO CORRECT ENTITIES
	 * {COMMENT_ID-SENTENCE_ID:ALVO=POLARITY}
	 */
	private List<String> loadGoldSent(String gold, String input) throws Exception{
		List<String> correctNer = getCorrectNer(gold, input); // {COMMENT_ID-SENTENCE_ID:ALVO}
				
		Serializer serializer = new Persister();
		File source = new File(gold);

		Corpus goldCorpus = serializer.read(Corpus.class, source);
		
		int lastSentId = -1;
		String sentKey, nerKey;
		List<String> goldList = new ArrayList<String>();
		for (Comment comment : goldCorpus.getComments()) {
			lastSentId = -1;
			for (Sentence sentence : comment.getSentences()) {
				nerKey = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo();
				sentKey = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo()+"="+sentence.getPolarity();
				
				// entry is in the correct ner and not in the list already
				if(correctNer.contains(nerKey) && !goldList.contains(sentKey)){
					goldList.add(sentKey);
					
					switch(sentence.getPolarity()){
						case 1: GOLD_POS++; break;
						case -1: GOLD_NEG++; break;
						case 0: GOLD_NEUT++; break;
						
						default: break;
					}

					if(sentence.getId() != lastSentId){ // ignore repeated sentences
						GOLD_SENT_SENTENCES++;
						lastSentId = sentence.getId();
					}
				}
			}
		}

		GOLD_SENT = goldList.size(); // all opinion
		
		return goldList;
	}
	
	/**
	 * 
	 * LOAD into a list the full sentiment input file (full system evaluation)
	 * 
	 * FORMAT
	 * 
	 * SENTIMENT EVALUATION ALL
	 * {COMMENT_ID-SENTENCE_ID:ALVO=POLARITY}
	 */
	private List<String> loadInputSentFull(String input) throws Exception{
		Serializer serializer = new Persister();
		File source = new File(input);

		Corpus inputCorpus = serializer.read(Corpus.class, source);
		
		int lastSentId = -1;
		String key;
		List<String> inputList = new ArrayList<String>();
		for (Comment comment : inputCorpus.getComments()) {
			lastSentId = -1;
			for (Sentence sentence : comment.getSentences()) {
				key = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo()+"="+sentence.getPolarity();
								
				if(sentence.getAlvo() != null && !inputList.contains(key)){
					inputList.add(key);

					switch(sentence.getPolarity()){
						case 1: INPUT_POS_FULL++; break;
						case -1: INPUT_NEG_FULL++; break;
						case 0: INPUT_NEUT_FULL++; break;
						
						default: break;
					}

					if(sentence.getId() != lastSentId){ // ignore repeated sentences
						INPUT_SENT_FULL_SENTENCES++;
						lastSentId = sentence.getId();
					}
				}
			}
		}
		INPUT_SENT_FULL = inputList.size(); // all opinions
		
		return inputList;
	}
	
	/**
	 * 
	 * LOAD into a list the sentiment input file (sentiment evaluation only)
	 * 
	 * FORMAT
	 * 
	 * SENTIMENT EVALUATION RESTRICTED TO CORRECT ENTITIES
	 * {COMMENT_ID-SENTENCE_ID:ALVO=POLARITY}
	 */
	private List<String> loadInputSent(String gold, String input) throws Exception{
		List<String> correctNer = getCorrectNer(gold, input); // {COMMENT_ID-SENTENCE_ID:ALVO}
		
		Serializer serializer = new Persister();
		File source = new File(input);

		Corpus inputCorpus = serializer.read(Corpus.class, source);
		
		int lastSentId = -1;
		String nerKey, sentKey;
		List<String> inputList = new ArrayList<String>();
		for (Comment comment : inputCorpus.getComments()) {
			lastSentId = -1;
			for (Sentence sentence : comment.getSentences()) {
				nerKey = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo();
				sentKey = comment.getId()+"-"+sentence.getId()+":"+sentence.getAlvo()+"="+sentence.getPolarity();
				
				// entry is in the correct ner and not in the list already
				if(sentence.getAlvo() != null && correctNer.contains(nerKey) && !inputList.contains(sentKey)){
					inputList.add(sentKey);
					
					switch(sentence.getPolarity()){
						case 1: INPUT_POS++; break;
						case -1: INPUT_NEG++; break;
						case 0: INPUT_NEUT++; break;
						
						default: break;
					}
					
					if(sentence.getId() != lastSentId){ // ignore repeated sentences
						INPUT_SENT_SENTENCES++;
						lastSentId = sentence.getId();
					}
				}
			}
		}
		INPUT_SENT=inputList.size(); // all opinions
		
		return inputList;
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
		List<String> goldNER = loadGoldNER(gold);
		List<String> inputNER = loadInputNER(input);
		FileOutputStream out = new FileOutputStream(output);
		
		float tp = 0, fp = 0, fn = 0;
		for (String goldEntry : goldNER) {
			// selected correct
			if(inputNER.contains(goldEntry)){
				tp++;
			}
			// not selected correct
			else{
				fn++;
			}
		}
			
		for (String inputEntry : inputNER) {
			// selected not correct
			if(!goldNER.contains(inputEntry)){
				fp++;
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
		List<String> goldSent = loadGoldSent(gold, input);
		List<String> inputSent = loadInputSent(gold, input);
		FileOutputStream out = new FileOutputStream(output);
		
		int polarity;
		float tpPos = 0, fpPos = 0, fnPos = 0;
		float tpNeg = 0, fpNeg = 0, fnNeg = 0;
		float tpNeut = 0, fpNeut = 0, fnNeut = 0;
		float tp = 0, fp = 0, fn = 0;
		for (String goldEntry : goldSent) {
			polarity = Integer.parseInt(goldEntry.split("=")[1]);

			// if the input has the gold value its a hit
			if(inputSent.contains(goldEntry)){
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
//								Sentence sentence = corpus.getSentence(Integer.parseInt(key.split("-")[0]), Integer.parseInt(key.split("-")[1]));
//								Clause sentClause = sentence.getReferedClause();
//								
//								System.out.println(key + " : " + goldEntry + " : "+ sentClause.getTokenText());
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
		
		for (String inputEntry : inputSent) {
			// selected not correct
			if(!goldSent.contains(inputEntry)){
				polarity = Integer.parseInt(inputEntry.split("=")[1]);
				
				switch(polarity){
					case 1: fpPos++; break;
					case -1: fpNeg++; break;
					case 0: fpNeut++; break;
					default: break;
				}
				
				fp++;
			}
		}

		List<String> outputLines = new ArrayList<String>();
		
		outputLines.add("### Sentiment Evaluation ###\n");
		
		outputLines.add("# Gold #");
		outputLines.add("Sentences: " + GOLD_SENT_SENTENCES + " Entities: " + GOLD_SENT);
		outputLines.add("Positive: " + GOLD_POS + " Negative: " + GOLD_NEG + " Neutral: " + GOLD_NEUT);
		
		outputLines.add("\n# Input #");
		outputLines.add("Sentences: " + INPUT_SENT_SENTENCES + " Entities: " + INPUT_SENT + " (Might higher than gold if several polarities were found for the same entity)");
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
		List<String> goldSent = loadGoldSentFull(gold);
		List<String> inputSent = loadInputSentFull(input);
		FileOutputStream out = new FileOutputStream(output);
		
		int polarity;
		float tpPos = 0, fpPos = 0, fnPos = 0;
		float tpNeg = 0, fpNeg = 0, fnNeg = 0;
		float tpNeut = 0, fpNeut = 0, fnNeut = 0;
		float tp = 0, fp = 0, fn = 0;
		for (String goldEntry : goldSent) {
			polarity = Integer.parseInt(goldEntry.split("=")[1]);

			// if the input has the gold value its a hit
			if(inputSent.contains(goldEntry)){
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
		
		for (String inputEntry : inputSent) {
			// selected not correct
			if(!goldSent.contains(inputEntry)){
				polarity = Integer.parseInt(inputEntry.split("=")[1]);
				switch(polarity){
					case 1: fpPos++; break;
					case -1: fpNeg++; break;
					case 0: fpNeut++; break;
					default: break;
				}
				
				fp++;
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
		Evaluation2 eval = new Evaluation2();

		String INPUT_0 = "output/SentiCorpus-PT_sentiment_0.xml";
		String OUTPUT_NER_0 = "output/evalNER_N0_P0.txt";
		String OUTPUT_SENT_0 = "output/evalSENT_S0_N0_P0.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S0_N0_P0.txt";
		
		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate1() throws Exception{
		Evaluation2 eval = new Evaluation2();
		
		// Only NER was altered here, because of the bad performance no sentiment eval was performed.
		String INPUT_0 = "output/SentiCorpus-PT_ner_1.xml";
		String OUTPUT_NER_0 = "output/evalNER_N1_P1.txt"; //names per NP
		
		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
	}
	
	public static void evaluate2() throws Exception{
		Evaluation2 eval = new Evaluation2();
		
		String INPUT_0 = "output/SentiCorpus-PT_sentiment_1.xml"; // polarity per fcl -> result of ner change
		String OUTPUT_NER_0 = "output/evalNER_N2_P3.txt";
		String OUTPUT_SENT_0 = "output/evalSENT_S1_N2_P3.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S1_N2_P3.txt";
		
		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate3() throws Exception{
		Evaluation2 eval = new Evaluation2();

		String INPUT_0 = "output/SentiCorpus-PT_sentiment_2.xml"; // included names polarity
		String OUTPUT_NER_0 = "output/evalNER_N2_P3.txt";
		String OUTPUT_SENT_0 = "output/evalSENT_S2_N2_P3.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S2_N2_P3.txt";
		
		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	

	public static void evaluate4() throws Exception{
		Evaluation2 eval = new Evaluation2();

		String INPUT_0 = "output/SentiCorpus-PT_sentiment_3.xml"; // included verbs polarity
		String OUTPUT_SENT_0 = "output/evalSENT_S3_N2_P3.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S3_N2_P3.txt";
		
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate5() throws Exception{
		Evaluation2 eval = new Evaluation2();
		
		String INPUT_0 = "output/SentiCorpus-PT_sentiment_4.xml"; // Negation 
		String OUTPUT_SENT_0 = "output/evalSENT_S4_N2_P3.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S4_N2_P3.txt";
		
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate6() throws Exception{
		Evaluation2 eval = new Evaluation2();
		
		String INPUT_0 = "output/SentiCorpus-PT_sentiment_5.xml"; // Ner pre process, no change sentiment
		String OUTPUT_NER_0 = "output/evalNER_N3_P4.txt";
		String OUTPUT_SENT_0 = "output/evalSENT_S5_N3_P4.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S5_N3_P4.txt";
		
		eval.evaluateNer(Files.GOLD, INPUT_0, OUTPUT_NER_0);
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate7() throws Exception{
		Evaluation2 eval = new Evaluation2();
		
		String INPUT_0 = "output/SentiCorpus-PT_sentiment_6.xml";  
		String OUTPUT_SENT_0 = "output/evalSENT_S6_N3_P4.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S6_N3_P4.txt";
		
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}

	public static void evaluate8() throws Exception{
		Evaluation2 eval = new Evaluation2();
		
		String INPUT_0 = "output/SentiCorpus-PT_sentiment_7.xml"; 
		String OUTPUT_SENT_0 = "output/evalSENT_S7_N4_P5.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S7_N4_P5.txt";
		
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate8Sentituites() throws Exception{
		Evaluation2 eval = new Evaluation2();
		
		String INPUT_0 = "output/Sentituites_sentiment_7.xml"; 
		String OUTPUT_SENT_0 = "output/evalSENTTuites_S7_N4_P5.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULLTuites_S7_N4_P5.txt";
		String OUTPUT_NER_0 = "output/evalNERTuites_N4_P5.txt";
		
		eval.evaluateNer("resources/Sentituites.xml", INPUT_0, OUTPUT_NER_0);
		eval.evaluateSent("resources/Sentituites.xml", INPUT_0, OUTPUT_SENT_0);
		eval.evaluateSystem("resources/Sentituites.xml", INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate9() throws Exception{
		Evaluation2 eval = new Evaluation2();
		
		String INPUT_0 = "output/SentiCorpus-PT_sentiment_8.xml"; 
		String OUTPUT_SENT_0 = "output/evalSENT_S8_N4_P5.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULL_S8_N4_P5.txt";
		
		eval.evaluateSent(Files.GOLD, INPUT_0, OUTPUT_SENT_0);
//		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluate9Sentituites() throws Exception{
		Evaluation2 eval = new Evaluation2();
		
		String INPUT_0 = "output/Sentituites_sentiment_8.xml"; 
		String OUTPUT_SENT_0 = "output/evalSENTTuites_S8_N4_P5.txt";
		String OUTPUT_OVERALL_0 = "output/evalFULLTuites_S8_N4_P5.txt";
		
		eval.evaluateSent("resources/Sentituites.xml", INPUT_0, OUTPUT_SENT_0);
//		eval.evaluateSystem(Files.GOLD, INPUT_0, OUTPUT_OVERALL_0);
	}
	
	public static void evaluateChatter() throws Exception{
		Evaluation2 eval = new Evaluation2();
		Evaluation2 eval2 = new Evaluation2();
		
		String INPUT_0 = "sampleInput_classified.xml";  
		String OUTPUT_SENT_0 = "evalMySampleSent.txt";
		String INPUT_1 = "sampleInput_chatter.xml";  
		String OUTPUT_SENT_1 = "evalChatterSent.txt";
		
		eval.evaluateSent("sampleGold.xml", INPUT_0, OUTPUT_SENT_0);
		eval2.evaluateSent("sampleGold.xml", INPUT_1, OUTPUT_SENT_1);
	}
	
	public static void evaluateSampleForML() throws Exception{
		Evaluation2 eval = new Evaluation2();
		
		String INPUT_0 = "output/CorrectOneEntitySentences_input.xml";  
		String OUTPUT_SENT_0 = "output/MLMyImplSENT_S7_N4_P5.txt";
		String OUTPUT_FULL_0 = "output/MLMyImplFULL_S7_N4_P5.txt";
		
		eval.evaluateSent("output/CorrectOneEntitySentences_Gold.xml", INPUT_0, OUTPUT_SENT_0);
//		eval.evaluateSystem("CorrectOneEntitySentences_input.xml", INPUT_0, OUTPUT_FULL_0);
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
//		evaluate8();
		evaluate8Sentituites();
//		evaluateSampleForML();
//		evaluateChatter();
		
	}
	
}
