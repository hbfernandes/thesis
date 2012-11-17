package pt.uevora.hfernandes.objects;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="ENTITIES")
public class NerResource {

	@ElementList(inline=true)
	List<Entity> entities = new ArrayList<Entity>();

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
}
