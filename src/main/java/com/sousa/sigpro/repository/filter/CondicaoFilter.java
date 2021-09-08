package com.sousa.sigpro.repository.filter;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import com.fincatto.documentofiscal.DFAmbiente;
import com.sousa.sigpro.model.Agente;
import com.sousa.sigpro.model.Cartao;
import com.sousa.sigpro.model.Cliente;
import com.sousa.sigpro.model.Colaborador;
import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.OperacaoFiscal;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Profissao;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.Usuario;
import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.model.tipo.TipoAgendaStatus;
import com.sousa.sigpro.model.tipo.TipoAtividadePrincipal;
import com.sousa.sigpro.model.tipo.TipoCaixaStatus;
import com.sousa.sigpro.model.tipo.TipoExpedicao;
import com.sousa.sigpro.model.tipo.TipoPagamento;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.model.tipo.TipoSituacaoFinanceira;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.SuporteData;

public class CondicaoFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private int opcao;
	private Long numeroDe;
	private Long numeroAte;
	private Date inicio;
	private Date termino;
	private boolean defineVendedor;
	private Date emissaoInicial;
	private Date emissaoFinal;
	private Date vencimentoInicial;
	private Date vencimentoFinal;
	private Date cancelamentoInicial;
	private Date cancelamentoFinal;
	private Date previsaoInicial;
	private Date previsaoFinal;
	private Boolean disponivel;

	private String codigo;
	private String nome;
	private String nomeVendedor;
	private String nomeCliente;
	private String cpf;
	private String cnpj;
	private String descricao;

	private Profissao profissao;
	private Agente agente;
	private Cliente cliente;
	private Usuario usuario;
	private Pessoa pessoa;
	private Veiculo veiculo;
	private Titulo titulo;
	private Cartao cartao;
	private Colaborador consultor;
	private OperacaoFiscal operacaoFiscal;
	private TipoTituloOrigem[] tipoDC;
	private TipoExpedicao[] expedicaoTipos;
	private TipoPagamento[] pagamentoTipos;
	private TipoSituacaoFinanceira tituloStatus;
	private TipoCaixaStatus caixaStatus;
	private TipoAgendaStatus tipoAgendaStatus;
	private TipoPessoa[] tipoPessoa;
	private TipoAtividadePrincipal atividade;
	private DFAmbiente ambienteFiscal;
	private Date pagamentoInicial;
	private Date pagamentoFinal;
	private ContaCorrente contaCorrente;
	private ContaCorrente[] contas;

	private Long primeiroRegistro;
	private Long quantidadeRegistros;
	private String sortField;
	private boolean ascendente;
	private String ordem;
	private String sql;

	private int size;
	private int pagina;
	private Long first;
	private Long pageSize;

	public CondicaoFilter() {
		codigo = "";
		nome = "";
		cliente = new Cliente();
		veiculo = new Veiculo();
		pessoa = new Pessoa();
	}

	public String getTituloRelatorio() {
		String valor = "";

		if (inicio != null && termino != null)
			valor += (valor.length() == 0 ? "" : " / ") + "Período: "
					+ SuporteData.formataDataToStr(inicio, "dd/MM/yyyy") + " a "
					+ SuporteData.formataDataToStr(termino, "dd/MM/yyyy");

		if (emissaoInicial != null && emissaoFinal != null)
			valor += (valor.length() == 0 ? "" : " / ") + "Emissão: "
					+ SuporteData.formataDataToStr(emissaoInicial, "dd/MM/yyyy") + " a "
					+ SuporteData.formataDataToStr(emissaoFinal, "dd/MM/yyyy");

		if (vencimentoInicial != null && vencimentoFinal != null)
			valor += (valor.length() == 0 ? "" : " / ") + "Vencimento: "
					+ SuporteData.formataDataToStr(vencimentoInicial, "dd/MM/yyyy") + " a "
					+ SuporteData.formataDataToStr(vencimentoFinal, "dd/MM/yyyy");

		if (pagamentoInicial != null && pagamentoFinal != null)
			valor += (valor.length() == 0 ? "" : " / ") + "Pagamento: "
					+ SuporteData.formataDataToStr(pagamentoInicial, "dd/MM/yyyy") + " a "
					+ SuporteData.formataDataToStr(pagamentoFinal, "dd/MM/yyyy");

		if (emissaoInicial != null && emissaoFinal != null)
			valor += (valor.length() == 0 ? "" : " / ") + "Emissão: "
					+ SuporteData.formataDataToStr(emissaoInicial, "dd/MM/yyyy") + " a "
					+ SuporteData.formataDataToStr(emissaoFinal, "dd/MM/yyyy");

		if (cancelamentoInicial != null && cancelamentoFinal != null)
			valor += (valor.length() == 0 ? "" : " / ") + "Cancelamento: "
					+ SuporteData.formataDataToStr(cancelamentoInicial, "dd/MM/yyyy") + " a "
					+ SuporteData.formataDataToStr(cancelamentoFinal, "dd/MM/yyyy");

		return valor;
	}

	public CondicaoFilter(String nome) {
		this.nome = nome;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public TipoTituloOrigem[] getTipoDC() {
		return tipoDC;
	}

	public void setTipoDC(TipoTituloOrigem[] tipoDC) {
		this.tipoDC = tipoDC;
	}

	public Long getNumeroDe() {
		return numeroDe;
	}

	public void setNumeroDe(Long numeroDe) {
		this.numeroDe = numeroDe;
	}

	public Long getNumeroAte() {
		return numeroAte;
	}

	public void setNumeroAte(Long numeroAte) {
		this.numeroAte = numeroAte;
	}

	public Date getEmissaoInicial() {
		return emissaoInicial;
	}

	public void setEmissaoInicial(Date emissaoInicial) {
		this.emissaoInicial = emissaoInicial;
	}

	public Date getEmissaoFinal() {
		return emissaoFinal;
	}

	public void setEmissaoFinal(Date emissaoFinal) {
		this.emissaoFinal = emissaoFinal;
	}

	public TipoExpedicao[] getExpedicaoTipos() {
		return expedicaoTipos;
	}

	public TipoPagamento[] getPagamentoTipos() {
		return pagamentoTipos;
	}

	public void setPagamentoTipos(TipoPagamento[] pagamentoTipos) {
		this.pagamentoTipos = pagamentoTipos;
	}

	public void setExpedicaoTipos(TipoExpedicao[] expedicaoTipos) {
		this.expedicaoTipos = expedicaoTipos;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getTermino() {
		return termino;
	}

	public void setTermino(Date termino) {
		this.termino = termino;
	}

	public TipoSituacaoFinanceira getTituloStatus() {
		return tituloStatus;
	}

	public void setTituloStatus(TipoSituacaoFinanceira tituloStatus) {
		this.tituloStatus = tituloStatus;
	}

	public TipoCaixaStatus getCaixaStatus() {
		return caixaStatus;
	}

	public void setCaixaStatus(TipoCaixaStatus caixaStatus) {
		this.caixaStatus = caixaStatus;
	}

	public Date getVencimentoFinal() {
		return vencimentoFinal;
	}

	public void setVencimentoFinal(Date vencimentoFinal) {
		this.vencimentoFinal = vencimentoFinal;
	}

	public Date getVencimentoInicial() {
		return vencimentoInicial;
	}

	public void setVencimentoInicial(Date vencimentoInicial) {
		this.vencimentoInicial = vencimentoInicial;
	}

	public Agente getAgente() {
		return agente;
	}

	public void setAgente(Agente agente) {
		this.agente = agente;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public String getNomeVendedor() {
		return nomeVendedor;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public void setNomeVendedor(String nomeVendedor) {
		this.nomeVendedor = nomeVendedor;
	}

	public String getOrdem() {
		return ordem;
	}

	public void setOrdem(String ordem) {
		this.ordem = ordem;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	public Date getCancelamentoFinal() {
		return cancelamentoFinal;
	}

	public void setCancelamentoFinal(Date cancelamentoFinal) {
		this.cancelamentoFinal = cancelamentoFinal;
	}

	public Date getCancelamentoInicial() {
		return cancelamentoInicial;
	}

	public void setCancelamentoInicial(Date cancelamentoInicial) {
		this.cancelamentoInicial = cancelamentoInicial;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public TipoAgendaStatus getTipoAgendaStatus() {
		return tipoAgendaStatus;
	}

	public void setTipoAgendaStatus(TipoAgendaStatus tipoAgendaStatus) {
		this.tipoAgendaStatus = tipoAgendaStatus;
	}

	public TipoPessoa[] getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(TipoPessoa[] tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public DFAmbiente getAmbienteFiscal() {
		return ambienteFiscal;
	}

	public void setAmbienteFiscal(DFAmbiente ambienteFiscal) {
		this.ambienteFiscal = ambienteFiscal;
	}

	public TipoAtividadePrincipal getAtividade() {
		return atividade;
	}

	public void setAtividade(TipoAtividadePrincipal atividade) {
		this.atividade = atividade;
	}

	public boolean isDefineVendedor() {
		return defineVendedor;
	}

	public void setDefineVendedor(boolean defineVendedor) {
		this.defineVendedor = defineVendedor;
	}

	@CPF(message = "inválido")
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	@CNPJ(message = "inválido")
	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void analisaPeriodoEmissao() {
		if (emissaoInicial == null || emissaoFinal == null)
			throw new NegocioException("Período de emissão indefinido!");
		if (emissaoInicial.after(emissaoFinal))
			throw new NegocioException("Período de emissão incorreto!");
	}

	public int getOpcao() {
		return opcao;
	}

	public void setOpcao(int opcao) {
		this.opcao = opcao;
	}

	public Date getPrevisaoFinal() {
		return previsaoFinal;
	}

	public void setPrevisaoFinal(Date previsaoFinal) {
		this.previsaoFinal = previsaoFinal;
	}

	public Date getPrevisaoInicial() {
		return previsaoInicial;
	}

	public void setPrevisaoInicial(Date previsaoInicial) {
		this.previsaoInicial = previsaoInicial;
	}

	public Date getPagamentoInicial() {
		return pagamentoInicial;
	}

	public void setPagamentoInicial(Date pagamentoInicial) {
		this.pagamentoInicial = pagamentoInicial;
	}

	public Date getPagamentoFinal() {
		return pagamentoFinal;
	}

	public void setPagamentoFinal(Date pagamentoFinal) {
		this.pagamentoFinal = pagamentoFinal;
	}

	public OperacaoFiscal getOperacaoFiscal() {
		return operacaoFiscal;
	}

	public void setOperacaoFiscal(OperacaoFiscal operacaoFiscal) {
		this.operacaoFiscal = operacaoFiscal;
	}

	public Long getPrimeiroRegistro() {
		return primeiroRegistro;
	}

	public void setPrimeiroRegistro(Long primeiroRegistro) {
		this.primeiroRegistro = primeiroRegistro;
	}

	public Long getQuantidadeRegistros() {
		return quantidadeRegistros;
	}

	public void setQuantidadeRegistros(Long quantidadeRegistros) {
		this.quantidadeRegistros = quantidadeRegistros;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public boolean isAscendente() {
		return ascendente;
	}

	public void setAscendente(boolean ascendente) {
		this.ascendente = ascendente;
	}

	public Colaborador getConsultor() {
		return consultor;
	}

	public void setConsultor(Colaborador consultor) {
		this.consultor = consultor;
	}

	public Profissao getProfissao() {
		return profissao;
	}

	public void setProfissao(Profissao profissao) {
		this.profissao = profissao;
	}

	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getDisponivel() {
		return disponivel;
	}

	public void setDisponivel(Boolean disponivel) {
		this.disponivel = disponivel;
	}

	public ContaCorrente[] getContas() {
		return contas;
	}

	public void setContas(ContaCorrente[] contas) {
		this.contas = contas;
	}

	public Titulo getTitulo() {
		return titulo;
	}

	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
	}

	public Cartao getCartao() {
		return cartao;
	}

	public void setCartao(Cartao cartao) {
		this.cartao = cartao;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}

	public Long getFirst() {
		return first;
	}

	public void setFirst(Long first) {
		this.first = first;
	}

	public Long getPageSize() {
		return pageSize;
	}

	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}

}