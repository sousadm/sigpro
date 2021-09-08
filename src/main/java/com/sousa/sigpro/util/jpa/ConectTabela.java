package com.sousa.sigpro.util.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.sousa.sigpro.util.Suporte;

public class ConectTabela {

	private static EntityManagerFactory factory;

	public ConectTabela() {

		Map<String, String> props = new HashMap<>();

		props.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
		props.put("javax.persistence.jdbc.url", Suporte.URL_DATABASE);
		props.put("javax.persistence.jdbc.password", Suporte.SENHA_BANCO_DADOS);
		props.put("hibernate.default_schema", "tabelas");
		props.put("hibernate.hbm2ddl.auto", "none");
		props.put("javax.persistence.jdbc.user", Suporte.USUARIO_BANCO_DADOS);
		factory = Persistence.createEntityManagerFactory("SistemaPU", props);

	}

	public static EntityManager createEntityManager() {
		return factory.createEntityManager();
	}

}
