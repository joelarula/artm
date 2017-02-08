package website.services.impl;


import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import website.model.admin.ModelPhotoSize;
import website.model.admin.Size;
import website.model.admin.SupportedImageExtensions;
import website.model.database.Model;
import website.services.FileManager;
import website.services.WebsiteModule;

public class FileManagerImpl implements FileManager{

	private static final Logger logger = LoggerFactory.getLogger(FileManagerImpl.class);

	private final ObjectMapper mapper = new ObjectMapper();
	private final ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
	
	@Override
	public boolean supportsPhotoExtension(String name) {
		if(name.toLowerCase().endsWith(SupportedImageExtensions.PNG.name().toLowerCase())
		|| name.toLowerCase().endsWith(SupportedImageExtensions.JPG.name().toLowerCase())){
			logger.info("supported {}",name);
			return true;
		}
		logger.warn("not supported {}",name);
		return false;
	}
	
	@Override
	public void savePhoto(String name, ModelPhotoSize fileSize, InputStream stream,String fname) throws IOException  {
		SupportedImageExtensions ext = SupportedImageExtensions.valueOf(fname.substring(fname.length()-3).toUpperCase());
		
		String path = prepareCatalogPath(name,fileSize);
		BufferedImage source = ImageIO.read(stream);
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
	public Size getGraphics(String name) throws FileNotFoundException, IOException {
		final File f = new File(prepareCatalogPath(name,ModelPhotoSize.ORIGINAL));
		if(f.exists()){		
			return this.getImageDimension(f);
		}
		return null;
	
	}
	
	@Override
	public File getPhoto(String name, ModelPhotoSize fileSize) throws IOException {
		if(fileSize.equals(ModelPhotoSize.ORIGINAL)){
			return new File(prepareCatalogPath(name,fileSize));
		}else{
			logger.info("scaling {} to  {}",name,fileSize);
			final File file = new File(prepareCatalogPath(name,fileSize));
			if(!file.exists()){		
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				final File original = this.getPhoto(name, ModelPhotoSize.ORIGINAL);
				final BufferedImage source = ImageIO.read(new FileInputStream(original));
				
				final Graphics2D g = source.createGraphics();
				final Rectangle r = g.getDeviceConfiguration().getBounds();
				logger.info("original {} {} ",r.width, r.height);
				Double scaleFactor = null;
				Scalr.Mode mode= null;
				Double width = fileSize.getMaxWidthPx();
				Double height = fileSize.getMaxHeightPx();
				if(r.width >= r.height ){
					mode  = Scalr.Mode.FIT_TO_WIDTH;
					scaleFactor = new Double(fileSize.getMaxWidthPx() / r.width * fileSize.getMaxWidthPx());
					height  = new Double(fileSize.getMaxWidthPx() / r.width * r.height );
				}else{
					mode  = Scalr.Mode.FIT_TO_HEIGHT;
					scaleFactor = new Double(fileSize.getMaxHeightPx() / r.height * fileSize.getMaxHeightPx());
					width  = new Double(fileSize.getMaxHeightPx() / r.height * r.width );
				}
								
				BufferedImage  target = Scalr.resize(
                    source,  
                    Scalr.Method.QUALITY, 
                    mode,
                    width.intValue(), 
                    height.intValue(),
                    Scalr.OP_ANTIALIAS);
				
				final Graphics2D tg = target.createGraphics();
				
				tg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				tg.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				tg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				Rectangle tr = tg.getDeviceConfiguration().getBounds();
				
				logger.info("scaling "+name+"to "+fileSize.name()+" {}  {} ",tr.width,tr.height);

				ImageIO.write(target, "png", file);
				
			}
			return file;
		}
		
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
		
			try{
				ModelPhotoSize size = ModelPhotoSize.valueOf(tokens[0].toUpperCase());
				File file = this.getPhoto(tokens[1].substring(0,tokens[1].length()-4), size);
				logger.info("returning  {}",file.getAbsolutePath());
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

	@Override
	public void saveModel(Model model) throws IOException {
		
		final File file = new File(prepareModelPath(model.getKey()));
		if(!file.exists()){		
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file);
		writer.writeValue(out, model);
	
	}

	private String prepareModelPath(String modelKey) {
		return WebsiteModule.dbHomeDir+File.separator+modelKey+".json";
	}
	
	private String prepareModelKey(String path) {
		String prefix = path.substring((WebsiteModule.dbHomeDir+File.separator).length());
		return prefix.substring(0,prefix.length()-5);
	}

	@Override
	public Model getModel(String modelKey) throws JsonParseException, JsonMappingException, IOException {
		final File file = new File(prepareModelPath(modelKey));
		FileInputStream in = new FileInputStream(file);
		Model m = mapper.readValue(in, Model.class);
		logger.warn("model {}  found in disk {}",m.getKey());
		return m;
	}

	@Override
	public Map<String, Model> loadModels() {
		Map<String,Model> models = new HashMap<String,Model>();
		File file = new File(WebsiteModule.dbHomeDir);
		for(File f : file.listFiles()){
		  try {
			Model m = this.getModel(prepareModelKey(f.getAbsolutePath()));
			models.put(m.getKey(), m);
		  } catch (JsonParseException e) {
			  logger.info(e.getMessage());
		  } catch (JsonMappingException e) {
			  logger.info(e.getMessage());
		  } catch (IOException e) {
			  logger.info(e.getMessage());
		  }
		}
		
		return models;
	}



	/**
	 * Gets image dimensions for given file 
	 * @param imgFile image file
	 * @return dimensions of image
	 * @throws IOException if the file is not a known image
	 */
	public static Size getImageDimension(File imgFile) throws IOException {
	  int pos = imgFile.getName().lastIndexOf(".");
	  if (pos == -1)
	    throw new IOException("No extension for file: " + imgFile.getAbsolutePath());
	  String suffix = imgFile.getName().substring(pos + 1);
	  Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
	  while(iter.hasNext()) {
	    ImageReader reader = iter.next();
	    try {
	      ImageInputStream stream = new FileImageInputStream(imgFile);
	      reader.setInput(stream);
	      int width = reader.getWidth(reader.getMinIndex());
	      int height = reader.getHeight(reader.getMinIndex());
	      return new Size(width, height);
	    } catch (IOException e) {
	      logger.warn("Error reading: " + imgFile.getAbsolutePath(), e);
	    } finally {
	      reader.dispose();
	    }
	  }

	  throw new IOException("Not a known image file: " + imgFile.getAbsolutePath());
	}



}
