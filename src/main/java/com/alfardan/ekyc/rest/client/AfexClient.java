package com.alfardan.ekyc.rest.client;

import org.springframework.beans.factory.annotation.Value;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class AfexClient {
	
	@Value("${afex.url}")
    private String url;
	
	public String getOutputFromAfex() {
		Client client = Client.create();

		WebResource webResource = client.resource(url);
		
		ClientResponse response = webResource.header("Content-Type", "application/x-www-form-urlencoded")
				.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException(
					"Failed : HTTP error code : " + response.getStatus() + "--" + response.getHeaders());
		}

		String output = response.getEntity(String.class);

		System.out.println("Output from Server .... \n");
		System.out.println(output);
		return output;
	}
	
}
