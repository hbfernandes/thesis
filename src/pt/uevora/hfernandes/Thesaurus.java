package pt.uevora.hfernandes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class Thesaurus {

	public void convert() throws IOException{
		BufferedReader breader = new BufferedReader(new FileReader("th_pt_PT_v2_UTF8.txt"));
		BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("OpenThesaurusPT.properties"),"ISO8859-1"));
		 
		
		String line;
		String key = null;
		ArrayList<String> values = null;
		Properties prop = new Properties();
		while ((line = breader.readLine()) != null) { // Read the file line by line
			
			if(!line.startsWith("-")){
				if(key != null){
					System.out.println(key+"="+values.toString());
					prop.put(key, StringUtils.join(values, ","));
				}
				
				key = line.split("\\|")[0];
				values = new ArrayList<String>();
			}
			else{
				for (String value : Arrays.asList(line.substring(2).split("\\|"))) {
					if(!values.contains(value)){
						values.add(value);
					}
				}
				
			}
		}
		
		prop.store(bwriter, "Thesaurus properties style");
		
	}
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Thesaurus th = new Thesaurus();
		
		th.convert();

	}

}
