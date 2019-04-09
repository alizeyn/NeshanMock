package ir.alizeyn.neshanmock.request;

import org.neshan.core.LngLat;
import org.neshan.core.LngLatVector;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.ui.MapView;
import org.neshan.vectorelements.Line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import ir.alizeyn.neshanmock.util.PolylineEncoding;

/**
 * @author alizeyn
 * Created at 4/8/19
 */
public class RouteResponse {

    private List <Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }





    public class Route{

        private List<Leg> legs;

        public List<LngLatVector> getRouteLineVector() {

            List<LngLatVector> path = new ArrayList<>();
            for (Leg l :
                    legs) {
                for (Step s :
                        l.steps) {
                    path.add(s.getLine());
                }
            }
            return path;
        }
    }





    public class Leg {
        private List<Step> steps;

        public List<Step> getSteps() {
            return steps;
        }

        public void setSteps(List<Step> steps) {
            this.steps = steps;
        }

    }





    public class Step {

        private String polyline;

        public String getPolyline() {
            return polyline;
        }

        public void setPolyline(String polyline) {
            this.polyline = polyline;
        }

        public LngLatVector getLine() {
            List<LngLat> points = PolylineEncoding.decode(polyline);
            LngLatVector lngLatVector = new LngLatVector();
            for (LngLat point :
                    points) {

                lngLatVector.add(point);
            }
            return lngLatVector;
        }
    }



}
