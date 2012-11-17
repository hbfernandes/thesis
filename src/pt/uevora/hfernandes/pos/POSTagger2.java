package pt.uevora.hfernandes.pos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Sentence;
import pt.uevora.hfernandes.parsers.VISLParser;

/**
 * VISL Tagger
 * No NP's, just FCL's
 * @author hfernandes
 *
 */
public class POSTagger2 {

	/**
	 * Second version of VISL with no NP's and token list
	 * @param input
	 * @param outputPlain
	 * @param outputXMl
	 * @throws Exception
	 */
	private static void VISLTagger(String input, String output) throws Exception{
		// read input file
		Serializer serializer = new Persister();
		File inputFile = new File(input);
		Corpus corpus = serializer.read(Corpus.class, inputFile);
		
		DefaultExecutor executor = new DefaultExecutor();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		CommandLine cmdLine;
		File run = new File("bin/visl.sh");
		File tmp = new File("output/tmp.txt");
		FileOutputStream tmpOS;
		PumpStreamHandler streamHandler = new PumpStreamHandler(baos);
		executor.setStreamHandler(streamHandler);
		tmp.deleteOnExit();
		
		System.out.println("Tagging "+input+"...");
		
		String taggedValue;
		for (Comment comment : corpus.getComments()) {
		   for (Sentence sentence : comment.getSentences()) {
			   System.out.println("Sentence: " + sentence.getValue());
			   tmpOS = new FileOutputStream(tmp);
			   tmpOS.write(sentence.getValue().getBytes());
			   			   
			   cmdLine = new CommandLine(run.getAbsoluteFile());
			   cmdLine.addArgument(tmp.getAbsolutePath());
			   executor.execute(cmdLine);
			   
			   taggedValue = StringUtils.trim(baos.toString().replace("Segmentation fault (core dumped)", ""));
			   
			   sentence.setPosValue(taggedValue);
			   VISLParser.processSentence2(sentence); // process the sentence tokens
			   baos.reset();
			   tmpOS.close();
		   }
		   
		}
		
		
		Serializer serializerOut = new Persister();
		File result = new File(output);

		serializerOut.write(corpus, result);
		System.out.println("Done. Generated " + output);
	}
	
	public static void main(String args[]) throws Exception{
		String INPUT = "resources/SentiCorpus-PT_clean5.xml";
//		String INPUT = "test.xml";
		String OUTPUT = "output/SentiCorpus-PT_pos_2.xml";
		
		VISLTagger(INPUT, OUTPUT);
	}

}
