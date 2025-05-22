package com.proyects.ferremaspayment.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long usuarioId;

    private String buyOrder;

    @Column(name = "detalle_venta")
    @OneToMany(mappedBy = "venta", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<DetalleVenta> detalle = new ArrayList<>();

    private int total;

    @Column(name = "metodo_pago")
    private String metodoPago;

    private Estado estado;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta;

    private String moneda;
}
