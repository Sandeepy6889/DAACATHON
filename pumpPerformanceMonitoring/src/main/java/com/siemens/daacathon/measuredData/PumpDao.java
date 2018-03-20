package com.siemens.daacathon.measuredData;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class PumpDao {
	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	static DynamoDB dynamoDB = new DynamoDB(client);

	static String tableName = "PumpMeasuredData";

	public static void main(String[] args) {
		createItems();
		retrieveItem();
	}

	private static void createItems() {

		Table table = dynamoDB.getTable(tableName);
		try {

			Item item = new Item().withPrimaryKey("Id", 1).withNumber("SuctionTemprature", 20).withNumber("MotarRPM",
					1495);
			table.putItem(item);

			item = new Item().withPrimaryKey("Id", 2).withNumber("SuctionTemprature", 70).withNumber("MotarRPM", 1495);
			table.putItem(item);

		} catch (Exception e) {
			System.err.println("Create items failed.");
			System.err.println(e.getMessage());

		}
	}

	private static void retrieveItem() {
		Table table = dynamoDB.getTable(tableName);

		try {

			Item item = table.getItem("Id", 1, "Id, SuctionTemprature, MotarRPM", null);

			System.out.println("Printing item after retrieving it....");
			System.out.println("Data fetched from DynmoDB " + item.toJSONPretty());

		} catch (Exception e) {
			System.err.println("GetItem failed.");
			System.err.println(e.getMessage());
		}

	}
}
