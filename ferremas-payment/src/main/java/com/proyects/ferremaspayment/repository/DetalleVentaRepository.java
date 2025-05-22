package com.proyects.ferremaspayment.repository;

import com.proyects.ferremaspayment.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
}
