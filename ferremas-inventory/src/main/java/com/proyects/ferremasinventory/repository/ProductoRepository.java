package com.proyects.ferremasinventory.repository;

import com.proyects.ferremasinventory.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

}
