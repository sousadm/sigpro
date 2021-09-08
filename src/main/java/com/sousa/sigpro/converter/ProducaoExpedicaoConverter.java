package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.ProducaoExpedicao;
import com.sousa.sigpro.repository.Producoes;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ProducaoExpedicao.class)
public class ProducaoExpedicaoConverter implements Converter {

	private Producoes producoes;

	public ProducaoExpedicaoConverter() {
		producoes = CDIServiceLocator.getBean(Producoes.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ProducaoExpedicao retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = producoes.expedicaoPorId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ProducaoExpedicao producao = (ProducaoExpedicao) value;
			return producao.getId() == null ? null : producao.getId().toString();
		}

		return "";
	}

}