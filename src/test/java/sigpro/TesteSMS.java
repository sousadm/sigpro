package sigpro;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;

public class TesteSMS {

	static String awsKey = "AKIAJRVQS7BGH46MOD4A";
	static String awsSecret = "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";

	static String mensagem = "bom dia";
	static String mobile = "+5585996835561";

	public static void main(String[] args) {
		// Used for authenticating to AWS
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsKey, awsSecret);
		// Create SNS Client
		AmazonSNS snsClient = AmazonSNSClient.builder().withRegion(Regions.AP_SOUTHEAST_2)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

		try {
			snsClient.publish(new PublishRequest().withMessage(mensagem).withPhoneNumber(mobile));
			System.out.println("enviado");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
