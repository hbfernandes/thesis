package pt.uevora.hfernandes.ner;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Entity;
import pt.uevora.hfernandes.objects.Sentence;

/**
 * Add NER pre-processing
 * 
 * @author hfernandes
 *
 */
public class NER3PreProcess extends NER{
	
	public void preProcess(String input, String output) throws Exception{
		// Load entity list
		List<Entity> entities = getNerResource();
		
		Serializer serializer = new Persister();
		File inputFile = new File(input);
		
		// read from file
		Corpus corpus = serializer.read(Corpus.class, inputFile);
		
		for (Comment comment : corpus.getComments()) {
			for (Sentence sentence : comment.getSentences()) {
				sentence.setValueNerReplaced(sentence.getValue());
				
				// search for each reference and replace with true name
				for (Entity entity : entities) {
					sentence.setValueNerReplaced(replace(sentence.getValueNerReplaced(), entity.getAlvosArray(), entity.getName()));
				}
			}
		}
		
		// Write to file
		File result = new File(output);
		serializer.write(corpus, result);
	}

	public  String replace(String input, String[] matchList, String replacement){
		int matchIndex;
		int index = 0;
		int i = 0;
		
		List<String> asList = Arrays.asList(matchList);
		Collections.sort(asList, new MyComparator());
		String[] matchListOrdered = (String[]) asList.toArray();
		
		
		while(i < matchListOrdered.length){
			matchIndex = input.indexOf(matchListOrdered[i], index);
			if(matchIndex != -1){
				input = input.substring(0, index) + input.substring(index).replaceFirst(matchListOrdered[i], replacement);
				index = matchIndex + replacement.length();
				i = 0;
			}
			else{
				i++;
			}
		}
		
		return input;
	}
	
	
	
	public class MyComparator implements Comparator<String>{
	    @Override
	    public int compare(String o1, String o2) {  
	      if (o1.length() < o2.length()) {
	         return 1;
	      } else if (o1.length() > o2.length()) {
	         return -1;
	      }
	      return o1.compareTo(o2);
	    }
	}
	
	public static void main(String args[]) throws Exception {
		String OUTPUT = "output/Sentituites_ner_3_preprocess.xml";

		NER3PreProcess ner = new NER3PreProcess();
//		ner.preProcess(Files.BASE_INPUT, OUTPUT);
		ner.preProcess("resources/Sentituites_clean.xml", OUTPUT);
		
//		String[] matchList = new String[]{"ww", "eee", "w"};
//		List<String> lista = Arrays.asList(matchList);
//		
//		Collections.sort(lista, ner.new MyComparator());
//		
//		System.out.println(lista);
		
	}

}
