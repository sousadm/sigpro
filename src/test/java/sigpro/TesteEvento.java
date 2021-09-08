package sigpro;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.evento.EventoPessoa;

public class TesteEvento {

	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) {

		try {

			factory = Persistence.createEntityManagerFactory("SistemaPU");
			manager = factory.createEntityManager();

			EntityTransaction trx = manager.getTransaction();
			trx.begin();

			Pessoa pessoa = manager.find(Pessoa.class, 1L);
			EventoPessoa evento = new EventoPessoa();
			evento.setData(new Date());
			evento.setDescritivo("teste de sistema");
			evento.setUsuario(pessoa.getUsuario());
			evento = manager.merge(evento);

			trx.commit();
						
			System.out.println(evento.getId());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
