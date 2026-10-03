package com.universidad.catalogo.controller;

import com.universidad.catalogo.model.Categoria;
import com.universidad.catalogo.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", service.listarTodas());
        return "categorias/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("titulo", "Nueva Categoría");
        return "categorias/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Categoria categoria,
                          BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                service.guardar(categoria);
                return "redirect:/categorias";
            } catch (IllegalStateException e) {
                // Nombre duplicado: se muestra junto al campo en lugar de una página de error
                result.rejectValue("nombre", "duplicado", e.getMessage());
            }
        }
        model.addAttribute("titulo", categoria.getId() == null
            ? "Nueva Categoría" : "Editar Categoría");
        return "categorias/formulario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", service.buscarPorId(id));
        model.addAttribute("titulo", "Editar Categoría");
        return "categorias/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String confirmarEliminar(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", service.buscarPorId(id));
        return "categorias/confirmar-eliminar";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Model model) {
        try {
            service.eliminar(id);
            return "redirect:/categorias";
        } catch (IllegalStateException e) {
            // Categoría con productos asociados: se muestra el motivo del rechazo
            model.addAttribute("categoria", service.buscarPorId(id));
            model.addAttribute("error", e.getMessage());
            return "categorias/confirmar-eliminar";
        }
    }
}
