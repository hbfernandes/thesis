package pt.uevora.hfernandes.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="GROUP_FORM")
public class GroupForm {
	@Attribute(name="TYPE")
	String type;
	@Attribute(name="ALVO", required=false)
	String alvo;
	@ElementList(name="ALVOS", required=false)
	List<Alvo> alvos = new ArrayList<Alvo>();
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
	
	public List<Token> getGramaticalCategories() {
		return gramaticalCategories;
	}
	public void setGramaticalCategories(
			List<Token> gramaticalCategories) {
		this.gramaticalCategories = gramaticalCategories;
	}

	public String getAlvo() {
		return alvo;
	}
	public void setAlvo(String alvo) {
		this.alvo = alvo;
	}
	public List<Alvo> getAlvos() {
		return alvos;
	}
	public void setAlvos(List<Alvo> alvos) {
		this.alvos = alvos;
	}
	public String getText(){
		List<String> tokensText = new ArrayList<String>();
		for (Token token : gramaticalCategories) {
			tokensText.add(token.getText());
		}
		
		return StringUtils.join(tokensText, " ");
	}
	
}
