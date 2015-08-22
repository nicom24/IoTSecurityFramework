package it.unipr.iot.oas.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class RESTRequest {

	private static Logger logger = LoggerFactory.getLogger(RESTRequest.class);

	public static <T> T request(String url, Class<T> returnType) {
		try{
			HttpClient client = new DefaultHttpClient();
			HttpGet method = new HttpGet(url);
			HttpResponse response = client.execute(method);
			HttpEntity entity = response.getEntity();
			String body = null;
			if(entity != null){
				InputStream instream = entity.getContent();
				try{
					BufferedReader br = new BufferedReader(new InputStreamReader(instream));
					String line;
					while (((line = br.readLine()) != null)){
						if(body == null) body = line;
						else body += line;
					}
				} finally{
					instream.close();
					method.releaseConnection();
				}
			}
			if(body == null) return null;
			else{
				Gson gson = new Gson();
				return gson.fromJson(body, returnType);
			}
		} catch (JsonSyntaxException e){
			logger.error(e.getMessage());
		} catch (UnsupportedEncodingException e){
			logger.error(e.getMessage());
		} catch (ClientProtocolException e){
			logger.error(e.getMessage());
		} catch (IllegalStateException e){
			logger.error(e.getMessage());
		} catch (IOException e){
			logger.error(e.getMessage());
		}
		return null;
	}
	
}
