package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Pessoa.class)
public class PessoaConverter implements Converter {

	private Pessoas pessoas;
	
	public PessoaConverter() {
		pessoas = CDIServiceLocator.getBean(Pessoas.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Pessoa retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = pessoas.porId(id);
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Pessoa pessoa = (Pessoa) value;
			return pessoa.getId() == null ? null : pessoa.getId().toString();
		}
		
		return "";
	}

}