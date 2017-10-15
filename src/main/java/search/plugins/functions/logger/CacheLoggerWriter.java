/*
 * Licensed to es-sp-tools under one or more contributor
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package search.plugins.functions.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import search.plugins.common.Common;

/**
 * 
 * @author psfu vv
 * 
 *         normal operation with out lock, even if using the blockingQueue it's have a lock cost; we want using a way that has minima use the lock;
 *
 */
public class CacheLoggerWriter {
	private static ThreadLocal<ESRecordList> tlr = new ThreadLocal<>();
	protected static BlockingQueue<ESRecordList> bqr = new LinkedBlockingQueue<>();

	protected static Map<Long, ESRecordList> llr = new java.util.concurrent.ConcurrentHashMap<>();

	public static int poolSize = 200;

	// private static LogConfiguration conf;

	public static void addRecordHighSpeed(ESRecord r) {

		ESRecordList lr = tlr.get();
		if (lr == null) {
			lr = new ESRecordList(poolSize);
			tlr.set(lr);
		}

		lr.lr.add(r);

		if (lr.lr.size() == poolSize) {
			bqr.add(lr);
			tlr.set(null);
		}

	}

	public static void addRecordMixed(ESRecord r) {
		ESRecordList lr = tlr.get();
		if (lr == null) {
			lr = new ESRecordList(poolSize);
			tlr.set(lr);

			//
			llr.put(Thread.currentThread().getId(), lr);

		}

		lr.lr.add(r);

		if (lr.lr.size() == poolSize) {
			bqr.add(lr);
			tlr.set(null);
			//
			llr.remove(Thread.currentThread().getId());
		}

	}

	public static void addRecordInTime(ESRecord r) {
		long tid = Thread.currentThread().getId();
		ESRecordList lr = llr.get(tid);
		if (lr == null) {
			lr = new ESRecordList(poolSize);
			//
			llr.put(tid, lr);

		}

		lr.lr.add(r);

		if (lr.lr.size() == poolSize) {
			bqr.add(lr);
			//
			llr.remove(Thread.currentThread().getId());
		}

	}
	//
	// public static void flushR() {
	// System.out.println("-------->flushLog:" + Thread.currentThread().getName());
	// ESRecordList lr = tlr.get();
	// if (lr != null) {
	// bqr.add(lr);
	// tlr.set(null);
	// }
	// }

	static void init(LogConfiguration conf) {
		ESLogThread slt = new ESLogThread(conf);
		Thread t = new Thread(slt);
		t.start();
	}

}

class ESLogThread implements Runnable {

	class Buffer {
		private StringBuilder sb;

		public Buffer() {
			sb = new StringBuilder(chunk);
		}

		public ByteBuffer getByteBuffer() {
			String r = sb.toString();
			ByteBuffer bb = ByteBuffer.wrap(r.getBytes());
			return bb;
		}

		public StringBuilder getStringBuilder() {
			return sb;
		}

		public int capacity() {
			// TODO
			return sb.capacity();
		}

		public int position() {
			return sb.length();
		}

		public void clear() {
			sb = new StringBuilder(chunk);

		}
	}

	LogConfiguration conf;
	final String filePath;
	final String path;

	public ESLogThread(LogConfiguration conf) {
		this.conf = conf;

		this.filePath = conf.pathLogs + "/eslog_";
		this.path = conf.pathLogs;
		log(1, path);
		log(1, filePath);
		f = getFile();
	}

	static final int chunk = 4 * 1024 * 1024;
	static final int chunk2 = 1 * 1024 * 1024;
	//static final int fileInterval = 1 * 60 * 60 * 1000;
	static final int fileInterval = 1 * 60 * 60 * 1000;
	static final int checkInterval = 2 * 1000;

	static final SimpleDateFormat dformat1 = new SimpleDateFormat("yyyyMMdd-HHmmss");
	static final SimpleDateFormat dformat2 = new SimpleDateFormat("yyyyMMdd-HH");

	public void log(int level, Object o) {
		if (level > 1) {
			Common.log0(o);
		}
	}

	Long t00 = null;
	Long t01 = null;
	private File f;
	private FileOutputStream fos;
	private FileChannel fc;

	@Override
	public void run() {

		log(10, "---->log init....");

		// TODO
		// ByteBuffer bb is for old function be instead of the StringBuilder, May some later time change back it.
		// ByteBuffer bb = ByteBuffer.allocate(chunk);
		Buffer bb = new Buffer();
		// sb.delete(start, end)
		// log(1, bb.remaining());

		fc = getChannel(f, fc);

		t00 = System.currentTimeMillis();

		try {
			for (;;) {
				writeLog(bb);
				// executeR(bb, fc);
			}

		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				write0(bb, fc);
				fc.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void writeLog(Buffer bb) throws InterruptedException, IOException {
		execute(bb, fc);
	}

	public FileChannel getChannel(File f, FileChannel fc) {
		try {
			if (fc != null && fc.isOpen()) {
				fc.close();
			}
			if (fos != null) {
				fos.close();
			}

			fos = new FileOutputStream(f);
			this.fc = fos.getChannel();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.fc;
	}

	public File getFile() {
		
		Long t0 = System.currentTimeMillis();
		String t01 = dformat1.format(new Date(t0));
		File f = new File(filePath + t01 + ".log");
		File fileDir;

		fileDir = new File(path);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		log(2, "f-->" + (System.currentTimeMillis() - t0));
		log(10, f.getAbsolutePath());
		return f;
	}

	public void execute(Buffer bb, FileChannel fc) throws InterruptedException, IOException {

		// ESRecordList lr = CacheLoggerWriter.bqr.take(2000);
		ESRecordList lr = CacheLoggerWriter.bqr.poll(checkInterval + 200, TimeUnit.MILLISECONDS);

		// Long t1 = System.currentTimeMillis();

		try {
			if (lr != null) {
				dealRecordList(bb, fc, lr, false);
			} else {
				// TODO
				/*
				 * Set<Long> ks = CacheLoggerWriter.llr.keySet(); for (long k : ks) { // lr = CacheLoggerWriter.llr.remove(k); dealRecordList(bb, fc, lr, true);
				 * }
				 */
				Set<Long> ks = CacheLoggerWriter.llr.keySet();
				for (long k : ks) {
					lr = CacheLoggerWriter.llr.get(k);
					dealRecordList(bb, fc, lr, true);
				}
			}

			//
			// log(1,bb.position());

			dealLogInterval(bb, fc);

			//
			// log(1,(System.currentTimeMillis() - t1) + "ms");
		} catch (Exception e) {

			e.printStackTrace();
			throw (e);
		}
	}

	private void dealRecordList(Buffer bb, FileChannel fc, ESRecordList lr, boolean isInTime) throws IOException {
		int i = lr.position;
		lr.setCurrentPosition();
		// if (isInTime) {
		// lr.setCurrentPosition();
		// }

		for (; i < lr.lr.size(); ++i) {
			ESRecord r = lr.lr.get(i);
			// log(1,r);
			// if (sb.remaining() < chunk2) {
			if (bb.capacity() < chunk2) {
				log(2, "full write....");
				write0(bb, fc);
			}
			if (isInTime) {
				r.isInTime = true;
			}

			r.write(bb);
		}

	}

	public void dealLogInterval(Buffer bb, FileChannel fc) throws IOException {
		if (this.t01 == null) {
			this.t01 = System.currentTimeMillis();
		}

		//
		// log(1,"checkInterval...." + System.currentTimeMillis() +"-" +
		// (System.currentTimeMillis() - t01));

		if ((System.currentTimeMillis() - t01) > checkInterval) {
			if (bb.position() > 0) {
				write0(bb, fc);
				log(2, "interval write....");
				this.t01 = System.currentTimeMillis();
			}

		}

		Long t001 = System.currentTimeMillis();
		if ((t001 - this.t00) > fileInterval) {
			
			dealOldFile();
			
			f = getFile();
			fc = getChannel(f, fc);
			this.t00 = t001;
			log(1, "file....");
		}
	}
	

	// delete the 0 size file
	private void dealOldFile() {
		if (this.f.length() <= 0) {
			try {
				fc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			f.delete();
		}

	}

	public void write0old(ByteBuffer bb, FileChannel fc) throws IOException {
		long t = System.currentTimeMillis();
		bb.flip();
		fc.write(bb);
		bb.clear();
		log(1, "-->" + (System.currentTimeMillis() - t));
	}

	public void write0(Buffer bb, FileChannel fc) throws IOException {
		long t = System.currentTimeMillis();
		// bb.flip();
		ByteBuffer bbb = bb.getByteBuffer();
		fc.write(bbb);
		bb.clear();
		// sb.
		log(1, "-->" + (System.currentTimeMillis() - t));
	}

}