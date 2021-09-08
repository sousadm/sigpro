package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import com.sousa.sigpro.controller.parametro.ParametroMail;
import com.sousa.sigpro.model.AnaliseFinanceira;
import com.sousa.sigpro.model.Meta;
import com.sousa.sigpro.model.tipo.TipoGraficoModelo;
import com.sousa.sigpro.model.tipo.TipoMes;
import com.sousa.sigpro.model.tipo.TipoMeta;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.CaixaDiarios;
import com.sousa.sigpro.repository.Caixas;
import com.sousa.sigpro.repository.Expedicoes;
import com.sousa.sigpro.repository.Metas;
import com.sousa.sigpro.repository.Produtos;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.SuporteData;

@Named
@ViewScoped
public class GraficoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Caixas caixas;
	@Inject
	private CaixaDiarios diarios;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Metas metas;
	@Inject
	private Expedicoes expedicoes;
	@Inject
	private Produtos produtos;
	@Inject
	private ParametroMail mail;

	private LineChartModel model;
	private TipoGraficoModelo modelo;
	private int ano;
	private TipoMes mes;
	private AnaliseFinanceira analise;

	Calendar atual;

	@PostConstruct
	public void apos() {
		mail.ler();
		analise = new AnaliseFinanceira();
		atual = Calendar.getInstance();
		modelo = mail.getModelo() == null ? TipoGraficoModelo.FLUXO_PREVISTO : mail.getModelo();
		ano = Calendar.getInstance().get(Calendar.YEAR);
		mes = TipoMes.values()[Calendar.getInstance().get(Calendar.MONTH)];
		montar();
		analise.setAtivoPatrimonial(produtos.valorAtivoPatrimonial());
	}

	public boolean isModeloFluxoCaixa() {
		return modelo == TipoGraficoModelo.FLUXO_CAIXA;
	}

	public void montar() {

		if (modelo == TipoGraficoModelo.PAGAMENTOS)
			pagamentos();
		else if (modelo == TipoGraficoModelo.FATURAMENTO)
			faturamento();
		else if (modelo == TipoGraficoModelo.FLUXO_CAIXA)
			fluxoCaixa();
		else if (modelo == TipoGraficoModelo.FLUXO_PREVISTO)
			fluxoCaixaPrevisto();

		analise.calcular();

	}

	public void pagamentos() {
		double soma = 0;
		model = new LineChartModel();
		model.setLegendPosition("e");
		model.setZoom(true);
		model.setTitle("Pagamentos: " + mes + "/" + String.valueOf(ano));
		model.setLegendPosition("w");
		Axis yAxis = model.getAxis(AxisType.Y);
		yAxis.setMin(0);

		atual.set(ano, mes.ordinal(), 1);

		double ttdias = SuporteData.ultimoDiaDoMes(atual.getTime());
		Axis xAxis = model.getAxis(AxisType.X);
		xAxis.setMin(1);
		xAxis.setMax(ttdias);
		xAxis.setTickFormat("%d");

		LineChartSeries seriesRecebido = new LineChartSeries();
		seriesRecebido.setLabel("Previsto");
		model.addSeries(seriesRecebido);
		soma = 0;
		Map<Integer, Double> mapa = caixas.graficoFluxoPrevisto(ano, mes, TipoTituloOrigem.PAGAR);
		for (Integer numero : mapa.keySet()) {
			soma += mapa.get(numero);
			seriesRecebido.set(numero, soma);
		}

		LineChartSeries seriesPago = new LineChartSeries();
		seriesPago.setLabel("Realizado");
		model.addSeries(seriesPago);
		soma = 0;
		Map<Integer, Double> mapaPgto = diarios.graficoFluxoCaixa(ano, mes, TipoTituloOrigem.PAGAR);
		for (Integer numero : mapaPgto.keySet()) {
			soma += mapaPgto.get(numero);
			seriesPago.set(numero, soma);
		}

	}

	public void fluxoCaixaPrevisto() {

		double soma = 0;
		model = new LineChartModel();
		model.setLegendPosition("e");
		model.setZoom(true);
		model.setTitle("Fluxo Previsto: " + mes + "/" + String.valueOf(ano));
		model.setLegendPosition("w");
		Axis yAxis = model.getAxis(AxisType.Y);
		yAxis.setMin(0);

		atual.set(ano, mes.ordinal(), 1);

		double ttdias = SuporteData.ultimoDiaDoMes(atual.getTime());
		Axis xAxis = model.getAxis(AxisType.X);
		xAxis.setMin(1);
		xAxis.setMax(ttdias);
		xAxis.setTickFormat("%d");

		LineChartSeries seriesRecebido = new LineChartSeries();
		seriesRecebido.setLabel("Recebimento");
		model.addSeries(seriesRecebido);
		soma = 0;
		Map<Integer, Double> mapa = caixas.graficoFluxoPrevisto(ano, mes, TipoTituloOrigem.RECEBER);
		for (Integer numero : mapa.keySet()) {
			soma += mapa.get(numero);
			seriesRecebido.set(numero, soma);
		}

		LineChartSeries seriesPago = new LineChartSeries();
		seriesPago.setLabel("Pagamento");
		model.addSeries(seriesPago);
		soma = 0;
		Map<Integer, Double> mapaPgto = caixas.graficoFluxoPrevisto(ano, mes, TipoTituloOrigem.PAGAR);
		for (Integer numero : mapaPgto.keySet()) {
			soma += mapaPgto.get(numero);
			seriesPago.set(numero, soma);
		}

	}

	public LineChartModel getModel() {
		return model;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public TipoMes getMes() {
		return mes;
	}

	public void setMes(TipoMes mes) {
		this.mes = mes;
	}

	public TipoGraficoModelo getModelo() {
		return modelo;
	}

	public void setModelo(TipoGraficoModelo modelo) {
		this.modelo = modelo;
	}

	public AnaliseFinanceira getAnalise() {
		return analise;
	}

	public void setAnalise(AnaliseFinanceira analise) {
		this.analise = analise;
	}

	public void faturamento() {

		double tendencia = 0;
		int ultimoDia = SuporteData.ultimoDiaDoMes(atual.getTime());
		int diaAtual = SuporteData.diaDoMes(new Date());

		model = new LineChartModel();
		model.setLegendPosition("e");
		model.setZoom(true);
		model.setTitle("Faturamento: " + mes + "/" + String.valueOf(ano));
		model.setLegendPosition("w");
		Axis yAxis = model.getAxis(AxisType.Y);
		yAxis.setMin(0);

		atual.set(ano, mes.ordinal(), 1);

		LineChartSeries series1 = new LineChartSeries();
		series1.setLabel("Faturamento");
		model.addSeries(series1);

		LineChartSeries series2 = new LineChartSeries();
		series2.setLabel("Tendência");
		model.addSeries(series2);

		Axis xAxis = model.getAxis(AxisType.X);
		xAxis.setMin(1);
		xAxis.setMax(ultimoDia);
		xAxis.setTickFormat("%d");

		double soma = 0;
		Map<Integer, Double> mapa = expedicoes.graficoFaturamentoNoMes(ano, mes);
		for (Integer numero : mapa.keySet()) {
			soma += mapa.get(numero);
			series1.set(numero, soma);
		}

		Meta meta = metas.pesquisa(seguranca.getPessoaLogadoOrigem(), TipoMeta.FATURAMENTO, ano, mes.ordinal());
		if (meta != null && meta.getValor() > 0) {
			LineChartSeries series3 = new LineChartSeries();
			series3.setLabel("Meta");
			model.addSeries(series3);
			for (Integer numero : mapa.keySet()) {
				series3.set(numero, meta.getValor());
			}
		}

		Date ultimaDataDoMes = SuporteData.ultimaDataDoMes(atual.getTime());

		if (!ultimaDataDoMes.after(new Date())) {
			tendencia = soma / ultimoDia;
		} else {
			tendencia = ((soma / diaAtual) * (ultimoDia - SuporteData.quantDomingos(mes.ordinal(), ano))) / ultimoDia;
		}

		for (Integer numero : mapa.keySet()) {
			series2.set(numero, tendencia * numero);
		}

	}

	public void fluxoCaixa() {

		double soma = 0;
		model = new LineChartModel();
		model.setLegendPosition("e");
		model.setZoom(true);
		model.setTitle("Fluxo de Caixa: " + mes + "/" + String.valueOf(ano));
		model.setLegendPosition("w");
		Axis yAxis = model.getAxis(AxisType.Y);
		yAxis.setMin(0);

		atual.set(ano, mes.ordinal(), 1);

		double ttdias = SuporteData.ultimoDiaDoMes(atual.getTime());
		Axis xAxis = model.getAxis(AxisType.X);
		xAxis.setMin(1);
		xAxis.setMax(ttdias);
		xAxis.setTickFormat("%d");

		LineChartSeries seriesRecebido = new LineChartSeries();
		seriesRecebido.setLabel("Recebido");
		model.addSeries(seriesRecebido);
		soma = 0;
		Map<Integer, Double> mapa = diarios.graficoFluxoCaixa(ano, mes, TipoTituloOrigem.RECEBER);
		for (Integer numero : mapa.keySet()) {
			soma += mapa.get(numero);
			seriesRecebido.set(numero, soma);
		}
		analise.setReceita(soma);

		LineChartSeries seriesPago = new LineChartSeries();
		seriesPago.setLabel("Pago");
		model.addSeries(seriesPago);
		soma = 0;
		Map<Integer, Double> mapaPgto = diarios.graficoFluxoCaixa(ano, mes, TipoTituloOrigem.PAGAR);
		for (Integer numero : mapaPgto.keySet()) {
			soma += mapaPgto.get(numero);
			seriesPago.set(numero, soma);
		}
		analise.setDespesa(soma);

	}

}