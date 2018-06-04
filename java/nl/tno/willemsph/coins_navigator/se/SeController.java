package nl.tno.willemsph.coins_navigator.se;

import java.io.File;
import java.io.FileNotFoundException;
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

import nl.tno.willemsph.coins_navigator.se.model.GetFunction;
import nl.tno.willemsph.coins_navigator.se.model.GetHamburger;
import nl.tno.willemsph.coins_navigator.se.model.GetNumericProperty;
import nl.tno.willemsph.coins_navigator.se.model.GetPerformance;
import nl.tno.willemsph.coins_navigator.se.model.GetPortRealisation;
import nl.tno.willemsph.coins_navigator.se.model.GetRealisationModule;
import nl.tno.willemsph.coins_navigator.se.model.GetRealisationPort;
import nl.tno.willemsph.coins_navigator.se.model.GetRequirement;
import nl.tno.willemsph.coins_navigator.se.model.GetSystemInterface;
import nl.tno.willemsph.coins_navigator.se.model.GetSystemSlot;
import nl.tno.willemsph.coins_navigator.se.model.PutFunction;
import nl.tno.willemsph.coins_navigator.se.model.PutHamburger;
import nl.tno.willemsph.coins_navigator.se.model.PutNumericProperty;
import nl.tno.willemsph.coins_navigator.se.model.PutPerformance;
import nl.tno.willemsph.coins_navigator.se.model.PutPortRealisation;
import nl.tno.willemsph.coins_navigator.se.model.PutRealisationModule;
import nl.tno.willemsph.coins_navigator.se.model.PutRealisationPort;
import nl.tno.willemsph.coins_navigator.se.model.PutRequirement;
import nl.tno.willemsph.coins_navigator.se.model.PutSystemInterface;
import nl.tno.willemsph.coins_navigator.se.model.PutSystemSlot;

@RestController
public class SeController {

	@Autowired
	private SeService _seService;

	//
	// D A T A S E T S
	//

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets")
	public List<Dataset> getAllDatasets() throws URISyntaxException {
		return _seService.getAllDatasets();
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}")
	public Dataset getDataset(@PathVariable int id) throws URISyntaxException {
		return _seService.getDataset(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/se/datasets/{id}/save")
	public Dataset saveDataset(@PathVariable int id) throws URISyntaxException, FileNotFoundException, IOException {
		return _seService.saveDataset(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/file/{filePath}")
	public File getDatasetFile(@PathVariable int id, @PathVariable String filePath) throws URISyntaxException, FileNotFoundException, IOException {
		return _seService.getDatasetFile(id, filePath);
	}

	//
	// F U N C T I O N S
	//

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/functions")
	public List<GetFunction> getAllFunctions(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.getAllFunctions(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/se/datasets/{id}/functions")
	public GetFunction createFunction(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.createFunction(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/functions/{localName}")
	public GetFunction getFunction(@PathVariable int id, @PathVariable String localName)
			throws IOException, URISyntaxException {
		return _seService.getFunction(id, localName);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/se/datasets/{id}/functions/{localName}")
	public GetFunction updateFunction(@PathVariable int id, @PathVariable String localName, @RequestBody PutFunction putFunction)
			throws URISyntaxException, IOException {
		return _seService.updateFunction(id, localName, putFunction);
	}

	//
	// S Y S T E M - S L O T S
	//

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/system-slots")
	public List<GetSystemSlot> getAllSystemSlots(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.getAllSystemSlots(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/se/datasets/{id}/system-slots")
	public GetSystemSlot createSystemSlot(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.createSystemSlot(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/system-slots/{localName}")
	public GetSystemSlot getSystemSlot(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getSystemSlot(id, localName);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/se/datasets/{id}/system-slots/{localName}")
	public GetSystemSlot updateSystemSlot(@PathVariable int id, @PathVariable String localName,
			@RequestBody PutSystemSlot putSystemSlot) throws URISyntaxException, IOException {
		return _seService.updateSystemSlot(id, localName, putSystemSlot);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/system-slots/{localName}/hamburgers")
	public List<GetHamburger> getHamburgersForSystemSlot(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getHamburgersForSystemSlot(id, localName);
	}

	//
	// R E A L I S A T I O N - M O D U L E S
	//

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/realisation-modules")
	public List<GetRealisationModule> getAllRealisationModules(@PathVariable int id)
			throws IOException, URISyntaxException {
		return _seService.getAllRealisationModules(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/se/datasets/{id}/realisation-modules")
	public GetRealisationModule createRealisationModule(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.createRealisationModule(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/realisation-modules/{localName}")
	public GetRealisationModule getRealisationModule(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getRealisationModule(id, localName);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/se/datasets/{id}/realisation-modules/{localName}")
	public GetRealisationModule updateRealisationModule(@PathVariable int id, @PathVariable String localName,
			@RequestBody PutRealisationModule putRealisationModule) throws URISyntaxException, IOException {
		return _seService.updateRealisationModule(id, localName, putRealisationModule);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/realisation-modules/{localName}/hamburgers")
	public List<GetHamburger> getHamburgersForRealisationModule(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getHamburgersForRealisationModule(id, localName);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/realisation-modules/{localName}/ports")
	public List<GetRealisationPort> getPortsForRealisationModule(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getPortsForRealisationModule(id, localName);
	}
	
	//
	// S Y S T E M - I N T E R F A C E S
	//

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/system-interfaces")
	public List<GetSystemInterface> getAllSystemInterfaces(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.getAllSystemInterfaces(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/se/datasets/{id}/system-interfaces")
	public GetSystemInterface createSystemInterface(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.createSystemInterface(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/system-interfaces/{localName}")
	public GetSystemInterface getSystemInterface(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getSystemInterface(id, localName);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/se/datasets/{id}/system-interfaces/{localName}")
	public GetSystemInterface updateSystemInterface(@PathVariable int id, @PathVariable String localName,
			@RequestBody PutSystemInterface putSystemInterface) throws URISyntaxException, IOException {
		return _seService.updateSystemInterface(id, localName, putSystemInterface);
	}

	//
	// R E A L I S A T I O N - P O R T S
	//

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/realisation-ports")
	public List<GetRealisationPort> getAllRealisationPorts(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.getAllRealisationPorts(id);
	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/se/datasets/{id}/realisation-ports")
	public GetRealisationPort createRealisationPort(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.createRealisationPort(id);
	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/realisation-ports/{localName}")
	public GetRealisationPort getRealisationPort(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getRealisationPort(id, localName);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/se/datasets/{id}/realisation-ports/{localName}")
	public GetRealisationPort updateRealisationPort(@PathVariable int id, @PathVariable String localName,
			@RequestBody PutRealisationPort putRealisationPort) throws URISyntaxException, IOException {
		return _seService.updateRealisationPort(id, localName, putRealisationPort);
	}

	//
	// R E Q U I R E M E N T S
	//

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/requirements")
	public List<GetRequirement> getAllRequirements(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.getAllRequirements(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/se/datasets/{id}/requirements")
	public GetRequirement createRequirement(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.createRequirement(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/requirements/{localName}")
	public GetRequirement getRequirement(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getRequirement(id, localName);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/se/datasets/{id}/requirements/{localName}")
	public GetRequirement updateRequirement(@PathVariable int id, @PathVariable String localName,
			@RequestBody PutRequirement requirement) throws URISyntaxException, IOException {
		return _seService.updateRequirement(id, localName, requirement);
	}

	// @CrossOrigin
	// @RequestMapping(method = RequestMethod.GET, value =
	// "/se/datasets/{id}/requirements/{localName}/min-value")
	// public NumericProperty getMinValueOfRequirement(@PathVariable int id,
	// @PathVariable String localName)
	// throws URISyntaxException, IOException {
	// return _seService.getMinValueOfRequirement(id, localName);
	// }
	//
	// @CrossOrigin
	// @RequestMapping(method = RequestMethod.GET, value =
	// "/se/datasets/{id}/requirements/{localName}/max-value")
	// public NumericProperty getMaxValueOfRequirement(@PathVariable int id,
	// @PathVariable String localName)
	// throws URISyntaxException, IOException {
	// return _seService.getMaxValueOfRequirement(id, localName);
	// }

	//
	// P E R F O R M A N C E S
	//

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/performances")
	public List<GetPerformance> getAllPerformances(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.getAllPerformances(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/se/datasets/{id}/performances")
	public GetPerformance createPerformance(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.createPerformance(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/performances/{localName}")
	public GetPerformance getPerformance(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getPerformance(id, localName);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/se/datasets/{id}/performances/{localName}")
	public GetPerformance updatePerformance(@PathVariable int id, @PathVariable String localName,
			@RequestBody PutPerformance putPerformance) throws URISyntaxException, IOException {
		return _seService.updatePerformance(id, localName, putPerformance);
	}

	//
	// H A M B U R G E R S
	//

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/hamburgers")
	public List<GetHamburger> getAllHamburgers(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.getAllHamburgers(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/se/datasets/{id}/hamburgers")
	public GetHamburger createHamburger(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.createHamburger(id);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/hamburgers/{localName}")
	public GetHamburger getHamburger(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getHamburger(id, localName);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/se/datasets/{id}/hamburgers/{localName}")
	public GetHamburger updateHamburger(@PathVariable int id, @PathVariable String localName,
			@RequestBody PutHamburger putHamburger) throws URISyntaxException, IOException {
		return _seService.updateHamburger(id, localName, putHamburger);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/hamburgers/{localName}/port-realisations")
	public List<GetPortRealisation> getPortRealisationsOfHamburger(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getPortRealisationsOfHamburger(id, localName);
	}
	
	//
	// P O R T - R E A L I S A T I O N
	//
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/port-realisations")
	public List<GetPortRealisation> getAllPortRealisations(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.getAllPortRealisations(id);
	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/se/datasets/{id}/port-realisations")
	public GetPortRealisation createPortRealisation(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.createPortRealisation(id);
	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/port-realisations/{localName}")
	public GetPortRealisation getPortRealisation(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getPortRealisation(id, localName);
	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/se/datasets/{id}/port-realisations/{localName}")
	public GetPortRealisation updatePortRealisation(@PathVariable int id, @PathVariable String localName,
			@RequestBody PutPortRealisation putPortRealisation) throws URISyntaxException, IOException {
		return _seService.updatePortRealisation(id, localName, putPortRealisation);
	}

	//
	// N U M E R I C - P R O P E R T I E S
	//

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/numeric-properties")
	public List<GetNumericProperty> getAllNumericProperties(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.getAllNumericProperties(id);
	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/se/datasets/{id}/numeric-properties")
	public GetNumericProperty createNumericProperty(@PathVariable int id) throws IOException, URISyntaxException {
		return _seService.createNumericProperty(id);
	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/se/datasets/{id}/numeric-properties/{localName}")
	public GetNumericProperty getNumericProperty(@PathVariable int id, @PathVariable String localName)
			throws URISyntaxException, IOException {
		return _seService.getNumericProperty(id, localName);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.PUT, value = "/se/datasets/{id}/numeric-properties/{localName}")
	public GetNumericProperty updateNumericProperty(@PathVariable int id, @PathVariable String localName,
			@RequestBody PutNumericProperty putNumericProperty) throws URISyntaxException, IOException {
		return _seService.updateNumericProperty(id, localName, putNumericProperty);
	}

}
