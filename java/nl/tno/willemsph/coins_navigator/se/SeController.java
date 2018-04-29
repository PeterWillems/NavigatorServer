package nl.tno.willemsph.coins_navigator.se;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeController {

	@Autowired
	private SeService _seService;

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets")
	public List<Dataset> getAllDatasets() throws URISyntaxException {
		return _seService.getAllDatasets();
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/system-slots")
	public List<SystemSlot> getAllSystemSlots(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.getAllSystemSlots(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/se/datasets/{id}/system-slots")
	public SystemSlot createSystemSlot(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.createSystemSlot(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/se/datasets/{id}/system-slots/{localName}")
	public SystemSlot updateSystemSlot(@PathVariable int id, @PathVariable String localName,
			@RequestBody SystemSlot systemSlot) throws URISyntaxException, IOException {
		System.out.println("Assembly: " + systemSlot.getAssembly());
		return _seService.updateSystemSlot(id, localName, systemSlot);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/functions")
	public List<Function> getAllFunctions(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.getAllFunctions(id);
	}

}
