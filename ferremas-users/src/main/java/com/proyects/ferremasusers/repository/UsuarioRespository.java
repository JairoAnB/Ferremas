package com.proyects.ferremasusers.repository;


import com.proyects.ferremasusers.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRespository extends JpaRepository<Usuario, Long> {
}
