package org.riotfamily.forms.fileupload;

import java.util.HashMap;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.web.multipart.MultipartException;


/**
 * Class that holds upload information like progress and transfer rate.
 */
public class UploadStatus {
	
	private static HashMap<String, UploadStatus> statusMap = new HashMap<String, UploadStatus>();

	private String uploadId;
	
	private CountingServletInputStream inputStream;
	
	private long bytesTotal;
	
	private long startTime;
	
	private MultipartException exception;
	
	protected UploadStatus(String uploadId, HttpUploadRequest request) {
		this.uploadId = uploadId;
		this.inputStream = request.getCountingInputStream();
		this.bytesTotal = request.getContentLength();
		this.startTime = System.currentTimeMillis();
	}
	
	public int getProgress() {
		return (int) ((float) inputStream.getBytesRead() / bytesTotal * 100);
	}
	
	public int getProgressWidth(int totalWidth, int stepWidth) {
		int i = (int) ((float) inputStream.getBytesRead() / 
				bytesTotal * totalWidth);
		
		return i - i % stepWidth;
	}
	
	public int getKbTransfered() {
		return (int) (inputStream.getBytesRead() / 1024);
	}
	
	public String getDataTransfered() {
		return FormatUtils.formatByteSize(inputStream.getBytesRead());
	}
		
	public int getTimeElapsed() {
		return (int) ((System.currentTimeMillis() - startTime) / 1000);
	}
	
	public String getTransferRate() {
		if (getTimeElapsed() == 0 || isComplete()) {
			return "";
		}
		return FormatUtils.formatByteSize(inputStream.getBytesRead() / 
				getTimeElapsed()) + "/s";
	}
	
	public MultipartException getException() {
		return exception;
	}

	public void setException(MultipartException exception) {
		this.exception = exception;
	}
	
	public boolean isComplete() {
		return inputStream.getBytesRead() == bytesTotal;
	}

	public boolean isError() {
		return exception != null;
	}
	
	public void clear() {
		statusMap.remove(uploadId);
	}
	
	static UploadStatus add(String uploadId, HttpUploadRequest request) {
		UploadStatus status = new UploadStatus(uploadId, request);
		statusMap.put(uploadId, status);
		return status;
	}
	
	public static UploadStatus getStatus(String uploadId) {
		return (UploadStatus) statusMap.get(uploadId);
	}
	
	public static synchronized String createUploadId() {
		return String.valueOf(statusMap.size());
	}
}
