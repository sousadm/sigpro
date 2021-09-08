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
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.Categoria;
import com.sousa.sigpro.repository.Categorias;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class CategoriaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Categorias categorias;

	private Categoria categoria;
	private CondicaoFilter filtro = new CondicaoFilter();

	private Categoria subCategoria;
	private LazyDataModel<Categoria> lista;

	public CategoriaBean() {

	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			filtro.setDisponivel(true);
			pesquisar();
		}
	}

	public void novo() {
		categoria = new Categoria();
		subCategoria = new Categoria();
	}

	public void addSubcategoria() {
		subCategoria = new Categoria();
	}

	public void salvar() {
		try {
			categoria = categorias.guardar(categoria);
			FacesUtil.addInfoMessage("Registro gravado com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public LazyDataModel<Categoria> getLista() {
		return lista;
	}

	public void setLista(LazyDataModel<Categoria> lista) {
		this.lista = lista;
	}

	public String getTitulo() {
		return "Categoria";
	}

	public Categoria getSubCategoria() {
		return subCategoria;
	}

	public void setSubCategoria(Categoria subCategoria) {
		this.subCategoria = subCategoria;
	}

	public void imprimir() throws JRException, SQLException {
		try {

			filtro.setDisponivel(true);
			List<Categoria> lst = categorias.lista(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());

			String arquivo = seguranca.pathClass("relatorio") + "categoria_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void gravarSubCategoria() {
		try {

			if (subCategoria.getDescricao().length() < 3)
				throw new NegocioException("Informe uma descrição válida");

			if (subCategoria.getCategoriaPai() == null) {
				subCategoria.setPessoa(seguranca.getPessoaLogado().getOrigem());
				subCategoria.setCategoriaPai(categoria);
				categoria.getSubcategorias().add(subCategoria);
			}

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void excluirSubCategoria(int linha) {
		try {
			categoria.getSubcategorias().remove(linha);
			FacesUtil.addInfoMessage("excluido com sucesso");
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void excluir(Categoria categoria) {
		try {
			categorias.remover(categoria);
			FacesUtil.addInfoMessage("Registro excluído com sucesso.");
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public void pesquisar() {
		lista = new LazyDataModel<Categoria>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Categoria> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(categorias.quantidadeFiltrados(filtro));
				return categorias.lista(filtro);
			}

		};
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
		if (categoria != null) {
			this.categoria = categorias.porId(categoria.getId());
			this.categoria.setSubcategorias(categorias.subcategoriasDe(categoria));
		}
	}

}