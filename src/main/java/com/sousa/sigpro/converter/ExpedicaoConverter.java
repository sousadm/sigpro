package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.repository.Expedicoes;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Expedicao.class)
public class ExpedicaoConverter implements Converter {

	private Expedicoes expedicoes;
	
	public ExpedicaoConverter() {
		expedicoes = CDIServiceLocator.getBean(Expedicoes.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Expedicao retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = expedicoes.porId(id);
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Expedicao expedicao = (Expedicao) value;
			return expedicao.getId() == null ? null : expedicao.getId().toString();
		}
		
		return "";
	}

}