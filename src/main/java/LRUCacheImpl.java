import java.util.HashMap;

/**
 *
 * Google的一道面试题：
 * Design an LRU cache with all the operations to be done in O(1)。
 *
 * 所有操作时间复杂度都为O(1): 插入(insert)、替换(replace)、查找（lookup）
 * 利用双向链表+哈希表:前者支持插入和替换 O(1), 后者支持查询 O(1)
 *
 * Created by Andy.Gao on 2019/8/30.
 */
public class LRUCacheImpl<K,V> extends LruCache<K,V> {

    HashMap<K, Node> map = new HashMap<K, Node>();
    //先声明一个头结点和一个尾节点
    Node head=null;
    Node end=null;

    public LRUCacheImpl(int capacity, Storage<K, V> lowSpeedStorage) {
        super(capacity, lowSpeedStorage);
    }

    public V get(K key) {
        // 查询缓存有没有
        V v1 = getValue(key);
        // 缓存内不存在
        if(v1 == null) {
            // 查询lowSpeedStorage(类似于磁盘)
            V v2 = lowSpeedStorage.get(key);
            if(v2 != null) {
                // lowSpeedStorage存在, 将它放入缓存
                set(key, v2);
            }
            return v2;
        }
        return v1;
    }

    //获取一个缓存数据之后，应该把这个数据在当前位置中移除，并重新添加到头的位置，这些都是在返回数据之前完成的
    public V getValue(K key) {
        if(map.containsKey(key)){
            Node n = map.get(key);
            remove(n);
            setHead(n);
            return n.value;
        }
        return null;
    }

    //移除元素分为，N的前边和N的后边都要看是怎么样的情况
    public void remove(Node n){
        if(n.pre != null){
            n.pre.next = n.next;
        }else{
            head = n.next;
        }

        if(n.next != null){
            n.next.pre = n.pre;
        }else{
            end = n.pre;
        }

    }

    public void setHead(Node n){
        n.next = head;//head原位置应该是指向第一个元素，现在把这个位置给n.next
        n.pre = null;

        if(head != null){
            head.pre = n;
        }

        head = n;
        //判断头尾是够为空
        if(end == null) {
            end = head;
        }
    }

    //设置看原位置是否有元素，如果有的话就替换，这证明使用过了，然后将其替换为头结点的元素，如果是一个新的节点就要判断它的大小是否符合规范
    public void set(K key, V value) {
        if(map.containsKey(key)){
            Node old = map.get(key);
            old.value = value;
            remove(old);
            setHead(old);
        }else{
            Node created = new Node(key, value);
            if(map.size() >= capacity){
                map.remove(end.key);
                remove(end);
                setHead(created);
            }else{
                setHead(created);
            }
            map.put(key, created);
        }
    }



    class Node{
        K key;
        V value;
        Node pre;
        Node next;

        public Node(K key, V value){
            this.key = key;
            this.value = value;
        }
    }
}
