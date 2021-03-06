package website.services;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.tapestry5.upload.services.UploadedFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import website.model.admin.ModelPhotoSize;
import website.model.admin.Size;
import website.model.database.Model;

public interface FileManager {

	public static final String catalog = "catalog";
	
	boolean supportsPhotoExtension(String name);
	
	void savePhoto(String name,ModelPhotoSize fileSize, InputStream stream, String fname) throws IOException;
	
	File getPhoto(String name,ModelPhotoSize fileSize) throws IOException;
	
	Size getGraphics(String name) throws FileNotFoundException, IOException;

	String getPath(String key, ModelPhotoSize preview);

	File getCatalogFile(String catalogPath);

	void saveModel(Model model) throws IOException;

	Model getModel(String modelKey) throws JsonParseException, JsonMappingException, IOException;

	Map<String, Model> loadModels();

}
