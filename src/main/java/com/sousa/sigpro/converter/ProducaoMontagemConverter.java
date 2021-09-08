package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.ProducaoMontagem;
import com.sousa.sigpro.repository.Producoes;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ProducaoMontagem.class)
public class ProducaoMontagemConverter implements Converter {

	private Producoes producoes;

	public ProducaoMontagemConverter() {
		producoes = CDIServiceLocator.getBean(Producoes.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ProducaoMontagem retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = producoes.montagemPorId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ProducaoMontagem producao = (ProducaoMontagem) value;
			return producao.getId() == null ? null : producao.getId().toString();
		}

		return "";
	}

}