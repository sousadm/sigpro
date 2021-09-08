package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.component.tabview.TabView;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

import com.sousa.sigpro.model.Categoria;
import com.sousa.sigpro.model.Custo;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.tipo.TipoProduto;
import com.sousa.sigpro.repository.Categorias;
import com.sousa.sigpro.repository.Produtos;
import com.sousa.sigpro.repository.filter.ProdutoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class FormacaoDePrecoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;
	@Inject
	private Categorias categorias;
	@Inject
	private Produtos produtos;

	private int idxTabView = 0;
	private int opcao;
	private Categoria categoriaPai;
	private List<Categoria> subcategorias;
	private Custo custo;
	private Produto produto;
	private ProdutoFilter filtro;
	private List<Produto> lista;

	public FormacaoDePrecoBean() {
		limpar();
	}

	public void pesquisar() {
		lista = produtos.lista(filtro);
	}

	public void listaPorCategoria() {
		lista = produtos.lista(filtro);
	}

	public void imprimir() throws JRException, SQLException {
		try {

			if (lista == null || lista.size() == 0)
				throw new NegocioException("sem itens para imprimir");

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "formaPreco.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void listarProdutosComFiltro() {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select p from Produto p where p.ativo = true ");
		if (opcao == 1)
			condicao.append(" and p.categoria.categoriaPai.tipoProduto <> '" + TipoProduto.SERVICO + "' ");
		else if (opcao == 2)
			condicao.append(" and p.categoria.categoriaPai.tipoProduto = '" + TipoProduto.SERVICO + "' ");

		if (filtro.isComPrecoInferior())
			condicao.append(
					" and p.valorUnitario < ((p.precoCompra * (1 + ((p.custo.imposto + p.custo.despesa + p.custo.frete - p.custo.credito) / 100))) / (1 - ((p.custo.margem + p.custo.lucro + p.custo.residual + p.custo.tributo) / 100))) ");

		if (filtro.isSemPrecoCompra())
			condicao.append("  and p.precoCompra = 0  ");

		if (filtro.isSemPrecoVenda())
			condicao.append("  and p.valorUnitario = 0  ");

		condicao.append(" order by p.nome   ");
		lista = manager.createQuery(condicao.toString(), Produto.class).getResultList();

	}

	public void limpar() {
		produto = new Produto();
		filtro = new ProdutoFilter();
		lista = new ArrayList<Produto>();
		custo = new Custo();
	}

	public void onProdutoChosen(SelectEvent event) {
		produto = (Produto) event.getObject();
		if (lista.indexOf(produto) < 0) {
			lista.add(produto);
		}
	}

	public String condicaoFiltroProduto() {
		String condicao = "";
		for (Produto prod : lista) {
			if (condicao != "")
				condicao = condicao + ", ";
			condicao = condicao + prod.getId();
		}
		if (condicao != "")
			condicao = " p.id in (" + condicao + ") ";
		return condicao;
	}

	public void carregarSubcategorias() {
		subcategorias = categorias.subcategoriasDe(categoriaPai);
	}

	public List<Categoria> getCategoriasRaizes() {
		return categorias.raizes();
	}

	public void removerItem(int linha) {
		lista.remove(linha);
	}

	public Produto getProduto() {
		return produto;
	}

	public ProdutoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(ProdutoFilter filtro) {
		this.filtro = filtro;
	}

	public List<Produto> getLista() {
		return lista;
	}

	public void setLista(List<Produto> lista) {
		this.lista = lista;
	}

	public Custo getCusto() {
		return custo;
	}

	public void setCusto(Custo custo) {
		this.custo = custo;
	}

	public Categoria getCategoriaPai() {
		return categoriaPai;
	}

	public void setCategoriaPai(Categoria categoriaPai) {
		this.categoriaPai = categoriaPai;
	}

	public List<Categoria> getSubcategorias() {
		return subcategorias;
	}

	public void setSubcategorias(List<Categoria> subcategorias) {
		this.subcategorias = subcategorias;
	}

	public int getOpcao() {
		return opcao;
	}

	public void setOpcao(int opcao) {
		this.opcao = opcao;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
		this.produto.setUnidades(produtos.produtoUnidades(produto));
		this.produto.setListaComposto(produtos.compostosPorProduto(produto));
	}

	@Transactional
	public void aplicar(boolean define) {
		try {

			if (!custo.isMarkUpConsistente())
				throw new NegocioException(
						"[Mark Up] soma do percentuais (margem + lucro + residual + tributo) deve ser menor que 100");

			filtro = new ProdutoFilter();
			filtro.setCondicaoExtra(condicaoFiltroProduto());

			StringBuilder condicao = new StringBuilder();

			condicao.append("UPDATE Produto p ");
			condicao.append("SET p.custo.credito = " + custo.getCredito());
			condicao.append("   ,p.custo.imposto = " + custo.getImposto());
			condicao.append("   ,p.custo.despesa = " + custo.getDespesa());
			condicao.append("   ,p.custo.frete = " + custo.getFrete());
			condicao.append("   ,p.custo.margem = " + custo.getMargem());
			condicao.append("   ,p.custo.lucro = " + custo.getLucro());
			condicao.append("   ,p.custo.residual = " + custo.getResidual());
			condicao.append("   ,p.custo.tributo = " + custo.getTributo());
			if (define)
				condicao.append("   ,p.valorUnitario = precoCompra * " + custo.getMarkupMultiplicador());
			condicao.append(" WHERE " + filtro.getCondicaoExtra());
			manager.createQuery(condicao.toString()).executeUpdate();
			lista = produtos.lista(filtro);

		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	@Transactional
	public void gravarProduto(boolean define) {
		try {
			if (define)
				produto.setValorUnitario(produto.getPrecoVendaSugerido());
			produto = manager.merge(produto);
		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public int getIdxTabView() {
		return idxTabView;
	}

	public void onTabChange(TabChangeEvent event) {
		TabView tabView = (TabView) event.getComponent();
		idxTabView = tabView.getChildren().indexOf(event.getTab());
	}

}