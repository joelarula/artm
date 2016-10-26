package website.services.impl;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.tapestry5.upload.services.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.FileSize;
import website.model.admin.SupportedImageExtensions;
import website.pages.admin.Board;
import website.services.FileManager;
import website.services.WebsiteModule;

public class FileManagerImpl implements FileManager{

	private static final Logger logger = LoggerFactory.getLogger(FileManagerImpl.class);

	
	@Override
	public boolean supports(String name) {
		if(name.toLowerCase().endsWith(SupportedImageExtensions.PNG.name().toLowerCase())
		|| name.toLowerCase().endsWith(SupportedImageExtensions.JPG.name().toLowerCase())){
			logger.info("supported {}",name);
			return true;
		}
		logger.warn("not supported {}",name);
		return false;
	}
	
	@Override
	public void saveFile(String name, FileSize fileSize, UploadedFile file) throws IOException {
		SupportedImageExtensions ext = SupportedImageExtensions.valueOf(file.getFileName().substring(file.getFileName().length()-3).toUpperCase());
		
		String path = prepareCatalogPath(name,fileSize);
		BufferedImage source = ImageIO.read(file.getStream());
		Graphics2D g = source.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		File target  = new File(path);
		if(!target.exists()){		
			if(!target.getParentFile().exists()){
				target.getParentFile().mkdirs();
			}
			target.createNewFile();
		}	
		logger.info("saving file into {}",path);
		ImageIO.write(source, "png", target);	
	
	}

	private String prepareCatalogPath(String name, FileSize fileSize) {
		return WebsiteModule.websiteFolder 
		+ File.separator + FileManager.catalog 
		+ File.separator + fileSize.name().toLowerCase()
		+ File.separator + name+".png";
	}

	@Override
	public File getFile(String name, FileSize fileSize) throws IOException {
		if(fileSize.equals(FileSize.ORIGINAL)){
			return new File(prepareCatalogPath(name,fileSize));
		}else{
			File file = new File(prepareCatalogPath(name,fileSize));
			//if(!file.exists()){		
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				File original = this.getFile(name, FileSize.ORIGINAL);
				BufferedImage source = ImageIO.read(new FileInputStream(original));
				Graphics2D g = source.createGraphics();
				
				
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				ImageIO.write(source, "png", file);
				return file;
			//}	
		}
		//return null;
		
	}

	@Override
	public String getPath(String key, FileSize fileSize) {
		return  FileManager.catalog 
				+ "/" + fileSize.name().toLowerCase()
				+ "/" + key+".png";
	}

	@Override
	public File getCatalogFile(String catalogPath) {
		String[] tokens = catalogPath.split("/");
		
		if(tokens.length == 2){
			logger.info("tokens {} {}",tokens[0],tokens[1]);
			try{
				FileSize size = FileSize.valueOf(tokens[0].toUpperCase());
				File file = this.getFile(tokens[1].substring(0,tokens[1].length()-4), size);
				return  file;
			}catch(Exception ex){
				logger.error(ex.toString());
			}
			
		}
		return null;
	}



}
