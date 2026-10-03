package com.universidad.catalogo.controller;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.service.ProductoService;
import com.universidad.catalogo.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService p, CategoriaService c) {
        this.productoService = p; this.categoriaService = c;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("titulo", "Nuevo Producto");
        return "productos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Long id, Model model) {
        Producto producto = productoService.buscarPorId(id);
        model.addAttribute("producto", producto);
        // getId() sobre el proxy LAZY no dispara consulta: el id ya está en la FK
        model.addAttribute("categoriaId", producto.getCategoria().getId());
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("titulo", "Editar Producto");
        return "productos/formulario";
    }

    // categoriaId llega aparte porque el formulario envía solo el id de la
    // categoría; el servicio carga la entidad y asigna la relación
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Producto producto,
                          BindingResult result,
                          @RequestParam(required = false) Long categoriaId,
                          Model model) {
        if (categoriaId == null) {
            result.reject("categoria", "Debe seleccionar una categoría");
        }
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("categoriaId", categoriaId);
            model.addAttribute("titulo", producto.getId() == null
                ? "Nuevo Producto" : "Editar Producto");
            return "productos/formulario";
        }
        productoService.guardar(producto, categoriaId);
        return "redirect:/productos";
    }

    // POST en lugar de GET: una petición GET no debe modificar datos
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }

    // Endpoint que usa la consulta JPQL personalizada del repositorio
    @GetMapping("/categoria/{categoriaId}/precio-mayor")
    public String porCategoriaConPrecioMayor(@PathVariable Long categoriaId,
                                             @RequestParam BigDecimal minimo,
                                             Model model) {
        model.addAttribute("productos",
            productoService.listarPorCategoriaConPrecioMayorA(categoriaId, minimo));
        model.addAttribute("categoria", categoriaService.buscarPorId(categoriaId));
        model.addAttribute("minimo", minimo);
        return "productos/filtrados";
    }
}
