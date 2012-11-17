package pt.uevora.hfernandes.ner;

import java.io.File;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import pt.uevora.hfernandes.objects.Entity;
import pt.uevora.hfernandes.objects.NerResource;

public class NER {
	private static final String ENTITIES_RESOURCE = "resources/SentiCorpus-PT_Entities.xml";

	protected static List<Entity> getNerResource() throws Exception {
		Serializer serializer = new Persister();
		File source = new File(ENTITIES_RESOURCE);

		NerResource ner = serializer.read(NerResource.class, source);
		return ner.getEntities();
	}
	
}
