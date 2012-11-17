package pt.uevora.hfernandes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class JoinEntitiesSentiTuites {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		 BufferedReader breader1 = new BufferedReader(new FileReader("sentituites-gold.csv"));
		 
		 
		 String line;
		 long id;
		 Map<Long, Integer> ids = new HashMap<Long, Integer>();
		 
		 
		 // get all keys on a map to eliminate multiple entity
		 while ((line = breader1.readLine()) != null) {
				id = Long.parseLong(line.split("\\|")[1]);
				
				if(!ids.containsKey(id)){
					ids.put(id, 0);
				}
				
				ids.put(id,ids.get(id)+1);
		 }
		 
		 List<Long> idsGoldToKeep = new ArrayList<Long>();

		 //Strip multiples
		 for (Long goldId : ids.keySet()) {
			if(ids.get(goldId).equals(1)){
				idsGoldToKeep.add(goldId);
			}
		}
		 
		 //get sentiment... Stupid way
		 
		 breader1 = new BufferedReader(new FileReader("sentituites-gold.csv"));
		 ids = new HashMap<Long, Integer>();
		 while ((line = breader1.readLine()) != null) {
				id = Long.parseLong(line.split("\\|")[1]);
		 
				if(idsGoldToKeep.contains(id)){
					try{
					ids.put(id, Integer.parseInt(line.split("\\|")[2]));
					}catch(Exception e){}
				}
				
		 } 
		 
		 List<String> entities = new ArrayList<String>();
		 //Ids has the entries we need
		 BufferedWriter writer = new BufferedWriter(new FileWriter("sentituites-all.csv"));
		 breader1 = new BufferedReader(new FileReader("sentituites-entities.csv"));
		 while ((line = breader1.readLine()) != null) {
				id = Long.parseLong(line.split("\\|")[1]);
		 
				if(ids.containsKey(id) && !line.split("\\|")[3].equals("ppcoelho")){
					writer.write(line.split("\\|")[0]+"|"+id+"|"+line.split("\\|")[3]+"|"+ids.get(id)+"\n");
					
					if(!entities.contains(line.split("\\|")[3])){
						entities.add(line.split("\\|")[3]);
					}
					
				}
				
		 } 
		 
		 IOUtils.closeQuietly(writer);
		 System.out.println(entities);
		 
		 

	}

}
