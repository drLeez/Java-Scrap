package Pool;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class Pool<E> extends AbstractSequentialList<E> implements List<E>
{
    protected PoolNode<E> Head, Mid, Tail;

    private final Supplier<E> _supplier;
    private final BiConsumer<E, Object[]> _setter;

    private int _total, _limit, _size;

    public Pool(Supplier<E> supplier, BiConsumer<E, Object[]> setter, int total)
    {
        this(supplier, setter, total, 0);
    }

    public Pool(Supplier<E> supplier, BiConsumer<E, Object[]> setter, int total, int limit)
    {
        if (limit < 0) limit = 0;
        if ((limit > 0 && limit < total) || total < 1) throw new IllegalArgumentException("1 <= total <= max");

        _supplier = supplier;
        _setter = setter;
        _total = total;
        _limit = limit;

        _size = 0;

        Mid = Head = new PoolNode<>(supplier.get());
        Tail = Head;
        while (--total > 0) push();
    }

    private void push()
    {
        var temp = Tail;
        Tail.Next = new PoolNode<>(_supplier.get());
        Tail = Tail.Next;
        Tail.Prev = temp;
    }

    public E add(Object... args)
    {
        if (_limit > 0 && _size >= _limit) return null;
        if (_size >= _total)
        {
            push();
            _total++;
        }
        _setter.accept(Mid.Data, args);
        _size++;
        if (Mid.Next != null) Mid = Mid.Next;
        return Mid.Data;
    }

    @Override
    public boolean remove(Object item)
    {
        var head = Head;
        var i = 0;
        while(head != null && i++ < _total)
        {
            if (head.Data == item) return remove(head) != null;
            head = head.Next;
        }
        return false;
    }

    @Override
    public E remove(int index)
    {
        var head = Head;
        PoolNode<E> next;
        var i = 0;
        while(head != null && i < _total)
        {
            next = head.Next;
            if (i == index) return remove(head);
            head = next;
            i++;
        }
        return null;
    }

    protected E remove(PoolNode<E> node)
    {
        if (node == Mid && Mid.Prev != null) Mid = Mid.Prev;

        if (node.Prev != null) node.Prev.Next = node.Next;
        if (node.Next != null) node.Next.Prev = node.Prev;
        Tail.Next = node;
        node.Prev = Tail;
        node.Next = null;
        Tail = node;

        _size--;

        return node.Data;
    }

    @Override
    public int size()
    {
        return _size;
    }
    public int capacity()
    {
        return _total;
    }

    @Override
    public Iterator<E> iterator()
    {
        return new PoolIterator<E>(this);
    }

    @Override
    public ListIterator<E> listIterator(int index)
    {
        return null;
    }

    @Override
    public int indexOf(Object o)
    {
        int i = 0;
        if (o == null)
        {
            for (var x = Head; x != null; x = x.Next, i++) if (x.Data == null) return i;
        }
        else
        {
            for (var x = Head; x != null; x = x.Next, i++) if (o.equals(x.Data)) return i;
        }
        return -1;
    }
}
class PoolNode<E>
{
    public E Data;
    public PoolNode<E> Next;
    public PoolNode<E> Prev;

    public PoolNode(E data)
    {
        Data = data;
    }
}
class PoolIterator<E> implements Iterator<E>
{
    Pool<E> pool;
    PoolNode<E> current;
    int index = 0;

    PoolIterator(Pool<E> pool)
    {
        this.pool = pool;
        current = pool.Head;
    }

    public boolean hasNext()
    {
        return current != null && index < pool.size();
    }

    public E next()
    {
        var ret = current.Data;
        current = current.Next;
        index++;
        return ret;
    }

    public void remove()
    {
        pool.remove(current);
    }
}
