package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;

import com.sousa.sigpro.model.tipo.TipoAtividadePrincipal;
import com.sousa.sigpro.model.tipo.TipoCategoriaHabilitacao;
import com.sousa.sigpro.model.tipo.TipoComoConheceu;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoPessoa;

@Entity
@NamedQueries({
		@NamedQuery(name = "Pessoa.listaClienteDigital", query = "select c from ClienteDigital c where c.pessoa = :pessoa order by c.agente"),
		@NamedQuery(name = "Pessoa.listaAgenteApi", query = "select p from Pessoa p join p.agente a where a.apiDeCobranca is not null order by p.nome"),
		@NamedQuery(name = "Pessoa.porUsuario", query = "select p from Pessoa p join p.usuario u where u.nome like :nome"),
		@NamedQuery(name = "Pessoa.porAgente", query = "select p from Pessoa p where p.agente = :agente"),
		@NamedQuery(name = "Pessoa.porColaborador", query = "select p from Pessoa p where p.colaborador = :colaborador"),
		@NamedQuery(name = "Pessoa.porCpf", query = "select p from Pessoa p where p.PF.cpf = :cpf and p.origem = :origem"),
		@NamedQuery(name = "Pessoa.porCnpj", query = "select p from Pessoa p where p.PJ.cnpj = :cnpj and p.origem = :origem"),
		@NamedQuery(name = "Pessoa.porCpfCnpj", query = "select p from Pessoa p where (p.PJ.cnpj = :codigo or p.PF.cpf = :codigo) and p.origem = :origem"),
		@NamedQuery(name = "Pessoa.clientes", query = "select p from Pessoa p join p.cliente c where p.defineCliente = true and p.origem = :origem"),
		@NamedQuery(name = "Pessoa.fisicas", query = "select p from Pessoa p where p.PF.cpf is not null and p.origem = :origem order by p.nome"),
		@NamedQuery(name = "Pessoa.porNome", query = "select p from Pessoa p where upper(p.nome) like :nome and p.origem = :origem"),
		@NamedQuery(name = "Pessoa.usuarios", query = "select p from Pessoa p join p.usuario v where p.defineUsuario = true and p.origem = :origem"),
		@NamedQuery(name = "Pessoa.vendedores", query = "select p from Pessoa p join p.vendedor v where upper(p.nome) like :nome and p.defineVendedor = true and p.origem = :origem"),
		@NamedQuery(name = "Pessoa.fornecedores", query = "select p from Pessoa p join p.fornecedor v where upper(p.nome) like :nome and p.defineFornecedor = true and p.origem = :origem"),
		@NamedQuery(name = "Pessoa.agentes", query = "select p from Pessoa p join p.agente a where p.defineAgente = true and p.origem = :origem") })
public class Pessoa implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Pessoa origem;
	private Date dataCadastro = new Date();
	private String nome;
	private String email;
	private String home;
	private String ddd;
	private String fone;
	private String celular;
	private String observacao;
	private PessoaFisica PF;
	private PessoaJuridica PJ;
	private UnidadeProdutiva unidadeProdutiva;
	private byte[] imagem;
	private Boolean aceitaSMS;
	private double valorCredito;
	private TipoPessoa tipo;
	private TipoAtividadePrincipal atividade;
	private TipoComoConheceu como;
	private List<String> modulos;

	private List<Endereco> enderecos;

	private Boolean defineCliente;
	private Boolean defineVendedor;
	private Boolean defineUsuario;
	private Boolean defineAgente;
	private Boolean defineFornecedor;
	private Boolean defineColaborador;
	private Boolean defineTransportador;
	private Boolean defineMotorista;

	private Cliente cliente;
	private Usuario usuario;
	private Vendedor vendedor;
	private Agente agente;
	private Fornecedor fornecedor;
	private Colaborador colaborador;
	private Transportador transportador;
	private Motorista motorista;

	public Pessoa() {
		defineCliente = false;
		defineTransportador = false;
		defineAgente = false;
		defineUsuario = false;
		defineFornecedor = false;
		defineVendedor = false;
		defineColaborador = false;
		defineMotorista = false;
		PF = new PessoaFisica();
		PJ = new PessoaJuridica();
		PJ.setIncentivoCultural(false);
		tipo = TipoPessoa.PF;
		transportador = new Transportador();
		usuario = new Usuario();
		cliente = new Cliente();
		vendedor = new Vendedor();
		fornecedor = new Fornecedor();
		colaborador = new Colaborador();
		unidadeProdutiva = new UnidadeProdutiva();
		motorista = new Motorista();
		agente = new Agente();
		modulos = new ArrayList<>();
		enderecos = new ArrayList<>();
	}

	@Transient
	public String getTratamento() {
		String descricao = "";
		if (this.PF != null) {
			if (PF.getTratamento() != null) {
				descricao = descricao + PF.getTratamento().getDescricao() + ", ";
			}
		}
		descricao = descricao + nome;
		return descricao;
	}

	public byte[] getImagem() {
		return imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	@OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<Endereco> getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(List<Endereco> enderecos) {
		this.enderecos = enderecos;
	}

	@Transient
	public String getDescritivo() {
		return nome + " " + getCpfCnpjToString();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 2)
	public String getDdd() {
		return ddd;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	@ElementCollection
	@Column(length = 20)
	public List<String> getModulos() {
		return modulos;
	}

	public void setModulos(List<String> modulos) {
		this.modulos = modulos;
	}

	@Embedded
	public PessoaFisica getPF() {
		return PF;
	}

	public void setPF(PessoaFisica pF) {
		PF = pF;
	}

	@Embedded
	public UnidadeProdutiva getUnidadeProdutiva() {
		return unidadeProdutiva;
	}

	public void setUnidadeProdutiva(UnidadeProdutiva unidadeProdutiva) {
		this.unidadeProdutiva = unidadeProdutiva;
	}

	@Embedded
	public PessoaJuridica getPJ() {
		return PJ;
	}

	public void setPJ(PessoaJuridica pJ) {
		PJ = pJ;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Column(length = 100, nullable = false)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Email(message = "e-mail inválido")
	@Column(length = 150)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFone(String fone) {
		this.fone = fone;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	@Column(name = "fone", length = 20)
	public String getFone() {
		return fone;
	}

	@Column(name = "celular", length = 20)
	public String getCelular() {
		return celular;
	}

	@Column(length = 2)
	@Enumerated(EnumType.STRING)
	public TipoPessoa getTipo() {
		return tipo;
	}

	public void setTipo(TipoPessoa tipo) {
		this.tipo = tipo;
	}

	public Boolean getDefineUsuario() {
		return defineUsuario;
	}

	public void setDefineUsuario(Boolean defineUsuario) {
		this.defineUsuario = defineUsuario;
	}

	public Boolean getDefineMotorista() {
		return defineMotorista;
	}

	public void setDefineMotorista(Boolean defineMotorista) {
		this.defineMotorista = defineMotorista;
	}

	public Boolean getDefineCliente() {
		return defineCliente;
	}

	public void setDefineCliente(Boolean defineCliente) {
		this.defineCliente = defineCliente;
	}

	public Boolean getDefineVendedor() {
		return defineVendedor;
	}

	public void setDefineVendedor(Boolean defineVendedor) {
		this.defineVendedor = defineVendedor;
	}

	public Boolean getDefineAgente() {
		return defineAgente;
	}

	public void setDefineAgente(Boolean defineAgente) {
		this.defineAgente = defineAgente;
	}

	public Boolean getDefineFornecedor() {
		return defineFornecedor;
	}

	public void setDefineFornecedor(Boolean defineFornecedor) {
		this.defineFornecedor = defineFornecedor;
	}

	public Boolean getDefineTransportador() {
		return defineTransportador;
	}

	public void setDefineTransportador(Boolean defineTransportador) {
		this.defineTransportador = defineTransportador;
	}

	public Boolean getDefineColaborador() {
		return defineColaborador;
	}

	public void setDefineColaborador(Boolean defineColaborador) {
		this.defineColaborador = defineColaborador;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Colaborador getColaborador() {
		return colaborador;
	}

	public void setColaborador(Colaborador colaborador) {
		this.colaborador = colaborador;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Transportador getTransportador() {
		return transportador;
	}

	public void setTransportador(Transportador transportador) {
		this.transportador = transportador;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Motorista getMotorista() {
		return motorista;
	}

	public void setMotorista(Motorista motorista) {
		this.motorista = motorista;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Agente getAgente() {
		return agente;
	}

	public void setAgente(Agente agente) {
		this.agente = agente;
	}

//	@OneToOne(cascade = CascadeType.ALL)
//	public Endereco getEndereco() {
//		return endereco;
//	}

//	public void setEndereco(Endereco endereco) {
//		this.endereco = endereco;
//	}

	@Transient
	public boolean isPessoaJuridica() {
		return tipo == TipoPessoa.PJ && PJ != null;
	}

	@Transient
	public boolean isPodeTerContato() {
		return id != null && tipo == TipoPessoa.PJ && PJ != null;
	}

	@Transient
	public boolean isPessoaFisica() {
		return tipo == TipoPessoa.PF && PF != null;
	}

	@ManyToOne
	public Pessoa getOrigem() {
		return origem;
	}

	public void setOrigem(Pessoa origem) {
		this.origem = origem;
	}

	@Column(length = 3)
	@Enumerated(EnumType.STRING)
	public TipoComoConheceu getComo() {
		return como;
	}

	public void setComo(TipoComoConheceu como) {
		this.como = como;
	}

	@Column(columnDefinition = "text")
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoAtividadePrincipal getAtividade() {
		return atividade;
	}

	public void setAtividade(TipoAtividadePrincipal atividade) {
		this.atividade = atividade;
	}

	@Column(name = "aceita_sms")
	public Boolean getAceitaSMS() {
		return aceitaSMS;
	}

	public void setAceitaSMS(Boolean aceitaSMS) {
		this.aceitaSMS = aceitaSMS;
	}

	@Transient
	public String getFoneCnpj() {
		return "Fone - " + fone + PJ == null ? "" : " CNPJ - " + PJ.getCnpj();
	}

	@Transient
	public boolean isPrincipal() {
		return origem == this;
	}

	@Transient
	public boolean isClinica() {
		return atividade.equals(TipoAtividadePrincipal.CLINICA);
	}

	@Transient
	public String getCpfCnpjToString() {
		if (tipo == TipoPessoa.PF) {
			String b1 = PF.getCpf().substring(0, 3);
			String b2 = PF.getCpf().substring(3, 6);
			String b3 = PF.getCpf().substring(6, 9);
			String b4 = PF.getCpf().substring(9, 11);
			return b1 + "." + b2 + "." + b3 + "-" + b4;

		} else if (tipo == TipoPessoa.PJ) {
			String b1 = PJ.getCnpj().substring(0, 2);
			String b2 = PJ.getCnpj().substring(2, 5);
			String b3 = PJ.getCnpj().substring(5, 8);
			String b4 = PJ.getCnpj().substring(8, 12);
			String b5 = PJ.getCnpj().substring(12, 14);
			return b1 + "." + b2 + "." + b3 + "/" + b4 + "-" + b5;
		} else {
			return "";
		}
	}

	@Transient
	public String getInscricaoIdentidade() {
		if (tipo == TipoPessoa.PF) {
			return PF.getIdentidade();
		} else if (tipo == TipoPessoa.PJ) {
			return PJ.getIE();
		} else {
			return "";
		}
	}

	@Column(length = 200)
	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	@Column(precision = 12, scale = 2, columnDefinition = "double precision default 0")
	public double getValorCredito() {
		return valorCredito;
	}

	public void setValorCredito(double valorCredito) {
		this.valorCredito = valorCredito;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pessoa other = (Pessoa) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Transient
	public boolean isPodeEnviarSenha() {
		return id != null && email != null && defineUsuario;
	}

	@Transient
	public boolean isMostraCNH() {
		return defineMotorista && motorista != null && motorista.getCnhCategoria() != TipoCategoriaHabilitacao.NA;
	}

	@Transient
	public boolean isExisteTodoComplemento() {
		try {
			return defineCliente && defineVendedor && defineUsuario && defineAgente && defineFornecedor
					&& defineColaborador && defineTransportador && defineMotorista;
		} catch (Exception e) {
			return false;
		}

	}

	@Transient
	public boolean isExisteAlgumComplemento() {
		try {
			return defineCliente || defineVendedor || defineUsuario || defineAgente || defineFornecedor
					|| defineColaborador || defineTransportador || defineMotorista;
		} catch (Exception e) {
			return false;
		}

	}

	@Transient
	public boolean isExiste() {
		return id != null && id > 0;
	}

	@Transient
	public boolean isPessoaIndefinida() {
		return tipo == TipoPessoa.NA;
	}

	@Transient
	public Endereco getEndereco(TipoEndereco tipoEndereco) {

		if (enderecos.size() > 0) {
			for (Endereco e : enderecos) {
				if (e.getTipoEndereco() == tipoEndereco) {
					return e;
				}
			}
			return enderecos.get(0);
		}

		return new Endereco();

	}

}