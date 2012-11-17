package pt.uevora.hfernandes.objects;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name="ALVO")
public class Alvo {
	
	@Attribute(name="TIPO")
	String tipo;
	@Text
	String value;

	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
