package async_tasks;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Aleks on 29-Mar-16.
 */
public class TaskParameters {
    GoogleMap gmap;
    LatLng location;

    public TaskParameters(GoogleMap map, LatLng location)
    {
        this.gmap = map;
        this.location = location;
    }
}
