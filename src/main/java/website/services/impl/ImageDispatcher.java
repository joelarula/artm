package website.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.assets.AssetRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.services.FileManager;


public class ImageDispatcher implements AssetRequestHandler{

	private static final Logger logger = LoggerFactory.getLogger(ImageDispatcher.class);

	private final FileManager fileManager;
	
	public ImageDispatcher(FileManager manager){
		this.fileManager = manager;
	}
	
	@Override
	public boolean handleAssetRequest(Request request, Response response,String extraPath) throws IOException {
		
		logger.info("dispatching {}",extraPath);
		File file = fileManager.getCatalogFile(extraPath);
		if(file != null){
			FileInputStream is = new FileInputStream(file);
			IOUtils.copy(is, response.getOutputStream("image/png"));
			return true;
		}else{
			logger.error ("unknown path {}",extraPath);
			return false;
		}
					
	}

}
