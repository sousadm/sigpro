package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Aquisicao;
import com.sousa.sigpro.repository.Aquisicoes;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Aquisicao.class)
public class AquisicaoConverter implements Converter {

	private Aquisicoes cores;

	public AquisicaoConverter() {
		cores = CDIServiceLocator.getBean(Aquisicoes.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Aquisicao retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Aquisicao cor = (Aquisicao) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}
		
		return "";		
	}

}