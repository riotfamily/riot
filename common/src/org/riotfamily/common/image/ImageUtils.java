/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.image;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.util.FormatUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class ImageUtils {
	
	static {
        System.setProperty("java.awt.headless", "true");
    }
    
    public static final String FORMAT_JPG = "jpg";
    
    public static final String FORMAT_PNG = "png";
    
    public static byte[] PIXEL_GIF = FormatUtils.hexStringToByteArray(
    		"4749463839610100010091ff00ffffff" +
    		"000000c0c0c000000021f90401000002" +
    		"002c0000000001000100000202540100" +
    		"3b");
    
    private ImageUtils() {
    }
    
    public static void serveTransparentPixelGif(OutputStream out) throws IOException {
    	IOUtils.serve(PIXEL_GIF, out);
    }
    
    public static BufferedImage readImage(File f) throws IOException {
    	return read(new FileInputStream(f));
    }
    
    public static BufferedImage read(Resource res) throws IOException {
    	if (res != null) {
    		return read(res.getInputStream());
    	}
    	return null;
    }
    
    public static BufferedImage read(InputStream in) throws IOException {
    	try {
    		return ImageIO.read(in);
    	}
    	finally {
    		try {
    			in.close();
    		}
    		catch (Exception e) {
    		}
    	}
    }
    
    public static void write(RenderedImage im, File f) throws IOException {
    	String formatName = FormatUtils.getExtension(f.getName()).toLowerCase();
    	if (!formatName.equals(FORMAT_JPG) && !formatName.equals(FORMAT_PNG)) {
    		formatName = FORMAT_JPG;
    	}
    	write(im, formatName, f);
    }
    
	public static void write(RenderedImage im, String formatName,
			Object dest) throws IOException {
		
    	ImageWriter writer = null;
        ImageOutputStream ios = null;
        try {
	        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(formatName);
	        if (it.hasNext()) {
	            writer = it.next();
	        }
	        Assert.notNull(writer, "No ImageWriter available for format " 
	        		+ formatName);
	        
	        ios = ImageIO.createImageOutputStream(dest);
	        writer.setOutput(ios);
	        
	        ImageWriteParam iwparam = null;
	        
	        if (formatName.equals(FORMAT_JPG)) {
		        iwparam = new JPEGImageWriteParam(Locale.getDefault());
		        iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		        iwparam.setCompressionQuality(1);
	        }
	        
	        writer.write(null, new IIOImage(im, null, null), iwparam);
	        ios.flush();
        }
        catch (SocketException e) {
        }
        catch (IOException e) {
			if (!SocketException.class.isInstance(e.getCause())) {
				throw e;
			}
		}
        finally {
        	if (writer != null) {
        		writer.dispose();
        	}
        	if (ios != null) {
        		ios.close();
        	}
        }
    }

}
