package com.sousa.sigpro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.sousa.sigpro.model.ProdutoUnidade;
import com.sousa.sigpro.repository.Produtos;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ProdutoUnidade.class)
public class ProdutoUnidadeConverter implements Converter {

	private Produtos produtos;

	public ProdutoUnidadeConverter() {
		produtos = CDIServiceLocator.getBean(Produtos.class);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ProdutoUnidade retorno = null;

		if (value != null) {
			Long id = Long.parseLong(value);
			retorno = produtos.produtoUnidadePorId(id);
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			ProdutoUnidade produto = (ProdutoUnidade) value;
			return produto.getId() == null ? null : produto.getId().toString();
		}

		return "";
	}

}