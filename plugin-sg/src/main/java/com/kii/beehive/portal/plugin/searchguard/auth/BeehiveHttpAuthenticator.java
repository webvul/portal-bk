package com.kii.beehive.portal.plugin.searchguard.auth;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.elasticsearch.ElasticsearchSecurityException;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.floragunn.searchguard.auth.HTTPAuthenticator;
import com.floragunn.searchguard.user.AuthCredentials;
import com.kii.beehive.portal.plugin.searchguard.data.BeehiveAuthCredential;

/**
 * Created by hdchen on 6/21/16.
 */
public class BeehiveHttpAuthenticator implements HTTPAuthenticator {
	public static final String BeehiveAuth = "BeehiveAuth";

	public BeehiveHttpAuthenticator(final Settings unused) {
		super();
	}

	@Override
	public String getType() {
		return "com.kii.beehive.portal.plugin.searchguard.auth.BeehiveHttpAuthenticator";
	}

	@Override
	public AuthCredentials extractCredentials(RestRequest request) throws ElasticsearchSecurityException {
		final String authorizationHeader = request.header("Authorization");
		if (null != authorizationHeader) {
			if (authorizationHeader.trim().toLowerCase().startsWith("bearer ")) {
				return new AuthCredentials(BeehiveAuth, authorizationHeader.split(" ")[1]).markComplete();
			} else if (authorizationHeader.trim().toLowerCase().startsWith("basic ")) {
				final String decodedBasicHeader = new String(DatatypeConverter.parseBase64Binary(
						authorizationHeader.split(" ")[1]), StandardCharsets.UTF_8);

				//username:password
				//special case
				//username must not contain a :, but password is allowed to do so
				//   username:pass:word
				//blank password
				//   username:

				final int firstColonIndex = decodedBasicHeader.indexOf(':');

				String username = null;
				String password = null;

				if (firstColonIndex > 0) {
					username = decodedBasicHeader.substring(0, firstColonIndex);

					if (decodedBasicHeader.length() - 1 != firstColonIndex) {
						password = decodedBasicHeader.substring(firstColonIndex + 1);
					} else {
						//blank password
						password = "";
					}
				}

				if (null == username || null == password) {
					return null;
				} else {
					return new AuthCredentials(username, password.getBytes(StandardCharsets.UTF_8)).markComplete();
				}
			} else {
				return null;
			}
		} else if (request.hasContent()) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				BeehiveAuthCredential credential = mapper.readValue(request.content().streamInput(),
						BeehiveAuthCredential.class);
				return new AuthCredentials(credential.getUserID(),
						credential.getPassword().getBytes(StandardCharsets.UTF_8)).markComplete();
			} catch (IOException e) {
				throw new ElasticsearchSecurityException(e.getMessage(), e);
			}
		}
		return null;
	}

	@Override
	public boolean reRequestAuthentication(RestChannel channel, AuthCredentials credentials) {
		if (null != credentials && BeehiveAuth.equals(credentials.getUsername())) {
			return false;
		}

		final BytesRestResponse wwwAuthenticateResponse = new BytesRestResponse(RestStatus.UNAUTHORIZED);
		wwwAuthenticateResponse.addHeader("WWW-Authenticate", "Basic realm=\"Search Guard\"");
		channel.sendResponse(wwwAuthenticateResponse);
		return true;
	}
}
