package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.CartaMenu;
import es.uclm.delivery.dominio.entidades.Restaurante;
import es.uclm.delivery.dominio.entidades.Usuario;
import org.junit.jupiter.api.*;
import org.mockito.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RestauranteDAOTest {

    private static final Long RESTAURANTE_ID = 1L;
    private static final String CODIGO_POSTAL_INEXISTENTE = "99999";
    private static final String USERNAME_EXISTENTE = "user1";
    private static final String USERNAME_INEXISTENTE = "userInexistente";

    private RestauranteDAO restauranteDAO;

    @Mock
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        restauranteDAO = new RestauranteDAO();

        // Inyectar mockEntityManager usando reflexión
        java.lang.reflect.Field entityManagerField = RestauranteDAO.class.getSuperclass()
                .getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(restauranteDAO, mockEntityManager);
    }

    private Restaurante crearRestaurante() {
        Restaurante restaurante = new Restaurante();
        restaurante.setId(RESTAURANTE_ID);
        return restaurante;
    }

    private Usuario crearUsuario(String username) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        return usuario;
    }

    private TypedQuery<Restaurante> crearMockTypedQueryRestaurante() {
        return mock(TypedQuery.class);
    }

    private TypedQuery<CartaMenu> crearMockTypedQueryCartaMenu() {
        return mock(TypedQuery.class);
    }

    @Test
    void testFindByUsuario_UsuarioExistente() {
        Usuario usuario = crearUsuario(USERNAME_EXISTENTE);
        Restaurante restaurante = crearRestaurante();
        TypedQuery<Restaurante> mockQuery = crearMockTypedQueryRestaurante();
        when(mockEntityManager.createQuery(anyString(), eq(Restaurante.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("usuario", usuario)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(restaurante);

        Optional<Restaurante> result = restauranteDAO.findByUsuario(usuario);

        assertTrue(result.isPresent());
        assertEquals(restaurante, result.get());
    }

    @Test
    void testFindByUsuario_UsuarioNoExistente() {
        Usuario usuario = crearUsuario(USERNAME_INEXISTENTE);
        TypedQuery<Restaurante> mockQuery = crearMockTypedQueryRestaurante();
        when(mockEntityManager.createQuery(anyString(), eq(Restaurante.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("usuario", usuario)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenThrow(new NoResultException());

        Optional<Restaurante> result = restauranteDAO.findByUsuario(usuario);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByCodigoPostal_ExistenRestaurantes() {
        List<Restaurante> restaurantes = Arrays.asList(new Restaurante(), new Restaurante());
        TypedQuery<Restaurante> mockQuery = mock(TypedQuery.class);
        when(mockEntityManager.createQuery(anyString(), eq(Restaurante.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("codigoPostal"), anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(restaurantes);

        List<Restaurante> result = restauranteDAO.findByCodigoPostal("12345");

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindCartasMenuByRestauranteId_ExistenCartas() {
        List<CartaMenu> cartas = Arrays.asList(new CartaMenu(), new CartaMenu());
        TypedQuery<CartaMenu> mockQuery = crearMockTypedQueryCartaMenu();
        when(mockEntityManager.createQuery(anyString(), eq(CartaMenu.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("restauranteId"), eq(RESTAURANTE_ID))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(cartas);

        List<CartaMenu> result = restauranteDAO.findCartasMenuByRestauranteId(RESTAURANTE_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindCartasMenuByRestauranteId_NoExistenCartas() {
        TypedQuery<CartaMenu> mockQuery = crearMockTypedQueryCartaMenu();
        when(mockEntityManager.createQuery(anyString(), eq(CartaMenu.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("restauranteId"), eq(RESTAURANTE_ID))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<CartaMenu> result = restauranteDAO.findCartasMenuByRestauranteId(RESTAURANTE_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testObtenerRestaurantesAleatorios_ExistenRestaurantes() {
        List<Restaurante> restaurantes = Arrays.asList(crearRestaurante(), crearRestaurante(), crearRestaurante());
        TypedQuery<Restaurante> mockQuery = crearMockTypedQueryRestaurante();
        when(mockEntityManager.createQuery(anyString(), eq(Restaurante.class))).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(anyInt())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(restaurantes);

        List<Restaurante> result = restauranteDAO.obtenerRestaurantesAleatorios(3);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testObtenerRestaurantesAleatorios_NoExistenRestaurantes() {
        TypedQuery<Restaurante> mockQuery = crearMockTypedQueryRestaurante();
        when(mockEntityManager.createQuery(anyString(), eq(Restaurante.class))).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(anyInt())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<Restaurante> result = restauranteDAO.obtenerRestaurantesAleatorios(3);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByUsuario_PersistenceException() {
        Usuario usuario = crearUsuario(USERNAME_EXISTENTE);
        TypedQuery<Restaurante> mockQuery = crearMockTypedQueryRestaurante();
        when(mockEntityManager.createQuery(anyString(), eq(Restaurante.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("usuario", usuario)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenThrow(new PersistenceException());

        Optional<Restaurante> result = restauranteDAO.findByUsuario(usuario);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByCodigoPostal_PersistenceException() {
        when(mockEntityManager.createQuery(anyString(), eq(Restaurante.class)))
                .thenThrow(new PersistenceException());

        List<Restaurante> result = restauranteDAO.findByCodigoPostal("12345");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testObtenerRestaurantesAleatorios_PersistenceException() {
        when(mockEntityManager.createQuery(anyString(), eq(Restaurante.class)))
                .thenThrow(new PersistenceException());

        List<Restaurante> result = restauranteDAO.obtenerRestaurantesAleatorios(3);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}