package com.universidad.catalogo.service;

import com.universidad.catalogo.model.Categoria;
import com.universidad.catalogo.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repo;

    public CategoriaService(CategoriaRepository repo) {
        this.repo = repo;
    }

    public List<Categoria> listarTodas() {
        return repo.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + id));
    }

    // Verificación temprana del nombre único: se rechaza con un mensaje legible
    // antes de que MySQL lance la excepción de restricción UNIQUE
    @Transactional
    public Categoria guardar(Categoria categoria) {
        repo.findByNombreIgnoreCase(categoria.getNombre()).ifPresent(existente -> {
            if (!existente.getId().equals(categoria.getId())) {
                throw new IllegalStateException("Ya existe una categoría con ese nombre.");
            }
        });
        return repo.save(categoria);
    }

    // Sin borrado en cascada: si la categoría tiene productos se rechaza la
    // operación, obligando a reasignarlos o eliminarlos antes
    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);
        if (!categoria.getProductos().isEmpty()) {
            throw new IllegalStateException(
                "No se puede eliminar la categoría: tiene " +
                categoria.getProductos().size() + " producto(s) asociado(s).");
        }
        repo.deleteById(id);
    }
}
