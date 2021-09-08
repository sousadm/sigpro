package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.repository.Contas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ContaCorrente.class)
public class ContaCorrenteConverter implements Converter {

	private Contas contas;

	public ContaCorrenteConverter() {
		contas = CDIServiceLocator.getBean(Contas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ContaCorrente retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = contas.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ContaCorrente conta = (ContaCorrente) value;
			return conta.getId() == null ? null : conta.getId().toString();
		}

		return "";
	}

}