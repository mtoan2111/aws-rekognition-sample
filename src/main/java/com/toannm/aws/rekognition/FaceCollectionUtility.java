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
	static final Scanner scanner = new Scanner(System.in);
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
		
		while (true) {
			System.out.println(USAGE);
			System.out.print("Choose an options: ");


			int option = -1;
			if (scanner.hasNextLine()){
				option = Integer.parseInt(scanner.nextLine());
			}

			switch (option) {
				case 1:
					createMyCollection(rekClient);
					break;
				case 2:
					List<String> collectionIds = listFaceCollections(rekClient);
					for (String id : collectionIds) {
						System.out.println(id);
					}
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
			String collectionId = scanner.nextLine();
						
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
		}
	}
	
	public static List<String> listFaceCollections(RekognitionClient rekClient) {
		try {
			ListCollectionsRequest listCollectionsRequest = ListCollectionsRequest.builder().maxResults(10).build();
			
			ListCollectionsResponse listCollectionsResponse = rekClient.listCollections(listCollectionsRequest);
			List<String> collectionIds = listCollectionsResponse.collectionIds();
			
			return collectionIds;
		} catch (RekognitionException e) {
			System.out.println(e.getMessage());
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
		String collectionId = scanner.nextLine();

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
