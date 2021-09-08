package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.EnderecoFocus;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.FocusEndereco;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class SelecaoEnderecoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;

	private String uf;
	private String localidade;
	private String logradouro;
	private DFUnidadeFederativa xUf;
	private List<EnderecoFocus> enderecos;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			Endereco endereco = seguranca.getPessoaLogadoOrigem().getEndereco(TipoEndereco.COMERCIAL);
			xUf = endereco.getMunicipio().getUf();
		}
	}

	public void pesquisar() {
		FocusEndereco focus = new FocusEndereco(seguranca.getPessoaLogadoOrigem().getPJ());
		enderecos = focus.pesquisa(xUf.getCodigo(), localidade, logradouro);
	}

	public void selecionar(EnderecoFocus endereco) {
		PrimeFaces.current().dialog().closeDynamic(endereco);
	}

	public List<EnderecoFocus> getEnderecos() {
		return enderecos;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public DFUnidadeFederativa getxUf() {
		return xUf;
	}

	public void setxUf(DFUnidadeFederativa xUf) {
		this.xUf = xUf;
	}

}