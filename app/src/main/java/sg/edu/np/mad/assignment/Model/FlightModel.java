package sg.edu.np.mad.assignment.Model;

public class FlightModel {
    String flighttitle,from,time,timedep,to;

    public FlightModel(String flighttitle, String from, String time, String timedep, String to) {
        this.flighttitle = flighttitle;
        this.from = from;
        this.time = time;
        this.timedep = timedep;
        this.to = to;
    }

    public FlightModel() {
    }


    public String getFlighttitle() {
        return flighttitle;
    }

    public void setFlighttitle(String flighttitle) {
        this.flighttitle = flighttitle;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimedep() {
        return timedep;
    }

    public void setTimedep(String timedep) {
        this.timedep = timedep;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
