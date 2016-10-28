package com.kii.beehive.portal.plugin.searchguard.auth;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import org.elasticsearch.ElasticsearchSecurityException;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import com.floragunn.searchguard.auth.HTTPAuthenticator;
import com.floragunn.searchguard.user.AuthCredentials;

/**
 * Created by hdchen on 6/21/16.
 */
public class BeehiveHttpAuthenticator implements HTTPAuthenticator {
	private final ESLogger log = Loggers.getLogger(this.getClass());

	public BeehiveHttpAuthenticator(final Settings unused) {
		super();
	}

	@Override
	public String getType() {
		return "com.kii.beehive.portal.plugin.searchguard.auth.BeehiveHttpAuthenticator";
	}

	@Override
	public AuthCredentials extractCredentials(RestRequest request) throws ElasticsearchSecurityException {
		String authorizationHeader = request.header("Authorization");
		if (null != authorizationHeader) {
			log.debug("Authorization {}", authorizationHeader);
			if (authorizationHeader.trim().toLowerCase().startsWith("bearer ")) {
				return new AuthCredentials(authorizationHeader, authorizationHeader.split(" ")[1]).markComplete();
			} else if (authorizationHeader.trim().toLowerCase().startsWith("basic ")) {
				String authInfo = authorizationHeader.split(" ")[1];
				final String decodedBasicHeader = new String(DatatypeConverter.parseBase64Binary(
						authInfo), StandardCharsets.UTF_8);

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
					return new AuthCredentials(authInfo, decodedBasicHeader).markComplete();
				}
			}
		}
		return null;
	}

	@Override
	public boolean reRequestAuthentication(RestChannel channel, AuthCredentials credentials) {
		final BytesRestResponse wwwAuthenticateResponse =
				new BytesRestResponse(RestStatus.UNAUTHORIZED, "UNAUTHORIZED");
		wwwAuthenticateResponse.addHeader("WWW-Authenticate", "Basic realm=\"Search Guard\"");
		channel.sendResponse(wwwAuthenticateResponse);
		return true;
	}
}
