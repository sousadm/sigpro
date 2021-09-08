package com.sousa.sigpro.controller;

import java.io.Serializable;

import javax.inject.Named;

import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.mdfe3.classes.def.MDFTipoCarroceria;
import com.fincatto.documentofiscal.mdfe3.classes.def.MDFTipoProprietario;
import com.fincatto.documentofiscal.mdfe3.classes.def.MDFTipoRodado;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe400.classes.NFFinalidade;
import com.fincatto.documentofiscal.nfe400.classes.NFIndicadorFormaPagamento;
import com.fincatto.documentofiscal.nfe400.classes.NFModalidadeFrete;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoCombustivelTipo;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoImpostoTributacaoICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoItemModalidadeBCICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaCOFINS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaIPI;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaPIS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoVeiculoCor;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaSituacaoOperacionalSimplesNacional;
import com.fincatto.documentofiscal.nfe400.classes.NFOrigem;
import com.fincatto.documentofiscal.nfe400.classes.NFProcessoEmissor;
import com.fincatto.documentofiscal.nfe400.classes.NFRegimeTributario;
import com.fincatto.documentofiscal.nfe400.classes.NFTipo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIdentificadorLocalDestinoOperacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorIEDestinatario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorPresencaComprador;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFMeioPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFOperacaoConsumidorFinal;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFOperadoraCartao;
import com.sousa.sigpro.model.clinica.TipoAtendimentoPadrao;
import com.sousa.sigpro.model.tipo.TipoAcesso;
import com.sousa.sigpro.model.tipo.TipoAgendaStatus;
import com.sousa.sigpro.model.tipo.TipoApiCobranca;
import com.sousa.sigpro.model.tipo.TipoAreaQuestionario;
import com.sousa.sigpro.model.tipo.TipoAtividadePrincipal;
import com.sousa.sigpro.model.tipo.TipoBoleto;
import com.sousa.sigpro.model.tipo.TipoCaixaStatus;
import com.sousa.sigpro.model.tipo.TipoCategoriaHabilitacao;
import com.sousa.sigpro.model.tipo.TipoCentroCusto;
import com.sousa.sigpro.model.tipo.TipoCheckListLocVeiculo;
import com.sousa.sigpro.model.tipo.TipoCobranca;
import com.sousa.sigpro.model.tipo.TipoComando;
import com.sousa.sigpro.model.tipo.TipoComoConheceu;
import com.sousa.sigpro.model.tipo.TipoDiaSemana;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoEngajamento;
import com.sousa.sigpro.model.tipo.TipoEspecialidade;
import com.sousa.sigpro.model.tipo.TipoEstrategia;
import com.sousa.sigpro.model.tipo.TipoFluxoFinanceiro;
import com.sousa.sigpro.model.tipo.TipoFuncao;
import com.sousa.sigpro.model.tipo.TipoGraficoModelo;
import com.sousa.sigpro.model.tipo.TipoGrupo;
import com.sousa.sigpro.model.tipo.TipoImpacto;
import com.sousa.sigpro.model.tipo.TipoImportancia;
import com.sousa.sigpro.model.tipo.TipoMes;
import com.sousa.sigpro.model.tipo.TipoMeta;
import com.sousa.sigpro.model.tipo.TipoMetodoCalculo;
import com.sousa.sigpro.model.tipo.TipoModulo;
import com.sousa.sigpro.model.tipo.TipoMovimentacaoFiscal;
import com.sousa.sigpro.model.tipo.TipoNaturezaOperacaoNFSe;
import com.sousa.sigpro.model.tipo.TipoOcorrencia;
import com.sousa.sigpro.model.tipo.TipoPagamento;
import com.sousa.sigpro.model.tipo.TipoPergunta;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.model.tipo.TipoPorteEmpresa;
import com.sousa.sigpro.model.tipo.TipoProbabilidade;
import com.sousa.sigpro.model.tipo.TipoProduto;
import com.sousa.sigpro.model.tipo.TipoRegimeEspecial;
import com.sousa.sigpro.model.tipo.TipoRepeticao;
import com.sousa.sigpro.model.tipo.TipoReposicao;
import com.sousa.sigpro.model.tipo.TipoRequisito;
import com.sousa.sigpro.model.tipo.TipoSexo;
import com.sousa.sigpro.model.tipo.TipoSimNao;
import com.sousa.sigpro.model.tipo.TipoSituacaoCredito;
import com.sousa.sigpro.model.tipo.TipoSituacaoFinanceira;
import com.sousa.sigpro.model.tipo.TipoStakeHolder;
import com.sousa.sigpro.model.tipo.TipoStatusProducao;
import com.sousa.sigpro.model.tipo.TipoTheme;
import com.sousa.sigpro.model.tipo.TipoTituloCaracteristica;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.model.tipo.TipoTratamento;
import com.sousa.sigpro.model.tipo.TipoTributacaoRps;
import com.sousa.sigpro.model.tipo.TipoUnidade;
import com.sousa.sigpro.model.tipo.TipoVeiculo;

@Named
public class ListaTipoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public String getEditorControl() {
		return "bold italic underline | font size style color highlight | "
				+ "bullets numbering | alignleft center alignright justify | "
				+ "undo redo | copy cut paste pastetext | outdent indent | removeformat";
	}

	public NFNotaInfoCombustivelTipo[] getNFNotaInfoCombustivelTipo() {
		NFNotaInfoCombustivelTipo[] lst = new NFNotaInfoCombustivelTipo[7];
		lst[0] = NFNotaInfoCombustivelTipo.ALCOOL;
		lst[1] = NFNotaInfoCombustivelTipo.GASOLINA;
		lst[2] = NFNotaInfoCombustivelTipo.DIESEL;
		lst[3] = NFNotaInfoCombustivelTipo.ALCOOL_GAS_NATURAL_VEICULAR;
		lst[4] = NFNotaInfoCombustivelTipo.GASOLINA_GAS_NATURAL_VEICULAR;
		lst[5] = NFNotaInfoCombustivelTipo.GASOLINA_ALCOOL_GAS_NATURAL;
		lst[6] = NFNotaInfoCombustivelTipo.DIESEL_GAS_NATURAL_VEICULAR;
		return lst;
	}

	public Object[] getTiposDebitoCredito() {
		Object[] tipos = new Object[2];
		tipos[0] = TipoTituloOrigem.RECEBER;
		tipos[1] = TipoTituloOrigem.PAGAR;
		return tipos;
	}

	public TipoApiCobranca[] getTipoApiCobranca() {
		return TipoApiCobranca.values();
	}

	public TipoEndereco[] getTipoEndereco() {
		return TipoEndereco.values();
	}

	public TipoStatusProducao[] getTipoStatusProducao() {
		return TipoStatusProducao.values();
	}

	public TipoImpacto[] getTipoImpacto() {
		return TipoImpacto.values();
	}

	public TipoProbabilidade[] getTipoProbabilidade() {
		return TipoProbabilidade.values();
	}

	public TipoEstrategia[] getTipoEstrategia() {
		return TipoEstrategia.values();
	}

	public TipoOcorrencia[] getTipoOcorrencia() {
		return TipoOcorrencia.values();
	}

	public TipoTituloCaracteristica[] getTipoTituloCaracteristica() {
		return TipoTituloCaracteristica.values();
	}

	public TipoBoleto[] getTipoBoleto() {
		return TipoBoleto.values();
	}

	public TipoCobranca[] getTipoCobranca() {
		return TipoCobranca.values();
	}

	public TipoAtendimentoPadrao[] getTipoAtendimentoPadrao() {
		return TipoAtendimentoPadrao.values();
	}

	public TipoDiaSemana[] getTipoDiaSemana() {
		return TipoDiaSemana.values();
	}

	public NFNotaInfoVeiculoCor[] getNFNotaInfoVeiculoCor() {
		return NFNotaInfoVeiculoCor.values();
	}

	public NFOperacaoConsumidorFinal[] getNFOperacaoConsumidorFinal() {
		return NFOperacaoConsumidorFinal.values();
	}

	public TipoVeiculo[] getTipoVeiculo() {
		return TipoVeiculo.values();
	}

	public MDFTipoProprietario[] getTipoProprietario() {
		return MDFTipoProprietario.values();
	}

	public MDFTipoCarroceria[] getTipoCarroceria() {
		return MDFTipoCarroceria.values();
	}

	public TipoProduto[] getTipoProduto() {
		return TipoProduto.values();
	}

	public TipoCentroCusto[] getTipoCentroCusto() {
		return TipoCentroCusto.values();
	}

	public TipoAgendaStatus[] getTipoAgendaStatus() {
		return TipoAgendaStatus.values();
	}

	public TipoCategoriaHabilitacao[] getTipoCategoriaHabilitacao() {
		return TipoCategoriaHabilitacao.values();
	}

	public TipoCheckListLocVeiculo[] getTipoCheckListLocVeiculo() {
		return TipoCheckListLocVeiculo.values();
	}

	public TipoEspecialidade[] getTipoEspecialidade() {
		return TipoEspecialidade.values();
	}

	public TipoRequisito[] getTipoRequisito() {
		return TipoRequisito.values();
	}

	public TipoGrupo[] getTipoGrupo() {
		return TipoGrupo.values();
	}

	public TipoSimNao[] getTipoSimNao() {
		return TipoSimNao.values();
	}

	public TipoTributacaoRps[] getTipoTributacaoRps() {
		return TipoTributacaoRps.values();
	}

	public TipoNaturezaOperacaoNFSe[] getTipoNaturezaOperacaoNFSe() {
		return TipoNaturezaOperacaoNFSe.values();
	}

	public TipoRegimeEspecial[] getTipoRegimeEspecial() {
		return TipoRegimeEspecial.values();
	}

	public TipoRepeticao[] getTipoRepeticao() {
		return TipoRepeticao.values();
	}

	public NFOperadoraCartao[] getNFOperadoraCartao() {
		return NFOperadoraCartao.values();
	}

	public NFMeioPagamento[] getNFFormaPagamentoMoeda() {
		return NFMeioPagamento.values();
	}

	public NFTipoEmissao[] getNFTipoEmissao() {
		return NFTipoEmissao.values();
	}

	public NFTipo[] getNFTipo() {
		return NFTipo.values();
	}

	public NFProcessoEmissor[] getNFProcessoEmissor() {
		return NFProcessoEmissor.values();
	}

	public NFIdentificadorLocalDestinoOperacao[] getNFIdentificadorLocalDestinoOperacao() {
		return NFIdentificadorLocalDestinoOperacao.values();
	}

	public NFIndicadorPresencaComprador[] getNFIndicadorPresencaComprador() {
		return NFIndicadorPresencaComprador.values();
	}

	public NFIndicadorFormaPagamento[] getNFFormaPagamentoPrazo() {
		return NFIndicadorFormaPagamento.values();
	}

	public NFFinalidade[] getNFFinalidade() {
		return NFFinalidade.values();
	}

	public DFAmbiente[] getNFAmbiente() {
		return DFAmbiente.values();
	}

	public NFNotaInfoImpostoTributacaoICMS[] getCstIcms() {
		return NFNotaInfoImpostoTributacaoICMS.values();
	}

	public NFNotaInfoSituacaoTributariaIPI[] getCstIPI() {
		return NFNotaInfoSituacaoTributariaIPI.values();
	}

	public NFNotaSituacaoOperacionalSimplesNacional[] getCstIcmsSN() {
		return NFNotaSituacaoOperacionalSimplesNacional.values();
	}

	public TipoStakeHolder[] getTipoStakeHolder() {
		return TipoStakeHolder.values();
	}

	public TipoImportancia[] getTipoImportancia() {
		return TipoImportancia.values();
	}

	public TipoEngajamento[] getTipoEngajamento() {
		return TipoEngajamento.values();
	}

	public TipoPergunta[] getTipoPergunta() {
		return TipoPergunta.values();
	}

	public TipoAcesso[] getTipoAcesso() {
		return TipoAcesso.values();
	}

	public TipoModulo[] getTipoModulo() {
		return TipoModulo.values();
	}

	public TipoAreaQuestionario[] getTipoAreaQuestionario() {
		return TipoAreaQuestionario.values();
	}

	public TipoTheme[] getTipoTheme() {
		return TipoTheme.values();
		// List<TipoTheme> list = Arrays.asList(TipoTheme.values());
		// list.sort((s1, s2) -> s1.compareTo(s2));
		// TipoTheme[] myArray = new TipoTheme[list.size()];
		// list.toArray(myArray);
		// return myArray;
	}

	public NFModalidadeFrete[] getTipoModalidadeFrete() {
		return NFModalidadeFrete.values();
	}

	public TipoGraficoModelo[] getTipoGraficoModelo() {
		return TipoGraficoModelo.values();
	}

	public TipoMes[] getTipoMes() {
		return TipoMes.values();
	}

	public TipoFluxoFinanceiro[] getTipoFluxoFinanceiro() {
		return TipoFluxoFinanceiro.values();
	}

	public TipoMeta[] getTipoMeta() {
		return TipoMeta.values();
	}

	public TipoMovimentacaoFiscal[] getMovimentacaoFiscal() {
		return TipoMovimentacaoFiscal.values();
	}

	public TipoSimNao[] getSimNao() {
		return TipoSimNao.values();
	}

	public TipoFuncao[] getFuncao() {
		return TipoFuncao.values();
	}

	public TipoReposicao[] getReposicao() {
		return TipoReposicao.values();
	}

	public TipoUnidade[] getUnidade() {
		return TipoUnidade.values();
	}

	public TipoMetodoCalculo[] getMetodoCalculo() {
		return TipoMetodoCalculo.values();
	}

	public NFOrigem[] getOrigem() {
		return NFOrigem.values();
	}

	public NFNotaInfoItemModalidadeBCICMS[] getModalidadeCalculoIcms() {
		return NFNotaInfoItemModalidadeBCICMS.values();
	}

	public NFNotaInfoSituacaoTributariaPIS[] getCstPIS() {
		return NFNotaInfoSituacaoTributariaPIS.values();
	}

	public NFNotaInfoSituacaoTributariaCOFINS[] getCstCofins() {
		return NFNotaInfoSituacaoTributariaCOFINS.values();
	}

	public MDFTipoRodado[] getTipoRodado() {
		return MDFTipoRodado.values();
	}

	public TipoComando[] getComando() {
		return TipoComando.values();
	}

	public TipoPagamento[] getPagamento() {
		return TipoPagamento.values();
	}

	public TipoCaixaStatus[] getCaixaStatus() {
		return TipoCaixaStatus.values();
	}

	public TipoSituacaoFinanceira[] getTipoSituacaoFinanceira() {
		return TipoSituacaoFinanceira.values();
	}

	public TipoDeTitulo[] getTipoTituloDoc() {
		return TipoDeTitulo.values();
	}

	public TipoPagamento[] getTipoPagamento() {
		return TipoPagamento.values();
	}

	public TipoSexo[] getTipoSexo() {
		return TipoSexo.values();
	}

	public NFIndicadorIEDestinatario[] getIndicadorIEDestinatario() {
		return NFIndicadorIEDestinatario.values();
	}

	public TipoPessoa[] getTipoPessoa() {
		return TipoPessoa.values();
	}

	public TipoPorteEmpresa[] getPortesEmpresa() {
		return TipoPorteEmpresa.values();
	}

	public TipoSituacaoCredito[] getTipoSituacaoCredito() {
		return TipoSituacaoCredito.values();
	}

	public NFRegimeTributario[] getRegimeTributario() {
		return NFRegimeTributario.values();
	}

	public TipoMetodoCalculo[] getTipoMetodoCalculo() {
		return TipoMetodoCalculo.values();
	}

	public TipoAtividadePrincipal[] getTipoAtividadePrincipal() {
		return TipoAtividadePrincipal.values();
	}

	public TipoTratamento[] getTipoTratamento() {
		return TipoTratamento.values();
	}

	public TipoComoConheceu[] getTipoComoConheceu() {
		return TipoComoConheceu.values();
	}

	public DFUnidadeFederativa[] getUnidadeFederativa() {
		return DFUnidadeFederativa.values();
	}

}