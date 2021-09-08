package sigpro;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class TesteNewSchema {

	static String sql = "SELECT clone_schema('_padrao_','bureau')";
	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) {

		try {

			factory = Persistence.createEntityManagerFactory("SistemaPU");
			manager = factory.createEntityManager();
			EntityTransaction trx = manager.getTransaction();
			trx.begin();

			manager.createQuery(sql).executeUpdate();
			trx.commit();

//			Class.forName("org.postgresql.Driver");
//			Connection conexao = DriverManager.getConnection("jdbc:postgresql://localhot:5432", "postgres", "contrate");
//			Statement stmnt = conexao.createStatement();
//			System.out.println(stmnt.executeUpdate(sql));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}