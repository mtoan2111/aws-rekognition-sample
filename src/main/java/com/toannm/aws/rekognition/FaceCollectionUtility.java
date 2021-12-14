package com.toannm.aws.rekognition;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 */
public class FaceCollectionUtility {
	public static void main(String[] args) {
		final String USAGE = "\n" +
				"Usage: \n\n" +
				"   <1> Create new collection\n" +
				"   <2> Get created collections\n" +
				"   <3> Upload an image into a collection\n" +
				"   <4> Find an image\n";
		
		Region region = Region.AP_SOUTHEAST_1;
		RekognitionClient rekClient = RekognitionClient.builder()
				.region(region)
				.build();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Shutdown hook ran!");
				rekClient.close();
				System.exit(1);
			}
		});
		
		while (true) {
			System.out.println(USAGE);
			System.out.print("Choose an options: ");
			
			Scanner optionScanner = new Scanner(System.in);
			int option = Integer.parseInt(optionScanner.nextLine());
			optionScanner.close();
			switch (option) {
				case 1:
					createMyCollection(rekClient);
					break;
				case 2:
					listFaceCollections(rekClient);
					break;
				case 3:
					addFaceToCollection(rekClient);
					break;
				case 4:
					searchFaceByImage(rekClient);
					break;
				default:
					break;
			}
		}
	}
	
	public static void createMyCollection(RekognitionClient rekClient) {
		
		try {
			System.out.println("Creating collection");
			System.out.print("Type your collectionId: ");
			Scanner collectionIdScanner = new Scanner(System.in);
			String collectionId = collectionIdScanner.nextLine();
			collectionIdScanner.close();
			
			CreateCollectionRequest collectionRequest = CreateCollectionRequest.builder()
					.collectionId(collectionId)
					.build();
			
			CreateCollectionResponse collectionResponse = rekClient.createCollection(collectionRequest);
			System.out.println("CollectionArn : " +
					collectionResponse.collectionArn());
			
					System.out.println("Status code : " +
					collectionResponse.statusCode().toString());
			
		} catch (RekognitionException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public static List<String> listFaceCollections(RekognitionClient rekClient) {
		try {
			ListCollectionsRequest listCollectionsRequest = ListCollectionsRequest.builder().maxResults(10).build();
			
			ListCollectionsResponse listCollectionsResponse = rekClient.listCollections(listCollectionsRequest);
			List<String> collectionIds = listCollectionsResponse.collectionIds();
			
			return collectionIds;
		} catch (RekognitionException e) {
			return null;
		}
	}
	
	public static void addFaceToCollection(RekognitionClient rekClient) {
		String collectionId = getCollectionId(rekClient);
		if (collectionId == ""){
			return;
		}
		FaceUtility fc = new FaceUtility();
		fc.updateFaceToCollection(rekClient, collectionId);
	}
	
	public static void searchFaceByImage(RekognitionClient rekClient){
		String collectionId = getCollectionId(rekClient);
		if (collectionId == ""){
			return;
		}
		FaceUtility fc = new FaceUtility();
		fc.FaceSearchingByImage(rekClient, collectionId);
	}
	
	private static String getCollectionId(RekognitionClient rekClient){
		System.out.print("Type your collection id here:");
		
		Scanner collectionIdScanner = new Scanner(System.in);
		String collectionId = collectionIdScanner.nextLine();
		collectionIdScanner.close();

		List<String> collectionIds = listFaceCollections(rekClient);

		if (!collectionIds.contains(collectionId)) {
			System.out.println("Collection Ids not found");
			System.out.println("Your collections:");
			
			for (String id : collectionIds) {
				System.out.println(id);
			}
			
			return "";
		}
		return collectionId;
	}
}
