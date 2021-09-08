package com.sousa.sigpro.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.mdfe3.classes.def.MDFTipoCarroceria;
import com.fincatto.documentofiscal.mdfe3.classes.def.MDFTipoRodado;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoCombustivelTipo;
import com.sousa.sigpro.model.tipo.TipoVeiculo;

@Entity
@DiscriminatorValue("VEICULO")
@NamedQueries({
		@NamedQuery(name = "Veiculo.lista", query = "select v from Veiculo v where (v.placa like :placa or v.pessoa.nome like :nome) and v.pessoa.origem = :origem order by v.placa"),
		@NamedQuery(name = "Veiculo.listaConvidado", query = "select v from Veiculo v where v.pessoa = :pessoa order by v.placa") })
public class Veiculo extends Equipamento {

	private String placa;
	private String codigoRenavam;
	private DFUnidadeFederativa unidadeFederativaLicenciamento;
	private TipoVeiculo tipoVeiculo;
	private MDFTipoRodado tipoRodado;
	private MDFTipoCarroceria tipoCarroceria;
	private NFNotaInfoCombustivelTipo tipoCombustivel;
	private Long odometroInicial;
	private Long odometroFinal;
	private Long odometroAbastecimento;
	private Long mediaKmMes;
	private double consumoMedio;
	private String rntrc_proprietario;

	public Veiculo() {
		this.setMarca(new Marca());
		tipoVeiculo = TipoVeiculo.AUTO;
		tipoRodado = MDFTipoRodado.OUTROS;
		tipoCarroceria = MDFTipoCarroceria.NAO_APLICAVEL;
		odometroInicial = 0L;
		odometroFinal = 0L;
		mediaKmMes = 0L;
	}

	@Transient
	public String getDescritivo() {
		return (placa == null ? "" : placa) + " " + (this.getDescricao() == null ? "" : this.getDescricao());
	}

	// @PLACA
	@NotEmpty
	@Column(name = "codigo", length = 20)
	public String getPlaca() {
		return placa;
	}

	@Column(length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	public DFUnidadeFederativa getUnidadeFederativaLicenciamento() {
		return unidadeFederativaLicenciamento;
	}

	public void setUnidadeFederativaLicenciamento(DFUnidadeFederativa unidadeFederativaLicenciamento) {
		this.unidadeFederativaLicenciamento = unidadeFederativaLicenciamento;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	@Column(length = 10)
	public String getRntrc_proprietario() {
		return rntrc_proprietario;
	}

	public void setRntrc_proprietario(String rntrc_proprietario) {
		this.rntrc_proprietario = rntrc_proprietario;
	}

	@Column(length = 20)
	public String getCodigoRenavam() {
		return codigoRenavam;
	}

	public void setCodigoRenavam(String codigoRenavam) {
		this.codigoRenavam = codigoRenavam;
	}

	public Long getOdometroInicial() {
		return odometroInicial;
	}

	public void setOdometroInicial(Long odometroInicial) {
		this.odometroInicial = odometroInicial;
	}

	public Long getOdometroFinal() {
		return odometroFinal;
	}

	public void setOdometroFinal(Long odometroFinal) {
		this.odometroFinal = odometroFinal;
	}

	public Long getMediaKmMes() {
		return mediaKmMes;
	}

	public void setMediaKmMes(Long mediaKmMes) {
		this.mediaKmMes = mediaKmMes;
	}

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	public MDFTipoRodado getTipoRodado() {
		return tipoRodado;
	}

	public void setTipoRodado(MDFTipoRodado tipoRodado) {
		this.tipoRodado = tipoRodado;
	}

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	public TipoVeiculo getTipoVeiculo() {
		return tipoVeiculo;
	}

	public void setTipoVeiculo(TipoVeiculo tipoVeiculo) {
		this.tipoVeiculo = tipoVeiculo;
	}

	@Column(length = 40)
	@Enumerated(EnumType.STRING)
	public NFNotaInfoCombustivelTipo getTipoCombustivel() {
		return tipoCombustivel;
	}

	public void setTipoCombustivel(NFNotaInfoCombustivelTipo tipoCombustivel) {
		this.tipoCombustivel = tipoCombustivel;
	}

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	public MDFTipoCarroceria getTipoCarroceria() {
		return tipoCarroceria;
	}

	public void setTipoCarroceria(MDFTipoCarroceria tipoCarroceria) {
		this.tipoCarroceria = tipoCarroceria;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getConsumoMedio() {
		return consumoMedio;
	}

	public Long getOdometroAbastecimento() {
		return odometroAbastecimento;
	}

	public void setOdometroAbastecimento(Long odometroAbastecimento) {
		this.odometroAbastecimento = odometroAbastecimento;
	}

	public void setConsumoMedio(double consumoMedio) {
		this.consumoMedio = consumoMedio;
	}

	@Transient
	public boolean isLiberaOdometro() {
		return odometroFinal == null || odometroFinal == 0;
	}

}