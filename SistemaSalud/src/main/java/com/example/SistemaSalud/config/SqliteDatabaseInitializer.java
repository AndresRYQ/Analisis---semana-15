package com.example.SistemaSalud.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SqliteDatabaseInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public SqliteDatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        createTables();
        migrateTables();
        seedUsuarios();
        seedAlertas();
        seedRoles();
        seedAuditorias();
        seedMedicos();
        seedPacientes();
        seedSlots();
        seedCitas();
        seedHistorias();
        seedDocumentos();
        seedTurnos();
        seedRequerimientos();
    }

    private void createTables() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE,
                    password TEXT,
                    nombre TEXT NOT NULL,
                    rol TEXT NOT NULL,
                    iniciales TEXT NOT NULL,
                    estado TEXT NOT NULL,
                    activo INTEGER NOT NULL DEFAULT 1
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS alertas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo TEXT NOT NULL,
                    detalle TEXT NOT NULL,
                    prioridad TEXT NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS roles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE,
                    descripcion TEXT NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS auditorias (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fecha_hora TEXT NOT NULL,
                    usuario TEXT NOT NULL,
                    rol TEXT NOT NULL,
                    accion TEXT NOT NULL,
                    resultado TEXT NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS medicos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    especialidad TEXT NOT NULL,
                    disponibilidad TEXT NOT NULL,
                    ubicacion TEXT NOT NULL,
                    detalle TEXT NOT NULL,
                    disponible_hoy INTEGER NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS pacientes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    dni TEXT NOT NULL UNIQUE,
                    edad INTEGER NOT NULL,
                    tipo_seguro TEXT NOT NULL,
                    riesgo TEXT NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS citas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    paciente_id INTEGER,
                    medico_id INTEGER,
                    codigo TEXT NOT NULL UNIQUE,
                    paciente TEXT NOT NULL,
                    dni TEXT NOT NULL,
                    especialidad TEXT NOT NULL,
                    fecha_programada TEXT NOT NULL,
                    fecha_etiqueta TEXT NOT NULL,
                    hora_programada TEXT NOT NULL,
                    estado TEXT NOT NULL,
                    canal TEXT NOT NULL,
                    prioridad TEXT NOT NULL,
                    motivo TEXT NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS slots (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    hora TEXT NOT NULL,
                    especialidad TEXT NOT NULL,
                    estado TEXT NOT NULL,
                    disponible INTEGER NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS historias_eventos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    paciente_id INTEGER,
                    medico_id INTEGER,
                    paciente_dni TEXT NOT NULL,
                    fecha TEXT NOT NULL,
                    fecha_etiqueta TEXT NOT NULL,
                    titulo TEXT NOT NULL,
                    descripcion TEXT NOT NULL,
                    profesional TEXT NOT NULL,
                    estado TEXT NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS documentos_clinicos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    paciente_id INTEGER,
                    codigo TEXT NOT NULL UNIQUE,
                    paciente_dni TEXT NOT NULL,
                    nombre TEXT NOT NULL,
                    estado TEXT NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS turnos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    dni TEXT,
                    cita_codigo TEXT,
                    codigo TEXT NOT NULL UNIQUE,
                    especialidad TEXT NOT NULL,
                    ubicacion TEXT NOT NULL,
                    estado TEXT NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS requerimientos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    codigo TEXT NOT NULL UNIQUE,
                    prioridad TEXT NOT NULL,
                    descripcion TEXT NOT NULL
                )
                """);
    }

    private void migrateTables() {
        ensureColumn("usuarios", "username", "TEXT");
        ensureColumn("usuarios", "password", "TEXT");
        ensureColumn("usuarios", "activo", "INTEGER NOT NULL DEFAULT 1");
        ensureColumn("citas", "paciente_id", "INTEGER");
        ensureColumn("citas", "medico_id", "INTEGER");
        ensureColumn("historias_eventos", "paciente_id", "INTEGER");
        ensureColumn("historias_eventos", "medico_id", "INTEGER");
        ensureColumn("documentos_clinicos", "paciente_id", "INTEGER");
        ensureColumn("turnos", "dni", "TEXT");
        ensureColumn("turnos", "cita_codigo", "TEXT");

        jdbcTemplate.update("""
                UPDATE citas
                SET paciente_id = (SELECT id FROM pacientes WHERE pacientes.dni = citas.dni)
                WHERE paciente_id IS NULL
                """);
        jdbcTemplate.update("""
                UPDATE citas
                SET medico_id = (
                    SELECT id FROM medicos
                    WHERE lower(medicos.especialidad) = lower(citas.especialidad)
                    ORDER BY disponible_hoy DESC, id
                    LIMIT 1
                )
                WHERE medico_id IS NULL
                """);
        jdbcTemplate.update("""
                UPDATE historias_eventos
                SET paciente_id = (SELECT id FROM pacientes WHERE pacientes.dni = historias_eventos.paciente_dni)
                WHERE paciente_id IS NULL
                """);
        jdbcTemplate.update("""
                UPDATE documentos_clinicos
                SET paciente_id = (SELECT id FROM pacientes WHERE pacientes.dni = documentos_clinicos.paciente_dni)
                WHERE paciente_id IS NULL
                """);
    }

    private void seedUsuarios() {
        if (isEmpty("usuarios")) {
            insertUsuario("admin", "admin123", "Administrador HIS", "Administrador", "AD", "Sesion activa");
            insertUsuario("medico", "medico123", "Dra. Sofia Ramirez", "Medico", "SR", "Sesion activa");
            insertUsuario("paciente", "paciente123", "Maria Torres Huaman", "Paciente", "MT", "Asegurada activa");
            return;
        }
        jdbcTemplate.update("""
                UPDATE usuarios
                SET username = COALESCE(username, 'paciente'),
                    password = COALESCE(password, 'paciente123'),
                    activo = COALESCE(activo, 1)
                WHERE id = (SELECT MIN(id) FROM usuarios)
                """);
        ensureUsuario("admin", "admin123", "Administrador HIS", "Administrador", "AD", "Sesion activa");
        ensureUsuario("medico", "medico123", "Dra. Sofia Ramirez", "Medico", "SR", "Sesion activa");
        ensureUsuario("paciente", "paciente123", "Maria Torres Huaman", "Paciente", "MT", "Asegurada activa");
    }

    private void seedAlertas() {
        if (!isEmpty("alertas")) {
            return;
        }
        jdbcTemplate.batchUpdate("INSERT INTO alertas(titulo, detalle, prioridad) VALUES (?, ?, ?)", java.util.List.of(
                new Object[] { "Cita por confirmar", "Cardiologia - 06 Jul", "Media" },
                new Object[] { "Historia actualizada", "Nuevo diagnostico agregado", "Alta" }));
    }

    private void seedRoles() {
        if (!isEmpty("roles")) {
            return;
        }
        jdbcTemplate.batchUpdate("INSERT INTO roles(nombre, descripcion) VALUES (?, ?)", java.util.List.of(
                new Object[] { "Paciente", "Consulta citas, confirma turnos y revisa informacion permitida." },
                new Object[] { "Medico", "Consulta y actualiza historias clinicas de pacientes atendidos." },
                new Object[] { "Administrativo", "Gestiona agendas, disponibilidad y registros de citas." },
                new Object[] { "Administrador", "Controla usuarios, permisos, auditorias y configuracion." }));
    }

    private void seedAuditorias() {
        if (!isEmpty("auditorias")) {
            return;
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO auditorias(fecha_hora, usuario, rol, accion, resultado) VALUES (?, ?, ?, ?, ?)",
                java.util.List.of(
                        new Object[] { "2026-07-04T08:12:00", "Medico", "Medico",
                                "Apertura de historia clinica", "Autorizado" },
                        new Object[] { "2026-07-04T09:35:00", "Administrativo", "Administrativo",
                                "Reprogramacion de cita", "Autorizado" },
                        new Object[] { "2026-07-04T10:20:00", "Paciente", "Paciente",
                                "Consulta de proxima cita", "Autorizado" },
                        new Object[] { "2026-07-04T11:05:00", "Usuario externo", "Invitado",
                                "Intento de acceso a historia", "Bloqueado" }));
    }

    private void seedMedicos() {
        if (!isEmpty("medicos")) {
            return;
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO medicos(nombre, especialidad, disponibilidad, ubicacion, detalle, disponible_hoy) VALUES (?, ?, ?, ?, ?, ?)",
                java.util.List.of(
                        new Object[] { "Dra. Sofia Ramirez", "Medicina General", "Disponible hoy", "Teleconsulta",
                                "14 pacientes atendidos", 1 },
                        new Object[] { "Dr. Andres Molina", "Cardiologia", "Alta demanda", "Hospital Rebagliati",
                                "Agenda hasta 18:00", 1 },
                        new Object[] { "Dra. Isabel Quispe", "Psicologia", "Disponible", "Virtual",
                                "Seguimiento preventivo", 1 },
                        new Object[] { "Dr. Luis Huaman", "Pediatria", "Disponible manana",
                                "Policlinico Central", "Atencion familiar", 0 },
                        new Object[] { "Dra. Carla Rojas", "Medicina Interna", "Disponible",
                                "Hospital Almenara", "Casos cronicos", 1 },
                        new Object[] { "Dra. Elena Torres", "Odontologia", "Turno tarde",
                                "Centro Odontologico", "Control preventivo", 1 }));
    }

    private void seedPacientes() {
        if (!isEmpty("pacientes")) {
            return;
        }
        jdbcTemplate.batchUpdate("INSERT INTO pacientes(nombre, dni, edad, tipo_seguro, riesgo) VALUES (?, ?, ?, ?, ?)",
                java.util.List.of(
                        new Object[] { "Maria Torres Huaman", "45872163", 32, "Asegurada titular", "Bajo" },
                        new Object[] { "Luis Ramirez Soto", "44556677", 58, "Asegurado titular", "Medio" },
                        new Object[] { "Ana Paredes Leon", "77889900", 8, "Derechohabiente", "Bajo" }));
    }

    private void seedSlots() {
        if (!isEmpty("slots")) {
            return;
        }
        jdbcTemplate.batchUpdate("INSERT INTO slots(hora, especialidad, estado, disponible) VALUES (?, ?, ?, ?)",
                java.util.List.of(
                        new Object[] { "08:30", "Medicina General", "Disponible", 1 },
                        new Object[] { "10:00", "Cardiologia", "Alta demanda", 1 },
                        new Object[] { "12:15", "Pediatria", "Disponible", 1 },
                        new Object[] { "15:40", "Odontologia", "Confirmacion kiosco", 1 },
                        new Object[] { "17:00", "Psicologia", "Disponible", 1 }));
    }

    private void seedCitas() {
        if (!isEmpty("citas")) {
            return;
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO citas(codigo, paciente, dni, especialidad, fecha_programada, fecha_etiqueta, hora_programada, estado, canal, prioridad, motivo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                java.util.List.of(
                        new Object[] { "C-1024", "Maria Torres Huaman", "45872163", "Medicina General",
                                "2026-07-04", "Hoy", "14:30", "Confirmada", "Virtual", "Normal", "" },
                        new Object[] { "C-1025", "Luis Ramirez Soto", "44556677", "Cardiologia", "2026-07-06",
                                "06 Jul", "11:00", "Pendiente", "Presencial", "Paciente cronico",
                                "Control cardiologico" },
                        new Object[] { "C-1026", "Ana Paredes Leon", "77889900", "Pediatria", "2026-07-07",
                                "07 Jul", "09:10", "Confirmada", "Kiosco", "Normal", "Control pediatrico" },
                        new Object[] { "C-1027", "Carlos Medina Flores", "66778899", "Odontologia",
                                "2026-07-09", "09 Jul", "16:20", "Reprogramable", "Presencial", "Normal",
                                "Control preventivo" }));
    }

    private void seedHistorias() {
        if (!isEmpty("historias_eventos")) {
            return;
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO historias_eventos(paciente_dni, fecha, fecha_etiqueta, titulo, descripcion, profesional, estado) VALUES (?, ?, ?, ?, ?, ?, ?)",
                java.util.List.of(
                        new Object[] { "45872163", "2026-07-03", "03 Jul 2026",
                                "Consulta de medicina general",
                                "Control preventivo con indicacion de analisis de sangre.",
                                "Dra. Sofia Ramirez", "Finalizada" },
                        new Object[] { "45872163", "2026-06-18", "18 Jun 2026",
                                "Diagnostico respiratorio",
                                "Registro de sintomas leves y tratamiento ambulatorio.", "Dr. Luis Huaman",
                                "Actualizada" },
                        new Object[] { "45872163", "2026-05-02", "02 May 2026", "Control cardiologico",
                                "Electrocardiograma sin hallazgos de alarma.", "Dr. Andres Molina",
                                "Archivada" },
                        new Object[] { "45872163", "2026-04-10", "10 Abr 2026", "Vacunacion preventiva",
                                "Registro de vacuna y proxima fecha de refuerzo.", "Enfermeria", "Validada" }));
    }

    private void seedDocumentos() {
        if (!isEmpty("documentos_clinicos")) {
            return;
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO documentos_clinicos(codigo, paciente_dni, nombre, estado) VALUES (?, ?, ?, ?)",
                java.util.List.of(
                        new Object[] { "HC-2026-001", "45872163", "Historia clinica digital",
                                "Actualizada hoy" },
                        new Object[] { "LAB-2026-118", "45872163", "Resultados de laboratorio",
                                "Pendiente de revision" },
                        new Object[] { "AUD-2026-040", "45872163", "Auditoria de accesos",
                                "3 accesos autorizados" }));
    }

    private void seedTurnos() {
        if (!isEmpty("turnos")) {
            return;
        }
        jdbcTemplate.batchUpdate("INSERT INTO turnos(codigo, especialidad, ubicacion, estado) VALUES (?, ?, ?, ?)",
                java.util.List.of(
                        new Object[] { "A-021", "Medicina General", "Consultorio 204", "Llamando" },
                        new Object[] { "A-022", "Cardiologia", "Consultorio 118", "En espera" },
                        new Object[] { "A-023", "Pediatria", "Consultorio 310", "Confirmado" }));
    }

    private void seedRequerimientos() {
        if (!isEmpty("requerimientos")) {
            return;
        }
        jdbcTemplate.batchUpdate("INSERT INTO requerimientos(codigo, prioridad, descripcion) VALUES (?, ?, ?)",
                java.util.List.of(
                        new Object[] { "RF02", "Alta", "Programar citas medicas desde web o kiosco digital." },
                        new Object[] { "RF05", "Alta", "Acceso del medico al historial clinico digital." },
                        new Object[] { "RF11", "Alta",
                                "Evitar registros duplicados de pacientes e historias clinicas." },
                        new Object[] { "RF14", "Alta",
                                "Registrar auditorias de acceso a informacion clinica." },
                        new Object[] { "RNF04", "Alta", "Abrir historias clinicas en menos de 3 segundos." },
                        new Object[] { "RNF09", "Media",
                                "Funcionamiento en computadoras, tablets y kioscos." }));
    }

    private boolean isEmpty(String tableName) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
        return count == null || count == 0;
    }

    private void ensureColumn(String tableName, String columnName, String definition) {
        boolean exists = jdbcTemplate.query("PRAGMA table_info(" + tableName + ")",
                (rs, rowNum) -> rs.getString("name")).stream()
                .anyMatch(columnName::equalsIgnoreCase);
        if (!exists) {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition);
        }
    }

    private void ensureUsuario(String username, String password, String nombre, String rol, String iniciales,
            String estado) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuarios WHERE username = ?", Integer.class,
                username);
        if (count == null || count == 0) {
            insertUsuario(username, password, nombre, rol, iniciales, estado);
        }
    }

    private void insertUsuario(String username, String password, String nombre, String rol, String iniciales,
            String estado) {
        jdbcTemplate.update("""
                INSERT INTO usuarios(username, password, nombre, rol, iniciales, estado, activo)
                VALUES (?, ?, ?, ?, ?, ?, 1)
                """, username, password, nombre, rol, iniciales, estado);
    }
}
