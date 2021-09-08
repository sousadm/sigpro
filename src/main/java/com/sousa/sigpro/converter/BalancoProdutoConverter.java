package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.BalancoProduto;
import com.sousa.sigpro.repository.Balancos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = BalancoProduto.class)
public class BalancoProdutoConverter implements Converter {

	private Balancos balancos;
	
	public BalancoProdutoConverter() {
		balancos = CDIServiceLocator.getBean(Balancos.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		BalancoProduto retorno = null;
		
		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = balancos.porProdutoId(id);			
		}
		
		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			BalancoProduto balancoProduto = (BalancoProduto) value;
			return balancoProduto.getId() == null ? null : balancoProduto.getId().toString();
		}
		
		return "";
	}

}
