package com.universidad.catalogo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true: el catálogo no admite dos categorías con el mismo nombre
    // (también se valida antes en CategoriaService para dar un mensaje legible)
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    @Column(name = "nombre", nullable = false, unique = true, length = 80)
    private String nombre;

    @Size(max = 250, message = "La descripción no puede superar los 250 caracteres")
    @Column(name = "descripcion", length = 250)
    private String descripcion;

    // Lado inverso de la relación: mappedBy indica que la clave foránea la
    // controla Producto.categoria. Sin cascade = REMOVE a propósito: borrar
    // una categoría no debe borrar sus productos (ver CategoriaService.eliminar).
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Producto> productos = new ArrayList<>();

    // Constructor vacío requerido por JPA
    public Categoria() {}

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // Sin setter: la lista se gestiona desde el lado propietario (Producto.asignarCategoria)
    public List<Producto> getProductos() { return productos; }
}
