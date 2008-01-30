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
package org.riotfamily.forms.fileupload;

import java.util.HashMap;

import org.riotfamily.common.util.FormatUtils;


/**
 * Class that holds upload information like progress and transfer rate.
 */
public class UploadStatus {
	
	private static HashMap statusMap = new HashMap();

	private CountingServletInputStream inputStream;
	
	private long bytesTotal;
	
	private long startTime;
	
	protected UploadStatus(HttpUploadRequest request) {
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
	
	public int getEstimatedTimeLeft() {
		return 0;
	}
	
	public int getTimeElapsed() {
		return (int) ((System.currentTimeMillis() - startTime) / 1000);
	}
	
	public String getTransferRate() {
		if (getTimeElapsed() == 0 || inputStream.getBytesRead() == bytesTotal) {
			return "";
		}
		return FormatUtils.formatByteSize(inputStream.getBytesRead() / 
				getTimeElapsed()) + "/s";
	}
	
	static void add(String uploadId, HttpUploadRequest request) {
		statusMap.put(uploadId, new UploadStatus(request));
	}
	
	public static UploadStatus getStatus(String uploadId) {
		return (UploadStatus) statusMap.get(uploadId);
	}
	
	public static void clearStatus(Object uploadId) {
		statusMap.remove(uploadId);
	}
	
	public static synchronized String createUploadId() {
		return String.valueOf(statusMap.size());
	}
}
