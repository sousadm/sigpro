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

import com.sousa.sigpro.model.CentroDeCusto;
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
public class CentroDeCustoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private CentrosDeCusto centros;

	private int indice;
	private CentroDeCusto centroCusto;
	private List<CentroDeCusto> lista;

	public CentroDeCustoBean() {
		novo();
	}

	public CentroDeCusto getCusto() {
		return centroCusto;
	}

	public List<CentroDeCusto> getLista() {
		return lista;
	}

	public void setCusto(CentroDeCusto custo) {
		this.centroCusto = custo;
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			consultar();
		}
	}

	public void editar(int numero) {
		this.indice = numero;
		this.centroCusto = centros.porId(lista.get(numero).getId());
	}

	public void novo() {
		indice = -1;
		centroCusto = new CentroDeCusto();
	}

	public void salvar() {
		try {
			if (centroCusto.getEmpresa() == null) {
				centroCusto.setEmpresa(seguranca.getPessoaLogadoOrigem());
			}

			centroCusto = centros.guardar(centroCusto);

			if (indice >= 0) {
				lista.set(indice, centroCusto);
			} else {
				consultar();
			}

			FacesUtil.addInfoMessage("Gravado com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void consultar() {
		lista = centros.lista();
	}

	public String getTitulo() {
		return "Centro de custo";
	}

	public String getModulo() {
		return "Financeiro";
	}

	public void excluir(int linha) {
		try {
			centros.remover(lista.get(linha));
			lista.remove(linha);
			FacesUtil.addInfoMessage("Removido com sucesso.");
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir() throws JRException, SQLException {
		try {

			List<CentroDeCusto> lst = centros.lista();

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String arquivo = seguranca.pathClass("relatorio") + "centrocusto_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}
	
}