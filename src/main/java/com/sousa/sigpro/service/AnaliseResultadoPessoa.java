package com.sousa.sigpro.service;

import java.util.Date;

import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Cliente;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;

public class AnaliseResultadoPessoa {

	Date dataPrimeiraCompra;
	Date dataUltimaCompra;
	double valorTicketMedio;
	Long volumeCompras;
	double prazoRecebimento;
	double prazoNegociacao;

	public AnaliseResultadoPessoa(Pessoa pessoa, EntityManager manager) {
		Date inicio = SuporteData.primeiraDataDoMes(new Date());
		inicio = SuporteData.incrementaMesNaData(inicio, -3);

		Date termino = SuporteData.incrementaMesNaData(inicio, 3);
		termino = SuporteData.incrementaDiaNaData(termino, -1);

		GerarResultado(pessoa, manager, inicio, termino);
	}

	public AnaliseResultadoPessoa(Pessoa pessoa, EntityManager manager, Date inicio, Date termino) {
		GerarResultado(pessoa, manager, inicio, termino);
	}

	private void GerarResultado(Pessoa pessoa, EntityManager manager, Date inicio, Date termino) {
		StringBuilder condicao;

		try {
			
			Pessoa p = manager.find(Pessoa.class, pessoa.getId());
			if (p.getCliente() != null) {
				condicao = new StringBuilder();
				condicao.append(
						"select MIN(e.dataEmissao), MAX(e.dataEmissao), AVG(valorTotal), COUNT(*) from expedicao e ");
				condicao.append("where e.cliente = " + p.getCliente().getId());
				condicao.append(" and dataCancelamento is null ");
				condicao.append(" and dataEmissao is not null ");
				Object[] obj = (Object[]) manager.createQuery(condicao.toString()).getSingleResult();

				dataPrimeiraCompra = (Date) obj[0];
				dataUltimaCompra = (Date) obj[1];
				valorTicketMedio = (double) obj[2];
				volumeCompras = (Long) obj[3];
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		String empresa = "sanpil";
		condicao = new StringBuilder();
		condicao.append("select sum(valor * case when (vencimento - cast(emissao as date)) < 0 then 0 ");
		condicao.append(" else vencimento - cast(emissao as date) end) / sum(valor) ");
		condicao.append("from " + empresa + ".Titulo where dataBaixa is null ");
		condicao.append(" and emissao is not null ");
		condicao.append(" and responsavel_id = " + pessoa.getId());
		condicao.append(" and tipoDC = 'RECEBER' ");
		condicao.append(" and tipoDocto not in ('CREDITO', 'DEBITO', 'DINHEIRO') ");
		condicao.append(" and cast(emissao as date) < " + Suporte.formataDataSQL_Quoted(termino));
		if (inicio != null)
			condicao.append(" and cast(emissao as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		try {
			prazoNegociacao = (Double) manager.createNativeQuery(condicao.toString()).getSingleResult();
		} catch (Exception e) {
			prazoNegociacao = 0;
		}

		condicao = new StringBuilder();
		condicao.append("select sum(valor * case when (dataBaixa - cast(emissao as date)) < 0 then 0 ");
		condicao.append(" else dataBaixa - cast(emissao as date) end) / sum(valor) ");
		condicao.append("from " + empresa + ".Titulo ");
		condicao.append("where dataBaixa is not null ");
		condicao.append(" and emissao is not null ");
		condicao.append(" and responsavel_id = " + pessoa.getId());
		condicao.append(" and tipoDC = 'RECEBER' ");
		condicao.append(" and tipoDocto not in ('CREDITO') ");
		condicao.append(" and cast(emissao as date) < " + Suporte.formataDataSQL_Quoted(termino));
		if (inicio != null)
			condicao.append(" and cast(emissao as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		try {
			prazoRecebimento = (Double) manager.createNativeQuery(condicao.toString()).getSingleResult();
		} catch (Exception e) {
			prazoRecebimento = 0.0;
		}
	}

	public Date getDataPrimeiraCompra() {
		return dataPrimeiraCompra;
	}

	public Date getDataUltimaCompra() {
		return dataUltimaCompra;
	}

	public double getPrazoRecebimento() {
		return prazoRecebimento;
	}

	public double getValorTicketMedio() {
		return valorTicketMedio;
	}

	public Long getVolumeCompras() {
		return volumeCompras;
	}

	public double getPrazoNegociacao() {
		return prazoNegociacao;
	}

}