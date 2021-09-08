package com.sousa.sigpro.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class Ender implements Serializable {

	private static final long serialVersionUID = 1L;

	private String xLogra;
	private int xNum;
	private String xCompl;
	private Municipio xMun;
	private String xBairro;

	@ManyToOne
	public Municipio getxMun() {
		return xMun;
	}

	public void setxMun(Municipio xMun) {
		this.xMun = xMun;
	}

	@Column(length = 150)
	public String getxLogra() {
		return xLogra;
	}

	public void setxLogra(String xLogra) {
		this.xLogra = xLogra;
	}

	public int getxNum() {
		return xNum;
	}

	public void setxNum(int xNum) {
		this.xNum = xNum;
	}

	@Column(length = 35)
	public String getxCompl() {
		return xCompl;
	}

	public void setxCompl(String xCompl) {
		this.xCompl = xCompl;
	}

	@Column(length = 50)
	public String getxBairro() {
		return xBairro;
	}

	public void setxBairro(String xBairro) {
		this.xBairro = xBairro;
	}
}