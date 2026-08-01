package com.sistemagestioncitas.hospital.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sistemagestioncitas.hospital.model.Cita;
import com.sistemagestioncitas.hospital.model.EspacioCita;
import com.sistemagestioncitas.hospital.model.Medico;
import com.sistemagestioncitas.hospital.model.Usuario;
import com.sistemagestioncitas.hospital.repository.UsuarioRepository;
import com.sistemagestioncitas.hospital.repository.MedicoRepository;
import com.sistemagestioncitas.hospital.repository.EspacioCitaRepository;
import com.sistemagestioncitas.hospital.repository.CitaRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;
    private final EspacioCitaRepository espacioCitaRepository;
    private final CitaRepository citaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, MedicoRepository medicoRepository,
            EspacioCitaRepository espacioCitaRepository, CitaRepository citaRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
        this.espacioCitaRepository = espacioCitaRepository;
        this.citaRepository = citaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByCorreo("step2299@gmail.com")) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setCorreo("step2299@gmail.com");
            admin.setCedula("117580750");
            admin.setContacto("8782-5958");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            admin.setActivo(true);
            usuarioRepository.save(admin);
            System.out.println("USUARIO ADMIN CREADO: step2299@gmail.com / admin123");
        }
        // ==========================================
        // 2. CREAR USUARIO PACIENTE DE PRUEBA
        // ==========================================
        if (!usuarioRepository.existsByCorreo("paciente@prueba.com")) {
            Usuario paciente = new Usuario();
            paciente.setNombre("Juan Pérez (Paciente)");
            paciente.setCorreo("paciente@prueba.com");
            paciente.setCedula("100000001");
            paciente.setContacto("8888-8888");
            paciente.setPassword(passwordEncoder.encode("user123"));
            paciente.setRol("USUARIO");
            paciente.setActivo(true);
            usuarioRepository.save(paciente);
            System.out.println("✅ USUARIO PACIENTE CREADO: paciente@prueba.com / user123");
        }

        // ==========================================
        // 3. CREAR MÉDICO DE PRUEBA
        // ==========================================
        if (medicoRepository.count() == 0) {
            Medico medico = new Medico();
            medico.setNombre("Dr. Roberto Gómez");
            medico.setEspecialidad("Cardiología");
            medicoRepository.save(medico);
            System.out.println("✅ MÉDICO CREADO: Dr. Roberto Gómez (Cardiología)");
        }

        // ==========================================
        // 4. CREAR DATOS HISTÓRICOS (FECHA PASADA)
        // ==========================================
        Usuario paciente = usuarioRepository.findByCorreo("paciente@prueba.com").orElse(null);
        Medico medico = medicoRepository.findAll().stream()
                .filter(m -> m.getNombre().equals("Dr. Roberto Gómez"))
                .findFirst().orElse(null);

        if (paciente != null && medico != null) {
            // Fecha de hace 5 días
            LocalDate fechaPasada = LocalDate.now().minusDays(5);
            LocalTime horaInicio = LocalTime.of(10, 0);
            LocalTime horaFin = LocalTime.of(11, 0);

            // Verificar si ya existe este espacio para no duplicar
            boolean existeEspacio = espacioCitaRepository.findAll().stream()
                    .anyMatch(e -> e.getMedico().getId().equals(medico.getId()) &&
                            e.getFecha().equals(fechaPasada) &&
                            e.getHoraInicio().equals(horaInicio));

            if (!existeEspacio) {
                // A. Crear Espacio de Cita en el pasado
                // (Se guarda directo en el Repository para saltar la validación del Service)
                EspacioCita espacio = new EspacioCita();
                espacio.setMedico(medico);
                espacio.setFecha(fechaPasada);
                espacio.setHoraInicio(horaInicio);
                espacio.setHoraFin(horaFin);
                espacio.setOcupado(true);
                espacio = espacioCitaRepository.save(espacio);
                System.out.println("✅ ESPACIO HISTÓRICO CREADO: " + fechaPasada + " 10:00 - 11:00");

                // B. Crear Cita Agendada en esa fecha pasada
                Cita cita = new Cita();
                cita.setUsuario(paciente);
                cita.setMedico(medico);
                cita.setEspacio(espacio);
                cita.setFechaHora(LocalDateTime.of(fechaPasada, horaInicio));
                cita.setEstado("CONFIRMADA"); // O "CANCELADA" para probar el historial

                // Fechas de auditoría también en el pasado para realismo
                cita.setFechaCreacion(LocalDateTime.now().minusDays(10));
                cita.setFechaConfirmacion(LocalDateTime.of(fechaPasada, horaInicio.minusHours(1)));

                citaRepository.save(cita);
                System.out.println("✅ CITA HISTÓRICA CREADA para el paciente en fecha pasada.");
            }
        }
    }
}
