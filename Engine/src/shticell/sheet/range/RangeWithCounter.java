package shticell.sheet.range;

public interface RangeWithCounter {

    Range GetRange();

    void SetRange(Range _range);

    int GetCounter();

    void AddUsing();

    void RemoveUsing();
}
