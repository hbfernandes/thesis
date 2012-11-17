package pt.uevora.hfernandes.objects;

public class SentiLexEntry {

	String word;
	String lemma;
	String pos;
	String flex;
	boolean tgn0;
	boolean tgn1;
	int poln0;
	int poln1;
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getFlex() {
		return flex;
	}
	public void setFlex(String flex) {
		this.flex = flex;
	}
	public boolean isTgn0() {
		return tgn0;
	}
	public void setTgn0(boolean tgn0) {
		this.tgn0 = tgn0;
	}
	public boolean isTgn1() {
		return tgn1;
	}
	public void setTgn1(boolean tgn1) {
		this.tgn1 = tgn1;
	}
	public int getPoln0() {
		return poln0;
	}
	public void setPoln0(int poln0) {
		this.poln0 = poln0;
	}
	public int getPoln1() {
		return poln1;
	}
	public void setPoln1(int poln1) {
		this.poln1 = poln1;
	}
	
	
}
