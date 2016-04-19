package async_tasks;

import java.util.List;

import model.google_items.Place;

/**
 * Created by Aleks on 12-Apr-16.
 * Interface for handling returned data from an Async task.
 */
public interface AsyncResponseBusStationsListener {
    void onLocationsTaskEndWithResult(int success);
    void onLocationsProcessFinish(List<Place> places);
}
