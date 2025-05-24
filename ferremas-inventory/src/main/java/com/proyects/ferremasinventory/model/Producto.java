package com.proyects.ferremasinventory.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // Cambiar a IDENTITY si no funciona
    private Long id;

    private Long categoriaId;

    private String codigo;
    private String nombre;
    private String descripcion;
    private String marca;
    private int stock;

    @Column(name = "stock_bodega")
    private int stockBodega;
    private int precio;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

}
