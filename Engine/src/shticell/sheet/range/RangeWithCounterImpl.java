package shticell.sheet.range;

public class RangeWithCounterImpl implements RangeWithCounter {

    private Range range;
    private int counter = 0;

    public RangeWithCounterImpl(Range _range, int _counter) {
        this.range = _range;
        this.counter = _counter;
    }

    public Range GetRange() { return range; }

    public void SetRange(Range _range) { this.range = _range; }

    public synchronized int GetCounter() { return counter; }

    public synchronized void AddUsing() { counter++; }

    public synchronized void RemoveUsing() {counter--; }
}
