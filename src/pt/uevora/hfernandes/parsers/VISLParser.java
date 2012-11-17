package pt.uevora.hfernandes.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.objects.Clause;
import pt.uevora.hfernandes.objects.GroupForm;
import pt.uevora.hfernandes.objects.Sentence;
import pt.uevora.hfernandes.objects.Token;

public class VISLParser {

	
	public static void read() throws IOException{
		File in = new File("visltextClean.txt");
		
		BufferedReader breader = new BufferedReader(new FileReader(in));
		
		Sentence sentence = new Sentence();
		sentence.setId(0);
		sentence.setValue("todos");
		sentence.setPolarity(0);
		String line;
		int actualFclLevel = 0;
		boolean inFcl = false;
		int actualNpLevel = 0;
		boolean inNp = false;
		int index = 0;
		String fcl = "fcl";
		
		int countGrammaticalCategory = 0;
		Clause clause = null;
		while ((line = breader.readLine()) != null) { // Read the file line by line
			if(line.isEmpty()){ // Blank line means end of paragraph, create new one
				break;
			}
			
			// check level
			int level = getLevel(line);
			
			// ignore not fcl level's 
			if(actualFclLevel == level){
				inFcl = false;
				actualFclLevel = 0;
			}
			
			// destroy np (clause context) if it's finished 
			if(actualNpLevel == level){
				inNp = false;
				actualNpLevel = 0;
			}
			
			if(line.contains(fcl)){
				// init fcl (clause) context
				inFcl = true;
				actualFclLevel = level;
				
				clause =  new Clause();
				clause.setType("FCL");
				sentence.getClauses().add(clause);
			} else if(inFcl) {
				
				List<String> infoElems = getExtraInfo(line);
				boolean isToken = infoElems.size() == 4; // 4 is token, 2 is group  
				
				if(isToken){
					Token token = new Token();
					token.setIndex(index++);
					token.setText(infoElems.get(3).trim());
					token.setType(infoElems.get(1).toUpperCase());
					sentence.getTokens().add(token);
				}
				
				if(infoElems.get(1).equals("np")){
					inNp = true;
					actualNpLevel = level;
					
					GroupForm groupForm = new GroupForm();
					groupForm.setType("NP");
					sentence.getLastClause().getGroupForms().add(groupForm);
					
				} else if(inNp) {
					if(isToken){	
						Token token = new Token();
						token.setIndex(countGrammaticalCategory++);
						token.setText(infoElems.get(3).trim());
						token.setType(infoElems.get(1).toUpperCase());
						sentence.getLastClause().getLastGroupForms().getGramaticalCategories().add(token);
					}
						
				} else {
					if(isToken){
						Token token = new Token();
						token.setIndex(countGrammaticalCategory++);
						token.setText(infoElems.get(3).trim());
						token.setType(infoElems.get(1).toUpperCase());
						sentence.getLastClause().getGramaticalCategories().add(token);
					}
					
				}
				
			}

		}
		try {
			Serializer serializer = new Persister();
			File result = new File("vislparse.txt");

			serializer.write(sentence, result);
			System.out.println(sentence.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<String> getExtraInfo(String line) {
		// ==P:v-fin('ser' PR 3S IND)      é
		Pattern p = Pattern.compile("(=*>?)(.*):(.*)\\((.*)\\)(.*)");
		Matcher matcher = p.matcher(line);
		
		List<String> list = new ArrayList<String>();
		
		if(matcher.find()){
			// ignore first and second goup
			for (int i=2; i<=matcher.groupCount(); i++) {
				list.add(matcher.group(i).trim());
			}
		} else {
			// ==SUBJ:np
			p = Pattern.compile("(=*>?)(.*):(.*)");
			matcher = p.matcher(line);
			
			if(matcher.find()){
				// ignore first and second goup
				for (int i=2; i<=matcher.groupCount(); i++) {
					list.add(matcher.group(i).trim());
				}
			}
			else {
				// ==(,|\\.+|?|!)
				p = Pattern.compile("(=*>?)(,|\\.+|\\?|!)(.*)");
				matcher = p.matcher(line);
				
				if(matcher.find()){
					// ignore first and second goup
					list.add(matcher.group(2).trim());
				}
			}
		}
		
		
		return list;
	}

	public static void processSentence(Sentence sentence) {

		try {
			String[] lines = sentence.getPosValue().split("\n");

			int actualFclLevel = 0;
			boolean inFcl = false;
			int actualNpLevel = 0;
			boolean inNp = false;
			int index = 0;
			String fcl = "fcl";

			int countGrammaticalCategory = 0;
			Clause clause = null;
			for (String line : lines) { // Read the file line by line
				if (line.isEmpty()) { // Blank line means end of paragraph,
										// create new one
					break;
				}

				// check level
				int level = getLevel(line);

				// ignore not fcl level's
				if (actualFclLevel == level) {
					inFcl = false;
					actualFclLevel = 0;
				}

				// destroy np (clause context) if it's finished
				if (actualNpLevel >= level) {
					inNp = false;
					actualNpLevel = 0;
				}

				if (line.contains(fcl) && !inFcl) {
					// init fcl (clause) context
					inFcl = true;
					actualFclLevel = level;

					clause = new Clause();
					clause.setType("FCL");
					sentence.getClauses().add(clause);
				} else if (inFcl) {

					List<String> infoElems = getExtraInfo(line);
					if (infoElems.isEmpty()) {
						continue;
					}
					boolean isToken = infoElems.size() == 4; // 4 is token, 2 is
																// group

					if (isToken) {
						Token token = new Token();
						token.setIndex(index++);
						token.setText(infoElems.get(3).trim());
						token.setType(infoElems.get(1).toUpperCase());
						sentence.getTokens().add(token);
					}

					if (infoElems.get(1).equals("np")) {
						inNp = true;
						actualNpLevel = level;

						GroupForm groupForm = new GroupForm();
						groupForm.setType("NP");
						sentence.getLastClause().getGroupForms().add(groupForm);

					} else if (inNp) {
						if (isToken) {
							Token token = new Token();
							token.setIndex(countGrammaticalCategory++);
							token.setText(infoElems.get(3).trim());
							token.setType(infoElems.get(1).toUpperCase());
							sentence.getLastClause().getLastGroupForms()
									.getGramaticalCategories().add(token);
						}

					} else {
						if (isToken) {
							Token token = new Token();
							token.setIndex(countGrammaticalCategory++);
							token.setText(infoElems.get(3).trim());
							token.setType(infoElems.get(1).toUpperCase());
							sentence.getLastClause().getGramaticalCategories()
									.add(token);
						}

					}

				}

			}

			if (sentence.getValue() == null) {
				sentence.setValue(sentence.getRawSentence());
			}
		} catch (Exception e) {
			System.out.println(sentence.getPosValue());
			e.printStackTrace();

		}
	}

	/**
	 * Same as processSentence but ignoring NP's and removing the general token list (breaks backward compatibility) 
	 * @param sentence
	 */
	public static void processSentence2(Sentence sentence) {

		try {
			String[] lines = sentence.getPosValue().split("\n");

			int actualFclLevel = 0;
			int fclId = 1;
			boolean inFcl = false;
			String fcl = "fcl";

			int countGrammaticalCategory = 0;
			Clause clause = null;
			for (String line : lines) { // Read the file line by line
				if (line.isEmpty()) { // Blank line means end of paragraph,
										// create new one
					break;
				}

				// check level
				int level = getLevel(line);

				// ignore not fcl level's
				if (actualFclLevel == level) {
					inFcl = false;
					actualFclLevel = 0;
				}

				if (line.contains(fcl) && !inFcl) {
					// init fcl (clause) context
					inFcl = true;
					actualFclLevel = level;
					
					clause = new Clause();
					clause.setType(fcl.toUpperCase());
					clause.setId(fclId);
					
					fclId++;
					sentence.getClauses().add(clause);
				} else if (inFcl) {
					List<String> infoElems = getExtraInfo(line);
					if (infoElems.isEmpty()) {
						continue;
					}
					
					boolean isToken = infoElems.size() == 4; // 4 is token, 2 is

					if (isToken) {
						Token token = new Token();
						token.setIndex(countGrammaticalCategory++);
						token.setText(infoElems.get(3).trim());
						token.setType(infoElems.get(1).toUpperCase());
						sentence.getLastClause().getGramaticalCategories().add(token);
					}
				}

			}

			if (sentence.getValue() == null) {
				sentence.setValue(sentence.getRawSentence());
			}
		} catch (Exception e) {
			System.out.println(sentence.getPosValue());
			e.printStackTrace();

		}
	}
	
	/**
	 * Same as processSentence but ignoring NP's and removing the general token list (breaks backward compatibility) 
	 * @param sentence
	 */
	public static void processSentence3(Sentence sentence) {

		try {
			String[] lines = sentence.getPosValue().split("\n");

			int actualFclLevel = 0;
			int fclId = 1;
			boolean inFcl = false;
			String fcl = "fcl";

			int countGrammaticalCategory = 0;
			Clause clause = null;
			for (String line : lines) { // Read the file line by line
				if (line.isEmpty()) { // Blank line means end of paragraph,
										// create new one
					break;
				}

				// check level
				int level = getLevel(line);

				// ignore not fcl level's
				if (actualFclLevel == level) {
					inFcl = false;
					actualFclLevel = 0;
				}

				if (line.contains(fcl) && !inFcl) {
					// init fcl (clause) context
					inFcl = true;
					actualFclLevel = level;
					
					clause = new Clause();
					clause.setType(fcl.toUpperCase());
					clause.setId(fclId);
					
					fclId++;
					sentence.getClauses().add(clause);
				} else if (inFcl) {
					List<String> infoElems = getExtraInfo(line);
					if (infoElems.isEmpty()) {
						continue;
					}
					
					boolean isToken = infoElems.size() == 4; // 4 is token, 2 is
					boolean isPunct = infoElems.size() == 1; // 4 is token, 2 is

					if (isToken) {
						Token token = new Token();
						token.setIndex(countGrammaticalCategory++);
						token.setText(infoElems.get(3).trim());
						token.setType(infoElems.get(1).toUpperCase());
						sentence.getLastClause().getGramaticalCategories().add(token);
					}
					
					if(isPunct){
						Token token = new Token();
						token.setIndex(countGrammaticalCategory++);
						token.setText(infoElems.get(0).trim());
						token.setType("PUNCT");
						sentence.getLastClause().getGramaticalCategories().add(token);
					}
				}

			}

			if (sentence.getValue() == null) {
				sentence.setValue(sentence.getRawSentence());
			}
		} catch (Exception e) {
			System.out.println(sentence.getPosValue());
			e.printStackTrace();

		}
	}
	
	/**
	 * Save information about verbs 
	 * @param sentence
	 */
	public static void processSentence4(Sentence sentence) {

		try {
			String[] lines = sentence.getPosValue().split("\n");

			int actualFclLevel = 0;
			int fclId = 1;
			boolean inFcl = false;
			String fcl = "fcl";

			int countGrammaticalCategory = 0;
			Clause clause = null;
			for (String line : lines) { // Read the file line by line
				if (line.isEmpty()) { // Blank line means end of paragraph,
										// create new one
					continue;
				}

				// check level
				int level = getLevel(line);

				// ignore not fcl level's
				if (actualFclLevel == level) {
					inFcl = false;
					actualFclLevel = 0;
				}

				if (line.contains(fcl) && !inFcl) {
					// init fcl (clause) context
					inFcl = true;
					actualFclLevel = level;
					
					clause = new Clause();
					clause.setType(fcl.toUpperCase());
					clause.setId(fclId);
					
					fclId++;
					sentence.getClauses().add(clause);
				} else if (inFcl) {
					List<String> infoElems = getExtraInfo(line);
					if (infoElems.isEmpty()) {
						continue;
					}
					
					boolean isToken = infoElems.size() == 4; // 4 is token, 2 is
					boolean isPunct = infoElems.size() == 1; // 4 is token, 2 is

					if (isToken) {
						Token token = new Token();
						token.setIndex(countGrammaticalCategory++);
						token.setText(infoElems.get(3).trim()); 
						token.setType(infoElems.get(1).toUpperCase());
						
						// verbs conjugation
						if(token.getType().startsWith("V-")){
							token.setInfinitive(infoElems.get(2).split("'")[1].trim());
							
							// some don't have conjugation
							if(infoElems.get(2).split("'").length >=3){
								token.setConjugation(infoElems.get(2).split("'")[2].trim());
							}
						}
						
						sentence.getLastClause().getGramaticalCategories().add(token);
					}
					
					if(isPunct){
						Token token = new Token();
						token.setIndex(countGrammaticalCategory++);
						token.setText(infoElems.get(0).trim());
						token.setType("PUNCT");
						sentence.getLastClause().getGramaticalCategories().add(token);
					}
				}

			}

			if (sentence.getValue() == null) {
				sentence.setValue(sentence.getRawSentence());
			}
		} catch (Exception e) {
			System.out.println(sentence.getPosValue());
			e.printStackTrace();

		}
	}
	
	public static int getLevel(String line){
		for (int i = 0; i < line.length(); i++) {
			if(line.charAt(i) != '='){
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("teste");
		read();
	}

}
