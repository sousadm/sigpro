package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.sousa.sigpro.model.Balanco;
import com.sousa.sigpro.model.BalancoProduto;
import com.sousa.sigpro.model.BalancoTitulo;
import com.sousa.sigpro.model.CaixaValor;
import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.FluxoDeCaixa;
import com.sousa.sigpro.model.PosicaoFinanceiraDetalhe;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoCentroCusto;
import com.sousa.sigpro.model.tipo.TipoFluxoCaixa;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jpa.Transactional;

public class Balancos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Titulos titulos;
	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;

	public Balanco porId(Long id) {
		String condicao = "select p from Balanco p where p.id = " + id;
		return manager.createQuery(condicao, Balanco.class).getSingleResult();
	}

	public double balancoValor(Long id) {
		StringBuilder condicao = new StringBuilder();
		condicao.append(
				"select ((ativoDisponivel + ativoInvestimento + ativoContaReceber + ativoEstoque + ativoImobilizado) ");
		condicao.append(
				"		 -(passivoTrabalhista + passivoProvisionado + passivoContaPagar + passivoOutros)) as valor ");
		condicao.append("from " + seguranca.getEmpresa() + ".balanco ");
		condicao.append("where id = " + id);
		double valor;
		try {
			valor = (Double) manager.createNativeQuery(condicao.toString()).getSingleResult();
		} catch (Exception e) {
			valor = 0.0;
		}
		return valor;
	}

	@Transactional
	public Balanco guardar(Balanco balanco) {
		return manager.merge(balanco);
	}

	public BalancoProduto porProdutoId(Long id) {
		return manager.find(BalancoProduto.class, id);
	}

	public BalancoTitulo porTituloId(Long id) {
		return manager.find(BalancoTitulo.class, id);
	}

	public List<Balanco> lista() {
		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select b from Balanco b WHERE 1 = 1 ");
			condicao.append(" AND b.empresa = " + seguranca.getPessoaLogadoOrigem().getId());
			return manager.createQuery(condicao.toString(), Balanco.class).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Balanco> lista(Date data) {
		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select b from Balanco b WHERE 1 = 1 ");
			condicao.append(" AND cast(b.dataApuracao as date) = " + Suporte.formataDataSQL_Quoted(data));
			condicao.append(" AND b.empresa = " + seguranca.getPessoaLogadoOrigem().getId());
			return manager.createQuery(condicao.toString(), Balanco.class).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public Balanco proximo(Date data) {
		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select b from Balanco b WHERE 1 = 1 ");
			condicao.append(" AND cast(b.dataApuracao as date) > " + Suporte.formataDataSQL_Quoted(data));
			condicao.append(" AND b.empresa = " + seguranca.getPessoaLogadoOrigem().getId());
			return manager.createQuery(condicao.toString(), Balanco.class).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public Balanco anterior(Date data) {
		try {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select b from Balanco b WHERE 1 = 1 ");
			condicao.append(" AND cast(b.dataApuracao as date) < " + Suporte.formataDataSQL_Quoted(data));
			condicao.append(" AND b.empresa = " + seguranca.getPessoaLogadoOrigem().getId());
			return manager.createQuery(condicao.toString(), Balanco.class).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public void remover(Balanco balanco) {
		try {
			balanco = porId(balanco.getId());
			manager.remove(balanco);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("Balanço não pode ser excluído.");
		}
	}

	public Balanco ultimoBalanco(Date data) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select max(b) from Balanco b where 1 = 1 ");
		if (Suporte.dataIsValida(data))
			condicao.append(" and cast(b.dataApuracao as date) < " + Suporte.formataDataSQL_Quoted(data));
		return manager.createQuery(condicao.toString(), Balanco.class).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public void montarBalancoPatrimonialTitulo(Balanco balanco) throws Exception {

		int dia = 0;
		double valor = 0;
		BalancoTitulo balancoTitulo;

		StringBuilder condicao = new StringBuilder();
		condicao.append("select t.id, t.valor - coalesce(sum(c.pgto), 0) as saldo from " + seguranca.getEmpresa()
				+ ".titulo t ");
		condicao.append("left join ( ");
		condicao.append("	select ct.titulo_id as titulo, sum(ct.valorpago) as pgto ");
		condicao.append("	from " + seguranca.getEmpresa() + ".caixa c inner join " + seguranca.getEmpresa()
				+ ".caixatitulo ct on c.id = ct.caixa_id ");
		condicao.append("	where 1 = 1 ");
		if (Suporte.dataIsValida(balanco.getDataApuracao()))
			condicao.append(
					"		and cast(c.emissao as date) < " + Suporte.formataDataSQL_Quoted(balanco.getDataApuracao()));
		condicao.append("			group by 1   ) c on c.titulo = t.id ");
		// condicao.append("where t.id = 2752 ");
		condicao.append("where t.saldo > 0 ");
		condicao.append("	and t.dataCancelamento is null ");

		if (Suporte.dataIsValida(balanco.getDataApuracao())) {
			condicao.append(
					"	and cast(t.emissao as date) <= " + Suporte.formataDataSQL_Quoted(balanco.getDataApuracao()));
		}

		condicao.append("group by 1	");
		condicao.append("order by t.vencimento, t.id ");

		List<Object[]> lst = manager.createNativeQuery(condicao.toString()).getResultList();
		for (Object[] obj : lst) {
			if (obj != null) {
				valor = (Double) obj[1];
				if (valor > 0) {
					BigInteger id = (java.math.BigInteger) obj[0];
					Titulo titulo = titulos.porId(id.longValue());
					balancoTitulo = new BalancoTitulo();
					balancoTitulo.setBalanco(balanco);
					balancoTitulo.setTitulo(titulo);
					balancoTitulo.setSaldo(valor);
					dia = SuporteData.diasEntreDatas(titulo.getVencimento(), balanco.getDataApuracao());
					if (dia > 0)
						balancoTitulo.setAtraso(dia);
					balanco.getTitulos().add(balancoTitulo);
				}
			}
		}
	}

	public void definirPrazoMedio(Balanco balanco) {
		// inicio = Suporte.incrementaDiaNaData(balanco.getDataApuracao(), -180);

		Double prazoPagamento;
		Double prazoRecebimento;
		Double prazoCompra;
		Double ticketMedio;
		BigInteger clienteComCompra;
		BigInteger carteiraCliente;

		Date inicio = null;
		Balanco ultimo = this.ultimoBalanco(balanco.getDataApuracao());
		if (ultimo != null)
			inicio = ultimo.getDataApuracao();

		StringBuilder condicao = new StringBuilder();
		condicao.append("select sum(valor * case when (vencimento - cast(emissao as date)) < 0 then 0 ");
		condicao.append("				else vencimento - cast(emissao as date) end) / sum(valor) ");
		condicao.append("from " + seguranca.getEmpresa() + ".Titulo where dataBaixa is null ");
		condicao.append("	and tipoDC = 'RECEBER' ");
		condicao.append("	and tipoDocto not in ('CREDITO', 'DEBITO', 'DINHEIRO') ");
		condicao.append("	and cast(emissao as date) < " + Suporte.formataDataSQL_Quoted(balanco.getDataApuracao()));
		if (inicio != null)
			condicao.append("	and cast(emissao as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		Double prazoNegociacao = (Double) manager.createNativeQuery(condicao.toString()).getSingleResult();

		condicao = new StringBuilder();
		condicao.append("select sum(valor * case when (dataBaixa - cast(emissao as date)) < 0 then 0 ");
		condicao.append("				else dataBaixa - cast(emissao as date) end) / sum(valor) ");
		condicao.append("from " + seguranca.getEmpresa() + ".Titulo ");
		condicao.append("where dataBaixa is not null ");
		condicao.append("	and tipoDC = 'RECEBER' ");
		condicao.append("	and tipoDocto not in ('CREDITO') ");
		condicao.append("	and cast(emissao as date) < " + Suporte.formataDataSQL_Quoted(balanco.getDataApuracao()));
		if (inicio != null)
			condicao.append("	and cast(emissao as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		try {
			prazoRecebimento = (Double) manager.createNativeQuery(condicao.toString()).getSingleResult();
		} catch (Exception e) {
			prazoRecebimento = 0.0;
		}

		condicao = new StringBuilder();
		condicao.append("select sum(valor * case when (dataBaixa - cast(emissao as date)) < 0 then 0 ");
		condicao.append("				else dataBaixa - cast(emissao as date) end) / sum(valor) ");
		condicao.append("from " + seguranca.getEmpresa() + ".Titulo ");
		condicao.append("where dataBaixa is not null ");
		condicao.append("	and tipoDC = 'PAGAR' ");
		condicao.append("	and cast(emissao as date) < " + Suporte.formataDataSQL_Quoted(balanco.getDataApuracao()));
		if (inicio != null)
			condicao.append("	and cast(emissao as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		try {
			prazoPagamento = (Double) manager.createNativeQuery(condicao.toString()).getSingleResult();
		} catch (Exception e) {
			prazoPagamento = 0.0;
		}

		condicao = new StringBuilder();
		condicao.append("select sum(valor * case when (vencimento - cast(emissao as date)) < 0 then 0 ");
		condicao.append("						else vencimento - cast(emissao as date) end) / sum(valor) ");
		condicao.append("from " + seguranca.getEmpresa() + ".Titulo ");
		condicao.append("where saldo > 0.0 ");
		condicao.append("	and dataBaixa is null ");
		condicao.append("	and tipoDC = 'PAGAR' ");
		condicao.append("	and cast(emissao as date) < " + Suporte.formataDataSQL_Quoted(balanco.getDataApuracao()));
		if (inicio != null)
			condicao.append("	and cast(emissao as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		try {
			prazoCompra = (Double) manager.createNativeQuery(condicao.toString()).getSingleResult();
		} catch (Exception e) {
			prazoCompra = 0.0;
		}

		condicao = new StringBuilder();
		condicao.append("select sum(valorsubtotal)/count(cliente_id) ");
		condicao.append("from " + seguranca.getEmpresa() + ".expedicao ");
		condicao.append("where datacancelamento is null ");
		condicao.append(
				"	and cast(data_pedido as date) < " + Suporte.formataDataSQL_Quoted(balanco.getDataApuracao()));
		if (inicio != null)
			condicao.append("	and cast(data_pedido as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		try {
			ticketMedio = (Double) manager.createNativeQuery(condicao.toString()).getSingleResult();
		} catch (Exception e) {
			ticketMedio = 0.0;
		}

		condicao = new StringBuilder();
		condicao.append("select count(distinct(cliente_id)) ");
		condicao.append("from " + seguranca.getEmpresa() + ".expedicao ");
		condicao.append("where datacancelamento is null ");
		condicao.append(
				"	and cast(data_pedido as date) < " + Suporte.formataDataSQL_Quoted(balanco.getDataApuracao()));
		if (inicio != null)
			condicao.append("	and cast(data_pedido as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		try {
			clienteComCompra = (BigInteger) manager.createNativeQuery(condicao.toString()).getSingleResult();
		} catch (Exception e) {
			clienteComCompra = BigInteger.ONE;
		}

		condicao = new StringBuilder();
		condicao.append("select count(*) from " + seguranca.getEmpresa() + ".pessoa ");
		condicao.append("where definecliente = true ");
		condicao.append(
				" and cast(datacadastro as date) < " + Suporte.formataDataSQL_Quoted(balanco.getDataApuracao()));
		try {
			carteiraCliente = (BigInteger) manager.createNativeQuery(condicao.toString()).getSingleResult();
		} catch (Exception e) {
			carteiraCliente = BigInteger.ONE;
		}

		balanco.setPrazoCompra(prazoCompra == null ? 0.0 : prazoCompra);
		balanco.setPrazoNegociacao(prazoNegociacao == null ? 0.0 : prazoNegociacao);
		balanco.setPrazoPagamento(prazoPagamento == null ? 0.0 : prazoPagamento);
		balanco.setPrazoRecebimento(prazoRecebimento == null ? 0.0 : prazoRecebimento);
		balanco.setTicketMedio(ticketMedio);
		balanco.setCarteiraCliente(carteiraCliente.intValue());
		balanco.setClienteComCompra(clienteComCompra.intValue());

	}

	public void definirGiroDeEstoque(Balanco balanco) throws Exception {
		Date inicio = SuporteData.incrementaDiaNaData(balanco.getDataApuracao(), -180);

		StringBuilder condicao = new StringBuilder();
		condicao.append("select 360/(sum(i.quantidade)/sum(coalesce(media, 1))) as giro ");
		condicao.append("from " + seguranca.getEmpresa() + ".expedicao e ");
		condicao.append("inner join " + seguranca.getEmpresa() + ".expedicao_item i on e.id = i.expedicao_id ");
		condicao.append("inner join " + seguranca.getEmpresa() + ".produto p on p.id = i.produto_id ");
		condicao.append("inner join ( ");
		condicao.append("	select c.id as categoria ");
		condicao.append("	from " + seguranca.getEmpresa() + ".categoria c ");
		condicao.append("	inner join " + seguranca.getEmpresa() + ".categoria c1 on c.categoria_pai_id = c1.id ");
		condicao.append("	where c1.tipoproduto <> 'SERVICO' and c1.tipoproduto <> 'IMOBILIZADO' ");
		condicao.append("		and c1.tipoproduto is not null) c on c.categoria = p.categoria_id ");
		condicao.append("left join ( ");
		condicao.append("	select produto_id as produto, sum(estoqueinicial + entrada - saida) as media ");
		condicao.append("	from " + seguranca.getEmpresa() + ".balancoproduto ");
		condicao.append("	where balanco_id = (select max(id) from " + seguranca.getEmpresa() + ".balanco) ");
		condicao.append("	group by 1) t on t.produto = i.produto_id ");
		condicao.append("where e.datacancelamento is null ");
		condicao.append("	and i.datacancelamento is null ");
		condicao.append("	and cast(data_pedido as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		Double giroDeEstoque = (Double) manager.createNativeQuery(condicao.toString()).getSingleResult();
		balanco.setGiroDeEstoque(giroDeEstoque);

	}

	@SuppressWarnings("unchecked")
	public void montarBalancoPatrimonialProduto(Balanco balanco, boolean gravar) {

		Date inicio = SuporteData.incrementaDiaNaData(balanco.getDataApuracao(), -180);
		Balanco ultimo = ultimoBalanco(balanco.getDataApuracao());

		StringBuilder condicao = new StringBuilder();
		condicao.append("select ");
		condicao.append("	 p.id "); // 0
		condicao.append("	,coalesce(estoqueinicial, 0) as estoqueinicial "); // 1
		condicao.append("	,coalesce(entrada, 0) as entrada "); // 2
		condicao.append("	,coalesce(saida, 0) as saida "); // 3
		condicao.append("	,coalesce(entradafiscal, 0) as entradafiscal "); // 4
		condicao.append("	,coalesce(saidafiscal, 0) as saidafiscal "); // 5
		condicao.append("	,coalesce(g.giro, 0) as giroDeEstoque "); // 6
		condicao.append("from " + seguranca.getEmpresa() + ".Produto p ");
		condicao.append("left join ( ");
		condicao.append("	select ");
		condicao.append("		produto_id, ");
		condicao.append("		sum(quantidade) as saida ");
		condicao.append("	from " + seguranca.getEmpresa() + ".expedicao t ");
		condicao.append("	inner join " + seguranca.getEmpresa() + ".expedicao_item on t.id = expedicao_id ");
		condicao.append("	where t.datacancelamento is null ");
		condicao.append("		and t.pessoa_id = " + seguranca.getPessoaLogadoOrigem().getId());
		if (ultimo != null) {
			condicao.append("		and cast(data_pedido as date) > "
					+ Suporte.formataDataSQL_Quoted(ultimo.getDataApuracao()));
		}
		condicao.append(
				"	and cast(data_pedido as date) <= " + Suporte.formataDataSQL_Quoted(balanco.getDataApuracao()));
		condicao.append("	group by 1) e on e.produto_id = p.id ");
		condicao.append("left join ( ");
		condicao.append("	select ");
		condicao.append("		produto_id, ");
		condicao.append("		sum(quantidade) as entrada, ");
		condicao.append("		sum(case when chave is null then 0 else 1 end * quantidade) as entradafiscal ");
		condicao.append("	from " + seguranca.getEmpresa() + ".aquisicao t ");
		condicao.append("	inner join " + seguranca.getEmpresa() + ".aquisicao_item on t.id = aquisicao_id ");
		condicao.append("	where t.datacancelamento is null ");
		condicao.append("		and t.empresa_id = " + seguranca.getPessoaLogadoOrigem().getId());
		if (ultimo != null) {
			condicao.append("	and cast(dataencerramento as date) > "
					+ Suporte.formataDataSQL_Quoted(ultimo.getDataApuracao()));
		}
		condicao.append(
				"	and cast(dataencerramento as date) <= " + Suporte.formataDataSQL_Quoted(balanco.getDataApuracao()));
		condicao.append("	group by 1) a on a.produto_id = p.id ");
		condicao.append("left join ( ");
		condicao.append("	select prod_id, sum(quantidade) as saidafiscal ");
		condicao.append("	from " + seguranca.getEmpresa() + ".notafiscal t ");
		condicao.append("	inner join " + seguranca.getEmpresa() + ".notafiscalitem on t.id = nota_id ");
		condicao.append("	where t.datacancelamento is null ");
		condicao.append("		and t.empresa_id = " + seguranca.getPessoaLogadoOrigem().getId());
		condicao.append("		and t.tipo = 1 and t.statussefaz = 100 ");
		if (ultimo != null)
			condicao.append(
					" and cast(dataemissao as date) > " + Suporte.formataDataSQL_Quoted(ultimo.getDataApuracao()));
		condicao.append(
				" and cast(dataemissao as date) <= " + Suporte.formataDataSQL_Quoted(balanco.getDataApuracao()));
		condicao.append("	group by 1) n on n.prod_id = p.id ");

		condicao.append("left join ( ");
		condicao.append(" select produto_id, sum(i.quantidade) as giro ");
		condicao.append(" from " + seguranca.getEmpresa() + ".expedicao e ");
		condicao.append(" inner join " + seguranca.getEmpresa() + ".expedicao_item i on e.id = i.expedicao_id ");
		condicao.append(" where e.datacancelamento is null ");
		condicao.append(" and i.datacancelamento is null ");
		condicao.append(" and cast(data_pedido as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		condicao.append(" group by 1) g on g.produto_id = p.id ");

		// condicao.append("where p.sku = '2707' ");
		condicao.append("order by categoria_id, nome ");

		double custo = 0;
		double medio = 0;
		balanco.setProdutos(new ArrayList<BalancoProduto>());
		List<Object[]> lstObj = manager.createNativeQuery(condicao.toString()).getResultList();
		for (Object[] obj : lstObj) {
			if (obj != null) {
				BigInteger id = (java.math.BigInteger) obj[0];
				Produto produto = manager.find(Produto.class, id.longValue());
				if (!produto.isTipoServico()) {
					BalancoProduto balancoProduto = new BalancoProduto();
					custo = produto.getPrecoCusto() > 0 ? produto.getPrecoCusto()
							: produto.getPrecoCompra() > 0 ? produto.getPrecoCompra() : produto.getValorUnitario();
					medio = produto.getPrecoMedio() > 0 ? produto.getPrecoMedio() : custo;
					balancoProduto.setEstoqueInicial((Double) obj[1]);
					balancoProduto.setEntrada((Double) obj[2]);
					balancoProduto.setSaida((Double) obj[3]);
					balancoProduto.setEntradaFiscal((Double) obj[4]);
					balancoProduto.setSaidaFiscal((Double) obj[5]);
					balancoProduto.setPrecoCusto(custo);
					balancoProduto.setPrecoMedio(medio);

					Double estoque = balancoProduto.getEstoqueGerencial();
					Double giroDeEstoque = (Double) obj[6];
					if (giroDeEstoque > 0) {
						giroDeEstoque = giroDeEstoque / (estoque > 0 ? estoque : 1);
						giroDeEstoque = 360 / (giroDeEstoque > 0 ? giroDeEstoque : 1);
						balancoProduto.setGiroDeEstoque(giroDeEstoque);
					} else {
						balancoProduto.setGiroDeEstoque(0);
					}

					if (balancoProduto.getEstoqueGerencial() > 0) {
						if (gravar) {
							produto.setQuantidadeEstoque(balancoProduto.getEstoqueGerencial());
							produto.setQuantidadeEstoqueContabil(balancoProduto.getEstoqueContabil());
						}
						balancoProduto.setProduto(produto);
						balancoProduto.setBalanco(balanco);
						balanco.getProdutos().add(balancoProduto);
					}
				}
			}
		}
	}

	public List<BalancoProduto> listaGiroProduto(Balanco balanco) {

		List<BalancoProduto> lst = null;

		if (balanco != null && balanco.isExiste()) {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select p from BalancoProduto p ");
			condicao.append("where p.balanco = " + balanco.getId());
			condicao.append(" order by p.produto.categoria.categoriaPai, p.produto.nome ");
			lst = manager.createQuery(condicao.toString(), BalancoProduto.class).getResultList();
		}

		return lst;
	}

	public List<BalancoProduto> listaInventario(Balanco balanco) {

		List<BalancoProduto> lst = null;

		if (balanco != null && balanco.isExiste()) {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select p from BalancoProduto p ");
			condicao.append("where (estoqueInicial + entradaFiscal - saidaFiscal) > 0 ");
			condicao.append("	and p.balanco = " + balanco.getId());
			condicao.append(" order by p.produto.categoria.categoriaPai, p.produto.nome ");
			lst = manager.createQuery(condicao.toString(), BalancoProduto.class).getResultList();
		}

		return lst;
	}

	public List<BalancoProduto> listaEstoque(Balanco balanco) {

		List<BalancoProduto> lst = null;

		if (balanco != null && balanco.isExiste()) {
			StringBuilder condicao = new StringBuilder();
			condicao.append("select p from BalancoProduto p ");
			condicao.append("where p.balanco = " + balanco.getId());
			condicao.append(" order by p.produto.categoria.categoriaPai, p.produto.nome ");
			lst = manager.createQuery(condicao.toString(), BalancoProduto.class).getResultList();
		}

		return lst;
	}

	public List<BalancoTitulo> listaTitulo(TipoTituloOrigem tipoDC) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select p from BalancoTitulo p join p.titulo t ");
		condicao.append("where tipoDC = " + Suporte.getQuotedStr(tipoDC.name()));
		condicao.append(" order by t.responsavel.nome, t.vencimento ");
		return manager.createQuery(condicao.toString(), BalancoTitulo.class).getResultList();
	}

	public List<FluxoDeCaixa> FluxoDeCaixaDetalhado(CondicaoFilter filtro) throws Exception {

		CentroDeCusto centro = new CentroDeCusto();
		String condicao;
		FluxoDeCaixa fluxoDeCaixa;
		FluxoDeCaixa grupo = new FluxoDeCaixa();
		FluxoDeCaixa receita = new FluxoDeCaixa("RECEITAS", "fluxo");
		FluxoDeCaixa custo = new FluxoDeCaixa("(-) CUSTOS VARIÁVEIS", "fluxo");
		FluxoDeCaixa margem = new FluxoDeCaixa("(=)MARGEM DE CONTRIBUIÇÃO", "resultado");
		FluxoDeCaixa fixa = new FluxoDeCaixa("(-) DESPESAS FIXAS", "fluxo");
		FluxoDeCaixa investimento = new FluxoDeCaixa("(-) INVESTIMENTO", "fluxo");
		FluxoDeCaixa operacional = new FluxoDeCaixa("(=) LUCRO OPERACIONAL", "resultado");
		FluxoDeCaixa liquido = new FluxoDeCaixa("(=) RESULTADO LÍQUIDO", "resultado");

		List<FluxoDeCaixa> lista = new ArrayList<>();
		for (TipoFluxoCaixa fluxo : TipoFluxoCaixa.values()) {

			condicao = "";
			for (TipoCentroCusto tipo : TipoCentroCusto.values()) {
				if (tipo.getFluxo().equals(fluxo)) {
					if (condicao.length() > 0)
						condicao = condicao + ", ";
					condicao = condicao + Suporte.getQuotedStr(tipo.name());
				}
			}

			if (fluxo == TipoFluxoCaixa.VARIAVEL) {
				lista.add(custo);
			} else if (fluxo == TipoFluxoCaixa.FIXO) {
				lista.add(fixa);
			} else if (fluxo == TipoFluxoCaixa.INVESTIMENTO) {
				lista.add(operacional);
				lista.add(investimento);
			}

			if (condicao.length() > 0) {
				StringBuilder sql = new StringBuilder();
				sql.append("select v from CaixaValor v join v.caixa c ");
				sql.append("where c.diario.origem = " + seguranca.getPessoaLogadoOrigem().getId());
				sql.append(
						" and cast(c.diario.dataMov as date) >= " + Suporte.formataDataSQL_Quoted(filtro.getInicio()));
				sql.append(
						" and cast(c.diario.dataMov as date) <= " + Suporte.formataDataSQL_Quoted(filtro.getTermino()));
				sql.append(" and c.centroDeCusto.tipo in (" + condicao + ") ");
				sql.append(" order by c.centroDeCusto ");

				List<CaixaValor> lst = manager.createQuery(sql.toString(), CaixaValor.class).getResultList();
				for (CaixaValor obj : lst) {

					if (!centro.equals(obj.getCaixa().getCentroDeCusto())) {
						centro = obj.getCaixa().getCentroDeCusto();
						grupo = new FluxoDeCaixa(obj.getCaixa().getCentroDeCusto().getDescricao(), "grupo");
						lista.add(grupo);
					}
					grupo.addValor(obj.getValor());

					fluxoDeCaixa = new FluxoDeCaixa();
					fluxoDeCaixa.setHistorico(obj.getCaixa().getObservacao());
					fluxoDeCaixa.setValor(obj.getValor());
					fluxoDeCaixa.setBanda("item");
					fluxoDeCaixa.calculaVertical(receita.getValor());
					lista.add(fluxoDeCaixa);

					switch (fluxo) {
					case RECEITA:
						receita.addValor(obj.getValor());
						break;
					case VARIAVEL:
						custo.addValor(obj.getValor());
						break;
					case FIXO:
						fixa.addValor(obj.getValor());
						break;
					case INVESTIMENTO:
						investimento.addValor(obj.getValor());
						break;
					}
				}
			}

			if (fluxo == TipoFluxoCaixa.VARIAVEL) {
				margem.addValor(receita.getValor() - custo.getValor());
				lista.add(margem);
			} else if (fluxo == TipoFluxoCaixa.FIXO) {
				operacional.addValor(margem.getValor() - fixa.getValor());
			}

		}

		liquido.addValor(operacional.getValor() - investimento.getValor());
		lista.add(0, receita);
		lista.add(liquido);

		custo.calculaVertical(receita.getValor());
		margem.calculaVertical(receita.getValor());
		fixa.calculaVertical(receita.getValor());
		investimento.calculaVertical(receita.getValor());
		operacional.calculaVertical(receita.getValor());
		liquido.calculaVertical(receita.getValor());

		return lista;
	}

	public String consultaDeProduto() {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select 						");
		condicao.append("	t.id		 				");
		condicao.append("	,produto_id as produto 		");
		condicao.append("	,data_pedido as data     	");
		condicao.append("	,(quantidade * -1) as quantidade	");
		condicao.append("	,cast('pedido venda ' as varchar(100)) ");
		condicao.append("from " + seguranca.getEmpresa() + ".expedicao t ");
		condicao.append("inner join " + seguranca.getEmpresa() + ".expedicao_item i on t.id = expedicao_id ");
		condicao.append("where t.datacancelamento is null ");
		condicao.append("	and t.pessoa_id = " + seguranca.getPessoaLogadoOrigem().getId());

		condicao.append("union all ");
		condicao.append("select ");
		condicao.append(" t.id ");
		condicao.append(" ,produto_id as produto ");
		condicao.append(" ,dataencerramento as data ");
		condicao.append(" ,quantidade as quantidade ");
		condicao.append(" ,cast('aquisição ' as varchar(100)) ");
		condicao.append("from " + seguranca.getEmpresa() + ".aquisicao t ");
		condicao.append("inner join " + seguranca.getEmpresa() + ".aquisicao_item on t.id = aquisicao_id ");
		condicao.append("where t.datacancelamento is null ");
		condicao.append(" and t.empresa_id = " + seguranca.getPessoaLogadoOrigem().getId());

		condicao.append("union all ");
		condicao.append("select ");
		condicao.append(" t.id ");
		condicao.append(" ,prod_id as produto ");
		condicao.append(" ,dataemissao as data ");
		condicao.append(" ,quantidade as quantidade ");
		condicao.append(" ,cast('nota fiscal ' as varchar(100)) ");
		condicao.append("from " + seguranca.getEmpresa() + ".notafiscal t ");
		condicao.append("inner join " + seguranca.getEmpresa() + ".notafiscalitem on t.id = nota_id ");
		condicao.append("where t.datacancelamento is null ");
		condicao.append(" and t.tipo = 1 and t.statussefaz = 100 ");
		condicao.append(" and t.empresa_id = " + seguranca.getPessoaLogadoOrigem().getId());

		return condicao.toString();
	}

	@SuppressWarnings("unchecked")
	public List<PosicaoFinanceiraDetalhe> movimentacaoDeProduto(Produto produto, Date inicio, Date termino) {

		StringBuilder condicao = new StringBuilder();

		condicao.append("select * from (" + consultaDeProduto() + ") as movimento ");
		condicao.append("where produto = " + produto.getId());
		if (inicio != null)
			condicao.append(" and cast(data as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		if (termino != null)
			condicao.append(" and cast(data as date) <= " + Suporte.formataDataSQL_Quoted(termino));

		List<PosicaoFinanceiraDetalhe> lista = new ArrayList<>();
		List<Object[]> lstObj = manager.createNativeQuery(condicao.toString()).getResultList();
		for (Object[] obj : lstObj) {
			if (obj != null) {
				lista.add(new PosicaoFinanceiraDetalhe((Date) obj[2], (String) obj[4] + obj[1], (Double) obj[3]));
			}
		}
		return lista;
	}

}