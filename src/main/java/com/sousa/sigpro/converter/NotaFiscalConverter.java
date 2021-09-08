package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.NotaFiscal;
import com.sousa.sigpro.repository.NotaFiscais;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = NotaFiscal.class)
public class NotaFiscalConverter implements Converter {

	private NotaFiscais cores;

	public NotaFiscalConverter() {
		cores = CDIServiceLocator.getBean(NotaFiscais.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		NotaFiscal retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			NotaFiscal cor = (NotaFiscal) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}

		return "";
	}

}