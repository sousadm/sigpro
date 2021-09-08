package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.repository.Enderecos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Endereco.class)
public class EnderecoConverter implements Converter {

	private Enderecos enderecos;

	public EnderecoConverter() {
		enderecos = CDIServiceLocator.getBean(Enderecos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Endereco retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = enderecos.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Endereco endereco = (Endereco) value;
			return endereco.getId() == null ? null : endereco.getId().toString();
		}

		return "";
	}

}