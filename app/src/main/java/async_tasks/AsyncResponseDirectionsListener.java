package async_tasks;

import java.util.List;

import model.google_items.Route;

/**
 * Created by Aleks on 17-Apr-16.
 * Interface for handling returned data from an Async task.
 */
public interface AsyncResponseDirectionsListener {
    void onDirectionsTaskEndWithResult(int success);
    void onDirectionsProcessFinish(List<Route> places);
}
