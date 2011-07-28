package org.riotfamily.cachius.http.content;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheContext;
import org.riotfamily.cachius.http.support.IOUtils;


public class ChunkedContent implements Content {

	private File file;
	
	private List<Chunk> chunks = new LinkedList<Chunk>();
	
	private transient int lastEnd = -1;
	
	public ChunkedContent(File file) {
		this.file = file;
	}
	
	public void addFragment(int start, int end, ContentFragment fragment) {
		int gap = start - (lastEnd + 1);
		if (gap > 0) {
			chunks.add(new Chunk(gap));
		}
		chunks.add(new FragmentChunk(end - start + 1, fragment));
		lastEnd = end;
	}
	
	public void addTail() {
		int gap = ((int) file.length()) - (lastEnd + 1);
		if (gap > 0) {
			chunks.add(new Chunk(gap));
		}
	}

	public int getLength(HttpServletRequest request, HttpServletResponse response) {
		int length = 0;
		if (chunks != null) {
			for (Chunk chunk : chunks) {
				int chunkLength = chunk.getLength(request, response);
				if (chunkLength < 0) {
					return -1;
				}
				length += chunkLength;
			}
		}
		return length;
	}
	
	public void serve(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Reader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "UTF-8"));
		
		try {
			if (chunks != null) {
				for (Chunk chunk : chunks) {
					chunk.serve(reader, request, response);
				}
			}
		}
		finally {
			IOUtils.closeReader(reader);
		}
	}
	
	public void delete() {
		file.delete();
	}
			
	private static class Chunk implements Serializable {

		protected int length;
		
		public Chunk(int length) {
			this.length = length;
		}

		public int getLength(HttpServletRequest request, HttpServletResponse response) {
			return length;
		}
		
		public void serve(Reader reader, HttpServletRequest request, HttpServletResponse response) 
				throws ServletException, IOException {
			
			IOUtils.copy(reader, response.getWriter(), length);
		}
	}
	
	private class FragmentChunk extends Chunk {

		private ContentFragment fragment;
		
		public FragmentChunk(int length, ContentFragment fragment) {
			super(length);
			this.fragment = fragment;
		}
		
		@Override
		public int getLength(HttpServletRequest request,
				HttpServletResponse response) {
			
			return fragment.getLength(request, response);
		}

		@Override
		public void serve(Reader reader, HttpServletRequest request, 
				HttpServletResponse response) throws ServletException, IOException {
			
			if (CacheContext.exists()) {
				IOUtils.copy(reader, response.getWriter(), length);
			}
			else {
				reader.skip(length);
				fragment.serve(request, response);
			}
			
		}
		
	}
	
}
