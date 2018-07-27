package io.opensaber.client.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistryTestBase {

	protected RestTemplate restTemplate;
	protected static Type mapType = new TypeToken<Map<String, String>>() {}.getType();

	private static String ssoUrl = System.getenv("sunbird_sso_url");
	private static String ssoClientId = System.getenv("sunbird_sso_client_id");
	private static String ssoUsername = System.getenv("sunbird_sso_username");
	private static String ssoPassword = System.getenv("sunbird_sso_password");
	private static String ssoRealm = System.getenv("sunbird_sso_realm");

	public static String accessToken = generateAuthToken();

	

	public static String extractIdWithoutContext(String label) {
		String extractedId = label;
		Pattern pattern = Pattern.compile("^" + Pattern.quote(Constants.INTEGRATION_TEST_BASE_URL) + "(.*?)$");
		Matcher matcher = pattern.matcher(label);
		if(matcher.find()) {
			extractedId = matcher.group(1);
		}
		return extractedId;
	}

	private static String generateAuthToken() {
		String ssoAuthBody = new StringBuilder()
				.append("client_id=").append(ssoClientId)
				.append("&username=").append(ssoUsername)
				.append("&password=").append(ssoPassword)
				.append("&grant_type=password").toString();
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl("no-cache");
		headers.set("content-type", "application/x-www-form-urlencoded");
		HttpEntity<String> request = new HttpEntity<>(ssoAuthBody, headers);
		String url = ssoUrl + "realms/" + ssoRealm + "/protocol/openid-connect/token ";
		ResponseEntity<String> response = new RestTemplate().postForEntity(url, request, String.class);
		Map<String, String> myMap = new Gson().fromJson(response.getBody(), mapType);
		return myMap.getOrDefault("access_token", "");
	}

}
