package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Funcao;
import com.sousa.sigpro.repository.Funcoes;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Funcao.class)
public class FuncaoConverterSimple implements Converter {

	private Funcoes funcoes;

	public FuncaoConverterSimple() {
		this.funcoes = CDIServiceLocator.getBean(Funcoes.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Funcao retorno = null;

		if (value != null) {
			retorno = this.funcoes.porId(Long.parseLong(value));
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Long codigo = ((Funcao) value).getId();
			String retorno = (codigo == null ? null : codigo.toString());

			return retorno;
		}

		return "";
	}

}