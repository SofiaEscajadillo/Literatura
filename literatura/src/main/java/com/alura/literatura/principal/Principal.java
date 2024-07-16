package com.alura.literatura.principal;

import com.alura.literatura.dto.Datos;
import com.alura.literatura.dto.DatosAutor;
import com.alura.literatura.dto.DatosLibro;
import com.alura.literatura.model.Autor;
import com.alura.literatura.model.Idioma;
import com.alura.literatura.model.Libro;
import com.alura.literatura.repository.AutorRepository;
import com.alura.literatura.repository.LibroRepository;
import com.alura.literatura.service.ConsumoAPI;
import com.alura.literatura.service.ConvierteDatos;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner scan = new Scanner(System.in);
    private AutorRepository repositorioAutor;
    private LibroRepository repositorioLibro;
    private Optional <DatosLibro> libroBuscado;
    private List<Autor> autor;
    private List<Libro> libro;
    private List<Libro> libros;
    private List<Autor> autores;
    private Autor autorApi;

    public Principal(LibroRepository repositorioLibro, AutorRepository repositorioAutor ) {
        this.repositorioLibro = repositorioLibro;
        this.repositorioAutor = repositorioAutor;
    }

    public void muestraElMenu() {
        int opcion;
        do {
            try{
                var menu = """
                    >>>>>>>>>>>>LITERATURA ONE<<<<<<<<<<<<
                    1 - Buscar libro por titulo
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    >>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<
                    Seleccione la opción deseada....
                    """;
                System.out.println(menu);
                opcion = scan.nextInt();
                scan.nextLine();
            }catch (InputMismatchException e){
                opcion = 0;
                scan.nextLine();
            }
            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosPorAnos();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción del menu inválida");
            }

        }while(opcion!=0);
    }

    private void buscarLibroPorTitulo() {
            System.out.println("Escribe el nombre del libro que deseas buscar");
            var nombreLibro = scan.nextLine();
            var json = consumoApi.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "+"));
            var datos = conversor.obtenerDatos(json, Datos.class);
            libroBuscado = datos.libros().stream().findFirst();

            if (libroBuscado.isPresent()) {
                String mensaje = "*****************LIBRO ENCONTRADO*****************" +
                        "\nTitulo: " + libroBuscado.get().titulo() +
                        "\nAutor: " + libroBuscado.get().autor() +
                        "\n ****************************************************";
                System.out.println(mensaje);
                // Se encontro el libro
                List<Libro> libroEncontrado = libroBuscado.stream().map(Libro::new).collect(Collectors.toList());
                try{
                    autorApi = libroBuscado.stream().
                            flatMap(l -> l.autor().stream()
                                    .map(Autor::new))
                            .collect(Collectors.toList()).stream().findFirst().get();
                }catch (NoSuchElementException e){
                    DatosAutor datosAutor = new DatosAutor("Desconocido",0, 0);
                    autorApi = new Autor(datosAutor);
                }

                Optional<Autor> autorBuscado = repositorioAutor.buscarAutor(libroBuscado.get().autor().stream()
                        .map(DatosAutor::nombre)
                        .collect(Collectors.joining()));
                Optional<Libro> libroBuscadoBD = repositorioLibro.findByNombreContainsIgnoreCase(String.valueOf(datos.libros().get(0).titulo()));
                if (libroBuscadoBD.isPresent()) {
                    System.out.println("El libro ya se encuentra en la BD");
                }else {
                    Autor a;
                    if (autorBuscado.isPresent()) {
                        a = autorBuscado.get();
                        System.out.println("El autor ya se encuentra en la BD");
                    }else {
                        a = autorApi;
                        repositorioAutor.save(a);
                    }
                    a.setLibros(libroEncontrado);
                    repositorioAutor.save(a);
                }
            } else {
                System.out.println("\n*************LIBRO NO ENCONTRADO*************");
            }
        }

    private void listarLibrosRegistrados() {
        libros = repositorioLibro.findAll();
        System.out.println("*****************LIBROS REGISTRADOS*****************");
        libros.forEach(System.out::println);
        System.out.println("\n**************************************************\n");
    }

    private void listarAutoresRegistrados() {
        autores = repositorioAutor.findAll();
        System.out.println("************AUTORES REGISTRADOS************");
        autores.forEach(System.out::println);
        System.out.println("\n****************************************\n");
    }

    private void listarAutoresVivosPorAnos(){
        System.out.println("Por favor ingrese el año que desea buscar: ");
        var busqueda = scan.nextInt();
        autores = repositorioAutor.autorVivoEnDeterminadoAnio(busqueda);

        if (autores != null){
            System.out.println("************AUTORES VIVOS************");
            autores.forEach(System.out::println);
            System.out.println("\n***************************************\n");
        }else {
            System.out.println("no se encuentra ningun Autor vivo en ese año");
        }
    }

    private void listarLibrosPorIdioma() {
        var menu = """
               Seleccione el idioma del libro que desea encontrar:
               ---------------------------------------------------
               1 - Español
               2 - Francés
               3 - Inglés
               4 - Portugués
               ----------------------------------------------------
                """;
        System.out.println(menu);
        
        try {
            var opcion = Integer.parseInt(scan.nextLine());
            
            switch (opcion) {
                case 1:
                    buscarLibrosPorIdioma("es");
                    break;
                case 2:
                    buscarLibrosPorIdioma("fr");
                    break;
                case 3:
                    buscarLibrosPorIdioma("en");
                    break;
                case 4:
                    buscarLibrosPorIdioma("pt");
                    break;
                default:
                    System.out.println("Opción inválida!");
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida: " + e.getMessage());
        }
    }
    
    private void buscarLibrosPorIdioma(String idioma) {
        try {
            Idioma idiomaEnum = Idioma.valueOf(idioma.toUpperCase());
            List<Libro> librosIdioma = repositorioLibro.buscarLibrosPorIdioma(idiomaEnum);
            if (libros.isEmpty()) {
                System.out.println("No hay libros registrados en ese idioma");
            } else {
                System.out.println();
                librosIdioma.forEach(System.out::println);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Introduce un idioma válido en el formato especificado.");
        }
    }
}
