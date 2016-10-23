package website.services;

import java.io.File;
import java.io.IOException;

import org.apache.tapestry5.upload.services.UploadedFile;

import website.model.admin.FileSize;

public interface FileManager {

	public static final String catalog = "catalog";
	
	boolean supports(String name);
	
	void saveFile(String name,FileSize fileSize, UploadedFile original) throws IOException;
	
	File getFile(String name,FileSize fileSize) throws IOException;

	String getPath(String key, FileSize preview);

	File getCatalogFile(String catalogPath);

}
