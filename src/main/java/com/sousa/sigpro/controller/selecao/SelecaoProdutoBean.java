package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.repository.Produtos;
import com.sousa.sigpro.repository.filter.ProdutoFilter;
import com.sousa.sigpro.util.Suporte;

@Named
@ViewScoped
public class SelecaoProdutoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Produtos produtos;

	private ProdutoFilter filtro;
	private LazyDataModel<Produto> model;

	public SelecaoProdutoBean() {
		filtro = new ProdutoFilter();
	}

	private void defineModelProduto() {
		model = new LazyDataModel<Produto>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Produto> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setPropriedadeOrdenacao(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(produtos.quantidadeFiltrados(filtro));
				return produtos.lista(filtro);
			}

		};
	}

	public void pesquisarProduto() {
		filtro.setComServico(false);
		defineModelProduto();
	}


	public void pesquisarServico() {
		filtro.setComProduto(false);
		defineModelProduto();
	}

	public void selecionar(Produto produto) {
		Produto resultado = produtos.porId(produto.getId());
		PrimeFaces.current().dialog().closeDynamic(resultado);
	}

	public ProdutoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(ProdutoFilter filtro) {
		this.filtro = filtro;
	}

	public void defineImagem(Produto produto) {
		String arquivo = Suporte.uploadLocal + produto.getNomeArquivoImagem();
		Suporte.setAtributoNaSessao("imagem", arquivo);
	}

	public LazyDataModel<Produto> getModel() {
		return model;
	}

	public void pesquisar(Boolean ativo) {
		filtro.setAtivo(ativo);
		defineModelProduto();
	}

	public void pesquisarProdutoMontagem() {
		filtro.setComComposto(true);
		defineModelProduto();
	}

}