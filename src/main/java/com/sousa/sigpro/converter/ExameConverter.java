package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.clinica.Exame;
import com.sousa.sigpro.repository.Exames;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Exame.class)
public class ExameConverter implements Converter {

	private Exames cids;

	public ExameConverter() {
		cids = CDIServiceLocator.getBean(Exames.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Exame retorno = null;
		if (value != null)
			retorno = cids.porId(value);
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Exame cid = (Exame) value;
			return cid.getCodigo() == null ? null : cid.getCodigo();
		}

		return "";
	}

}