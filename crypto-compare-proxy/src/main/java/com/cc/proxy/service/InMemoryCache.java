package com.cc.proxy.service;

import static com.cc.proxy.service.CryptoCompareService.USD_CURRENCY_CODE;
import static java.util.Map.entry;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LRUMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.cc.proxy.model.CryptoCoin;
import com.cc.proxy.model.Rate;

import lombok.extern.slf4j.Slf4j;

/**
 * The class InMemoryCache is a cache implementation. It is an LRU based map
 * with entries living a maximum of {@code ttl} milliseconds before a cleaner
 * thread evacuates them.
 *
 * @apiNote property {@code cache.ttl.ms} in {@code cache.properties} is the
 *          default time to live in milliseconds.
 * @apiNote when method {@link #get(Object)} is called and property
 *          {@code cache.refresh.on.null} in {@code cache.properties} is set to
 *          true, this cache will automatically retrieve the entry from live
 *          data, cache it, and return it in case it had been evacuated from the
 *          cache.
 * @apiNote if property {@code cache.auto.refresh} in {@code cache.properties}
 *          is set to true, this cache will automatically replace each evacuated
 *          entry with fresh data.
 *
 * @param <K> the type of cached entry key (in this application just a
 *            {@code String} representing a crypto-currency symbol.
 * @param <V> the type of cached entry (in this application an instance of
 *            {@code CachedEntry<V>}, where {@code V} is an instance of a
 *            {@link CryptoCoin}.
 */
@Service
@Scope(SCOPE_SINGLETON) // this is the default, and it's stated here for clarity only.
@Lazy // instantiate when needed, not immediately as with all singletons
@Slf4j
@SuppressWarnings("unchecked")
public class InMemoryCache<K, V> {

	// @formatter:off
	@Value("${cache.ttl.ms:15000}") private Long defaultTtl;
	@Value("${cache.refresh.on.null:true}") boolean refreshOnNull;
	@Value("${cache.auto.refresh:true}") boolean autoRefresh;
	@Value("${cache.default.timezone}") String timezone;
	@Value("${cache.datetime.format}") String dateTimeFormat;

	@Autowired CryptoCompareService service;
	// @formatter:on

	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final ReadLock readLock = readWriteLock.readLock(); // to lock on read operations
	private final WriteLock writeLock = readWriteLock.writeLock(); // to lock on write operations
	private volatile AtomicBoolean runEvacThread = new AtomicBoolean(true); // stop flag
	private final ExecutorService threadPool; // thread pool
	private AtomicLong staleCounter = new AtomicLong(), freshCounter = new AtomicLong(), ttl = new AtomicLong(); // counters
	private LRUMap<K, CachedEntry<V>> cache; // the cache is an LRU map.

	public InMemoryCache() {
		threadPool = Executors.newCachedThreadPool(runable -> {
			Thread thread = Executors.defaultThreadFactory().newThread(runable);
			thread.setDaemon(true); // threads are daemons, so the thread pool can be shutdown elegantly.
			return thread;
		});
	}

	/**
	 * Initialize the cache and submit a cache cleaning daemon thread.
	 */
	@PostConstruct // called by spring after construction and DI.
	void init() {
		setTtl(defaultTtl);
		Map<K, V> data = (Map<K, V>) service.getCoinlistData();
		cache = new LRUMap<>(data.size());
		data.entrySet().forEach(e -> put(e.getKey(), e.getValue()));
		log.info("initialized cache with {} entries", data.size());
		if (ttl.get() > 0)
			threadPool.submit(() -> {
				while (runEvacThread.get()) {
					try {
						log.debug("cache evacuation thread [{}] awaiting for {} ms...", Thread.currentThread().getName(), ttl.get());
						Thread.sleep(ttl.get());
						if (!runEvacThread.get())
							log.info("cache evacuation thread [{}] stopped!", Thread.currentThread().getName());
						else
							cleanup();
					} catch (InterruptedException ex) {
						log.info("cache evacuation thread [{}] will terminate now!", Thread.currentThread().getName());
					}
				}
			});
	}

	/**
	 * This methods shuts down the thread pool in an elegant fashion, by first
	 * signaling the spawned daemon threads to shutdown.
	 */
	@PreDestroy // called by spring on application context shutdown.
	void preDestroy() {
		log.info("thread pool will shutdown in maximum {} ms...", ttl.get());
		this.runEvacThread.set(false);
		try {
			threadPool.shutdownNow();
			threadPool.awaitTermination(ttl.get(), TimeUnit.MILLISECONDS);
			log.info("cache evacuation thread pool is now cleanly shutdown.");
		} catch (InterruptedException ignore) {
		}
	}

	public long getTtl() {
		readLock.lock();
		try {
			return ttl.get();
		} finally {
			readLock.unlock();
		}
	}

	public void setTtl(long ttl) {
		writeLock.lock();
		try {
			this.ttl.set(ttl);
			log.info("cache ttl set to {} ms", this.ttl.get());
		} finally {
			writeLock.unlock();
		}
	}

	public void put(K key, V value) {
		writeLock.lock();
		try {
			CachedEntry<V> result = new CachedEntry<V>(value);
			result.lastAccessed = System.currentTimeMillis();
			cache.put(key, result);
		} finally {
			writeLock.unlock();
		}
	}

	public V get(K key) {
		CachedEntry<V> cachedEntry = null;
		readLock.lock();
		try {
			cachedEntry = cache.get(key);
		} finally {
			readLock.unlock();
		}
		if (null == cachedEntry)
			createEntry(key);
		else
			updateEntry(key, cachedEntry);
		readLock.lock();
		try {
			cachedEntry = cache.get(key);
		} finally {
			readLock.unlock();
		}
		return null == cachedEntry ? null : cachedEntry.value;
	}

	/**
	 * A method to create a cache entry, which is an object of type
	 * {@code CachedEntry<V>} containing a {@code CryptoCoin} data.
	 *
	 * @param key the crypto-currency symbol
	 * @apiNote this method retrieves both the basic crypto-currency basic details
	 *          and the USD currency rxcahnge rate. In other words, it access two
	 *          different external REST API endpoints and combines the data
	 *          retrieved.
	 */
	private void createEntry(K key) {
		writeLock.lock();
		try {
			log.info("retrieving USD rate for new crypto currency {}", key);
			Map<K, V> data = (Map<K, V>) service.getCoinlistWithPriceMulti(key.toString());
			put(key, data.get(key));
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * A method to retrieve the USD exchange rate for a given crypto-currency
	 * identified by the {@code key}.
	 *
	 * @param key         the crypto-currency symbol
	 * @param cachedEntry a {@code CachedEntry<V>} object retrieved from the cache,
	 *                    containing a {@code CryptoCoin}, that may or may not
	 *                    contain a reference to a USD currency exchange rate.
	 * @apiNote this method will only update a {@code CachedEntry<V>} object
	 *          retrieved from the cache if the underlying {@code CryptoCoin} does
	 *          not already contain a USD exchange rate in its {@code toUSD}
	 *          reference.
	 */
	private void updateEntry(K key, CachedEntry<V> cachedEntry) {
		CryptoCoin coin = ((CryptoCoin) cachedEntry.value);
		if (null == coin.getToUSD()) {
			writeLock.lock();
			try {
				log.info("retrieving USD rate for cached crypto currency {}", Objects.requireNonNull(key));
				Map<String, Rate> data = service.getPriceMultiData(coin.getSymbol(), USD_CURRENCY_CODE);
				if (null != data) {
					Rate rate = data.get(key.toString());
					if (null != rate) {
						BigDecimal toUSD = rate.getDetails().get(USD_CURRENCY_CODE);
						coin.setToUSD(toUSD);
					}
				}
				put(key, (V) coin);
			} finally {
				writeLock.unlock();
			}
		}
	}

	/*
	private void debugLastAccessed(K key, CachedEntry<V> result) {
		if (null != result && log.isDebugEnabled()) {
			LocalDateTime now = Instant.ofEpochMilli(result.lastAccessed).atZone(ZoneId.of(timezone)).toLocalDateTime();
			String lastAccessed = DateTimeFormatter.ofPattern(dateTimeFormat).format(now);
			log.debug("crypto currency {} last accessed {}", key, lastAccessed);
		}
	}
	*/

	public int size() {
		readLock.lock();
		try {
			return cache.size();
		} finally {
			readLock.unlock();
		}
	}

	public void remove(K key) {
		writeLock.lock();
		try {
			if (cache.containsKey(key))
				cache.remove(key);
		} finally {
			writeLock.unlock();
		}
	}

	public Set<K> keySet() {
		readLock.lock();
		try {
			return cache.keySet();
		} finally {
			readLock.unlock();
		}
	}

	public Set<Map.Entry<K, V>> entrySet() {
		readLock.lock();
		try {
			Set<Map.Entry<K, V>> entrySet = new LinkedHashSet<>();
			MapIterator<K, CachedEntry<V>> iter = cache.mapIterator();
			while (iter.hasNext()) {
				K key = iter.next();
				if (null != key) {
					CachedEntry<V> value = iter.getValue();
					if (null != value && null != value.value)
						entrySet.add(entry(key, value.value));
				}
			}
			StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
			Pair<String, String> pair = walker.walk(frames -> frames
				      .map(frame -> Pair.of(frame.getDeclaringClass().getSimpleName(), frame.getMethodName()))
				      .skip(1)
				      .findAny()
				      .orElse(null));
			if (null != pair)
				log.info("{}.{}() accessed all cached data.", pair.getFirst(), pair.getSecond());
			return entrySet;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * removes from the cache all stale keys and refreshes the cache with fresh data
	 * from external REST API endpoints.
	 */
	private void cleanup() {
		if (!runEvacThread.get()) {
			log.debug("cache evacuation cancelled with cache containing {} entries", size());
			return;
		}
		log.info("\nSTART CLEANUP\n");
		List<K> staleDataKeys = getStaleDataKeys();
		log.debug("identified {} stale entries older than {} ms, will ignore {} fresh entries (total {} entries).", staleDataKeys.size(), ttl.get(), freshCounter.get(), size());
		evacuateStaleData(staleDataKeys);
		log.info(" evacuated {} stale entries older than {} ms, ignored {} fresh entries (total {} entries).", staleDataKeys.size(), ttl.get(), freshCounter.get(), size());
		if (autoRefresh) {
			autoRefresh(staleDataKeys);
			log.info(" refreshed {} stale entries older than {} ms, ignored {} fresh entries (total {} entries).", staleCounter.get(), ttl.get(), freshCounter.get(), size());
		}
		log.info("\nEND CLEANUP\n");
	}

	/**
	 * Iterates the cache using a MapIterator and accumulate all the keys of entries
	 * older than {@code ttl}.
	 *
	 * @return a list of cached keys of cached entries older than {@code ttl}.
	 */
	private List<K> getStaleDataKeys() {
		readLock.lock();
		try {
			freshCounter.set(0);
			List<K> staleDataKeys = new LinkedList<K>();
			MapIterator<K, CachedEntry<V>> iter = cache.mapIterator();
			K key = null;
			CachedEntry<V> value = null;
			while (runEvacThread.get() && iter.hasNext()) {
				key = iter.next();
				value = iter.getValue();
				long now = System.currentTimeMillis();
				if (null != value && (now > (ttl.get() + value.lastAccessed)))
					staleDataKeys.add(key);
				else if (null != value && (now <= (ttl.get() + value.lastAccessed)))
					freshCounter.incrementAndGet();
				else if (null == value)
					log.warn("no value associated with key {}", key);
			}
			log.debug("found {} stale data keys, and {} fresh data keys", staleDataKeys.size(), freshCounter.get());
			return staleDataKeys;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * removes all the keys in {@code staleDataKeys} from the cache
	 *
	 * @param staleDataKeys the list of keys of entries older than {@code ttl} that
	 *                      are eligible for evacuation.
	 */
	private void evacuateStaleData(List<K> staleDataKeys) {
		Iterator<K> iter = staleDataKeys.iterator();
		staleCounter.set(0);
		while (runEvacThread.get() && iter.hasNext()) {
			K key = iter.next();
			remove(key);
			staleCounter.incrementAndGet();
			Thread.yield();
		}
		if (!runEvacThread.get())
			log.warn("cache evacuation task cancelled. Cache contains {} data entries", cache.size());
	}

	/**
	 * Refresh the cache with live data obtained for each of the keys in the given
	 * {@code oldDataKeys}.
	 *
	 * @param staleDataKeys the list of deleted keys.
	 */
	private void autoRefresh(List<K> staleDataKeys) {
		writeLock.lock();
		staleCounter.set(0);
		try {
			staleCounter.set(0);
			Map<K, V> data = (Map<K, V>) service.getCoinlistData();
			data.entrySet().stream().filter(e -> staleDataKeys.contains(e.getKey())).forEach(e -> {
				if (cache.containsKey(e.getKey()))
					cache.replace(e.getKey(), new CachedEntry<V>(e.getValue()));
				else
					cache.put(e.getKey(), new CachedEntry<V>(e.getValue()));
				staleCounter.incrementAndGet();
			});
			log.debug("refreshed cache with {} new entries of total {}.", staleCounter.get(), cache.size());
			log.debug("to turn off auto-refresh set property 'cache.auto.refresh' to false in 'cache.properties'.");
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * The class CachedEntry is a wrapper object for any {@code T} type object to be
	 * cached. This wrapper simply holds a timestamp for the last access of the
	 * wrapped instance of {@code T}.
	 *
	 *
	 * @param <T> the type of object to be cached. In this application cached
	 *            objects are instances of {@link CryptoCoin}.
	 */
	private class CachedEntry<T> {

		private long lastAccessed = System.currentTimeMillis();
		private T value;

		private CachedEntry(T value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(value);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CachedEntry<T> other = (CachedEntry<T>) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			return Objects.equals(value, other.value);
		}

		private InMemoryCache<K, V> getEnclosingInstance() {
			return InMemoryCache.this;
		}

	}
}
