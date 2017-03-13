package website.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.assets.AssetRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.services.FileManager;


public class ImageDispatcher implements AssetRequestHandler{

	private static final Logger logger = LoggerFactory.getLogger(ImageDispatcher.class);
	
	 private static final int EOF = -1;

	private final FileManager fileManager;
	private final Boolean productionMode;
	
	public ImageDispatcher(FileManager manager, @Symbol(SymbolConstants.PRODUCTION_MODE) Boolean productionMode){
		this.fileManager = manager;
		this.productionMode = productionMode;
	}
	
	@Override
	public boolean handleAssetRequest(Request request, Response response,String extraPath) throws IOException {
		
		logger.debug("dispatching {}",extraPath);
		File file = fileManager.getCatalogFile(extraPath);
		if(file != null){
			if(productionMode){
				int CACHE_DURATION_IN_SECOND = 60 * 60 * 2 ; // 2 hours
				long CACHE_DURATION_IN_MS = CACHE_DURATION_IN_SECOND  * 1000;
				long now = System.currentTimeMillis();
				response.setHeader("Cache-Control", "max-age="+CACHE_DURATION_IN_SECOND);	
				response.setDateHeader("Last-Modified", file.lastModified());
				response.setDateHeader("Expires", now + CACHE_DURATION_IN_MS);
				//"Vary: Accept-Encoding" header:
				long ifm = request.getDateHeader("If-Modified-Since");
				if(file.lastModified() > ifm ){
					this.streamFile(file,request,response);
				}else{
					response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);;
				}
				
				
			}else{
				this.streamFile(file,request,response);
			}
			
			return true;
		}else{
			logger.error ("unknown path {}",extraPath);
			return false;
		}
					
	}

	private void streamFile(File file,Request request, Response response) throws IOException {
		byte[] buffer = new byte[1024 * 8];
		FileInputStream input = new FileInputStream(file);
		int i = file.getPath().length();
		OutputStream output = response.getOutputStream("image/"+file.getPath().substring(i-3,i));
		long count = 0;
	    int n = 0;
	    while (EOF != (n = input.read(buffer))) {
	    	output.write(buffer, 0, n);
	        count += n;
	        output.flush();
	    }
		
	}

}
