package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.Banco;
import com.sousa.sigpro.repository.Bancos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Banco.class)
public class BancoConverter implements Converter {

	private Bancos cores;

	public BancoConverter() {
		cores = CDIServiceLocator.getBean(Bancos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Banco retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = cores.porId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Banco cor = (Banco) value;
			return cor.getId() == null ? null : cor.getId().toString();
		}
		
		return "";		
	}

}