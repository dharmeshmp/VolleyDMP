package com.initfusion.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class JacksonRequest<ResponseType> extends JsonRequest<ResponseType> {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Class<ResponseType> responseClass;
	
	public JacksonRequest(String url, JSONObject parameterObject,
						  Class<ResponseType> responseClass,
						  Response.Listener<ResponseType> listener,
						  Response.ErrorListener errorListener) {

		this(Method.POST, url, null, parameterObject, responseClass, listener,
				errorListener);
	}

	public JacksonRequest(int method, String url, Map<String, String> headers,
						  JSONObject parameterObject, Class<ResponseType> responseClass,
						  Response.Listener<ResponseType> listener,
						  Response.ErrorListener errorListener) {

		super(method, url, parameterObject.toString(), listener, errorListener);

		this.responseClass = responseClass;
	}

	public Map<String, String> getHeaders() throws AuthFailureError {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json; charset=utf-8");
		return headers;
	}

	@Override
	protected Response<ResponseType> parseNetworkResponse(
			NetworkResponse response) {
		try {

			String json = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			
			Log.e("Response", json);



			ResponseType result = objectMapper.readValue(json, responseClass);


			return Response.success(result,
					HttpHeaderParser.parseCacheHeaders(response));

		} catch (UnsupportedEncodingException e) {

			Log.i("Jackson Error", e.getMessage());

			return Response.error(new ParseError(e));
		} catch (JsonMappingException e) {

			Log.i("Jackson Error", e.getMessage());
			return Response.error(new ParseError(e));
		} catch (JsonParseException e) {

			Log.i("Jackson Error", e.getMessage());
			return Response.error(new ParseError(e));
		} catch (IOException e) {

			Log.i("Jackson Error", e.getMessage());
			return Response.error(new ParseError(e));
		}
	}

}