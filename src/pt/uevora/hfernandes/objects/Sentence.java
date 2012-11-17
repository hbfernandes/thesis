package pt.uevora.hfernandes.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="F")
public class Sentence {

	
	@ElementList(name="ALVOS", required=false)
	List<Alvo> alvos = new ArrayList<Alvo>();
	
	@Element(name="VALUE")
	String value;
	@Element(name="VALUE-NER-REPLACED", required=false)
	String valueNerReplaced;
	@Element(name="POS", required=false, data=true)
	String posValue;
	@Attribute(name="ID")
	int id;
	@Attribute(name="REFERS_CLAUSE", required=false)
	Integer clauseId;
	@Attribute(name="ALVO", required=false)
	String alvo;
	@Attribute(name="ALVO_TOKEN", required=false)
	Integer alvoToken;
	@Attribute(name="POL")
	int polarity;
	@Attribute(name="INT", required=false)
	String interpretation;
	
	@ElementList(name="TOKENS", required=false)
	List<Token> tokens = new ArrayList<Token>();
	
	@ElementList(name="CLAUSES", required=false)
	List<Clause> clauses = new ArrayList<Clause>();
	
	public Sentence(){}
	
	public Sentence(int id){
		this.id = id;
	}
	
	public Sentence(int id, String value, String alvo, int polarity, Integer clauseId, 
			String valueNerReplaced, Integer alvoToken, String interpretation){
		this.id = id;
		this.value = value;
		this.alvo = alvo;
		this.polarity = polarity;
		this.interpretation = interpretation;
		this.clauseId = clauseId;
		this.valueNerReplaced = valueNerReplaced;
		this.alvoToken = alvoToken;
	}
	
	boolean hasEntity;
	boolean hasOpinion;
	
	public List<Token> getTokens() {
		return tokens;
	}
	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean getHasEntity() {
		return hasEntity;
	}
	public void setHasEntity(boolean hasEntity) {
		this.hasEntity = hasEntity;
	}
	public boolean getHasOpinion() {
		return hasOpinion;
	}
	public void setHasOpinion(boolean hasOpinion) {
		this.hasOpinion = hasOpinion;
	}

	public List getTypeTokens(String type){
		List<Token> typeTokens = new ArrayList<Token>();
		for (Token token : tokens) {
			if(token.getType().equals(type)){
				typeTokens.add(token);
			}
		}
		return typeTokens;
	}
	
	public String getRawSentence(){
		return StringUtils.join(tokens, " ");
	}
	
	public String toString(){
		String sentence = new String();
		sentence += "Opinion: "+hasOpinion+"\n";
		sentence += "ADJ: "+getTypeTokens("ADJ")+"\n";
		sentence += "NAMES: "+getTypeTokens("PNM")+"\n";
		sentence += "Sentence: "+getRawSentence()+"\n";
		return sentence;
	}

	public List<Alvo> getAlvos() {
		return alvos;
	}

	public void setAlvos(List<Alvo> alvos) {
		this.alvos = alvos;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAlvo() {
		return alvo;
	}

	public void setAlvo(String alvo) {
		this.alvo = alvo;
	}

	public int getPolarity() {
		return polarity;
	}

	public void setPolarity(int polarity) {
		this.polarity = polarity;
	}

	public String getInterpretation() {
		return interpretation;
	}

	public void setInterpretation(String interpretation) {
		this.interpretation = interpretation;
	}

	public String getPosValue() {
		return posValue;
	}

	public void setPosValue(String posValue) {
		this.posValue = posValue;
	}
	
	public Sentence getSentenceCopy(){
		Sentence sentence = new Sentence(id, value, alvo, polarity, clauseId, 
				valueNerReplaced, alvoToken, interpretation);
		sentence.getAlvos().addAll(alvos);
		sentence.getTokens().addAll(tokens);
		sentence.getClauses().addAll(clauses);
		
		return sentence;
	}

	public List<Clause> getClauses() {
		return clauses;
	}
	
	public Clause getLastClause() {
		return clauses.get(clauses.size()-1);
	}

	public void setClauses(List<Clause> clauses) {
		this.clauses = clauses;
	}

	public Integer getClauseId() {
		return clauseId;
	}

	public void setClauseId(Integer clauseId) {
		this.clauseId = clauseId;
	}
	
	public Clause getReferedClause(){
		for (Clause clause : clauses) {
			if(clause == null || clauseId == null){
				continue;
			}
			if(clauseId == clause.getId()){
				return clause;
			}
		}
		return null;
	}

	public String getValueNerReplaced() {
		return valueNerReplaced;
	}

	public void setValueNerReplaced(String valueNerReplaced) {
		this.valueNerReplaced = valueNerReplaced;
	}

	public Integer getAlvoToken() {
		return alvoToken;
	}

	public void setAlvoToken(Integer alvoToken) {
		this.alvoToken = alvoToken;
	}
	
}
