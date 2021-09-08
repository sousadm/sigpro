package sigpro;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.poi.util.SystemOutLogger;

import com.sousa.sigpro.model.clinica.Medicamento;
import com.sousa.sigpro.util.jpa.ConectTabela;

public class TesteGeral {

	public static void main(String[] args) {

		double valor = 12.356;
		System.out.println(String.valueOf(valor));

		// EntityManager managerTabela = null;
		// try {
		// ConectTabela con = new ConectTabela();
		// managerTabela = con.createEntityManager();
		// List<Medicamento> lst = managerTabela.createQuery("from Medicamento",
		// Medicamento.class).getResultList();
		// for (Medicamento med : lst) {
		// System.out.println(med.getCodigo());
		// }
		// } catch (Exception e) {
		// System.out.println(e.getMessage());
		// } finally {
		// managerTabela.close();
		// }

		// try {
		// EntityManagerFactory factory =
		// Persistence.createEntityManagerFactory("SistemaPU");
		// EntityManager manager = factory.createEntityManager();
		//
		// Pessoa paciente = manager.find(Pessoa.class, 1L);
		//
		// Date horaInicial = null;
		// Date horaIntervalo = null;
		// Date horaEncerra = null;
		// Date agora = SuporteData.somenteHora(new Date());
		// Date atual = SuporteData.somenteHora(new Date());
		//
		// boolean mesmo_dia = SuporteData.somenteData(new
		// Date()).equals(SuporteData.somenteData(new Date()));
		//
		// TipoDiaSemana diaSemana = SuporteData.diaDaSemana(atual);
		// System.out.println(diaSemana.getDescricao());
		//
		// List<ClinicaAgenda> lstAgenda = new ArrayList<>();
		//
		// Colaborador consultor = manager.find(Colaborador.class, 1L);
		// if (consultor.getTempo() == null)
		// throw new Exception("tempo de atendimento indefinido");
		//
		// int tempo = SuporteData.tempo(consultor.getTempo());
		//
		// String condicao = "select h from ClinicaHorario h where h.consultor.id = " +
		// consultor.getId();
		// List<ClinicaHorario> lst = manager.createQuery(condicao,
		// ClinicaHorario.class).getResultList();
		// for (ClinicaHorario horario : lst) {
		//
		// horaInicial = SuporteData.somenteHora(horario.getInicio());
		// horaIntervalo =
		// SuporteData.somenteHora(SuporteData.horaAddMinuto(horario.getIntervalo(),
		// -tempo + 1));
		// horaEncerra =
		// SuporteData.somenteHora(SuporteData.horaAddMinuto(horario.getEncerramento(),
		// -tempo + 1));
		//
		// for (TipoDiaSemana ds : horario.getDias()) {
		// if (ds == diaSemana) {
		//
		// while (!mesmo_dia || horaInicial.before(horario.getEncerramento())) {
		// if ((!mesmo_dia || agora.before(horaInicial)) &&
		// horario.getInicio().before(horaInicial)) {
		// if (horario.getIntervalo() == null || !mesmo_dia ||
		// horaIntervalo.before(horaInicial)) {
		// if (horario.getReinicio() == null || !mesmo_dia
		// || horario.getReinicio().before(horaInicial)) {
		// if (horaInicial.before(horaEncerra)) {
		// ClinicaAgenda agenda = new ClinicaAgenda();
		// agenda.setConsultor(consultor);
		// agenda.setDataMarcada(horaInicial);
		// lstAgenda.add(agenda);
		// }
		// }
		// }
		// }
		//
		// horaInicial = SuporteData.horaAddMinuto(horaInicial, tempo);
		//
		// }
		// }
		// }
		// }
		//
		// for (ClinicaAgenda agenda : lstAgenda)
		// System.out.println("Disponível: " +
		// SuporteData.formataHoraToStr(agenda.getDataMarcada()));

		// Map<String, Object> pm = factory.getProperties();
		// for (Map.Entry<String, Object> e : pm.entrySet()) {
		// System.out.println(e.getKey() + " - " + e.getValue());
		// }

		// Pessoa empresa = manager.find(Pessoa.class, 1L);
		// System.out.println(empresa.getNome());
		// } catch (
		// Exception e) {
		// System.out.println(e.getMessage());
		// }
		//

	}

}