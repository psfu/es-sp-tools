package search.plugins.functions.logger;

import java.util.ArrayList;

public class ESRecordList {

	public final ArrayList<ESRecord> lr;
	public final int poolSize;
	public int position = 0;

	public int setCurrentPosition() {
		int r = position;
		this.position = lr.size();
		return r;
	}

	public ESRecordList(int poolSize) {
		this.poolSize = poolSize;
		lr = new ArrayList<ESRecord>(poolSize);
	}

}
