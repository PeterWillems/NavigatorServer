package nl.tno.willemsph.coins_navigator.coins_class;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoinsClassController {

	@Autowired
	private CoinsClassService coinsClassService;

	@CrossOrigin
	@RequestMapping("/classes")
	public List<CoinsClass> getAllCoinsClasses() throws IOException {
		return coinsClassService.getAllCoinsClasses();
	}

	@CrossOrigin
	@RequestMapping("/classes/{name}")
	public List<CoinsProperty> getCoinsClassProperties(@PathVariable String name) throws IOException {
		return coinsClassService.getCoinsClassProperties(name);
	}
}
