package com.toannm.aws.rekognition;

import com.google.gson.Gson;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class FaceUtility {
	public void updateFaceToCollection(RekognitionClient rekClient, String collectionId) {
		addToCollection(rekClient, collectionId);
	}
	
	private void addToCollection(RekognitionClient rekClient, String collectionId) {
		String imgPath = this.getImagePath();
		if (imgPath == "") {
			return;
		}
		Image imgBytes = createImageStream(imgPath);
		
		if (imgBytes == null) {
			return;
		}
		
		List<FaceDetail> preFaceDetection = this.faceDection(rekClient, imgBytes);
		if (preFaceDetection != null && preFaceDetection.size() > 1){
			System.out.println("Please using a image which contains only one face");
			return;
		}
		
		IndexFacesRequest indexFacesRequest = IndexFacesRequest.builder()
				.collectionId(collectionId)
				.image(imgBytes)
				.maxFaces(10)
				.qualityFilter(QualityFilter.AUTO)
				.detectionAttributes(Attribute.ALL)
				.build();
		
		IndexFacesResponse indexFacesResponse = rekClient.indexFaces(indexFacesRequest);
		List<FaceRecord> faceRecords = indexFacesResponse.faceRecords();
		System.out.println("Indexed Faces:");
		Gson gson = new Gson();
		String faceRecordsSerialized = gson.toJson(faceRecords);
		System.out.println(faceRecordsSerialized);
		
		List<UnindexedFace> unindexedFaceRecords = indexFacesResponse.unindexedFaces();
		System.out.println("UnIndexed Faces:");
		String unindexedFaceRecordsSerialized = gson.toJson(unindexedFaceRecords);
		System.out.println(unindexedFaceRecordsSerialized);
	}
	
	public void FaceSearchingByFaceId(String faceId) {
	
	}
	
	public void FaceSearchingByImage(RekognitionClient rekClient, String collectionId) {
		String imgPath = this.getImagePath();
		if (imgPath == "") {
			return;
		}
		Image imgBytes = createImageStream(imgPath);
		
		if (imgBytes == null) {
			return;
		}
		
		SearchFacesByImageRequest searchFacesByImageRequest = SearchFacesByImageRequest.builder()
				.image(imgBytes)
				.maxFaces(10)
				.faceMatchThreshold(90F)
				.collectionId(collectionId)
				.build();
		
		SearchFacesByImageResponse searchFacesByImageResponse = rekClient.searchFacesByImage(searchFacesByImageRequest);
		List<FaceMatch> faceMatches = searchFacesByImageResponse.faceMatches();
		System.out.println("matching faces:");
		Gson gson = new Gson();
		String faceMatchesSerialized = gson.toJson(faceMatches);
		System.out.println(faceMatchesSerialized);
	}
	
	private String getImagePath() {
		final JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image File", "jpg", "jpeg", "png", "PNG", "JPG", "JPEG");
		fc.addChoosableFileFilter(filter);
		int retVal = fc.showOpenDialog(null);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String filePath = file.getPath();
			System.out.println(filePath);
			return filePath;
		}
		return "";
	}
	
	private Image createImageStream(String imgPath) {
		try {
			InputStream srcStream = new FileInputStream(imgPath);
			SdkBytes srcBytes = SdkBytes.fromInputStream(srcStream);
			
			Image srcImage = Image.builder()
					.bytes(srcBytes)
					.build();
			
			return srcImage;
			
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	private List<FaceDetail> faceDection(RekognitionClient rekClient, Image image) {
		DetectFacesRequest detectFaceRequest = DetectFacesRequest.builder()
				.image(image)
				.attributes(Attribute.ALL)
				.build();
		
		DetectFacesResponse detectFacesResponse = rekClient.detectFaces(detectFaceRequest);
		List<FaceDetail> faceDetails = detectFacesResponse.faceDetails();
		System.out.println("Detecting face:");
		Gson gson = new Gson();
		String faceDetailsSerialized = gson.toJson(faceDetails);
		System.out.println(faceDetailsSerialized);
		return faceDetails;
	}
}
