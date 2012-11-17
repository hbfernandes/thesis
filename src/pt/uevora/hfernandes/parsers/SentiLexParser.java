package pt.uevora.hfernandes.parsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pt.uevora.hfernandes.objects.SentiLexEntry;

public class SentiLexParser {

	public static Map<String, SentiLexEntry> parse(String input) throws IOException {
		BufferedReader breader = new BufferedReader(new FileReader(input));
		HashMap<String, SentiLexEntry> map = new HashMap<String, SentiLexEntry>();
		
		String line;
		SentiLexEntry entry;
		String part1, part2;
		String split[];
		while ((line = breader.readLine()) != null) {
			if (!line.isEmpty()) {
				
				// ignore idiom and verb for now...  || line.contains("PoS=V")
				if(line.contains("PoS=IDIOM")){
					continue;
				}
				
				entry = new SentiLexEntry();
				
				part1 = line.split(",")[0];
				part2 = line.split(",")[1];
				entry.setWord(part1);
				
				part1 = part2.split("\\.")[0];
				part2 = part2.split("\\.")[1];
				entry.setLemma(part1);
				
				split = part2.split(";");
				for (String string : split) {
					if(string.startsWith("PoS")){
						entry.setPos(string.split("=")[1].toUpperCase());
					}
					if(string.startsWith("TG")){
						entry.setTgn0(string.contains("N0"));
						entry.setTgn1(string.contains("N1"));
					}
					if(string.startsWith("POL")){
						String[] splitPol = string.split(":")[1].split("=");
						if(splitPol[0].equals("N0")){
							entry.setPoln0(Integer.parseInt(splitPol[1]));
						}
						if(splitPol[0].equals("N1")){
							entry.setPoln1(Integer.parseInt(splitPol[1]));
						}
					}
				}
				
				if(map.containsKey(entry.getWord()+";"+entry.getPos())){
//					System.out.println("DUPLICATE FOUND: "+line);
				}
				else{
					map.put(entry.getWord()+";"+entry.getPos(), entry);
				}
			}
		}
		
		return map;
	}
	
	
	public static void main(String args[]) throws IOException{
		parse("resources/SentiLex-flex-PT02.txt");
	}

}
