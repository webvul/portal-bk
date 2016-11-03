package com.kii.beehive.portal.web.controller;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.jar.Manifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hdchen on 11/2/16.
 */
@RestController
@RequestMapping(value = "/info", consumes = {MediaType.ALL_VALUE}, produces = {MediaType.ALL_VALUE})
public class ServerInfoController {
	private static final Logger LOG = LoggerFactory.getLogger(ServerInfoController.class);

	@Autowired
	private ServletContext context;

	@RequestMapping(value = "/buildNum", method = {RequestMethod.GET})
	public String buildNumber() {
		final String version = getClass().getPackage().getImplementationVersion();
		if (null != version) {
			return version;
		}

		final Manifest manifest;
		try {
			manifest = new Manifest(context.getResourceAsStream("/META-INF/MANIFEST.MF"));
			return manifest.getMainAttributes().getValue("Implementation-Version");
		} catch (IOException e) {
			LOG.info(e.getMessage(), e);
			return null;
		}
	}
}
