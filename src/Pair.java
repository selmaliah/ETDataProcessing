import java.util.Objects;

public class Pair<X, Y> {
    public X key;
    public Y value;
    public Pair(X x, Y y) {
        this.key = x;
        this.value = y;
    }

    public X getKey() {
        return key;
    }

    public Y getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return getKey().equals(pair.getKey()) &&
                getValue().equals(pair.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue());
    }
}
