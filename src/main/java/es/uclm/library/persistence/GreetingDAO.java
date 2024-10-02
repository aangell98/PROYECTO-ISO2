package es.uclm.library.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.uclm.library.Business.entity.Greeting;

@Repository
public interface GreetingDAO extends JpaRepository<Greeting, Long> {
    /*
     * Se hereda de JpaRepository operaciones de acceso a datos comunes a todas las
     * entidades:
     * E save(E)
     * List<E> saveAll(List<E>)
     * List<E> findAll()
     * E findById(id)
     * delete(E)
     * deleteById(id)
     * ...
     *
     * Aquí se podrían definir consultas de selección más específicas y complejas
     **/
}
