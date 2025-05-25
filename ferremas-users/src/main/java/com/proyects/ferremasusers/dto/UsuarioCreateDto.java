package com.proyects.ferremasusers.dto;

import com.proyects.ferremasusers.model.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsuarioCreateDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;
    @NotBlank(message = "El apellido no puede estar vacío")
    private String apellido;
    @NotBlank(message = "El email no puede estar vacío")
    private String email;
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
    @NotNull(message = "El rol no puede estar vacío, debe ser ADMINISTRADOR, USUARIO o CLIENTE")
    private RolUsuario rol;

}