package pt.uevora.hfernandes.ner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.objects.Alvo;
import pt.uevora.hfernandes.objects.Clause;
import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Entity;
import pt.uevora.hfernandes.objects.Sentence;

/**
 * Find entities per FCL
 * @author hfernandes
 *
 */

public class NER2 extends NER{
	
	private static void findEntities(String input, String output) throws Exception{
		// Load entity list
		List<Entity> entities = getNerResource();
		
		Serializer serializer = new Persister();
		File inputFile = new File(input);
		
		// read from file
		Corpus corpus = serializer.read(Corpus.class, inputFile);
		
		// Define all the alvos and alvo for each fcl
		Sentence newSentence;
		List<Sentence> newSentences;
		List<Sentence> newSentencesFromSentence;
		for (Comment comment : corpus.getComments()) {
			newSentences = new ArrayList<Sentence>();
			for (Sentence sentence : comment.getSentences()) {
				newSentencesFromSentence = new ArrayList<Sentence>(); // list of sentences generated from this one
				for (Clause clause : sentence.getClauses()) { //for each fcl
					// search for each entities in each clause
					for (Entity entity : entities) {
						newSentence = sentence.getSentenceCopy(); // might not be used
						newSentence.setAlvo(entity.getName());
						newSentence.setClauseId(clause.getId()); // set the clause where the entity is found
						
						for (Alvo alvo : entity.getAlvos()) {
							if(clause.getTokenText().replace("_", " ").contains(alvo.getValue())){
								newSentence.getAlvos().add(alvo);
							}
						}
						
						// add the sentence to the new sentences from the current sentence list
						if(!newSentence.getAlvos().isEmpty()){
							newSentencesFromSentence.add(newSentence);
						}
					}
				}
				
				// add the new sentences that will replace the one in the comment
				if(!newSentencesFromSentence.isEmpty()){
					newSentences.addAll(newSentencesFromSentence);
				}
				// add the original one if no entity found
				else{
					newSentences.add(sentence);
				}
			}
			
			// Set the new sentences
			comment.setSentences(newSentences);
		}
		
		// Write to file
		File result = new File(output);
		serializer.write(corpus, result);
		
	}
	
	public static void main(String args[]) throws Exception{
		String INPUT = "output/SentiCorpus-PT_pos_3.xml";
		String OUTPUT = "output/SentiCorpus-PT_ner_2.xml";
		
		findEntities(INPUT, OUTPUT);
	}

}
