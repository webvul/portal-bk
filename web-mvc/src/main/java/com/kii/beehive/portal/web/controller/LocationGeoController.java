package com.kii.beehive.portal.web.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.kii.beehive.business.manager.LocationGeoManager;
import com.kii.beehive.portal.jdbc.entity.ThingGeo;
import com.kii.beehive.portal.web.entity.LocationGeoRestBean;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * Beehive API - Location Geo API
 *
 */
@RestController
@RequestMapping(value = "/locationGeo", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class LocationGeoController {

	@Autowired
	private LocationGeoManager locationGeoManager;

	/**
	 * 添加/更新POI位置信息
	 * POST /locationGeo
	 * <p>
	 * refer to doc "Beehive API - Location Geo API" for request/response details
	 */
	@RequestMapping(value = "", method = {RequestMethod.POST}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity saveLocationGeo(@RequestBody LocationGeoRestBean bean) {

		bean.verifyInput();

		ThingGeo thingGeo = bean.getThingGeo();
		long id = locationGeoManager.saveLocationGeo(thingGeo);

		ThingGeo thingGeoResponse = locationGeoManager.findById(id);
		LocationGeoRestBean response = new LocationGeoRestBean(thingGeoResponse);

		return new ResponseEntity(response, HttpStatus.OK);
	}

	/**
	 * 删除POI位置信息
	 * DELETE /locationGeo/{id}
	 * <p>
	 * refer to doc "Beehive API - Location Geo API" for request/response details
	 */
	@RequestMapping(value = "/{id}", method = {RequestMethod.DELETE})
	public ResponseEntity deleteLocationGeo(@PathVariable("id") long id) {

		locationGeoManager.removeLocationGeo(id);

		return new ResponseEntity(HttpStatus.OK);
	}


	/**
	 * 查询特定楼层的所有POI点位
	 * GET /locationGeo/{buildingID}/{floor}
	 * <p>
	 * refer to doc "Beehive API - Location Geo API" for request/response details
	 */
	@RequestMapping(value = "/{buildingID}/{floor}", method = {RequestMethod.GET})
	public ResponseEntity getLocationGeoByFloor(@PathVariable("buildingID") String buildingID, @PathVariable
			("floor") int floor) {

		List<ThingGeo> list = locationGeoManager.getLocationGeoByBuildingIDAndFloor(buildingID, floor);

		List<LocationGeoRestBean> response = this.toResponse(list);

		return new ResponseEntity(response, HttpStatus.OK);
	}

	/**
	 * 查询特定buildingID下的所有POI点位
	 * GET /locationGeo/{buildingID}
	 * <p>
	 * refer to doc "Beehive API - Location Geo API" for request/response details
	 */
	@RequestMapping(value = "/{buildingID}", method = {RequestMethod.GET})
	public ResponseEntity getLocationGeoByBuildingID(@PathVariable("buildingID") String buildingID) {

		List<ThingGeo> list = locationGeoManager.getLocationGeoByBuildingIDAndFloor(buildingID, null);

		List<LocationGeoRestBean> response = this.toResponse(list);

		return new ResponseEntity(response, HttpStatus.OK);
	}

	/**
	 * 查询圆形区域内的所有POI点位
	 * POST /locationGeo/search/circle
	 * <p>
	 * refer to doc "Beehive API - Location Geo API" for request/response details
	 */
	@RequestMapping(value = "/search/circle", method = {RequestMethod.POST}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity searchCircle(@RequestBody Map<String, Object> searchMap) {

		// get fields in request body, if any invalid input, return code 400
		int floor = this.getValidValue(searchMap, "floor", Integer.class);
		String buildingID = this.getValidValue(searchMap, "buildingID", String.class);
		double centricLng = this.getValidValue(searchMap, "centricLng", Double.class);
		double centricLat = this.getValidValue(searchMap, "centricLat", Double.class);
		float radius = this.getValidValue(searchMap, "radius", Float.class);

		List<ThingGeo> list = locationGeoManager.searchCircle(buildingID, floor, centricLng, centricLat, radius);

		List<LocationGeoRestBean> response = this.toResponse(list);

		return new ResponseEntity(response, HttpStatus.OK);
	}

	/**
	 * 查询多边形区域内的所有POI点位
	 * GET /locationGeo/search/polygon
	 * <p>
	 * refer to doc "Beehive API - Location Geo API" for request/response details
	 */
	@RequestMapping(value = "/search/polygon", method = {RequestMethod.POST}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity searchPolygon(@RequestBody Map<String, Object> searchMap) {

		// get fields in request body, if any invalid input, return code 400
		int floor = this.getValidValue(searchMap, "floor", Integer.class);
		String buildingID = this.getValidValue(searchMap, "buildingID", String.class);
		List<Map<String, Double>> scope = (List<Map<String, Double>>)searchMap.get("scope");

		List<ThingGeo> list = locationGeoManager.searchPolygon(buildingID, floor, scope);

		List<LocationGeoRestBean> response = this.toResponse(list);

		return new ResponseEntity(response, HttpStatus.OK);
	}

	/**
	 * convert from list of ThingGeo to list of LocationGeoRestBean
	 *
	 * @param list
	 * @return
	 */
	private List<LocationGeoRestBean> toResponse(List<ThingGeo> list) {

		List<LocationGeoRestBean> response = new ArrayList<>();

		if(list != null) {
			list.forEach(e -> {
				response.add(new LocationGeoRestBean(e));
			});
		}

		return response;
	}

	/**
	 * get value from map by key, and convert the value to the specified class;
	 * if anything unexpected during the convert, return http code 400
	 * only basic types(String/Integer/Long/Float/Double) are supported
	 *
	 * @param map
	 * @param key
	 * @param <T>
	 * @return
	 */
	private <T> T getValidValue(Map<String, Object> map, String key, Class<T> targetClass) {

		Object obj = map.get(key);
		if(obj == null) {
			throw new PortalException(ErrorCode.INVALID_INPUT,"field", key);
		}

		if(targetClass == String.class) {
			if(Strings.isBlank((String)obj)) {
				throw new PortalException(ErrorCode.INVALID_INPUT,"field", key);
			} else {
				return (T)obj;
			}
		}

		T result = null;

		if(targetClass == Integer.class || targetClass == Long.class || targetClass == Float.class || targetClass == Double.class) {
			try {
				String temp = obj.toString();
				result = targetClass.getConstructor(String.class).newInstance(temp);
			} catch (ReflectiveOperationException e) {
				throw new PortalException(ErrorCode.INVALID_INPUT,"field", key);
			}
		}

		return result;
	}

	/**
	 * 同步特定楼层的所有POI点位
	 * POST /locationGeo/sync/{buildingID}/{floor}
	 * <p>
	 * refer to doc "Beehive API - Location Geo API" for request/response details
	 */
	@RequestMapping(value = "/sync/{buildingID}/{floor}", method = {RequestMethod.POST})
	public ResponseEntity syncLocationGeoByFloor(@PathVariable("buildingID") String buildingID, @PathVariable
			("floor") int floor) {

		List<ThingGeo> list = locationGeoManager.syncLocationGeoByBuildingIDAndFloor(buildingID, floor);

		List<LocationGeoRestBean> response = this.toResponse(list);

		return new ResponseEntity(response, HttpStatus.OK);
	}

}
