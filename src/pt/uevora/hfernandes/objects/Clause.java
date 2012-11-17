package pt.uevora.hfernandes.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="CLAUSE")
public class Clause {
	@Attribute(name="TYPE")
	String type;
	@Attribute(name="ID", required=false)
	int id;
	@Attribute(name="POL", required=false)
	int polarity;
	@Element(name="VALUE", required=false)
	String text;
	@ElementList(required=false, inline=true)
	List<GroupForm> groupForms = new ArrayList<GroupForm>();
	@ElementList(required=false, inline=true)
	List<Token> gramaticalCategories = new ArrayList<Token>();
	
	String textAndType;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTextAndType() {
		return textAndType;
	}
	public void setTextAndType(String textAndType) {
		this.textAndType = textAndType;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getPolarity() {
		return polarity;
	}
	public void setPolarity(int polarity) {
		this.polarity = polarity;
	}
	public String toString(){
		return text;
	}
	public List<GroupForm> getGroupForms() {
		return groupForms;
	}
	public GroupForm getLastGroupForms() {
		return groupForms.get(groupForms.size()-1);
	}
	public void setGroupForms(List<GroupForm> groupForms) {
		this.groupForms = groupForms;
	}
	public List<Token> getGramaticalCategories() {
		return gramaticalCategories;
	}
	public void setGramaticalCategories(
			List<Token> gramaticalCategories) {
		this.gramaticalCategories = gramaticalCategories;
	}
	
	public String getTokenText(){
		List<String> text = new ArrayList<String>();
		for (Token token : gramaticalCategories) {
			text.add(token.getText());
		}
		return StringUtils.join(text, " ");
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
