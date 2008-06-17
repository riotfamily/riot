package org.riotfamily.components.editor;

import java.io.IOException;

import org.riotfamily.media.model.RiotImage;
import org.springframework.web.multipart.MultipartFile;

public interface UploadManager {

	public boolean isValidToken(String token);
	
	public RiotImage storeImage(String token, MultipartFile multipartFile)
			throws IOException;
	
}
