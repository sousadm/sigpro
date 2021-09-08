package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Pergunta;
import com.sousa.sigpro.repository.Perguntas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Pergunta.class)
public class PerguntaConverter implements Converter {

	private Perguntas perguntas;

	public PerguntaConverter() {
		perguntas = CDIServiceLocator.getBean(Perguntas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Pergunta retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = perguntas.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Pergunta pergunta = (Pergunta) value;
			return pergunta.getId() == null ? null : pergunta.getId().toString();
		}
		
		return "";		
	}

}