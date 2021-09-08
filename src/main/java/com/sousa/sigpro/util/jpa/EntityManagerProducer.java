package com.sousa.sigpro.util.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

@ApplicationScoped
public class EntityManagerProducer {

	private EntityManagerFactory factory;

	@Produces
	@RequestScoped
	public EntityManager createEntityManager() {
		try {

			String empresa = (String) Suporte.getAtributoDaSessao("empresa");
			if (empresa == null)
				throw new NegocioException("Defina a empresa para acesso ao sistema");

			factory = (EntityManagerFactory) Suporte.getAtributoDaSessao("factory");

			if (factory == null) {

				Map<String, String> props = new HashMap<>();

				props.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
				props.put("javax.persistence.jdbc.url", Suporte.URL_DATABASE);
				props.put("javax.persistence.jdbc.password", Suporte.SENHA_BANCO_DADOS);
				props.put("hibernate.default_schema", empresa);
				props.put("javax.persistence.jdbc.user", Suporte.USUARIO_BANCO_DADOS);

				props.put("javax.persistence.schema-generation.create-source", "metadata");
				props.put("hibernate.hbm2ddl.auto", "update");
				props.put("hibernate.format_sql", "true");

				props.put("hibernate.c3p0.min_size", "5");
				props.put("hibernate.c3p0.max_size", "10");
				props.put("hibernate.c3p0.acquire_increment", "5");
				props.put("hibernate.c3p0.idle_test_period", "3000");
				props.put("hibernate.c3p0.max_statements", "30");
				props.put("hibernate.c3p0.timeout", "300");
				factory = Persistence.createEntityManagerFactory("SistemaPU", props);

				Suporte.setAtributoNaSessao("factory", factory);

			} 

			return factory.createEntityManager();

		} catch (

		Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
			return null;
		}
	}

	public void closeEntityManager(@Disposes EntityManager manager) {
		if (manager != null && manager.isOpen())
			manager.close();
	}

	@Override
	protected void finalize() throws Throwable {
		if (factory != null && factory.isOpen())
			factory.close();
	}
}