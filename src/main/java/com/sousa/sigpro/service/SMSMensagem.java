package com.sousa.sigpro.service;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public class SMSMensagem {

	static String awsKey = "AKIAJRVQS7BGH46MOD4A";
	static String awsSecret = "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";
	
	public static PublishResult Enviar(String mobile, String mensagem) {

		Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();
		// smsAttributes.put("AWS.SNS.SMS.SenderID",
		// new
		// MessageAttributeValue().withStringValue("mySenderID").withDataType("String"));
		// smsAttributes.put("AWS.SNS.SMS.MaxPrice",
		// new MessageAttributeValue().withStringValue("0.50").withDataType("Number"));
		smsAttributes.put("AWS.SNS.SMS.SMSType",
				new MessageAttributeValue().withStringValue("Promotional").withDataType("String"));

		BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsKey, awsSecret);
		AmazonSNS snsClient = AmazonSNSClient.builder().withRegion(Regions.AP_SOUTHEAST_2)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
		return snsClient.publish(new PublishRequest().withMessage(mensagem).withPhoneNumber(mobile)
				.withMessageAttributes(smsAttributes));
	}

}