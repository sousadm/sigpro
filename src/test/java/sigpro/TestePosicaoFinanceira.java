package sigpro;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.sousa.sigpro.model.tipo.TipoTituloOrigem;

public class TestePosicaoFinanceira {

	static EntityManagerFactory factory;
	static EntityManager manager;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		
		factory = Persistence.createEntityManagerFactory("SistemaPU");
		manager = factory.createEntityManager();

		Map<Integer, Double> mapa = new TreeMap<>(); 

		String empresa = "TESTE";

		StringBuilder condicao = new StringBuilder();
		condicao.append("SELECT dia, sum(valor) FROM ( 							");

		condicao.append("	SELECT cast(EXTRACT(DAY FROM t.dataBaixa) as integer) dia, v.valor ");
		condicao.append("	FROM " + empresa + ".caixavalor v								");
		condicao.append("	INNER JOIN " + empresa + ".titulo t ON t.id = v.titulo_id 	");
		condicao.append("	WHERE t.datacancelamento is null 					");
		condicao.append("		and t.contabancaria_id is null					");
		condicao.append("		and t.tipoDC = :tipo 							");
		condicao.append("		and EXTRACT(MONTH FROM t.dataBaixa) = :mes 		");
		condicao.append("		and EXTRACT(YEAR FROM t.dataBaixa) = :ano 		");

		condicao.append("	UNION ");
		condicao.append("	SELECT cast(EXTRACT(DAY FROM c.encerramento) as integer) dia, v.valor ");
		condicao.append("	FROM " + empresa + ".caixavalor v								");
		condicao.append("	INNER JOIN " + empresa + ".caixa c ON v.caixa_id = c.id 		");
		condicao.append("	WHERE c.tipoDC = :tipo 								");
		condicao.append("		and EXTRACT(MONTH FROM c.encerramento) = :mes 	");
		condicao.append("		and EXTRACT(YEAR FROM c.encerramento) = :ano 	");

		condicao.append("	UNION 												");
		condicao.append("	SELECT cast(EXTRACT(DAY FROM t.dataBaixa) as integer) dia, t.valor ");
		condicao.append("	FROM " + empresa + ".titulo t 								");
		condicao.append("	WHERE t.datacancelamento is null 					");
		condicao.append("		and t.contabancaria_id is not null				");
		condicao.append("		and t.tipoDC = :tipo 							");
		condicao.append("		and EXTRACT(MONTH FROM t.dataBaixa) = :mes 		");
		condicao.append("		and EXTRACT(YEAR FROM t.dataBaixa) = :ano 		");
		condicao.append("	) t GROUP BY 1 ORDER BY 1							");

		try {

			Query query = manager.createNativeQuery(condicao.toString()).setParameter("ano", 2020)
					.setParameter("mes", 5).setParameter("tipo", TipoTituloOrigem.RECEBER.name());

			List<Object[]> lstObj = (List<Object[]>) query.getResultList();
			for (Object[] obj : lstObj) {
				if (obj != null) {
					mapa.put((Integer) obj[0], (Double) obj[1]);
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}