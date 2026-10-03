package com.universidad.catalogo.repository;

import com.universidad.catalogo.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Consulta derivada: buscar por nombre (para validar duplicados)
    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    // Consulta derivada: búsqueda parcial por nombre
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}
