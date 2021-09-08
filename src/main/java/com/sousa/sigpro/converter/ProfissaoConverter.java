package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Profissao;
import com.sousa.sigpro.repository.Profissoes;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Profissao.class)
public class ProfissaoConverter implements Converter {

	private Profissoes profissoes;

	public ProfissaoConverter() {
		profissoes = CDIServiceLocator.getBean(Profissoes.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Profissao retorno = null;
		retorno = profissoes.porId(value);
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		
		if (value != null) {
			Profissao cor = (Profissao) value;
			return cor.getCodigo() == null ? null : cor.getCodigo();
		}

		return "";
	}

}