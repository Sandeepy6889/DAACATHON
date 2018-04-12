package com.siemens.primecult.emailutil;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

public class MailService {

	 static final String FROM = "er.aarushi@gmail.com";  // Replace with your "From" address. This address must be verified.
	    static final String TO = "er.aarushi@gmail.com"; // Replace with a "To" address. If you have not yet requested
	                                                      // production access, this address must be verified.
	    static final String BODY = "This email was sent through Amazon SES by using the AWS SDK for Java.";
	    static final String SUBJECT = "Amazon SES test (AWS SDK for Java)";

		public static void main(String[] args)
	    {
//	    	//create a new SNS client and set endpoint
//	    	@SuppressWarnings("deprecation")
//			AmazonSNSClient snsClient = new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider());		                           
//	    	snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));
			System.out.println("Attempting to send an email through Amazon SNS by using the AWS SDK for Java...");
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
//	                [default]
//	        		 aws_access_key_id=AKIAJ4QBYRM52M73OARA
//	        		 aws_secret_access_key=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
	    	//create a new SNS topic
//	    	CreateTopicRequest createTopicRequest = new CreateTopicRequest("MyNewTopic");
//	    	CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
//	    	//print TopicArn
//	    	System.out.println(createTopicResult);
//	    	//get request id for CreateTopicRequest from SNS metadata		
//	    	System.out.println("CreateTopicRequest - " + snsClient.getCachedResponseMetadata(createTopicRequest));
	    	String topicArn = "arn:aws:sns:us-east-1:907340377072:smstopic";
	    	//subscribe to an SNS topic
	    	SubscribeRequest subRequest = new SubscribeRequest(topicArn, "email", FROM);
	    	snsClient.subscribe(subRequest);
	    	//get request id for SubscribeRequest from SNS metadata
	    	System.out.println("SubscribeRequest - " + snsClient.getCachedResponseMetadata(subRequest));
	    	System.out.println("Check your email and confirm subscription.");
	    	
	    	//publish to an SNS topic
	    	String msg = "My text published to SNS topic with email endpoint";
	    	PublishRequest publishRequest = new PublishRequest(topicArn, msg);
	    	PublishResult publishResult = snsClient.publish(publishRequest);
	    	//print MessageId of message published to SNS topic
	    	System.out.println("MessageId - " + publishResult.getMessageId());
	    	
	    	//delete an SNS topic
	    	DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
	    	snsClient.deleteTopic(deleteTopicRequest);
	    	//get request id for DeleteTopicRequest from SNS metadata
	    	System.out.println("DeleteTopicRequest - " + snsClient.getCachedResponseMetadata(deleteTopicRequest));
	    }
	
}
