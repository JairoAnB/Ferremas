package com.proyects.ferremasusers.dto;

import com.proyects.ferremasusers.model.RolUsuario;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsuarioDto {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private RolUsuario rol;

}
