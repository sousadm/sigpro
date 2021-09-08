package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;

import com.sousa.sigpro.model.Cartao;
import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.tipo.TipoEntradaSaida;
import com.sousa.sigpro.repository.Cartoes;
import com.sousa.sigpro.repository.CentrosDeCusto;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class CartaoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Cartoes cartoes;
	@Inject
	private CentrosDeCusto centros;

	private List<Cartao> lista;
	List<Integer> listaDias;
	private List<CentroDeCusto> listaCusto;
	private Cartao cartao = new Cartao();

	public CartaoBean() {

	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			listaCusto = centros.lista(TipoEntradaSaida.SAIDA);
			listaDias = new ArrayList<Integer>();
			for (int i = 1; i <= 30; i++) {
				listaDias.add(i);
			}
			pesquisar();
		}
	}

	public void pesquisar() {
		cartao = new Cartao();
		lista = cartoes.lista();
	}

	public void novo() {
		cartao = new Cartao();
	}

	public void excluir(Cartao cartao) {
		try {
			cartoes.remover(cartao);
			pesquisar();
			novo();
			FacesUtil.addInfoMessage("Registro excluído com sucesso.");
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void salvar() {
		try {
			cartao = cartoes.guardar(cartao);
			pesquisar();
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public List<Cartao> getLista() {
		return lista;
	}

	public Cartao getCartao() {
		return cartao;
	}

	public void setCartao(Cartao cartao) {
		this.cartao = cartao;
	}

	public List<Integer> getListaDias() {
		return listaDias;
	}

	public List<CentroDeCusto> getListaCusto() {
		return listaCusto;
	}

	public void imprimir() throws JRException, SQLException {
		try {

			pesquisar();
			if (lista.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String arquivo = seguranca.pathClass("relatorio") + "cartaocredito_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}
}