package com.proyects.ferremasusers.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String nombre;
    private String apellido;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING) // <-- Esto es importante para lograr que se guarde el rol como texto en la base de datos
    private RolUsuario rol;

}
