package net.matchbox.api;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.params.HttpConnectionParams;

import org.apache.http.impl.client.BasicResponseHandler;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.params.CoreConnectionPNames;
import org.json.*;

public class Connection {
	private DefaultHttpClient httpClient;
	private String apiUrl;
	private String userName;
	private String password;

	public Connection(String username, String password) {
		apiUrl = "https://app.admitpad.com/api/v1/";
		this.userName = username;
		this.password = password;
		httpClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 100000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 100000);
	}

	public void finalize() {
		httpClient.getConnectionManager().shutdown();
	}

	public ArrayList<JSONObject> getApplicationStates() throws JSONException {
		return getList(this.apiUrl + "application/");
	}

	public ArrayList<JSONObject> getApplications() throws JSONException {
		return getList(this.apiUrl + "applicationstate/");
	}

	public ArrayList<JSONObject> getList(String listUrl) throws JSONException {
		ArrayList<JSONObject> objectsToReturn = new ArrayList<JSONObject>();
		boolean didReachEndOfQuerySet = false;
		int paginationOffset = 0;
		int paginationLimit = 1;
		while (!didReachEndOfQuerySet) {
			String requestUri = listUrl + "?completed=1&all=1&offset="
					+ paginationOffset + "&limit=" + paginationLimit;
			String content = this.get(requestUri);
			JSONObject contentParsed = new JSONObject(content);
			// read the objects that were returned into the local data structure
			JSONArray objects = contentParsed.getJSONArray("objects");
			for (int i = 0; i < objects.length(); i++) {
				objectsToReturn.add(objects.getJSONObject(i));
			}

			// pagination
			JSONObject meta = contentParsed.getJSONObject("meta");
			Object nextUrl = meta.get("next");
			if (nextUrl != JSONObject.NULL) {
				paginationOffset += paginationLimit;
			} else {
				break;
			}
		}
		return objectsToReturn;
	}


	private String get(String uriAsString) {
		String content = "";
		try {
			// Set url
			URI uri = new URI(uriAsString);
			httpClient.getCredentialsProvider().setCredentials(
					new AuthScope(uri.getHost(), uri.getPort(),
							AuthScope.ANY_SCHEME),
					new UsernamePasswordCredentials(this.userName,
							this.password));
			HttpGet request = new HttpGet(uri);
			request.addHeader("accept", "application/json");
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream responseIS = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(responseIS));
				String line = reader.readLine();
				while (line != null) {
					// System.out.println(line);
					content += line;
					line = reader.readLine();
				}
			} else {
				System.out.println("Resource not available");
				System.out.println(content);
			}
		} catch (URISyntaxException e) {
			System.out.println(e.getMessage());
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
		} catch (ConnectTimeoutException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			// if (httpClient != null) {
			// httpClient.getConnectionManager().shutdown();
			// }
		}
		return content;
	}

}
