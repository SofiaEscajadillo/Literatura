/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.alura.literatura.repository;

import com.alura.literatura.model.Autor;
import com.alura.literatura.model.Idioma;
import com.alura.literatura.model.Libro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LibroRepository extends JpaRepository<Libro, Long>{
    // Para buscar libro
    Optional<Libro> findByNombreContainsIgnoreCase(String nombre);
    // Para buscar libros que tengan determinado autor
    @Query("SELECT l FROM Libro WHERE l.autor = :autor")
    List<Libro> buscarLibrosPorAutor(@Param("autor") Autor autor);
    @Query("SELECT l FROM Libro l WHERE l.idioma = :idioma")
    List<Libro> buscarLibrosPorIdioma(@Param("idioma") Idioma idioma);
}
