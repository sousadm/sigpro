package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;

import com.sousa.sigpro.model.Funcao;
import com.sousa.sigpro.repository.Funcoes;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class FuncaoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Funcoes funcoes;

	private String descricao;
	private Funcao funcao;
	private List<Funcao> lista;

	public FuncaoBean() {

	}

	public void pesquisar() {
		lista = funcoes.lista(descricao);
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			novo();
			pesquisar();
		}
	}

	public void novo() {
		funcao = new Funcao();
		funcao.setOrigem(seguranca.getPessoaLogadoOrigem());
	}

	public void salvar() {
		try {
			funcao = funcoes.guardar(funcao);
			pesquisar();
			FacesUtil.addInfoMessage("Registro gravado com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void excluir(int indice) {
		try {
			funcoes.remover(lista.get(indice));
			lista.remove(indice);
			FacesUtil.addInfoMessage("Registro excluído com sucesso.");
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Funcao getFuncao() {
		return funcao;
	}

	public void setFuncao(Funcao funcao) {
		this.funcao = funcao;
	}

	public List<Funcao> getLista() {
		return lista;
	}

	public void setLista(List<Funcao> lista) {
		this.lista = lista;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void imprimir_lista() throws JRException, SQLException {
		try {

			List<Funcao> lst = funcoes.lista(descricao);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "funcao_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}