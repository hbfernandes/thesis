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
import pt.uevora.hfernandes.objects.GroupForm;
import pt.uevora.hfernandes.objects.Sentence;

/**
 * Find entities per NP
 * @author hfernandes
 *
 */
public class NER1 extends NER{
	
	private static void findEntities1(String input, String output) throws Exception{
		// Load entity list
		List<Entity> entities = getNerResource();
		
		Serializer serializer = new Persister();
		File inputFile = new File(input);
		
		// read from file
		Corpus corpus = serializer.read(Corpus.class, inputFile);
		
		// Define all the alvos and alvo for each NP
		Sentence newSentence;
		List<Sentence> newSentences;
		List<Sentence> newSentencesFromSentence;
		for (Comment comment : corpus.getComments()) {
			newSentences = new ArrayList<Sentence>();
			for (Sentence sentence : comment.getSentences()) {
				newSentencesFromSentence = new ArrayList<Sentence>(); // list of sentences generated from this one
				for (Clause clause : sentence.getClauses()) { //for each fcl
					for (GroupForm np : clause.getGroupForms()) {
						
						// search for each entity in each NP
						for (Entity entity : entities) {
							for (Alvo alvo : entity.getAlvos()) {
								if(np.getText().contains(alvo.getValue())){
									np.getAlvos().add(alvo);
								}
							}
							
							// add the sentence to the new sentences from the current sentence list
							if(!np.getAlvos().isEmpty()){
								np.setAlvo(entity.getName());
								
								// add the new sentence
								newSentence = sentence.getSentenceCopy();
								newSentence.setAlvo(np.getAlvo());
								newSentence.getAlvos().addAll(np.getAlvos());
								newSentencesFromSentence.add(newSentence);
								
								break; // if we got one skip for next ones, assuming there is only 1 entity per NP
							}
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
		String INPUT = "output/SentiCorpus-PT_pos_1.xml";
		String OUTPUT = "output/SentiCorpus-PT_ner_1.xml";
		
		findEntities1(INPUT, OUTPUT);
	}

}
