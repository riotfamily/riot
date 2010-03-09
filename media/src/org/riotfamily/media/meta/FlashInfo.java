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
/**
 * FlashInfo is (c) 2006 Paul Brooks Andrus and is released under the MIT
 * License: http://www.opensource.org/licenses/mit-license.php
 */
package org.riotfamily.media.meta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author brooks
 */
public class FlashInfo {

	protected Logger log = LoggerFactory.getLogger(getClass());

	public static String COMPRESSED = "compressed";

	public static String UNCOMPRESSED = "uncompressed";

	private String signature;

	private String compressionType;

	private int version;

	private long size;

	private int nbits;

	private int xmax;

	private int ymax;

	private int width;

	private int height;

	private int frameRate;

	private int frameCount;

	private boolean valid = false;

	public FlashInfo(File file) throws IOException {
		valid = parseHeader(file);
	}

	public boolean parseHeader(File file) throws IOException {
		log.debug("Parsing file '" + file + "'...");

		FileInputStream fis = null;
		byte[] temp = new byte[(int) file.length()];
		byte[] swf = null;

		fis = new FileInputStream(file);
		fis.read(temp);
		fis.close();

		if (!isSWF(temp)) {
			log.debug("The file does not appear to be a swf - incorrect file signature");
			return false;
		}
		else {
			signature = "" + (char) temp[0] + (char) temp[1] + (char) temp[2];
		}

		if (isCompressed(temp[0])) {
			try {
				swf = decompress(temp);
			}
			catch (DataFormatException dfe) {
				log.warn("The file appears to be compressed, but decompression failed: "
						+ dfe.getMessage());

				return false;
			}
			compressionType = FlashInfo.COMPRESSED;
		}
		else {
			swf = temp;
			compressionType = FlashInfo.UNCOMPRESSED;
		}

		// version is the 4th byte of a swf;
		version = swf[3];

		// bytes 5 - 8 represent the size in bytes of a swf
		size = readSize(swf);

		// Stage dimensions are stored in a rect

		nbits = ((swf[8] & 0xff) >> 3);

		PackedBitObj pbo = readPackedBits(swf, 8, 5, nbits);

		PackedBitObj pbo2 = readPackedBits(swf, pbo.nextByteIndex, pbo.nextBitIndex, nbits);

		PackedBitObj pbo3 = readPackedBits(swf, pbo2.nextByteIndex, pbo2.nextBitIndex, nbits);

		PackedBitObj pbo4 = readPackedBits(swf, pbo3.nextByteIndex, pbo3.nextBitIndex, nbits);

		xmax = pbo2.value;
		ymax = pbo4.value;

		width = convertTwipsToPixels(xmax);
		height = convertTwipsToPixels(ymax);

		int bytePointer = pbo4.nextByteIndex + 2;

		frameRate = swf[bytePointer];
		bytePointer++;

		int fc1 = swf[bytePointer] & 0xFF;
		bytePointer++;

		int fc2 = swf[bytePointer] & 0xFF;
		bytePointer++;

		frameCount = (fc2 << 8) + fc1;

		log.debug("signature:   " + getSignature());
		log.debug("version:     " + getVersion());
		log.debug("compression: " + getCompressionType());
		log.debug("size:        " + getSize());
		log.debug("nbits:       " + getNbits());
		log.debug("xmax:        " + getXmax());
		log.debug("ymax:        " + getYmax());
		log.debug("width:       " + getWidth());
		log.debug("height:      " + getHeight());
		log.debug("frameRate:   " + getFrameRate());
		log.debug("frameCount:  " + getFrameCount());

		return true;
	}

	public void read(byte[] output, byte[] input, int offset) {
		System.arraycopy(input, offset, output, 0, output.length - offset);
	}

	public int readSize(byte[] bytes) {
		long size = 0;
		for (int i = 0; i < 4; i++) {
			size |= bytes[7 - i] & 0xFF;
			if (i < 3) {
				size <<= 8;
			}
		}
		return (int) size;
	}
	
	public PackedBitObj readPackedBits(byte[] bytes, int byteMarker, int bitMarker, int length) {
		int total = 0;
		int shift = 7 - bitMarker;
		int counter = 0;
		int bitIndex = bitMarker;
		int byteIndex = byteMarker;

		while (counter < length) {

			for (int i = bitMarker; i < 8; i++) {
				int bit = ((bytes[byteMarker] & 0xff) >> shift) & 1;
				total = (total << 1) + bit;
				bitIndex = i;
				shift--;
				counter++;
				if (counter == length) {
					break;
				}
			}
			byteIndex = byteMarker;
			byteMarker++;
			bitMarker = 0;
			shift = 7;
		}
		return new PackedBitObj(bitIndex, byteIndex, total);
	}

	public int convertTwipsToPixels(int twips) {
		return twips / 20;
	}

	public int convertPixelsToTwips(int pixels) {
		return pixels * 20;
	}

	public boolean isSWF(byte[] signature) {
		String sig = "" + (char) signature[0] + (char) signature[1] + (char) signature[2];

		if (sig.equals("FWS") || sig.equals("CWS")) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isCompressed(int firstByte) {
		if (firstByte == 67) {
			return true;
		}
		else {
			return false;
		}
	}

	public byte[] compress(byte[] bytes, int length) {

		byte[] compressed = null;
		byte[] temp = new byte[length];

		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
		deflater.setInput(bytes, 8, length - 8);
		deflater.finish();

		int compressedLength = deflater.deflate(temp);

		compressed = new byte[compressedLength + 8];

		// the first 8 bytes of the header are uncompressed
		System.arraycopy(bytes, 0, compressed, 0, 8);

		// copy the compressed data from the temporary byte array to its new
		// byte array
		System.arraycopy(temp, 0, compressed, 8, compressedLength);

		// the first byte of the swf indicates the swf is compressed
		temp[0] = 67;

		// make sure the swf size structure represents the uncompressed size of
		// the swf
		int bl = bytes.length;
		temp[4] = (byte) (bl & 0x000000FF);
		temp[5] = (byte) ((bl & 0x0000FF00) >> 8);
		temp[6] = (byte) ((bl & 0x00FF0000) >> 16);
		temp[7] = (byte) ((bl & 0xFF000000) >> 24);

		return compressed;
	}

	public byte[] decompress(byte[] bytes) throws DataFormatException {
		int size = readSize(bytes);

		byte[] uncompressed = new byte[size];
		System.arraycopy(bytes, 0, uncompressed, 0, 8);

		Inflater inflater = new Inflater();
		inflater.setInput(bytes, 8, bytes.length - 8);
		inflater.inflate(uncompressed, 8, size - 8);
		inflater.finished();

		// the first byte of the swf indicates the swf is uncompressed
		uncompressed[0] = 70;

		return uncompressed;
	}


	/**
	 * @return the frameCount
	 */
	public int getFrameCount() {
		return frameCount;
	}

	/**
	 * @return the frameRate
	 */
	public int getFrameRate() {
		return frameRate;
	}

	/**
	 * @return the nbits
	 */
	public int getNbits() {
		return nbits;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @return the xmax
	 */
	public int getXmax() {
		return xmax;
	}

	/**
	 * @return the ymax
	 */
	public int getYmax() {
		return ymax;
	}

	/**
	 * @return the compressionType
	 */
	public String getCompressionType() {
		return compressionType;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return true if no exception occured during parsing
	 */
	public boolean isValid() {
		return valid;
	}


	/**
	 * @author brooks
	 */
	public static class PackedBitObj {

		public int bitIndex = 0;

		public int byteIndex = 0;

		public int value = 0;

		public int nextBitIndex = 0;

		public int nextByteIndex = 0;

		public int nextByteBoundary = 0;

		/**
		 * @param bitIndex The index of the last bit read
		 * @param byteMarker The index of the last byte read
		 * @param decimalValue The decimal value of the packed bit sequence
		 * @param binaryString
		 */
		public PackedBitObj(int bitMarker, int byteMarker, int decimalValue) {
			bitIndex = bitMarker;
			byteIndex = byteMarker;
			value = decimalValue;
			nextBitIndex = bitMarker;

			if (bitMarker < 7) {
				nextBitIndex++;
				nextByteIndex = byteMarker;
				nextByteBoundary = byteMarker++;
			}
			else {
				nextBitIndex = 0;
				nextByteIndex++;
				nextByteBoundary = nextByteIndex;
			}
		}

	}

}