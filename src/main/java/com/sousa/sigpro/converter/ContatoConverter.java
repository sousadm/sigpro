package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Contato;
import com.sousa.sigpro.repository.Contatos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Contato.class)
public class ContatoConverter implements Converter {

	private Contatos contatos;
	
	public ContatoConverter() {
		contatos = CDIServiceLocator.getBean(Contatos.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Contato retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = contatos.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Contato conta = (Contato) value;
			return conta.getId() == null ? null : conta.getId().toString();
		}

		return "";
	}

}