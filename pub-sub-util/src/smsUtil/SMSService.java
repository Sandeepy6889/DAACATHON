package smsUtil;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public class SMSService {

	public static void main(String[] args) {
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
					.withRegion(Regions.US_WEST_2)
					.withCredentials(credentialsProvider)
					.build();
         // Instantiate an Amazon SES client, which will make the service call with the supplied AWS credentials.
//         AmazonSNSClient snsClient = AmazonSNSClient.builder()
//        		 .withRegion("us-east-1")
//             .withCredentials(credentialsProvider)
//             // Choose the AWS region of the Amazon SES endpoint you want to connect to. Note that your production
//             // access status, sending limits, and Amazon SES identity-related settings are specific to a given
//             // AWS region, so be sure to select an AWS region in which you set up Amazon SES. Here, we are using
//             // the US East (N. Virginia) region. Examples of other regions that Amazon SES supports are US_WEST_2
//             // and EU_WEST_1. For a complete list, see http://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html
//             .build();
        //AmazonSNSClient snsClient = new AmazonSNSClient();
        String message = "My SMS message";
        String phoneNumber = "+919899735480";

        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("mySenderID") //The sender ID shown on the device.
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("0.50") //Sets the max price to 0.50 USD.
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Promotional") //Sets the type to promotional.
                .withDataType("String"));
        sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);
}

public static void sendSMSMessage(AmazonSNS snsClient, String message, 
		String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
        PublishResult result = snsClient.publish(new PublishRequest()
                        .withMessage(message)
                        .withPhoneNumber(phoneNumber)
                        .withMessageAttributes(smsAttributes));
        System.out.println(result); // Prints the message ID.
}

}
