package com.kii.beehive.portal.plugin.searchguard.auth;

import static com.kii.beehive.portal.plugin.searchguard.auth.BeehiveHttpAuthenticator.BeehiveAuth;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.ElasticsearchSecurityException;
import org.elasticsearch.common.settings.Settings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.floragunn.searchguard.auth.AuthenticationBackend;
import com.floragunn.searchguard.configuration.ConfigChangeListener;
import com.floragunn.searchguard.user.AuthCredentials;
import com.floragunn.searchguard.user.User;
import com.kii.beehive.portal.entitys.AuthRestBean;
import com.kii.beehive.portal.plugin.searchguard.data.BeehiveAuthCredential;


/**
 * Created by hdchen on 6/21/16.
 */
public class BeehiveAuthenticationProxy implements AuthenticationBackend, ConfigChangeListener {

	private static final String PROP_API_ROOT = "beehive.api.root";

	private static final String PROP_ADMIN_TOKEN = "beehive.admin.token";

	private final ObjectMapper mapper;

	private final String ADMIN_TOKEN;

	private final String API_ROOT;

	private final String VALIDATE_TOKEN = "/oauth2/validateLoginAccessToken";

	private final String LOGIN = "/oauth2/login";

	private final String QUERY_USER = "/usermanager/";

	private static final String ADMIN = "admin";

	public BeehiveAuthenticationProxy(final Settings settings) {
		super();
		API_ROOT = settings.get(PROP_API_ROOT);
		ADMIN_TOKEN = settings.get(PROP_ADMIN_TOKEN);
		mapper = new ObjectMapper();
	}

	@Override
	public String getType() {
		return "com.kii.beehive.portal.plugin.searchguard.auth.BeehiveAuthenticationProxy";
	}

	@Override
	public User authenticate(AuthCredentials credentials) throws ElasticsearchSecurityException {
		if (!isInitialized()) {
			throw new ElasticsearchSecurityException(
					"Internal authentication backend not configured. May be Search Guard is not initialized.");
		}

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpPost request;
			if (BeehiveAuth.equals(credentials.getUsername())) {
				if (ADMIN_TOKEN.equals(credentials.getNativeCredentials().toString())) {
					return new User(ADMIN, Arrays.asList(ADMIN));
				}
				request = new HttpPost(API_ROOT + VALIDATE_TOKEN);
				request.setHeader("Authorization", "Bearer " + credentials.getNativeCredentials().toString());
				request.setEntity(new StringEntity(credentials.getNativeCredentials().toString()));
			} else {
				request = new HttpPost(API_ROOT + LOGIN);
				BeehiveAuthCredential authInfo = new BeehiveAuthCredential();
				authInfo.setUserID(credentials.getUsername());
				authInfo.setPassword(new String(credentials.getPassword()));
				request.setEntity(AccessController.doPrivileged(new PrivilegedAction<StringEntity>() {
					@Override
					public StringEntity run() {
						try {
							return new StringEntity(mapper.writeValueAsString(authInfo));
						} catch (UnsupportedEncodingException e) {
							throw new ElasticsearchSecurityException(
									BeehiveAuthenticationProxy.class.getSimpleName() + ": " +
											e.getMessage(), e);
						} catch (JsonProcessingException e) {
							throw new ElasticsearchSecurityException(
									BeehiveAuthenticationProxy.class.getSimpleName() + ": " +
											e.getMessage(), e);
						}
					}
				}));
			}
			request.setHeader("Content-Type", "application/json");

			HttpResponse response = client.execute(request);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				StringBuilder sb = new StringBuilder();
				char[] buf = new char[512];
				int len = 0;
				BufferedReader br = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				while (0 < (len = br.read(buf))) {
					sb.append(buf, 0, len);
				}
				AuthRestBean auth = AccessController.doPrivileged(
						new PrivilegedAction<AuthRestBean>() {
							@Override
							public AuthRestBean run() {
								try {
									return mapper.readValue(sb.toString(), AuthRestBean.class);
								} catch (IOException e) {
									throw new ElasticsearchSecurityException(
											BeehiveAuthenticationProxy.class.getSimpleName() + ": " +
													e.getMessage(), e);
								}
							}
						}
				);
				System.out.println("User: " + auth.getUser().getUserName() + ", Role: " + auth.getUser().getRoleName());
				return new User(auth.getUser().getUserName(), Arrays.asList(auth.getUser().getRoleName()));
			} else {
				throw new ElasticsearchSecurityException(response.getStatusLine().getStatusCode() + ", " + response
						.getEntity().getContent().toString());
			}
		} catch (IOException e) {
			throw new ElasticsearchSecurityException(
					BeehiveAuthenticationProxy.class.getSimpleName() + ": " +
							e.getMessage(), e);
		}
	}

	@Override
	public boolean exists(User user) {
		if (!isInitialized()) {
			throw new ElasticsearchSecurityException(
					"Internal authentication backend not configured. May be Search Guard is not initialized.");
		}

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpGet request = new HttpGet(API_ROOT + QUERY_USER + user.getName());
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Authorization", "Bearer " + ADMIN_TOKEN);
			HttpResponse response = client.execute(request);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				return true;
			}
		} catch (IOException e) {
			throw new ElasticsearchSecurityException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public void onChange(String event, Settings settings) {
	}

	@Override
	public void validate(String event, Settings settings) throws ElasticsearchSecurityException {

	}

	@Override
	public boolean isInitialized() {
		return null != API_ROOT && !API_ROOT.isEmpty() && null != ADMIN_TOKEN &&
				!ADMIN_TOKEN.isEmpty();
	}
}
