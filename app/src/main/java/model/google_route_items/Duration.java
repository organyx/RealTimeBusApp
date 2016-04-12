package model.google_route_items;

/**
 * Created by Aleks on 10-Apr-16.
 * Duration class model for Google Directions API response.
 */
public class Duration {
    public Duration(String text, long value) {
        this.text = text;
        this.value = value;
    }

    private String text;
    private long value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Duration{" +
                "text='" + text + '\'' +
                ", value=" + value +
                '}';
    }
}
