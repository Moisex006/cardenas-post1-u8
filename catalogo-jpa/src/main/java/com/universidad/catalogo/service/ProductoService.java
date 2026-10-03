package com.universidad.catalogo.service;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.model.Categoria;
import com.universidad.catalogo.repository.ProductoRepository;
import com.universidad.catalogo.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepo;
    private final CategoriaRepository categoriaRepo;

    public ProductoService(ProductoRepository productoRepo, CategoriaRepository categoriaRepo) {
        this.productoRepo = productoRepo;
        this.categoriaRepo = categoriaRepo;
    }

    public List<Producto> listarTodos() {
        return productoRepo.findAllConCategoria();
    }

    public Producto buscarPorId(Long id) {
        return productoRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
    }

    @Transactional
    public Producto guardar(Producto producto, Long categoriaId) {
        Categoria categoria = categoriaRepo.findById(categoriaId)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + categoriaId));
        producto.asignarCategoria(categoria);
        return productoRepo.save(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        productoRepo.deleteById(id);
    }

    public List<Producto> listarPorCategoriaConPrecioMayorA(Long categoriaId, BigDecimal precioMinimo) {
        return productoRepo.buscarPorCategoriaConPrecioMayorA(categoriaId, precioMinimo);
    }
}
