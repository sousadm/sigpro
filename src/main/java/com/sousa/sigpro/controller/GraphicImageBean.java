package com.sousa.sigpro.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CaptureEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.sousa.sigpro.util.Suporte;

@Named
@RequestScoped
public class GraphicImageBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private StreamedContent image;
	private byte[] foto = new byte[0];

	public GraphicImageBean() {
		if (Suporte.existeAtributoNaSessao("imagem")) {
			try {
				foto = (byte[]) Suporte.getAtributoDaSessao("imagem");
			} catch (Exception e) {
				foto = null;
			}
		}
	}

	public StreamedContent getImagem() {

		DefaultStreamedContent content = null;
		String arquivo = (String) Suporte.getAtributoDaSessao("imagem");
		if (arquivo != null) {
			File foto = new File(arquivo);
			if (foto.exists()) {
				try {
					BufferedInputStream in = new BufferedInputStream(new FileInputStream(foto));
					byte[] bytes = new byte[in.available()];
					in.read(bytes);
					in.close();
					content = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "image/jpeg");
				} catch (Exception e) {
					// log.error(e);
				}
			}
		}

		return content;

	}

	public StreamedContent getImage() {
		return image;
	}

	public void setImage(StreamedContent image) {
		this.image = image;
	}

	public void onCapture(CaptureEvent captureEvent) {
		//InputStream inputStream = new ByteArrayInputStream(captureEvent.getData());
		//image = new DefaultStreamedContent(inputStream, "image/png");
		//foto = captureEvent.getData();
		Suporte.setAtributoNaSessao("imagem", captureEvent.getData());
		PrimeFaces.current().dialog().closeDynamic(null);
	}

	public void uploadImagem(FileUploadEvent event) {
		foto = event.getFile().getContents();
		Suporte.setAtributoNaSessao("imagem", foto);
		PrimeFaces.current().dialog().closeDynamic(null);
	}

	public StreamedContent getPhoto() {
		DefaultStreamedContent content = null;
		try {
			if (foto != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(foto), "image/jpeg");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return content;
	}

	public boolean isExistePhoto() {
		return foto != null && foto.length > 0;
	}
	
}