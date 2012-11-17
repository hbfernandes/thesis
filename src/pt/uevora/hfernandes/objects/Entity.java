package pt.uevora.hfernandes.objects;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root(name="Entity")
public class Entity {

	@Attribute(name="NAME")
	String name;
	
	@ElementList(inline=true)
	List<Alvo> alvos = new ArrayList<Alvo>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Alvo> getAlvos() {
		return alvos;
	}

	public void setAlvos(List<Alvo> alvos) {
		this.alvos = alvos;
	}

	public String[] getAlvosArray(){
		String[] alvosArray = new String[alvos.size()];
		for (int i = 0; i< alvos.size(); i++) {
			alvosArray[i] = alvos.get(i).getValue();
		}
		return alvosArray;
	}
	
}
