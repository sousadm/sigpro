package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Projeto;
import com.sousa.sigpro.repository.Projetos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Projeto.class)
public class ProjetoConverter implements Converter {

	private Projetos cores;

	public ProjetoConverter() {
		cores = CDIServiceLocator.getBean(Projetos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Projeto retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Projeto cor = (Projeto) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}

		return "";
	}

}