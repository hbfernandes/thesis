package pt.uevora.hfernandes.ner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.objects.Clause;
import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Entity;
import pt.uevora.hfernandes.objects.Sentence;
import pt.uevora.hfernandes.objects.Token;

/**
 * Use Preprocessing
 * 
 * @author hfernandes
 *
 */

public class NER3 extends NER{
	
	private static List<String> getDomainEntities() throws Exception{
		List<Entity> entities = getNerResource();
		
		List<String> entitiesString = new ArrayList<String>();
		for (Entity entity : entities) {
			entitiesString.add(entity.getName());
		}
		
		return entitiesString;
	}
	
	private static void findEntities(String input, String output) throws Exception{
		// Load entity list
		List<String> entities = getDomainEntities();
		String outOfDomainEntity = "Outra Entidade ";
		
		Serializer serializer = new Persister();
		File inputFile = new File(input);
		
		// read from file
		Corpus corpus = serializer.read(Corpus.class, inputFile);
		
		// Define all the alvos and alvo for each fcl
		Sentence newSentence;
		List<String> clauseEntities;
		List<Sentence> newSentences;
		List<Sentence> newSentencesFromSentence;
		int outOfDomain;
		for (Comment comment : corpus.getComments()) {
			newSentences = new ArrayList<Sentence>();
			for (Sentence sentence : comment.getSentences()) {
				newSentencesFromSentence = new ArrayList<Sentence>(); // list of sentences generated from this one
				outOfDomain = 0;
				for (Clause clause : sentence.getClauses()) { //for each fcl
					// search for each entities in each clause
					clauseEntities = new ArrayList<String>();
					
					for (Token token : clause.getGramaticalCategories()) {
						// check for proper names
						if(token.getType().equals("PROP")){
							// its an entity from the domain
							if(entities.contains(token.getText().replace("_", " "))){
								// only add if its for a different entity from the previous found on the clause
								if(!clauseEntities.contains(token.getText().replace("_", " "))){
									newSentence = sentence.getSentenceCopy(); 
									newSentence.setAlvo(token.getText().replace("_", " "));
									newSentence.setClauseId(clause.getId());
									newSentence.setAlvoToken(token.getIndex());
									
									newSentencesFromSentence.add(newSentence);
								}
							}
							//Entity not in the domain but found, this raises FP in this corpus!
//							else{
//								outOfDomain++;
//								newSentence = sentence.getSentenceCopy(); 
//								newSentence.setAlvo(outOfDomainEntity+outOfDomain);
//								newSentence.setClauseId(clause.getId());
//								newSentence.setAlvoToken(token.getIndex());
//								
//								newSentencesFromSentence.add(newSentence);
//							}
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
		String INPUT = "output/SentiCorpus-PT_pos_4.xml";
		String OUTPUT = "output/SentiCorpus-PT_ner_3.xml";
		
		findEntities(INPUT, OUTPUT);
	}

}
