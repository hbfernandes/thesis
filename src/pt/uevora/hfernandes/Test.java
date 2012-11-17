package pt.uevora.hfernandes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Entity;
import pt.uevora.hfernandes.objects.NerResource;
import pt.uevora.hfernandes.objects.Sentence;

public class Test {

	public static void generateNERResource() throws Exception{
		   Serializer serializer2 = new Persister();
		   File source = new File("resources/SentiCorpus-PT_modified3.xml");

		   Corpus example2 = serializer2.read(Corpus.class, source);

		   Map<String, Entity> entities = new HashMap<String, Entity>();
		   Entity entity;
		   for (Comment comment : example2.getComments()) {
			   for (Sentence sentence : comment.getSentences()) {
				   if(!entities.containsKey(sentence.getAlvo().toLowerCase())){
					   entity = new Entity();
					   entity.setName(sentence.getAlvo());
					   entities.put(sentence.getAlvo().toLowerCase(), entity);
				   }
				   
				   entities.get(sentence.getAlvo().toLowerCase()).getAlvos().addAll(sentence.getAlvos());
			   }
		   }

		   NerResource ner = new NerResource();
		   ner.getEntities().addAll(entities.values());
		   
		   File result = new File("resources/SentiCorpus-PT_Entities.xml");
		   serializer2.write(ner, result);
		   
	}
	
	
   public static void main(String args[]) throws Exception{
	   generateNERResource();
	   
	   
//	   //Write
//	   Serializer serializer = new Persister();
//	   Corpus example = new Corpus();
//	   
//	   Comment comment = new Comment("1");
//	   Sentence sentence = new Sentence("1", "joao", 1, "literal");
//	   sentence.setValue("Teste da cena.");
//	   
//	   comment.getSentences().add(sentence);
//	   example.getComments().add(comment);
//	   
//	   
//	   File result = new File("test.xml");
//
//	   serializer.write(example, result);
//	   
	  
	   // REARRANGE ID'S, MAKE THEM SEQUENTIAL! ENABLES FUTURE MATCH
//	   Serializer serializer = new Persister();
//	   File source = new File("resources/SentiCorpus-PT_modified.txt");
//	   Corpus corpus = serializer.read(Corpus.class, source);
//	   
////	   BufferedWriter bwriter2 = new BufferedWriter(new FileWriter("resources/SentiCorpus-PT_clean2.txt"));
//	   
//	   List<String> parag = new ArrayList<String>();
//	   int id=0;
//	   for (Comment comment : corpus.getComments()) {
//		   for (Sentence sentence : comment.getSentences()) {
//			   if(!parag.contains(StringUtils.trim(sentence.getValue()))){
//				   parag.add(StringUtils.trim(sentence.getValue()));
//				   id++;
//				   sentence.setId(id);
//			   }
//			   else{
//				   sentence.setId(id);
//			   }
//		   }
//		   
////		   bwriter2.write(StringUtils.join(parag, " ")+"\n");
//		   parag =  new ArrayList<String>();
//		   id=0;
//	   }
////	   bwriter2.close();
//	   
//	   File result = new File("test3.xml");
//	   serializer.write(corpus, result);
	   
	   
	   
	   
	   
//	   // GENERATE CLEAN2 from MODIFIED
//	   Serializer serializer2 = new Persister();
//	   File source = new File("resources/SentiCorpus-PT_modified3.xml");
//
//	   Corpus example2 = serializer2.read(Corpus.class, source);
//	   
////	   BufferedWriter bwriter2 = new BufferedWriter(new FileWriter("resources/SentiCorpus-PT_clean5.xml"));
//
//	   Corpus newCorpus = new Corpus();
//	   Comment newComment;
//	   Sentence newSentence;
//	   int sid = 0;
//	   for (Comment comment : example2.getComments()) {
//		   newComment = new Comment(comment.getId());
//		   for (Sentence sentence : comment.getSentences()) {
//			   if(sid != sentence.getId()){
//				   sid = sentence.getId();
//				   newSentence = new Sentence(sid);
//				   newSentence.setValue(StringUtils.trim(sentence.getValue()));
//				   newComment.getSentences().add(newSentence);
//			   }
//		   }
//		   sid = 0;
//		   newCorpus.getComments().add(newComment);
//	   }
//
//	   
//	   File result = new File("resources/SentiCorpus-PT_clean5.xml");
//	   serializer2.write(newCorpus, result);
	   
	   
	   
	   
//	   GENERATE FILES
	   
//	   BufferedReader breader = new BufferedReader(new FileReader("resources/SentiCorpus-PT.txt"));
//	   BufferedWriter bwriter1 = new BufferedWriter(new FileWriter("resources/SentiCorpus-PT_modified.txt"));
//	   BufferedWriter bwriter2 = new BufferedWriter(new FileWriter("resources/SentiCorpus-PT_clean.txt"));
//	   
//	   
//		String line;
//		String origLine = "";
//		List<String> alvos = new ArrayList<String>();
//		int comment = 1;
//		int lastSentence = 0;
//		int currentSentence = 0;
//		while ((line = breader.readLine()) != null) { // Read the file line by line
//			if(!line.startsWith("<") || line.startsWith("<ALVO ")){
//				origLine = line;
//				alvos = new ArrayList<String>();
//				while(line.contains("<ALVO")){
//					alvos.add(line.substring(line.indexOf("<ALVO"), line.indexOf("/ALVO>")+6));
//					line = line.substring(line.indexOf("/ALVO>")+6);
//				}
//				
//				if(!alvos.isEmpty()){
//					bwriter1.write("<ALVOS>\n"+StringUtils.join(alvos, "\n")+"\n</ALVOS>\n");
////					System.out.print("<ALVOS>\n"+StringUtils.join(alvos, "\n")+"\n</ALVOS>\n");
//				}
//				
//				origLine = origLine.replaceAll("<ALVO TIPO=\"(NOME|CARGO|ORG|ALCUNHA|PRON|GN_livre)\">", "");
//				origLine = origLine.replaceAll("</ALVO>", "");
//				bwriter1.write("<VALUE>\n"+origLine+"\n</VALUE>\n");
////				System.out.print("<VALUE>\n"+origLine+"\n</VALUE>\n");
//				if(lastSentence != currentSentence){
//					lastSentence = currentSentence;
////					System.out.print(origLine+" ");
//					bwriter2.write(origLine+" ");
//				}
//			}
//			else{
//				if(line.startsWith("<COMENT")){
////					System.out.print("<COMENT ID=\""+comment+"\">\n");
//					bwriter1.write("<COMENT ID=\""+comment+"\">\n");
//					comment++;
//					bwriter2.write("\n");
//					lastSentence = 0;
//				}
//				else if(line.startsWith("<F")){ //<F ID="1"
//					bwriter1.write(line+"\n");
//					currentSentence = Integer.parseInt(line.substring(line.indexOf("ID=\"")+4, line.indexOf("\" ALVO")));
//					
//				}
//				else{
//					bwriter1.write(line+"\n");
////					System.out.print(line+"\n");
//				}
//			}
//		}
//		
//		bwriter1.close();
//		bwriter2.close();
	   
   }
   
}
