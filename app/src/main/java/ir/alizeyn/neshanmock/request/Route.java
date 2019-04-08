package ir.alizeyn.neshanmock.request;

import org.neshan.core.LngLat;

import java.util.Collections;
import java.util.List;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import ir.alizeyn.neshanmock.util.PolylineEncoding;

/**
 * @author alizeyn
 * Created at 4/8/19
 */
public class Route {

    private List<Leg> legs;

    public List<LngLat> getRoutePoses() {

        List<LngLat> path = Collections.emptyList();
        for (Leg l :
                legs) {
            for (Step s :
                    l.steps) {
                path.addAll(PolylineEncoding.decode(s.polyline));
            }
        }
        return path;
    }


    class Leg {
        private List<Step> steps;

        public List<Step> getSteps() {
            return steps;
        }

        public void setSteps(List<Step> steps) {
            this.steps = steps;
        }
    }

    class Step {

        private String polyline;

        public String getPolyline() {
            return polyline;
        }

        public void setPolyline(String polyline) {
            this.polyline = polyline;
        }
    }

}
