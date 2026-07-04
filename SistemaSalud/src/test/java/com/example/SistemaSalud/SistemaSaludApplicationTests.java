package com.example.SistemaSalud;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.RedirectView;

import com.example.SistemaSalud.config.AuthInterceptor;
import com.example.SistemaSalud.controller.AuthController;
import com.example.SistemaSalud.controller.CitaController;
import com.example.SistemaSalud.controller.GlobalModelAdvice;
import com.example.SistemaSalud.controller.HistoriaController;
import com.example.SistemaSalud.controller.HomeController;
import com.example.SistemaSalud.controller.KioscoController;
import com.example.SistemaSalud.controller.MedicoController;
import com.example.SistemaSalud.controller.PacienteController;
import com.example.SistemaSalud.controller.ReporteController;
import com.example.SistemaSalud.controller.SeguridadController;
import com.example.SistemaSalud.dao.CitaDao;
import com.example.SistemaSalud.dao.HistoriaClinicaDao;
import com.example.SistemaSalud.dao.InMemoryCitaDao;
import com.example.SistemaSalud.dao.InMemoryHistoriaClinicaDao;
import com.example.SistemaSalud.dao.InMemoryKioscoDao;
import com.example.SistemaSalud.dao.InMemoryMedicoDao;
import com.example.SistemaSalud.dao.InMemoryPacienteDao;
import com.example.SistemaSalud.dao.InMemoryReporteDao;
import com.example.SistemaSalud.dao.InMemorySeguridadDao;
import com.example.SistemaSalud.dao.KioscoDao;
import com.example.SistemaSalud.dao.MedicoDao;
import com.example.SistemaSalud.dao.PacienteDao;
import com.example.SistemaSalud.dao.ReporteDao;
import com.example.SistemaSalud.dao.SeguridadDao;
import com.example.SistemaSalud.model.UsuarioSesion;
import com.example.SistemaSalud.service.CitaService;
import com.example.SistemaSalud.service.DashboardService;
import com.example.SistemaSalud.service.HistoriaClinicaService;
import com.example.SistemaSalud.service.KioscoService;
import com.example.SistemaSalud.service.MedicoService;
import com.example.SistemaSalud.service.PacienteService;
import com.example.SistemaSalud.service.ReporteService;
import com.example.SistemaSalud.service.SeguridadService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class SistemaSaludApplicationTests {

	private static final UsuarioSesion USUARIO_SESION = new UsuarioSesion(
			"Administrador HIS", "Administrador", "AD", "Sesion activa", "admin");

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		MedicoDao medicoDao = new InMemoryMedicoDao();
		CitaDao citaDao = new InMemoryCitaDao();
		PacienteDao pacienteDao = new InMemoryPacienteDao();
		HistoriaClinicaDao historiaClinicaDao = new InMemoryHistoriaClinicaDao();
		KioscoDao kioscoDao = new InMemoryKioscoDao();
		ReporteDao reporteDao = new InMemoryReporteDao();
		SeguridadDao seguridadDao = new InMemorySeguridadDao();

		MedicoService medicoService = new MedicoService(medicoDao, citaDao);
		CitaService citaService = new CitaService(citaDao, medicoDao, pacienteDao);
		HistoriaClinicaService historiaClinicaService = new HistoriaClinicaService(pacienteDao, historiaClinicaDao);
		KioscoService kioscoService = new KioscoService(kioscoDao, citaService);
		SeguridadService seguridadService = new SeguridadService(seguridadDao);
		PacienteService pacienteService = new PacienteService(pacienteDao, citaDao);
		DashboardService dashboardService = new DashboardService(medicoService, citaService, historiaClinicaService);
		ReporteService reporteService = new ReporteService(reporteDao, citaService, historiaClinicaService);

		mockMvc = MockMvcBuilders.standaloneSetup(
				new HomeController(dashboardService),
				new AuthController(seguridadService),
				new MedicoController(medicoService),
				new PacienteController(pacienteService, seguridadService),
				new CitaController(citaService, seguridadService),
				new HistoriaController(historiaClinicaService, seguridadService),
				new KioscoController(kioscoService),
				new ReporteController(reporteService),
				new SeguridadController(seguridadService))
				.setControllerAdvice(new GlobalModelAdvice(seguridadService))
				.addInterceptors(new AuthInterceptor())
				.setViewResolvers((viewName, locale) -> {
					if (viewName.startsWith("redirect:")) {
						return new RedirectView(viewName.substring("redirect:".length()), true);
					}
					return new AbstractView() {
						@Override
						protected void renderMergedOutputModel(
								Map<String, Object> model,
								HttpServletRequest request,
								HttpServletResponse response) {
							response.setStatus(HttpServletResponse.SC_OK);
						}
					};
				})
				.build();
	}

	@Test
	void unauthenticatedHomeRedirectsToLogin() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void authenticatedHomeShowsDashboard() throws Exception {
		mockMvc.perform(get("/").sessionAttr("usuarioSesion", USUARIO_SESION))
				.andExpect(status().isOk())
				.andExpect(view().name("index"));
	}

	@Test
	void publicAuthInterfacesAreAvailable() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));
	
		mockMvc.perform(get("/registro"))
				.andExpect(status().isOk())
				.andExpect(view().name("registro"));
	}

	@Test
	void protectedInterfacesRequireSession() throws Exception {
		mockMvc.perform(get("/pacientes"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void authenticatedSeparatedInterfacesAreAvailable() throws Exception {
		mockMvc.perform(get("/medicos").sessionAttr("usuarioSesion", USUARIO_SESION))
				.andExpect(status().isOk())
				.andExpect(view().name("medicos"));

		mockMvc.perform(get("/pacientes").sessionAttr("usuarioSesion", USUARIO_SESION))
				.andExpect(status().isOk())
				.andExpect(view().name("pacientes"));

		mockMvc.perform(get("/citas").sessionAttr("usuarioSesion", USUARIO_SESION))
				.andExpect(status().isOk())
				.andExpect(view().name("citas"));

		mockMvc.perform(get("/historias").sessionAttr("usuarioSesion", USUARIO_SESION))
				.andExpect(status().isOk())
				.andExpect(view().name("historias"));

		mockMvc.perform(get("/kiosco").sessionAttr("usuarioSesion", USUARIO_SESION))
				.andExpect(status().isOk())
				.andExpect(view().name("kiosco"));

		mockMvc.perform(get("/reportes").sessionAttr("usuarioSesion", USUARIO_SESION))
				.andExpect(status().isOk())
				.andExpect(view().name("reportes"));

		mockMvc.perform(get("/seguridad").sessionAttr("usuarioSesion", USUARIO_SESION))
				.andExpect(status().isOk())
				.andExpect(view().name("seguridad"));
	}

	@Test
	void appointmentFormRedirectsAndShowsSuccessSignal() throws Exception {
		mockMvc.perform(post("/citas")
					.sessionAttr("usuarioSesion", USUARIO_SESION)
					.param("nombre", "Ana")
					.param("dni", "12345678")
					.param("especialidad", "Medicina General")
					.param("fecha", "2026-07-10")
					.param("prioridad", "Normal")
					.param("mensaje", "Primera consulta"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("/citas?success=true&codigo=C-*"));
	}

	@Test
	void registrationFormRedirectsToLoginWhenValid() throws Exception {
		mockMvc.perform(post("/registro")
					.param("nombre", "Nuevo Usuario")
					.param("username", "nuevo_usuario")
					.param("rol", "Paciente")
					.param("password", "clave123")
					.param("confirmPassword", "clave123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("/login?success=*"));
	}
}
