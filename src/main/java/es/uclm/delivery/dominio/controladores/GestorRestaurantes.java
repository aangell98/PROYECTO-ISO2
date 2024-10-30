package es.uclm.delivery.dominio.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.ui.Model;
import es.uclm.delivery.dominio.entidades.CartaMenu;
import es.uclm.delivery.dominio.entidades.Direccion;
import es.uclm.delivery.dominio.entidades.ItemMenu;
import es.uclm.delivery.dominio.entidades.Restaurante;
import es.uclm.delivery.dominio.entidades.TipoItemMenu;
import es.uclm.delivery.persistencia.CartaMenuDAO;
import es.uclm.delivery.persistencia.ItemMenuDAO;

import java.util.List;

@Controller
public class GestorRestaurantes {

    @Autowired
    private CartaMenuDAO cartaMenuDAO;

    @Autowired
    private ItemMenuDAO itemMenuDAO;

    @GetMapping("/homeRestaurante")
    public String showHomeRestaurante(Model model) {
        List<ItemMenu> items = itemMenuDAO.findAll();
        model.addAttribute("items", items);
        model.addAttribute("cartaMenu", new CartaMenu());
		model.addAttribute("itemMenu", new ItemMenu());
        return "homeRestaurante";
    }

    @GetMapping("/altaMenu")
    public String showAltaMenu(Model model) {
        List<ItemMenu> items = itemMenuDAO.findAll();
        model.addAttribute("items", items);
        model.addAttribute("cartaMenu", new CartaMenu());
        return "altaMenu";
    }

    @PostMapping("/crearItemMenu")
    public String crearItemMenu(@ModelAttribute ItemMenu itemMenu) {
        itemMenuDAO.insert(itemMenu);
        return "redirect:/homeRestaurante";
    }

    @PostMapping("/crearCartaMenu")
    public String crearCartaMenu(@ModelAttribute CartaMenu cartaMenu, @RequestParam List<Long> itemsIds) {
        List<ItemMenu> items = itemMenuDAO.findAllById(itemsIds);
        cartaMenu.setItems(items);
        cartaMenuDAO.insert(cartaMenu);
        return "redirect:/homeRestaurante";
    }
	/**
	 * 
	 * @param nombre
	 * @param cif
	 * @param d
	 */
	public Restaurante registrarRestaurante(String nombre, String cif, Direccion d) {
		// TODO - implement GestorRestaurantes.registrarRestaurante
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param nombre
	 * @param items
	 */
	public void editarCarta(String nombre, List<ItemMenu> items) {
		// TODO - implement GestorRestaurantes.editarCarta
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param nombre
	 * @param precio
	 * @param tipo
	 */
	private ItemMenu crearItem(String nombre, double precio, TipoItemMenu tipo) {
		// TODO - implement GestorRestaurantes.crearItem
		throw new UnsupportedOperationException();
	}

}