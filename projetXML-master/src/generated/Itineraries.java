package generated;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement (name = "itineraries")
@XmlAccessorType (XmlAccessType.FIELD)
public class Itineraries <T extends Itinerary>{
    @XmlElementWrapper(name="itinerary")
    @XmlElement(name="itinerary")
    private List<Itinerary> itineraries=null;

    public List<Itinerary> getItineraries() {
        return itineraries;
    }

    public void setItineraries(List<Itinerary> itineraries) {
        this.itineraries = itineraries;
    }

}
