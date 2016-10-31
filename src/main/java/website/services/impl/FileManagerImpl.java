package website.services.impl;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.tapestry5.upload.services.UploadedFile;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.model.admin.ModelPhotoSize;
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
	public void saveFile(String name, ModelPhotoSize fileSize, UploadedFile file) throws IOException {
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

	private String prepareCatalogPath(String name, ModelPhotoSize fileSize) {
		return WebsiteModule.websiteFolder 
		+ File.separator + FileManager.catalog 
		+ File.separator + fileSize.name().toLowerCase()
		+ File.separator + name+".png";
	}

	@Override
	public File getFile(String name, ModelPhotoSize fileSize) throws IOException {
		if(fileSize.equals(ModelPhotoSize.ORIGINAL)){
			return new File(prepareCatalogPath(name,fileSize));
		}else{
			logger.info("scaling {} to  {}",name,fileSize);
			File file = new File(prepareCatalogPath(name,fileSize));
			//if(!file.exists()){		
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				File original = this.getFile(name, ModelPhotoSize.ORIGINAL);
				BufferedImage source = ImageIO.read(new FileInputStream(original));
				
				Graphics2D g = source.createGraphics();
				Rectangle r = g.getDeviceConfiguration().getBounds();
				
				Double scaleFactor = null;
				boolean scalingPossible = true; 
				if(r.width >= r.height ){
					scaleFactor = new Double(fileSize.getMaxWidthPx() / r.width * fileSize.getMaxWidthPx());
					scalingPossible  = new Double(fileSize.getMaxWidthPx() * r.height / r.width) < r.width;
				}else{
					scaleFactor = new Double(fileSize.getMaxHeightPx() / r.height * fileSize.getMaxHeightPx());
					scalingPossible  = new Double(fileSize.getMaxHeightPx()* r.width / r.height) < r.height;
				}
				
				logger.info("scaling to square {}  {}",scaleFactor,scalingPossible);
				
				BufferedImage target = source;
				if (scalingPossible){ 
                     target = Scalr.resize(source, scaleFactor.intValue());
				}
				
				
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
				ImageIO.write(target, "png", file);
				return file;
			//}	
		}
		//return null;
		
	}

	@Override
	public String getPath(String key, ModelPhotoSize fileSize) {
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
				ModelPhotoSize size = ModelPhotoSize.valueOf(tokens[0].toUpperCase());
				File file = this.getFile(tokens[1].substring(0,tokens[1].length()-4), size);
				return  file;
			}catch(Exception ex){
				logger.error(ex.toString());
			}
			
		}
		return null;
	}

	public static BufferedImage scale(BufferedImage imageToScale, int dWidth, int dHeight) {
        BufferedImage scaledImage = null;
        if (imageToScale != null) {
            scaledImage = new BufferedImage(dWidth, dHeight, imageToScale.getType());
            Graphics2D g = scaledImage.createGraphics();
    		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
            g.drawImage(imageToScale, 0, 0, dWidth, dHeight, null);
            g.dispose();
        }
        return scaledImage;
    }


}
