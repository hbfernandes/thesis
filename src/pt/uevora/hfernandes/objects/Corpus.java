package pt.uevora.hfernandes.objects;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="CORPUS")
public class Corpus {

	@ElementList(inline=true)
	List<Comment> comments = new ArrayList<Comment>();

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	
	public void printSubjective(){
		for (int i = 0; i< comments.size(); i++) {
			System.out.println("Paragraph "+i+":");
			for (Sentence sentence : comments.get(i).getSentences()) {
				System.out.println(sentence);
			}
		}
	}
	
	public Sentence getSentence(int idComment, int idSentence){
		for (Comment comment : comments) {
			if(comment.getId() == idComment){
				for (Sentence sentence : comment.getSentences()) {
					if(sentence.getId() == idSentence){
						return sentence;
					}
				}
			}
		}
		
		return null;
	}
}
