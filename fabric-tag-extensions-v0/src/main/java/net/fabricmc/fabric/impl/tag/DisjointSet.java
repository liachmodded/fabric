package net.fabricmc.fabric.impl.tag;

import java.util.HashMap;
import java.util.Map;

public class DisjointSet<T> {

	private final Map<T, Data<T>> map;

	public DisjointSet() {
		this(new HashMap<>());
	}

	private DisjointSet(Map<T, Data<T>> map) {
		this.map = map;
	}

	public T get(T start) {
		return getData(start).ref;
	}

	public T merge(T a, T b) {
		Data<T> one = getData(a);
		Data<T> two = getData(b);

		if (one == two) {
			return one.ref;
		}
		if (one.depth >= two.depth) {
			updateData(b, one);
			if (one.depth == two.depth) {
				one.depth++;
			}
			return one.ref;
		} else {
			updateData(a, two);
			return two.ref;
		}
	}
	
	public void clear() {
		this.map.clear();
	}

	private Data<T> getData(T start) {
		Data<T> data = data(start);
		return data.ref == start ? data : updateData(start, getData(data.ref));
	}

	private Data<T> data(T now) {
		return map.computeIfAbsent(now, Data::new);
	}

	private Data<T> updateData(T now, Data<T> update) {
		map.put(now, update);
		return update;
	}

	private static final class Data<T> {
		int depth;
		T ref;

		Data(T ref) {
			this.ref = ref;
			this.depth = 0;
		}

		Data<T> with(T root) {
			this.ref = root;
			return this;
		}
	}
}
