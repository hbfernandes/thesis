package pt.uevora.hfernandes;

import net.inductiva.semantic.client.SearchClient;

public class Semantic {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		SearchClient client = new SearchClient("http://rose.xdi.uevora.pt:8080/semantic/");
		client.activateCache();
		
		System.out.println(client.getSynonymList("rua"));
	}

}
