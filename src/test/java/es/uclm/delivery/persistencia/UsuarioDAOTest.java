package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioDAOTest {

    private static final String USERNAME_EXISTENTE = "testUser";
    private static final String USERNAME_INEXISTENTE = "nonExistentUser";
    private static final String USERNAME_NULO = null;
    private static final String USERNAME_VACIO = "";

    @InjectMocks
    private UsuarioDAO usuarioDAO;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Usuario> usuarioTypedQuery;

    @Mock
    private TypedQuery<String> roleTypedQuery;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEncontrarUser_UsernameValidoInexistente() {
        when(entityManager.createQuery(anyString(), eq(Usuario.class))).thenReturn(usuarioTypedQuery);
        when(usuarioTypedQuery.setParameter(anyString(), anyString())).thenReturn(usuarioTypedQuery);
        when(usuarioTypedQuery.getSingleResult()).thenThrow(new javax.persistence.NoResultException());

        Optional<Usuario> result = usuarioDAO.encontrarUser(USERNAME_INEXISTENTE);

        assertFalse(result.isPresent());
    }

    @Test
    void testEncontrarUser_UsernameNulo() {
        Optional<Usuario> result = usuarioDAO.encontrarUser(USERNAME_NULO);
        assertFalse(result.isPresent());
    }

    @Test
    void testEncontrarUser_UsernameVacio() {
        Optional<Usuario> result = usuarioDAO.encontrarUser(USERNAME_VACIO);
        assertFalse(result.isPresent());
    }

    @Test
    void testEncontrarUser_ErrorJPQL() {
        when(entityManager.createQuery(anyString(), eq(Usuario.class))).thenThrow(new javax.persistence.PersistenceException("JPQL error"));

        Optional<Usuario> result = usuarioDAO.encontrarUser(USERNAME_EXISTENTE);

        assertFalse(result.isPresent());
    }
}