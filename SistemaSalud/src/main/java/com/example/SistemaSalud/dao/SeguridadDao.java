package com.example.SistemaSalud.dao;

import java.util.List;
import java.util.Optional;

import com.example.SistemaSalud.entity.AlertaEntity;
import com.example.SistemaSalud.entity.AuditoriaEntity;
import com.example.SistemaSalud.entity.RolEntity;
import com.example.SistemaSalud.entity.UsuarioEntity;

public interface SeguridadDao {

    UsuarioEntity getUsuarioSesion();

    Optional<UsuarioEntity> findUsuarioByCredentials(String username, String password);

    List<UsuarioEntity> findUsuarios();

    List<AlertaEntity> findAlertas();

    List<RolEntity> findRoles();

    List<AuditoriaEntity> findAuditoriasRecientes();

    AuditoriaEntity saveAuditoria(AuditoriaEntity auditoria);
}
