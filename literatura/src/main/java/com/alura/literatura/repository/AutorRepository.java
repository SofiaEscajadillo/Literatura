package com.alura.literatura.repository;

import com.alura.literatura.model.Autor;
import com.alura.literatura.model.Libro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AutorRepository extends JpaRepository<Autor,Long>{
    // Para buscar autores vivos determinado año
    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento <= :fecha AND a.fechaDefuncion >= :fecha")
    List<Autor> autorVivoEnDeterminadoAnio (@Param("fecha") Integer fecha);
    // Para buscar un autor
    @Query("SELECT a FROM Autor a WHERE UPPER(a.nombre) LIKE UPPER (:nombre)")
    Optional<Autor> buscarAutor(@Param("nombre") String nombre);
}
