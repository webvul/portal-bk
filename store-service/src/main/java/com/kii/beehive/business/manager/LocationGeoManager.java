package com.kii.beehive.business.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.kii.beehive.portal.exception.DuplicateException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.ThingGeoDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingGeo;

/**
 * this class provides location geofencing related functions
 */
@Component
public class LocationGeoManager {

	private static final int EARTH_RADIUS = 6378137;

	@Autowired
	private ThingGeoDao thingGeoDao;

	@Autowired
	private GlobalThingSpringDao globalThingSpringDao;

	/**
	 * 添加/更新POI位置信息
	 *
	 * @param thingGeo
	 * @return
	 */
	public Long saveLocationGeo(ThingGeo thingGeo) {

		Long id = thingGeo.getId();

		if(id == null) {
			// check whether any duplicated POI already in DB
			List<ThingGeo> duplicatedThingGeoList = thingGeoDao.getDuplicatedThingGeo(thingGeo);

			if (duplicatedThingGeoList != null && duplicatedThingGeoList.size() > 0) {
				throw new DuplicateException("globalThingID, vendorThingID or aliThingID", "Location Geo");
			}

			// create POI
			id = thingGeoDao.insert(thingGeo);
		} else {
			// update POI
			thingGeoDao.updateEntityByID(thingGeo, id);
		}

		// sync globalThingID from global_thing into thing_geo
		this.syncGlobalThingIDByVendorThingID(thingGeo);

		return id;
	}

	private void syncGlobalThingIDByVendorThingID(ThingGeo thingGeo) {

		String vendorThingID = thingGeo.getVendorThingID();
		if(Strings.isBlank(vendorThingID)) {
			return;
		}

		// get global thing id
		Long globalThingID = null;
		GlobalThingInfo thingInfo = globalThingSpringDao.getThingByVendorThingID(vendorThingID);
		if(thingInfo != null) {
			globalThingID = thingInfo.getId();
		}

		// sync global thing id into thing_geo
		thingGeoDao.updateGlobalThingIDByVendorThingID(vendorThingID, globalThingID);
	}

	/**
	 * 删除POI位置信息
	 *
	 * @param id
	 * @return
	 */
	public boolean removeLocationGeo(long id) {

		int count = thingGeoDao.hardDeleteByID(id);

		return count > 0;
	}

	/**
	 * 查询特定buildingID, floor下的所有POI点位
	 * 如果floor为null, 则指按buildingID查询
	 *
	 * @param buildingID
	 * @param floor
	 * @return
	 */
	public List<ThingGeo> getLocationGeoByBuildingIDAndFloor(String buildingID, Integer floor) {

		return thingGeoDao.findByBuildingIDAndFloor(buildingID, floor);

	}

	/**
	 * 查询圆形区域内的所有POI点位
	 *
	 * @param buildingID
	 * @param floor
	 * @param centricLng
	 * @param centricLat
	 * @param radius
	 * @return
	 */
	public List<ThingGeo> searchCircle(String buildingID, int floor, double centricLng, double centricLat,
									   float radius) {

		List<ThingGeo> list = thingGeoDao.findByBuildingIDAndFloor(buildingID, floor);

		List<ThingGeo> response = new ArrayList<>();

		if(list != null) {
			list.forEach(e -> {
				// calculate the distance between current POI point and centric POI point
				double distance = this.calculateDistance(e.getLng(), e.getLat(), centricLng, centricLat);
				// if the distance is less than or equal to radius, put current POI point in response list
				if(distance <= radius) {
					response.add(e);
				}
			});
		}

		return response;
	}

	// calculate the distance between two POI points
	// algorithm in js, from AutoNavi
	//
	//	function distance(latlng1, latlng2) {
	//		var rad = Math.PI / 180,
	//		lat1 = latlng1.lat * rad,
	//		lat2 = latlng2.lat * rad,
	//		a = Math.sin(lat1) * Math.sin(lat2) +
	//			Math.cos(lat1) * Math.cos(lat2) * Math.cos((latlng2.lng - latlng1.lng) * rad);
	//
	//		return 6371000 * Math.acos(Math.min(a, 1));
	//	}
	// based on above:
	//	lat 1 degree in meter = 111194.92664454764
	//	lng 1 degree in meter = 96297.32567757886
	//
	private double calculateDistance(double lng1, double lat1, double lng2, double lat2) {

		double rad = Math.PI / 180;
		lat1 = lat1 * rad;
		lat2 = lat2 * rad;

		double a = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos((lng2 - lng1) * rad);

		return EARTH_RADIUS * Math.acos(Math.min(a, 1));
	}

	/**
	 * 查询多边形区域内的所有POI点位
	 * 多边形区域由scope中的经纬度点位按照顺时针围绕而成
	 *
	 * @param buildingID
	 * @param floor
	 * @param scope
	 * @return
	 */
	public List<ThingGeo> searchPolygon(String buildingID, int floor, List<Map<String, Double>> scope) {

		List<ThingGeo> list = thingGeoDao.findByBuildingIDAndFloor(buildingID, floor);

		int size = scope.size();
		Double[][] scopeArr = new Double[size][2];
		for (int i = 0; i < size; i++) {
			Map<String, Double> temp = scope.get(i);
			scopeArr[i][0] = temp.get("lng");
			scopeArr[i][1] = temp.get("lat");
		}

		List<ThingGeo> response = new ArrayList<>();

		if(list != null) {
			list.forEach(e -> {
				// check whether current POI point is in the right side of all the lines of polygon
				double lng = e.getLng();
				double lat = e.getLat();
				int count = 0;
				for (int i = 0; i < size; i++) {

					int nextI = (i + 1) % size;
					boolean inRightSide = isInRightSide(lng, lat, scopeArr[i][0], scopeArr[i][1],
							scopeArr[nextI][0], scopeArr[nextI][1]);
					if(inRightSide) {
						count++;
					} else {
						break;
					}
				}

				// if current POI point is in the right side of all the lines of polygon, put current POI point in response list
				if(count == size) {
					response.add(e);
				}

			});
		}

		return response;
	}

	/**
	 * (y - y0)(x1 - x0) - (x - x0)(y1 - y0) <= 0
	 *
	 * algorithm reference:
	 * http://www.360doc.com/content/12/1105/14/7662927_245870913.shtml
	 *
	 * @param lng
	 * @param lat
	 * @param lngFrom
	 * @param latFrom
	 * @param lngTo
	 * @param latTo
	 * @return
	 */
	private boolean isInRightSide(double lng, double lat, double lngFrom, double latFrom, double lngTo, double latTo) {

		double temp = (lat - latFrom) * (lngTo - lngFrom) - (lng - lngFrom) * (latTo - latFrom);

		return temp <= 0;
	}

	public static void main(String[] args) throws Exception {
		LocationGeoManager locationGeoManager = new LocationGeoManager();
		boolean result = locationGeoManager.isInRightSide(120,32,
				119.02845066671632,29.27824347382616,
				119.02845066671632,31.27824347382616);
		System.out.println(result);

		System.out.println(5%6);


	}

	/**
	 * 同步特定楼层的所有POI点位
	 *
	 * @param buildingID
	 * @param floor
	 * @return
	 */
	public List<ThingGeo> syncLocationGeoByBuildingIDAndFloor(String buildingID, Integer floor) {

		// clean the existing thing id
		thingGeoDao.cleanGlobalthingID(buildingID, floor);

		// sync global thing id
		thingGeoDao.syncGlobalThingID(buildingID, floor);

		// return the updated POI info
		return thingGeoDao.findByBuildingIDAndFloor(buildingID, floor);
	}

	public ThingGeo findById(long id) {
		return thingGeoDao.findByID(id);
	}

}
