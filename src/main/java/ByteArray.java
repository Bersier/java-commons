import java.util.Arrays;
import java.util.Iterator;

/**
 * Wrapper around byte arrays that allows to work with sub-arrays in a natural way.
 * <p>
 * Warning: Very mutable!
 *
 * @author stephanebersier
 */
@SuppressWarnings("unused")
public final class ByteArray implements Iterable<Byte> {
    private final byte[] array;
    private final int start;
    private final int length;

    private ByteArray(byte[] array, int start, int length) {
        this.array = array;
        this.start = start;
        this.length = length;
    }

    public static ByteArray of(byte[] array, int start, int length) {
        return new ByteArray(array, start, length);
    }

    public static ByteArray of(byte[] array) {
        return new ByteArray(array, 0, array.length);
    }

    public byte at(int index) {
        checkIndex(index);
        return array[start + index];
    }

    public void set(int index, byte b) {
        checkIndex(index);
        array[start + index] = b;
    }

    public byte[] toArray() {
        final byte[] subArray = new byte[length];
        System.arraycopy(array, start, subArray, 0, length);
        return subArray;
    }

    public int size() {
        return length;
    }

    public ByteArray sub(int start, int length) {
        return of(array, start, length);
    }

    public ByteArray sub(int start) {
        return of(array, start, length - start);
    }

    private void checkIndex(int index) {
        if (index >= length) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private final int end = start + length;
            private int index = start;

            @Override
            public boolean hasNext() {
                return index < end;
            }

            @Override
            public Byte next() {
                checkIndex(index);
                return array[index++];
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ByteArray)) {
            return false;
        }

        final ByteArray that = (ByteArray) o;

        return this.start == that.start &&
                this.length == that.length &&
                this.array == that.array;
    }

    @Override
    public int hashCode() {
        @SuppressWarnings("ArrayHashCode")
        int result = array.hashCode();
        result = 31 * result + start;
        result = 31 * result + length;
        return result;
    }

    @Override
    public String toString() {
        return "ByteArray" + Arrays.toString(toArray());
    }
}