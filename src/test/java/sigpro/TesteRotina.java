package sigpro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Usuario;

public class TesteRotina {

	static String schema = "sanpil";
	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) {

		try {

			String condicao;

			factory = Persistence.createEntityManagerFactory("SistemaPU");
			manager = factory.createEntityManager();
			
			Usuario empresa = manager.find(Usuario.class, 1L);
			System.out.println(empresa.getNome());
			

//			EntityTransaction trx = manager.getTransaction();
//			trx.begin();
//			Usuario usuario = manager.find(Usuario.class, 1L);
//			List<String> lst = new ArrayList<>();
//			lst.add("VENDA");
//			lst.add("CAIXA");
//			condicao = "delete from " + schema + ".usuario_rotinas where usuario_id = " + usuario.getId();
//			query = manager.createNativeQuery(condicao);
//			query.executeUpdate();
//
//			for (String str : lst) {
//				condicao = "insert into " + schema + ".usuario_rotinas values(" + usuario.getId() + ", '" + str + "') ";
//				query = manager.createNativeQuery(condicao);
//				query.executeUpdate();
//			}
//			trx.commit();
//			System.out.println("ok");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
