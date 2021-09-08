package com.sousa.sigpro.util.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

public class FacesUtil {

	public static boolean isPostback() {
		return FacesContext.getCurrentInstance().isPostback();
	}

	public static boolean isNotPostback() {
		return !isPostback();
	}

	public static void addErrorMessage(Exception e) {
		String message = e.getMessage() + "\\CAUSA: " + e.getCause().toString();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
	}

	public static void addErrorMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
	}

	public static void addInfoMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
	}

	public static void addRequestErrorMessage(String message) {
		PrimeFaces.current().dialog()
				.showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "sigpro", message));
	}

	public static void addRequestInfoMessage(String message) {
		PrimeFaces.current().dialog()
				.showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "sigpro", message));
	}

}