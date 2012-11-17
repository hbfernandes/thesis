package pt.uevora.hfernandes.objects;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="TOKEN")
public class Token {
	@Attribute(name="INDEX")
	int index;
	@Attribute(name="TYPE")
	String type;
	@Attribute(name="INFINITIVE", required=false)
	String infinitive;
	@Attribute(name="CONJUGATION", required=false)
	String conjugation;
	@Attribute(name="POL")
	double polarity;
	@Attribute(name="INVERTED_POL", required=false)
	Boolean inverted;
	@Element(name="VALUE", required=false)
	String text;
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
	public double getPolarity() {
		return polarity;
	}
	public void setPolarity(double polarity) {
		this.polarity = polarity;
	}
	
	public String toString(){
		return text;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Boolean isInverted() {
		return inverted;
	}
	public void setInverted(Boolean inverted) {
		this.inverted = inverted;
	}
	public String getInfinitive() {
		return infinitive;
	}
	public void setInfinitive(String infinitive) {
		this.infinitive = infinitive;
	}
	public String getConjugation() {
		return conjugation;
	}
	public void setConjugation(String conjugation) {
		this.conjugation = conjugation;
	}
	
}
