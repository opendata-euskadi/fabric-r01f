package r01f.collections;


import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Cache de objetos respaldada por un mapa de objetos SoftReference
 * 	
 * IMPORTANTE: Diferencia entre objetos StrongReference, WeakReference y SoftReference
 * - Una StrongReference es una referencia java "normal":
 * 			StringBuffer sb = new StringBuffer("");
 *   sb es una StrongReference al StringBuffer recien creado
 *   Si un objeto es "alcanzable" por una cadena de StrongReferences, entonces NO es procesado por el GC
 *   OJO!!		Cuando un objeto se indexa en un mapa se establece una StrongReference al mismo
 * 				map.put(key,obj) :< obj tiene una StrongReference y NO es limpiado por el GC
 * 
 * - Una WeakReference es una referencia a un objeto que permite al GC liberar el objeto al que referencia la WeakReference
 * 			WeakReference<Object> wr = new WeakReference<Object>(obj)	
 *   en este caso obj tiene una WeakReference sobre l, lo que permite al GC liberar obj (al llamar a wr.get() se devuelve null)
 *   Un WeakHashMap es un mapa "normal" en el que las CLAVES (no los valores!) son WeakReferences. Cuando las CLAVES dejan de ser utilizadas
 *   (no hay ninguna StringReference sobre ellas), pueden ser liberadas por el GC
 *   Esto hace que un WeakHashMap NO sea la estructura ms adecuada para implementar una cache ya que es raro mantener StrongReferences sobre las claves y 
 *   por lo tanto las entradas del WeakHashMap son liberadas casi de inmediato (en segundos o milisegundos) si NO se utilizan
 * 
 * - Una SoftReference es como una WeakReference salvo que es menos "propensa" a que el objeto que referencia sea liberado por el GC
 * 		* En el caso de una WeakReference sobre un objeto, el GC libera el objeto en cuanto no tiene ninguna StrongReference sobre el mismo
 * 		* En el caso de una SoftReference sobre un objeto, el GC libera el objeto en cuanto necesita memoria
 *  	  
 *  Por lo tanto, las SoftReferences son adecuadas para la construccin de caches... en especial mucho ms que las WeakReferences puesto que un 
 *  WeakHashMap contiene WeakReferences a las CLAVES y normalmente NO se guardan StrongReferences sobre las claves, por lo que enseguida (en el siguiente ciclo GC)
 *  se libera el objeto de la cache. Un SoftHashMap es mucho ms adecuado pues la entrada permanece hasta que es necesaria la memoria.  
 *  
 *  El problema es que la JVM NO proporciona una implementacin de un SoftHasMap.
 *  Esta clase es una implementacin de la misma en base a la implementacion de WeakHashMap cambiando WeakReference por SoftReference	
 *  	As como en el WeakHashMap se guardan WeakReferences a las CLAVES, en esta clase se guardan SoftReferences tambien a las CLAVES
 *  	de forma que una clave (y por tanto el objeto referenciado) se liberan en cuanto se necesita memoria, a diferencia de un WeakHashMap 
 *  	que se libera en cuanto la clave no tiene StrongReferences... es decir, habitualmente muy pronto.
 *  
 * IMPORTANTE!!
 * El comportamiento de la clase viene determinado por el GC: dado que las entradas 
 * del cache son eliminadas "transparentemente" por el GC, la clase se comporta como si 
 * un thread silenciosamente liberara entradas:
 * 		En particular, aunque se haga un synchronize sobre una instancia de <code>SoftCache</code>
 * 		y se llame a uno de los mtodo mutadores, es posible obtener resultados "extraos":
 * 			- <code>size</code> puede devolver valores distintos
 * 			- <code>isEmpty</code> puede devolver primero false y luego true
 * 			- <code>containsKey</code> para una clave puede devolver primero true y luego false
 * 			- <code>get</code> para una clave puede devolver un valor y luego null
 * 			- <code>put</code> puede devolver null y luego un valor
 * 			- <code>remove</code> puede devolver false para una clave que anteriormente estaba en el mapa
 * 			- En dos examenes sucesiveos keySet, valueSet y entrySet pueden devolver valores distintos (menos elementos)
 */
public final class SoftHashMap<K,V> 
		   extends AbstractMap<K,V> {
///////////////////////////////////////////////////////////////////////////////
// VALORES
///////////////////////////////////////////////////////////////////////////////
	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final int MAXIMUM_CAPACITY = 1 << 30;
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	private static final Object NULL_KEY = new Object();
	
///////////////////////////////////////////////////////////////////////////////
// MIEMBROS
///////////////////////////////////////////////////////////////////////////////	

	/**
	 * Array subyacente que contiene las entradas del mapa
	 * Las entradas son un objeto Entry<K,V> que extiende de SoftReference<K> (es decir, la clave es una SoftReference) y que
	 * adems almacena:
	 * 		- El value
	 * 		- Un hash de la clave
	 * 		- Una referencia a otro objeto Entry<K,V> con la misma clave (en realidad el mismo hash de la clave)
	 * (ver metodo PUT)
	 */
	private Entry<K,V>[] _table;			// Tabla que contiene las entradas
	private int _size;
	private int _threshold;
	private float _loadFactor;
	/**
	 * cola donde el GC "coloca" los objetos que libera para que se tomen acciones 
	 * sobre los mismos; en este caso quitar el objeto del array subyacente
	 */
	private final ReferenceQueue<K> _queue = new ReferenceQueue<K>();
	
	private volatile int _modCount;		// simplemente se incrementa cada vez que se llama a un metodo mutator de la tabla subyacente
										// sirve para validar si la tabla se ha modificado cuando se est iterando (ver HashIterator)
	private transient Set<Map.Entry<K,V>> _entrySet = null;
	private transient volatile Set<K> _keySet = null;
	private transient volatile Collection<V> _values = null;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORES
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor por defecto
	 */
	@SuppressWarnings({"unchecked"})	
	public SoftHashMap() {
		_loadFactor = DEFAULT_LOAD_FACTOR;
		_threshold = DEFAULT_INITIAL_CAPACITY;
		_table = new Entry[DEFAULT_INITIAL_CAPACITY];
	}	
	/**
	 * Constructor en base a la capacidad inicial y el factor de carga
	 * @param initialCapacity capacidad inicial
	 * @param loadFactor factor de carga
	 */  
	public SoftHashMap(final int initialCapacity,final float loadFactor) {
		_init(initialCapacity,loadFactor);
	}
	/**
	 * Constructor en base a la capacidad incial
	 * @param initialCapacity capacidad inicial
	 */
	public SoftHashMap(final int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}
	/**
	 * Constructor en base a un mapa de entradas.
	 * La capacidad inicial del mapa es la suficiente como para contener las entradas
	 * del mapa que se pasa como parametro
	 * @param otherMap el mapa de entradas
	 */
	public SoftHashMap(final Map<? extends K,? extends V> otherMap) {
		int capacity = Math.max((int)(otherMap.size() / DEFAULT_LOAD_FACTOR)+1,DEFAULT_INITIAL_CAPACITY);
		_init(capacity,DEFAULT_LOAD_FACTOR);
		putAll(otherMap);
	}
	@SuppressWarnings("unchecked")
	private void _init(final int initialCapacity,final float loadFactor) {
		if (initialCapacity < 0) throw new IllegalArgumentException("Illegal Initial Capacity: " + initialCapacity);
		int theInitialCapacity = initialCapacity;
		if (theInitialCapacity > MAXIMUM_CAPACITY) theInitialCapacity = MAXIMUM_CAPACITY;
		if (loadFactor <= 0 || Float.isNaN(loadFactor)) throw new IllegalArgumentException("Illegal Load factor: " + loadFactor);
		int capacity = 1;
		while (capacity < theInitialCapacity) capacity <<= 1;
		_table = new Entry[capacity];
		_loadFactor = loadFactor;
		_threshold = (int)(capacity * loadFactor);		
	}
///////////////////////////////////////////////////////////////////////////////
// METODOS
///////////////////////////////////////////////////////////////////////////////
	@Override
	public int size() {
		if (_size == 0) return 0;
		_expungeStaleEntries();		// antes de devolver el tamao borrar las entradas reclamadas por el GC
		return _size;
	}
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}
	@Override
	public V get(final Object key) {
		Object k = _maskNull(key);		// si se pasa null en la clave se "enmascara" con un Object 
		int h = _hash(k);
		Entry<K,V>[] tab = _getTable();
		int index = _indexFor(h,tab.length);
		Entry<K,V> e = tab[index];
		while (e != null) {
			if (e.hash == h && eq(k,e.get())) return e.value;	// varios elementos con el mismo hash estn enlazados en una lista
			e = e.next;
		}
		return null;
	}
	@Override
	public V put(final K key,final V value) {
		K k = _maskNull(key);
		int h = _hash(k);
		Entry<K,V>[] tab = _getTable();
		int i = _indexFor(h,tab.length);
		for (Entry<K,V> e = tab[i]; e != null; e = e.next) {
			if (h == e.hash && eq(k, e.get())) {
				V oldValue = e.value;
				if (value != oldValue) e.value = value;
				return oldValue;
			}
		}
		_modCount++;
		Entry<K,V> e = tab[i];
		tab[i] = new Entry<K,V>(k,value,_queue,h,e);
		if (++_size >= _threshold) _resize(tab.length * 2);
		return null;
	}
	@Override
	public boolean containsKey(final Object key) {
		return _getEntry(key) != null;
	}
	@Override
	public void putAll(final Map<? extends K,? extends V> m) {
		int numKeysToBeAdded = m.size();
		if (numKeysToBeAdded == 0) return;
		if (numKeysToBeAdded > _threshold) {
			int targetCapacity = (int) (numKeysToBeAdded / _loadFactor + 1);
			if (targetCapacity > MAXIMUM_CAPACITY) targetCapacity = MAXIMUM_CAPACITY;
			int newCapacity = _table.length;
			while (newCapacity < targetCapacity) newCapacity <<= 1;
			if (newCapacity > _table.length) _resize(newCapacity);
		}
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
			put(e.getKey(), e.getValue());
		}
	}
	@Override
	public V remove(final Object key) {
		Object k = _maskNull(key);
		int h = _hash(k);
		Entry<K,V>[] tab = _getTable();
		int i = _indexFor(h,tab.length);
		Entry<K,V> prev = tab[i];
		Entry<K,V> e = prev;
		while (e != null) {
			Entry<K,V> next = e.next;
			if (h == e.hash && eq(k,e.get())) {
				_modCount++;
				_size--;
				if (prev == e) {
					tab[i] = next;
				} else {
					prev.next = next;
				}
				return e.value;
			}
			prev = e;
			e = next;
		}
		return null;
	}
	@Override
	@SuppressWarnings("rawtypes")
	public void clear() {
		while (_queue.poll() != null) {/* nothing */}
		_modCount++;
		Entry[] tab = _table;
		for (int i = 0; i < tab.length; ++i) tab[i] = null;
		_size = 0;
		while (_queue.poll() != null) {/* nothing */}
	}
	@Override
	@SuppressWarnings("rawtypes")
	public boolean containsValue(final Object value) {
		if (value == null) return _containsNullValue();
		Entry[] tab = _getTable();
		for (int i = tab.length; i-- > 0;)
			for (Entry e = tab[i]; e != null; e = e.next)
				if (value.equals(e.value))
					return true;
		return false;
	}
	@Override
	public Set<K> keySet() {
		Set<K> ks = _keySet;
		return (ks != null ? ks : (_keySet = new KeySet()));
	}
	@Override
	public Collection<V> values() {
		Collection<V> vs = _values;
		return (vs != null ? vs : (_values = new Values()));
	}
	@Override
	public Set<Map.Entry<K,V>> entrySet() {
		Set<Map.Entry<K,V>> es = _entrySet;
		return (es != null ? es : (_entrySet = new EntrySet()));
	}
///////////////////////////////////////////////////////////////////////////////
// METODOS PRIVADOS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Borra las entradas que el GC ha liberado
	 * NOTA: Cuando el GC libera una WeakReference o SoftReference la mete en una cola
	 * 		 para que alguien (por ejemplo esta clase) tome las acciones pertientes como
	 * 		 es quitar las entradas del array
	 */
	@SuppressWarnings("unchecked")
	private void _expungeStaleEntries() {
		Entry<K,V> e;
		while ((e = (Entry<K,V>)_queue.poll()) != null) {
			int h = e.hash;
			int i = _indexFor(h,_table.length);		
			Entry<K,V> prev = _table[i];	// elemento a borrar del array
			Entry<K,V> p = prev;
			while (p != null) {		// reorganizar el array 
				Entry<K,V> next = p.next;
				if (p == e) {
					if (prev == e) {
						_table[i] = next;
					} else {
						prev.next = next;
					}
					e.next = null;  // Help GC
					e.value = null; //  "   "
					_size--;
					break;
				}
				prev = p;
				p = next;
			}
		}
	}
	private Entry<K,V>[] _getTable() {
		_expungeStaleEntries();
		return _table;
	}
	private Entry<K,V> _getEntry(final Object key) {
		Object k = _maskNull(key);
		int h = _hash(k);
		Entry<K,V>[] tab = _getTable();
		int index = _indexFor(h,tab.length);
		Entry<K,V> e = tab[index];
		while (e != null && !(e.hash == h && eq(k, e.get()))) e = e.next;
		return e;
	}
	@SuppressWarnings({"unchecked"})
	private void _resize(final int newCapacity) {
		Entry<K,V>[] oldTable = _getTable();
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			_threshold = Integer.MAX_VALUE;
			return;
		}
		Entry<K,V>[] newTable = new Entry[newCapacity];
		_transfer(oldTable, newTable);
		_table = newTable;
		if (_size >= _threshold / 2) {
			_threshold = (int) (newCapacity * _loadFactor);
		} else {
			_expungeStaleEntries();
			_transfer(newTable, oldTable);
			_table = oldTable;
		}
	}
	private void _transfer(final Entry<K,V>[] src,final Entry<K,V>[] dest) {
		for (int j = 0; j < src.length; ++j) {
			Entry<K,V> e = src[j];
			src[j] = null;
			while (e != null) {
				Entry<K,V> next = e.next;
				Object key = e.get();
				if (key == null) {
					e.next = null;  // Help GC
					e.value = null; //  "   "
					_size--;
				} else {
					int i = _indexFor(e.hash,dest.length);
					e.next = dest[i];
					dest[i] = e;
				}
				e = next;
			}
		}
	}
	@SuppressWarnings("rawtypes")
	private Entry<K,V> _removeMapping(final Object o) {
		if (!(o instanceof Map.Entry)) return null;
		Entry<K,V>[] tab = _getTable();
		Map.Entry entry = (Map.Entry)o;
		Object k = _maskNull(entry.getKey());
		int h = _hash(k);
		int i = _indexFor(h,tab.length);
		Entry<K,V> prev = tab[i];
		Entry<K,V> e = prev;
		while (e != null) {
			Entry<K,V> next = e.next;
			if (h == e.hash && e.equals(entry)) {
				_modCount++;
				_size--;
				if (prev == e) tab[i] = next;
				else prev.next = next;
				return e;
			}
			prev = e;
			e = next;
		}
		return null;
	}
	@SuppressWarnings("rawtypes")
	private boolean _containsNullValue() {
		Entry[] tab = _getTable();
		for (int i = tab.length; i-- > 0;)
			for (Entry e = tab[i]; e != null; e = e.next)
				if (e.value == null)
					return true;
		return false;
	}
	@SuppressWarnings({"unchecked"})
	private static <K> K _maskNull(final K key) {
		return (K) (key == null ? NULL_KEY : key);
	}
	private static <K> K _unmaskNull(final K key) {
		return key == NULL_KEY ? null : key;
	}
	private static boolean eq(final Object x,final Object y) {
		return x == y || x.equals(y);
	}
	private static int _indexFor(final int h,final int length) {
		return h & (length - 1);
	}
	private static int _hash(final Object key) {
		return _hash(key.hashCode());
	}
	private static int _hash(final int h) {
		int nh = 0;
		nh ^= (h >>> 20) ^ (h >>> 12);
		return nh ^ (nh >>> 7) ^ (nh >>> 4);
	}
///////////////////////////////////////////////////////////////////////////////
// INNER CLASS: Entry
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Encapsula un Map.Entry en un SoftReference
	 * @param <K>
	 * @param <V>
	 */
	private static class Entry<K,V>
			 	 extends SoftReference<K>
			  implements Map.Entry<K,V> {
		
		private V value;
		private final int hash;
		Entry<K,V> next;

		Entry(final K newKey,final V newValue,
			  final ReferenceQueue<K> queue,
			  final int newHash,
			  final Entry<K,V> newNextEntry) {
			super(newKey,queue);
			this.value = newValue;
			this.hash = newHash;
			this.next = newNextEntry;
		}
		@Override
		public K getKey() {
			return _unmaskNull(get());
		}
		@Override
		public V getValue() {
			return value;
		}
		@Override
		public V setValue(final V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}
		@Override
		@SuppressWarnings("rawtypes")
		public boolean equals(final Object o) {
			if (o == null) return false;
			if (this == o) return true;
			if (!(o instanceof Map.Entry)) return false;
			Map.Entry e = (Map.Entry)o;
			Object k1 = getKey();
			Object k2 = e.getKey();
			if (k1 == k2 || (k1 != null && k1.equals(k2))) {
				Object v1 = getValue();
				Object v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2))) return true;
			}
			return false;
		}
		@Override
		public int hashCode() {
			Object k = getKey();
			Object v = getValue();
			return ((k == null ? 0 : k.hashCode()) ^
					(v == null ? 0 : v.hashCode()));
		}
		@Override
		public String toString() {
			return getKey() + "=" + getValue();
		}
	}
///////////////////////////////////////////////////////////////////////////////
// INNER CLASS: HashIterator
///////////////////////////////////////////////////////////////////////////////
	private abstract class HashIterator<T> 
				implements Iterator<T> {
		int index;
		Entry<K,V> entry = null;
		Entry<K,V> lastReturned = null;
		int expectedModCount = _modCount;	// _modCount se incrementa en cada metodo mutator... 
											// al inicializar el iterator se inicializa con el valor que tiene _modCount en un momento dado
											// mas adelante en los metodos del iterador se comprueba si _modCount ha cambiado, lo que indica
											// que la tabla ha variado y por lo tanto el iterador ya NO es valido
		Object nextKey = null;
		Object currentKey = null;

		HashIterator() {
			index = (size() != 0 ? _table.length : 0);
		}
		@Override
		public boolean hasNext() {
			Entry<K,V>[] t = _table;
			while (nextKey == null) {
				Entry<K,V> e = entry;
				int i = index;
				while (e == null && i > 0) e = t[--i];
				entry = e;
				index = i;
				if (e == null) {
					currentKey = null;
					return false;
				}
				nextKey = e.get(); 		// establecer una StrongReference a la clave para evitar que sea liberada por el GC
										// durante la iteracion
				if (nextKey == null) entry = entry.next;
			}
			return true;
		}
		@Override
		public void remove() {
			if (lastReturned == null) throw new IllegalStateException();
			if (_modCount != expectedModCount) throw new ConcurrentModificationException();		// si se ha llamado a un metodo mutator de la tabla mientras se itera
			SoftHashMap.this.remove(currentKey);
			expectedModCount = _modCount;
			lastReturned = null;
			currentKey = null;
		}
		protected Entry<K,V> nextEntry() {
			if (_modCount != expectedModCount) throw new ConcurrentModificationException();
			if (nextKey == null && !hasNext()) throw new NoSuchElementException();
			lastReturned = entry;
			entry = entry.next;
			currentKey = nextKey;
			nextKey = null;
			return lastReturned;
		}
	}
///////////////////////////////////////////////////////////////////////////////
// INNER CLASS: ValueIterator
///////////////////////////////////////////////////////////////////////////////
	private class ValueIterator 
		  extends HashIterator<V> {
		@Override
		public V next() {
			return nextEntry().value;
		}
	}
///////////////////////////////////////////////////////////////////////////////
// INNER CLASS: KeyIterator
///////////////////////////////////////////////////////////////////////////////
	private class KeyIterator 
		  extends HashIterator<K> {
		@Override
		public K next() {
			return nextEntry().getKey();
		}
	}
///////////////////////////////////////////////////////////////////////////////
// INNER CLASS: HashIterator
///////////////////////////////////////////////////////////////////////////////
	private class EntryIterator 
		  extends HashIterator<Map.Entry<K,V>> {
		@Override
		public Map.Entry<K,V> next() {
			return nextEntry();
		}
	}
///////////////////////////////////////////////////////////////////////////////
// INNER CLASS: KeySet
///////////////////////////////////////////////////////////////////////////////
	private class KeySet 
		  extends AbstractSet<K> {
		@Override
		public Iterator<K> iterator() {
			return new KeyIterator();
		}
		@Override
		public int size() {
			return SoftHashMap.this.size();
		}
		@Override
		public boolean contains(final Object o) {
			return containsKey(o);
		}
		@Override
		public boolean remove(final Object o) {
			if (containsKey(o)) {
				SoftHashMap.this.remove(o);
				return true;
			} 
			return false;
		}
		@Override
		public void clear() {
			SoftHashMap.this.clear();
		}
		@Override
		public Object[] toArray() {
			Collection<K> c = new ArrayList<K>(size());
			for (K v : this) c.add(v);
			return c.toArray();
		}
		@Override
		public <T> T[] toArray(final T[] a) {
			Collection<K> c = new ArrayList<K>(size());
			for (K v : this) c.add(v);
			return c.toArray(a);
		}
	}
///////////////////////////////////////////////////////////////////////////////
// INNER CLASS: Values
///////////////////////////////////////////////////////////////////////////////
	private class Values 
		  extends AbstractCollection<V> {
		@Override
		public Iterator<V> iterator() {
			return new ValueIterator();
		}
		@Override
		public int size() {
			return SoftHashMap.this.size();
		}
		@Override
		public boolean contains(final Object o) {
			return containsValue(o);
		}
		@Override
		public void clear() {
			SoftHashMap.this.clear();
		}
		@Override
		public Object[] toArray() {
			Collection<V> c = new ArrayList<V>(size());
			for (V v : this) c.add(v);
			return c.toArray();
		}
		@Override
		public <T> T[] toArray(final T[] a) {
			Collection<V> c = new ArrayList<V>(size());
			for (V v : this) c.add(v);
			return c.toArray(a);
		}
	}
///////////////////////////////////////////////////////////////////////////////
// INNER CLASS: EntrySet
///////////////////////////////////////////////////////////////////////////////
	private class EntrySet 
		  extends AbstractSet<Map.Entry<K,V>> {
		@Override
		public Iterator<Map.Entry<K,V>> iterator() {
			return new EntryIterator();
		}
		@Override
		@SuppressWarnings("rawtypes")
		public boolean contains(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			Map.Entry e = (Map.Entry) o;
			Entry candidate = _getEntry(e.getKey());
			return candidate != null && candidate.equals(e);
		}
		@Override
		public boolean remove(final Object o) {
			return _removeMapping(o) != null;
		}
		@Override
		public int size() {
			return SoftHashMap.this.size();
		}
		@Override
		public void clear() {
			SoftHashMap.this.clear();
		}
		@Override
		public Object[] toArray() {
			Collection<Map.Entry<K,V>> c = new ArrayList<Map.Entry<K,V>>(size());
			for (Map.Entry<K,V> entry : this) c.add(new SimpleEntry<K,V>(entry));
			return c.toArray();
		}
		@Override
		public <T> T[] toArray(final T[] a) {
			Collection<Map.Entry<K,V>> c = new ArrayList<Map.Entry<K,V>>(size());
			for (Map.Entry<K,V> entry : this) c.add(new SimpleEntry<K,V>(entry));
			return c.toArray(a);
		}
	}
///////////////////////////////////////////////////////////////////////////////
// INNER CLASS: SimpleEntry
///////////////////////////////////////////////////////////////////////////////
	private static class SimpleEntry<K,V>
			  implements Map.Entry<K,V> {
		private final K key;
		private V value;
		
		@SuppressWarnings("unused")
		public SimpleEntry(final K key,final V value) {
			this.key = key;
			this.value = value;
		}
		public SimpleEntry(final Map.Entry<K,V> e) {
			this.key = e.getKey();
			this.value = e.getValue();
		}
		@Override
		public K getKey() {
			return key;
		}
		@Override
		public V getValue() {
			return value;
		}
		@Override
		public V setValue(final V value) {
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}
		@Override
		@SuppressWarnings("rawtypes")		
		public boolean equals(final Object o) {
			if (o == null) return false;
			if (o == this) return true;
			if (!(o instanceof Map.Entry)) return false;
			Map.Entry e = (Map.Entry) o;
			return eq(key, e.getKey()) && eq(value, e.getValue());
		}
		@Override
		public int hashCode() {
			return ((key == null) ? 0 : key.hashCode()) ^
					((value == null) ? 0 : value.hashCode());
		}
		@Override
		public String toString() {
			return key + "=" + value;
		}
		private static boolean eq(final Object o1,final Object o2) {
			return (o1 == null ? o2 == null : o1.equals(o2));
		}
	}
}