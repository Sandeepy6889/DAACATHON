package com.siemens.primecult.emailutil;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.siemens.storage.DBUtil;
import com.siemens.storage.Pair;

public class EmailUtil {

	
	public static void main(String[] args) {
		
	}
	
	public static String subscribeEMailID(String emailID)
	{
		
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
         try {
             credentialsProvider.getCredentials();
         } catch (Exception e) {
             throw new AmazonClientException(
                     "Cannot load the credentials from the credential profiles file. " +
                     "Please make sure that your credentials file is at the correct " +
                     "location (~/.aws/credentials), and is in valid format.",
                     e);
         }
         AmazonSNS snsClient = AmazonSNSClient.builder()
					.withRegion(Regions.US_EAST_1)
					.withCredentials(credentialsProvider)
					.build();

    	//create a new SNS topic
    	CreateTopicRequest createTopicRequest = new CreateTopicRequest("topicName1");
    	CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
    	//print TopicArn
    	System.out.println(createTopicResult);
    	String topicArn = createTopicResult.getTopicArn();
    	//subscribe to an SNS topic
    	SubscribeRequest subRequest = new SubscribeRequest(topicArn, "email", emailID);
    	snsClient.subscribe(subRequest);
    	//get request id for SubscribeRequest from SNS metadata
    	System.out.println("SubscribeRequest - " + snsClient.getCachedResponseMetadata(subRequest));
    	System.out.println("Check your email and confirm subscription.");
    	return topicArn;
	}
	
	public static void publishEmail(
			String raisedAlarm, String assetID )
	{
		try {

			ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
	        try 
	        {
	            credentialsProvider.getCredentials();
	        } catch (Exception e) {
	            throw new AmazonClientException(
	                    "Cannot load the credentials from the credential profiles file. " +
	                    "Please make sure that your credentials file is at the correct " +
	                    "location (~/.aws/credentials), and is in valid format.",
	                    e);
	        }
	        AmazonSNS snsClient = AmazonSNSClient.builder()
						.withRegion(Regions.US_EAST_1)
						.withCredentials(credentialsProvider)
						.build();
			//publish to an SNS topic
			StringBuilder msgBody = new StringBuilder();
			msgBody.append("Hi Admin,\n Please check, there is "+raisedAlarm+" in asset -"+assetID+".\nThanks.");
	        List<Object> columnValues = DBUtil.getColumnValues("select topic_arn from urls_info", "topic_arn");
	        String topicArn = ((Pair)columnValues.get(0)).getValue().toString();
	    	PublishRequest publishRequest = new PublishRequest(topicArn, msgBody.toString());
	    	PublishResult publishResult = snsClient.publish(publishRequest);
	    	//print MessageId of message published to SNS topic
	    	System.out.println("MessageId - " + publishResult.getMessageId());
		
		}
		catch(Exception e) {
			System.out.println("Mail not send");
		}
		
	}
	
}
