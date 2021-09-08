package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Cartao;
import com.sousa.sigpro.repository.Cartoes;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Cartao.class)
public class CartaoConverter implements Converter {

	private Cartoes cores;

	public CartaoConverter() {
		cores = CDIServiceLocator.getBean(Cartoes.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Cartao retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Cartao cor = (Cartao) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}
		
		return "";		
	}

}