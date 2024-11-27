package es.uclm.delivery.dominio.controladores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Disabled;

import java.lang.System.Logger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;

import es.uclm.delivery.dominio.entidades.Cliente;
import es.uclm.delivery.dominio.entidades.Direccion;
import es.uclm.delivery.dominio.entidades.EstadoPedido;
import es.uclm.delivery.dominio.entidades.Pedido;
import es.uclm.delivery.dominio.entidades.Repartidor;
import es.uclm.delivery.dominio.entidades.Restaurante;
import es.uclm.delivery.dominio.entidades.ServicioEntrega;
import es.uclm.delivery.persistencia.ClienteDAO;
import es.uclm.delivery.presentacion.IUBusqueda;
import es.uclm.delivery.presentacion.IULogin;
import es.uclm.delivery.presentacion.IUPedido;

@Disabled
@ExtendWith(SpringExtension.class)
@WebMvcTest(GestorClientes.class)
class GestorClientesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUBusqueda iuBusqueda;

    @MockBean
    private IULogin iuCliente;

    @MockBean
    private IUPedido iuPedido;

    @MockBean
    private Logger logger;

    @MockBean
    private ClienteDAO clienteDAO;

    @Test
    void testBuscarRestaurantesSuccess() throws Exception {
        // Prepara datos simulados
        Restaurante restaurante1 = new Restaurante();
        restaurante1.setNombre("Restaurante 1");
        Restaurante restaurante2 = new Restaurante();
        restaurante2.setNombre("Restaurante 2");
        List<Restaurante> restaurantes = Arrays.asList(restaurante1, restaurante2);
        when(iuBusqueda.buscarRestaurantesPorCodigoPostal("28001")).thenReturn(restaurantes);

        mockMvc.perform(get("/buscar_restaurantes")
                .param("codigoPostal", "28001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))  // Verifica la cantidad de restaurantes
                .andExpect(jsonPath("$[0].nombre").value("Restaurante 1")); // Verifica el nombre del primer restaurante
    }

    @Test
    void testBuscarRestaurantesNoResults() throws Exception {
        // Simulamos que no se encuentran restaurantes
        when(iuBusqueda.buscarRestaurantesPorCodigoPostal("28001")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/buscar_restaurantes")
                .param("codigoPostal", "28001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)); // Verifica que no hay resultados
    }

    @Test
    void testAgregarFavorito() throws Exception {
        // Configura el mock
        doNothing().when(iuBusqueda).marcarFavorito(1L);

        mockMvc.perform(post("/agregar_favorito")
                .param("idRestaurante", "1"))
                .andExpect(status().isOk());

        verify(iuBusqueda, times(1)).marcarFavorito(1L); // Verifica que el método fue llamado una vez
    }

    @Test
    void testEliminarFavorito() throws Exception {
        // Configura el mock
        doNothing().when(iuBusqueda).desmarcarFavorito(1L);

        mockMvc.perform(post("/eliminar_favorito")
                .param("idRestaurante", "1"))
                .andExpect(status().isOk());

        verify(iuBusqueda, times(1)).desmarcarFavorito(1L); // Verifica que el método fue llamado una vez
    }

    @Test
    void testListarFavoritos() throws Exception {
        // Simula una lista de favoritos
        Restaurante restauranteA = new Restaurante();
        restauranteA.setNombre("Restaurante A");
        Restaurante restauranteB = new Restaurante();
        restauranteB.setNombre("Restaurante B");
        List<Restaurante> favoritos = Arrays.asList(restauranteA, restauranteB);
        when(iuBusqueda.listarFavoritos()).thenReturn(favoritos);

        mockMvc.perform(get("/listar_favoritos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) // Verifica que la longitud sea 2
                .andExpect(jsonPath("$[0].nombre").value("Restaurante A")); // Verifica el nombre del primer favorito
    }

    @Test
    void testObtenerRestaurante() throws Exception {
        Restaurante restaurante = new Restaurante();
        restaurante.setNombre("Restaurante Test");
        when(iuBusqueda.obtenerRestaurante(1L)).thenReturn(restaurante);

        mockMvc.perform(get("/obtener_restaurante")
                .param("restauranteId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Restaurante Test"));
    }

    @Test
    void testListarPedidosEnCurso() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellidos("Perez");
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(EstadoPedido.PAGADO);
        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setEstado(EstadoPedido.RECOGIDO);
        when(iuPedido.obtenerPedidosEnCurso(cliente.getId())).thenReturn(Arrays.asList(pedido, pedido2));
        when(iuPedido.obtenerPedidosEnCurso(cliente.getId())).thenReturn(Arrays.asList(pedido));

        mockMvc.perform(get("/listar_pedidos_curso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testConfirmarRecepcionPedido() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(EstadoPedido.RECOGIDO);
        when(iuPedido.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        when(iuPedido.obtenerServicioEntregaPorPedido(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/confirmar_recepcion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idPedido\": 1}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Pedido actualizado a ENTREGADO"));
    }

    @Test
    void testConfirmarRecepcionPedidoNotFound() throws Exception {
        when(iuPedido.obtenerPedidoPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/confirmar_recepcion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idPedido\": 999}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Pedido no encontrado"));
    }

    @Test
    void testListarPedidosAnteriores() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellidos("Perez");
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(EstadoPedido.ENTREGADO);
        when(iuPedido.obtenerPedidosEntregados(cliente.getId())).thenReturn(Arrays.asList(pedido));

        mockMvc.perform(get("/listar_pedidos_anteriores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testValorarPedido() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(EstadoPedido.ENTREGADO);
        when(iuPedido.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        Repartidor repartidor = new Repartidor();
        repartidor.setNombre("Pepe");
        repartidor.setApellidos("Garcia");
        ServicioEntrega servicioEntrega = new ServicioEntrega();
        servicioEntrega.setRepartidor(repartidor);
        when(iuPedido.obtenerServicioEntregaPorPedido(1L)).thenReturn(Optional.of(servicioEntrega));

        mockMvc.perform(post("/valorar_pedido")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idPedido\": 1, \"valoracion\": 4}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Pedido valorado con éxito"));
    }

    @Test
    void testValorarPedidoNotFound() throws Exception {
        when(iuPedido.obtenerPedidoPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/valorar_pedido")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idPedido\": 999, \"valoracion\": 4}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Pedido no encontrado"));
    }

    @Test
    void testGuardarDireccion() throws Exception {
        Direccion direccion = new Direccion();
        direccion.setCalle("Calle Falsa 123");
        Cliente cliente = new Cliente();
        cliente.setApellidos("Perez");
        cliente.setNombre("Juan");
        when(iuBusqueda.obtenerClienteActual()).thenReturn(cliente);
        when(clienteDAO.findByUsername(any())).thenReturn(Optional.of(cliente));

        mockMvc.perform(post("/guardar_direccion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"calle\": \"Calle Falsa 123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calle").value("Calle Falsa 123"));
    }

    @Test
    void testListarDireccionesNotFound() throws Exception {
        when(clienteDAO.findByUsername(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/listar_direcciones"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cliente no encontrado"));
    }
}
