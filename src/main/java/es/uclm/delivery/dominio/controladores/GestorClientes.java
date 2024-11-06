package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.presentacion.IUBusqueda;
import es.uclm.delivery.dominio.controladores.GestorPedidos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GestorClientes {

    @Autowired
    private IUBusqueda IUBusqueda;

    @Autowired
    private GestorPedidos gestorPedidos;

    @GetMapping("/buscar_restaurantes")
    public List<Restaurante> buscarRestaurantes(@RequestParam("codigoPostal") String codigoPostal) {
        System.out.println("Código postal recibido: " + codigoPostal);
        List<Restaurante> restaurantes = IUBusqueda.buscarRestaurantesPorCodigoPostal(codigoPostal);
        System.out.println("Restaurantes encontrados: " + restaurantes.size());
        return restaurantes;
    }

    @PostMapping("/agregar_favorito")
    public void agregarFavorito(@RequestParam("idRestaurante") Long idRestaurante) {
        IUBusqueda.marcarFavorito(idRestaurante);
    }

    @PostMapping("/eliminar_favorito")
    public void eliminarFavorito(@RequestParam("idRestaurante") Long idRestaurante) {
        IUBusqueda.desmarcarFavorito(idRestaurante);
    }

    @GetMapping("/listar_favoritos")
    public List<Restaurante> listarFavoritos() {
        return IUBusqueda.listarFavoritos();
    }

    @GetMapping("/obtener_restaurante")
    public Restaurante obtenerRestaurante(@RequestParam("restauranteId") Long restauranteId) {
        return IUBusqueda.obtenerRestaurante(restauranteId);
    }

}