package com.kii.beehive.portal.web.control;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kii.beehive.portal.commons.LocationsGeneral;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.repositories.ThingInfoRepository;

@Controller
@RequestMapping("/things")
public class ThingController {


	@Autowired
	private ThingInfoRepository thingRepos;


	@RequestMapping(path = "/blocks/{blockNo}/floors/{floorNo}/type/{typeNo}/number/{number}", method = {RequestMethod.GET})
	public ModelAndView getThingsByLocation(@PathVariable("blockNo") String blockNo,@PathVariable("floorNo") String floorNo,
											  @PathVariable("type") String type,@PathVariable("number") String number) {


		String locationID= LocationsGeneral.general(blockNo,floorNo,type,number);


		Set<GlobalThingInfo> things=thingRepos.queryByLocationID(locationID);

		ModelAndView model=new ModelAndView();


		model.setViewName("json");
		model.addObject(things);

		return model;
	}
}