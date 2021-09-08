package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.sousa.sigpro.model.Cfop;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.OperacaoFiscal;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoMovimentacaoFiscal;
import com.sousa.sigpro.repository.Cfops;
import com.sousa.sigpro.repository.OperacaoFiscais;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class OperacaoFiscalBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private Cfops cfops;
	@Inject
	private OperacaoFiscais operacoes;

	private boolean somenteSaoPaulo = false;
	private OperacaoFiscal operacao;
	private List<OperacaoFiscal> lista;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			Endereco endereco = seguranca.getPessoaLogadoOrigem().getEndereco(TipoEndereco.COMERCIAL);
			if (endereco.getMunicipio() != null && endereco.getMunicipio().getId() != null)
				somenteSaoPaulo = endereco.getMunicipio().getUf().equals(DFUnidadeFederativa.SP);
			limpar();
		}
	}

	public void salvar() {
		try {
			operacao.setEmpresa(seguranca.getPessoaLogadoOrigem());
			operacao = operacoes.guardar(operacao);
			FacesUtil.addInfoMessage("Registro gravado com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public boolean isSomenteSaoPaulo() {
		return somenteSaoPaulo;
	}

	public void pesquisar() {
		lista = operacoes.lista();
	}

	public void novo() {
		operacao = new OperacaoFiscal();
	}

	private void limpar() {
		novo();
		lista = new ArrayList<OperacaoFiscal>();
	}

	public void atualiza() {
		operacao.setDescricao(Suporte.substring(operacao.getCfop().getDescricao(), 0, 60));
		if (operacao.getCfop().getId() >= 6000) {
			operacao.setTipo(TipoMovimentacaoFiscal.VENDA_PRA_FORA);
		} else if (operacao.getCfop().getId() >= 5000) {
			operacao.setTipo(TipoMovimentacaoFiscal.VENDA_INTERNA);
		} else if (operacao.getCfop().getId() >= 3000) {
			operacao.setTipo(null);
		} else if (operacao.getCfop().getId() >= 2000) {
			operacao.setTipo(TipoMovimentacaoFiscal.COMPRA_DE_FORA);
		} else if (operacao.getCfop().getId() >= 1000) {
			operacao.setTipo(TipoMovimentacaoFiscal.COMPRA_INTERNA);
		}
	}

	public List<OperacaoFiscal> getLista() {
		return lista;
	}

	public void setLista(List<OperacaoFiscal> lista) {
		this.lista = lista;
	}

	public OperacaoFiscal getOperacao() {
		return operacao;
	}

	public void setOperacao(OperacaoFiscal cfop) {
		this.operacao = cfop;
	}

	public List<Cfop> getListaBaseCfop() {
		return cfops.lista();
	}

	public boolean isEmpresaNormal() {
		return seguranca.isEmpresaNormal();
	}

	public void excluir(int linha) {
		try {
			operacoes.remover(lista.get(linha));
			lista.remove(linha);
			FacesUtil.addInfoMessage("Registro excluído com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}