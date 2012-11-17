package pt.uevora.hfernandes;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.objects.Comment;
import pt.uevora.hfernandes.objects.Corpus;
import pt.uevora.hfernandes.objects.Sentence;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("jaim76rWsRUJBXDdncIImQ")
		  .setOAuthConsumerSecret("PbuwbATW8ljBSm5cH4utes9KOSYrgrBus5f3hHc9lI")
		  .setOAuthAccessToken("908379379-RsdF1gxahAKEo9KIWl5BPmitD4BUjV8wo5rhaqe6")
		  .setOAuthAccessTokenSecret("32Im72LWtszdD5Ktjhj2QiveKScOKLZ67zVMyjkXo");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		
		
		BufferedReader breader = new BufferedReader(new FileReader("sentituites-all.csv"));
		 
		Corpus sampleCorpus = new Corpus();
		Comment comment = new Comment(1);
		
		sampleCorpus.getComments().add(comment);
		Sentence sentence;
		
		String line;
		Long id;
		int i=0;
		String entity; //[flouca, jsocrates, pportas, jsousa]
		int polarity;
		int stopCount=0;
		Status status;
		while ((line = breader.readLine()) != null) {
			id = Long.parseLong(line.split("\\|")[1]);
			entity = line.split("\\|")[2];
			polarity = Integer.parseInt(line.split("\\|")[3]);
			
			try{
				stopCount++;
				
				if(stopCount > 320){
					System.out.println("Sleeping at "+i);
					stopCount=0;
					Thread.sleep(3700000);
					System.out.println("Resuming");
				}
				
				status = twitter.showStatus(id);
				
				i++;
				sentence = new Sentence(i);
				sentence.setPolarity(polarity);
				sentence.setAlvo(entity);
				sentence.setValue(status.getText());
				comment.getSentences().add(sentence);
				
//				System.out.println(status.getText());
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			
		}
		
		Serializer serializer = new Persister();
		serializer.write(sampleCorpus, new File("twitter.xml"));
		System.out.println("Done");
		
	}

}
