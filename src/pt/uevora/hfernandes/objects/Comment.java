package pt.uevora.hfernandes.objects;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="COMENT")
public class Comment {

	@ElementList(inline=true, required=false)
	List<Sentence> sentences = new ArrayList<Sentence>();
	
	@Attribute(name="ID")
	int id;

	public Comment(){}
	
	public Comment(int id){
		this.id = id;
	}
	
	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(List<Sentence> sentences) {
		this.sentences = sentences;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
