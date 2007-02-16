/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.thumbnail;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * Thumbnailer that uses the Java ImageIO API.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ImageIOThumbnailer implements Thumbnailer {

	public static final String FORMAT_JPG = "jpg";
	
	public static final String FORMAT_PNG = "png";
	
	private static Log log = LogFactory.getLog(ImageIOThumbnailer.class);
	
	private static Set supportedMimeTypes;
	
    static {
        System.setProperty("java.awt.headless", "true");
        supportedMimeTypes = new HashSet();
        String[] mime = ImageIO.getReaderMIMETypes();
    	for (int i = 0; i < mime.length; i++) {
    		log.info("Supported mime-type: " + mime[i]);
    		String[] s = mime[i].split(",\\s*");
    		for (int j = 0; j < s.length; j++) {
    			supportedMimeTypes.add(s[j]);
    		}
    	}
    }
    
	private int maxWidth;
	
	private int maxHeight;
	
	private String format = FORMAT_JPG;
	
	private int maxCrop = 0; 
		
	public void setFormat(String format) {
		this.format = format;
	}

	public void setMaxCrop(int maxCrop) {
		this.maxCrop = maxCrop;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public boolean supports(String mimeType) {
		return supportedMimeTypes.contains(mimeType);
	}
	
	public void renderThumbnail(File source, String mimeType, OutputStream out) 
			throws IOException {
		
		BufferedImage originalImage = readImage(new FileInputStream(source));
        boolean alpha = originalImage.getColorModel().hasAlpha();
        
        int imageWidth = originalImage.getWidth(null);
        int imageHeight = originalImage.getHeight(null);
        
        int width;
        int height;
        
        double scaleX = (double) maxWidth / (double) imageWidth; 
        double scaleY = (double) maxHeight / (double) imageHeight;
        
        double scale = Math.min(Math.max(scaleX, scaleY), 1);
		width = (int) (imageWidth * scale);
    	height = (int) (imageHeight * scale);
    	double cropFactor = (double) maxCrop / 100;

    	if ((width - maxWidth > cropFactor * width) 
    			|| (height - maxHeight > cropFactor * height)) {
    		
    		scaleX = maxWidth / (imageWidth - cropFactor * imageWidth);
    		scaleY = maxHeight / (imageHeight - cropFactor * imageHeight);
    		scale = Math.min(Math.min(scaleX, scaleY), 1);
    	}
	
        width = (int) (imageWidth * scale);
	    height = (int) (imageHeight * scale);
        
        BufferedImage thumbImage = new BufferedImage(width, 
                height, alpha 
                ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        graphics2D.drawImage(originalImage, 0, 0, width, height, null);
        
        int x = Math.max((width - maxWidth) / 2, 0);
        int y = Math.max((height - maxHeight) / 2, 0);
        if (x > 0 || y > 0) {
        	width = Math.min(width, maxWidth);
        	height = Math.min(height, maxHeight);
        	thumbImage = thumbImage.getSubimage(x, y, width, height);
        }
        
        writeImage(thumbImage, format, out);
	}

	private BufferedImage readImage(InputStream in) throws IOException {
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
    
    private void writeImage(RenderedImage im, String formatName,
    		OutputStream output) throws IOException {
    	
    	Assert.notNull(formatName, "A formatName must be specified");
    	
        ImageWriter writer = null;
        ImageOutputStream ios = null;
        
        try {
	        Iterator it = ImageIO.getImageWritersByFormatName(formatName);
	        if (it.hasNext()) {
	            writer = (ImageWriter) it.next();
	        }
	        Assert.notNull(writer, "No ImageWriter available for format " 
	        		+ formatName);
	        
	        ios = ImageIO.createImageOutputStream(output);
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
