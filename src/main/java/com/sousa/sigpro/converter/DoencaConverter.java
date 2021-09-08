package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.clinica.Doenca;
import com.sousa.sigpro.repository.Doencas;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Doenca.class)
public class DoencaConverter implements Converter {

	private Doencas cids;

	public DoencaConverter() {
		cids = CDIServiceLocator.getBean(Doencas.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Doenca retorno = null;
		if (value != null)
			retorno = cids.porId(value);
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Doenca cid = (Doenca) value;
			return cid.getCodigo() == null ? null : cid.getCodigo();
		}

		return "";
	}

}