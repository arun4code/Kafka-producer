package com.java.batch.job;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.time.Instant;
import java.util.Base64;

import org.apache.http.HttpHeaders;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;

import com.java.batch.dto.PeopleDTO;
import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;

public class Test {

	public void graphLoad() {
		ConnectionPolicy connectionPolicy = new ConnectionPolicy();
		connectionPolicy.setMaxPoolSize(1000);
		DocumentClient client = new DocumentClient("HOST", "MASTER_KEY", connectionPolicy, ConsistencyLevel.Session);
	}

	/*@Bean
	public ItemWriter<PeopleDTO> writer() {
	return items->{
	for(PeopleDTO bean:items)
	{
	System.err.println(bean.getFileName());
	//System.err.println(“bean.toString() — -”+bean.getJsonFile().createNewFile());
	FileInputStream in = new FileInputStream (bean.getJsonFile());
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	byte[] buf = new byte[1024];
	for (int readNum; (readNum = in.read(buf)) != -1;) {
	bos.write(buf, 0, readNum);
	}
	byte[] byteArray = bos.toByteArray();
	Instant instant = Instant.now();
	long timeStampMillis = instant.toEpochMilli();
	String rowKey=bean.getFileName();
	HttpHeaders headers = new HttpHeaders();
	headers.set(“Accept”, “application/json”);
	headers.set(“content-type”, “application/json”);
	String fileName=bean.getFileName();
	String rowKeyHash=Base64.getEncoder().encodeToString(bean.getFileName().getBytes(“utf-8”));
	String URL =”http://localhost:8157/FILE_DATA/"+rowKeyHash;
	String jsonPaylod=”{\”Row\”: [{\”key\”: “+ “\””
	+ rowKeyHash
	+ “\”,\”Cell\”: [{\”column\”: \””
	+ “RGF0YUNGOnRlc3R4bWw=” //XML test
	+ “\”, \””
	+ “timestamp\”:”
	+ “\””+timeStampMillis+”\””
	+ “,\”$\”: \””+ Base64.getEncoder().encodeToString(byteArray)
	+ “\”}]}]}”;
	System.out.println(“byteArray===”+byteArray);
	System.out.println(“jsonPaylod===”+jsonPaylod);
	HttpEntity<String> entity = new HttpEntity<>(jsonPaylod, headers);
	ResponseEntity<String> response = restTemplate().exchange(URL, HttpMethod.POST, entity,String.class);
	System.out.println(“Response: “ + response);
	in.close();
	}
	};
	}*/
}
