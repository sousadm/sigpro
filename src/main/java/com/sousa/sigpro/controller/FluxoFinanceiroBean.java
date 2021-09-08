package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.FluxoFinanceiro;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.tipo.TipoFluxoFinanceiro;
import com.sousa.sigpro.model.tipo.TipoMes;
import com.sousa.sigpro.model.tipo.TipoMeta;
import com.sousa.sigpro.repository.Metas;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class FluxoFinanceiroBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private Metas metas;

	private FluxoFinanceiro fluxo;
	private Pessoa pessoa;
	private TipoMes mesInicial;
	private int ano;
	private int ciclo;
	private TipoFluxoFinanceiro tipo;
	private List<FluxoFinanceiro> lista;
	private List<ColumnModel> colunas;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			pessoa = seguranca.getPessoaLogadoOrigem();
			mesInicial = TipoMes.values()[GregorianCalendar.getInstance().get(Calendar.MONTH)];
			ano = GregorianCalendar.getInstance().get(Calendar.YEAR);
			ciclo = 3;
			limpar();
		}
	}

	public void limpar() {
		lista = new ArrayList<FluxoFinanceiro>();
		processarCabecalho();
	}

	public void analisar() {
		lista = new ArrayList<FluxoFinanceiro>();
		processarCabecalho();
		processarMetasVenda();
		processarMetasCompra();
		FacesUtil.addInfoMessage("Concluído");
	}

	private void processarMetasCompra() {
		FluxoFinanceiro fluxo = new FluxoFinanceiro("Meta de Compra", "", ciclo);
		for (int i = 0; i < ciclo; i++)
			fluxo.getValores()[i] = metas.pesquisa(pessoa, TipoMeta.COMPRA, colunas.get(i).ano, colunas.get(i).mes)
					.getValor();
		lista.add(fluxo);
	}

	private void processarMetasVenda() {
		FluxoFinanceiro fluxo = new FluxoFinanceiro("Meta de Faturamento", "", ciclo);
		for (int i = 0; i < ciclo; i++)
			fluxo.getValores()[i] = metas.pesquisa(pessoa, TipoMeta.FATURAMENTO, colunas.get(i).ano, colunas.get(i).mes)
					.getValor();
		lista.add(fluxo);
	}

	private void processarCabecalho() {

		ColumnModel coluna;
		colunas = new ArrayList<ColumnModel>();
		FluxoFinanceiro fluxo = new FluxoFinanceiro("Meses de análise", "", ciclo);

		Calendar cal = Calendar.getInstance();
		cal.set(GregorianCalendar.YEAR, ano);
		cal.set(GregorianCalendar.MONTH, mesInicial.ordinal());

		for (int i = 0; i < ciclo; i++) {
			coluna = new ColumnModel(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
			colunas.add(coluna);
			cal.add(Calendar.MONTH, 1);
			fluxo.getValores()[i] = 0;
		}

	}

	public void imprimir() {
		FacesUtil.addInfoMessage("Rotina de impressão");

	}

	public List<FluxoFinanceiro> getLista() {
		return lista;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getCiclo() {
		return ciclo;
	}

	public void setCiclo(int ciclo) {
		this.ciclo = ciclo;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public FluxoFinanceiro getFluxo() {
		return fluxo;
	}

	public void setFluxo(FluxoFinanceiro fluxo) {
		this.fluxo = fluxo;
	}

	public TipoMes getMesInicial() {
		return mesInicial;
	}

	public void setMesInicial(TipoMes mesInicial) {
		this.mesInicial = mesInicial;
	}

	public TipoFluxoFinanceiro getTipo() {
		return tipo;
	}

	public void setTipo(TipoFluxoFinanceiro tipo) {
		this.tipo = tipo;
	}

	public List<ColumnModel> getColunas() {
		return colunas;
	}

	public int getCicloMaximo() {
		return 12;
	}

	public int getAnoMinimo() {
		return 2017;
	}

	public int getAnoMaximo() {
		Calendar hoje = Calendar.getInstance();
		return hoje.get(Calendar.YEAR);
	}

	static public class ColumnModel implements Serializable {

		private static final long serialVersionUID = 1L;
		private String descricao;
		private int mes;
		private int ano;

		public ColumnModel(String descricao) {
			this.descricao = descricao;
			this.ano = -1;
		}

		public ColumnModel(int mes, int ano) {
			this.mes = mes;
			this.ano = ano;
			descricao = TipoMes.values()[mes].getReduzido();
		}

		public boolean isTemComplemento() {
			return ano >= 0;
		}

		public int getMes() {
			return mes;
		}

		public int getAno() {
			return ano;
		}

		public String getDescricao() {
			return descricao;
		}
	}
}