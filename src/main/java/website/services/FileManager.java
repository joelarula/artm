package website.services;

import java.io.File;
import java.io.IOException;

import org.apache.tapestry5.upload.services.UploadedFile;

import website.model.admin.ModelPhotoSize;

public interface FileManager {

	public static final String catalog = "catalog";
	
	boolean supports(String name);
	
	void saveFile(String name,ModelPhotoSize fileSize, UploadedFile original) throws IOException;
	
	File getFile(String name,ModelPhotoSize fileSize) throws IOException;

	String getPath(String key, ModelPhotoSize preview);

	File getCatalogFile(String catalogPath);

}
