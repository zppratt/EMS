package com.baconfiesta.ems.models.EmergencyRecord;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.internal.ExceptionResult;
import com.google.maps.model.*;
import javassist.tools.rmi.ObjectNotFoundException;
import jdk.nashorn.internal.runtime.ECMAException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

/**
 * A route in the system from the responder to the emergency
 */
public class Route implements Serializable {

    private String emergencyResponderAddress;
    private String emergencyLocationAddress;
    private File route;
    private String routeDirections;
    private String routeDistance;
    private String routeDuration;
    private Boolean alternateRouteSelected;


    /**
     * \brief Constructor for a Route object: constructs and determines routes (main and alternate) and calculates directions
     * @param completeAddressFrom the complete address of the end point beginning of the route
     * @param completeAddressTo the complete address of the end point of the route
     * @param alternateRoute a boolean telling if the route calculated is the main or the alternate one
     * */
    public Route(String completeAddressFrom, String completeAddressTo, Boolean alternateRoute) throws ObjectNotFoundException {
        this.emergencyLocationAddress = completeAddressTo;
        this.emergencyResponderAddress = completeAddressFrom;
        this.alternateRouteSelected = false;

        this.calculateRoute(alternateRoute);
            this.calculateDirections();
    }

    /**
     * \brief Retrieve the emergency responder's address
     * @return a String containing the emergency responder's address
     * */
    public String getEmergencyResponderAddress() {
        return emergencyResponderAddress;
    }

    /**
     * \brief Retrieve the emergency location's address
     * @return a String containing the emergency location's address
     * */
    public String getEmergencyLocationAddress() {
        return emergencyLocationAddress;
    }

    /**
     * \brief Retrieve the HTML file containing the route
     * @return the HTML file containing the main route
     * */
    public File getRoute() {
        return route;
    }

    /**
     * \brief Retrieve the directions of the main route
     * @return a String containing the directions of the main route
     * */
    public String getRouteDirections() {
        return routeDirections;
    }


    /**
     * \brief Retrieve the distance of the main route
     * @return a String containing the distance of the main route
     * */
    public String getRouteDistance() {
        return routeDistance;
    }


    /**
     * \brief Retrieve the duration of the main route
     * @return a String containing the duration of the main route
     * */
    public String getRouteDuration() {
        return routeDuration;
    }


    /**
     * get the route selected as a boolean
     * @return a boolean set to true if the alternate route has been selected, false if main route was selected
     */
    public Boolean getAlternateRouteSelected() {
        return alternateRouteSelected;
    }
    /**
     * gets the route selected as a string
     * @return a string containing the route selected (main or alternate)
     */
    public String getRouteSelectedString() {
        return(alternateRouteSelected?"Alternate Route":"Main Route");
    }

    /**
     * \brief sets the route chosen
     * @param alternateRouteSelected if true, the alternate route has been selected. Otherwise, the main has been selected.
     * */
    public void setAlternateRouteSelected(Boolean alternateRouteSelected) {
        this.alternateRouteSelected = alternateRouteSelected;
    }

    /**
     * \brief Determines the nearest emergency responder according to the type of the emergency
     *
     * @param record the emergency record currently being created
     *               <p>
     *               Determines an emergency responder according to the type of the emergency. Queries places from google, selects the nearest place,
     *               queries information about this place, and formats information according to a Responder. Creates a new Responder and returns it.
     * @return       an emergency Responder
     */
    public static Responder[] determineNearestResponders(EmergencyRecord record) throws ObjectNotFoundException {

        String firstSearchQuery = "Police Department";
        String secondSearchQuery = Route.determineRespondersType(record.getCategory());

        /* Getting location of emergency in order to format the query string */
        Location emergencyLocation = record.getLocation();
        firstSearchQuery += " near " + emergencyLocation.getAddress() + ", " + emergencyLocation.getCity() + ", " + emergencyLocation.getState();
        secondSearchQuery += " near " + emergencyLocation.getAddress() + ", " + emergencyLocation.getCity() + ", " + emergencyLocation.getState();

        /* Creating a context for the places API query using our team key */
        GeoApiContext context = new GeoApiContext();
        Properties properties = new Properties();
        InputStream input = null;

        /* Retrieving our Places API key */
        try {
            input = new FileInputStream("maps.private.properties");
            properties.load(input);
            context.setApiKey(properties.getProperty("PlacesKey")); // Our Places API key
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /* Fields required to create a new responder */
        String firstResponderPhone = "";
        String firstResponderAddress = "";
        String firstResponderState = "";
        String firstResponderCity = "";

        String secondResponderPhone = "";
        String secondResponderAddress = "";
        String secondResponderState = "";
        String secondResponderCity = "";

        /* Querying places according to the type of the emergency */
        try {
            PlacesSearchResponse firstResults = PlacesApi.textSearchQuery(context, firstSearchQuery).await();
            PlacesSearchResponse secondResults = PlacesApi.textSearchQuery(context, secondSearchQuery).await();

            if(firstResults.results.length <= 0 || secondResults.results.length <= 0)
                throw new ObjectNotFoundException("No results available for this place request");
            String firstPlaceId = firstResults.results[0].placeId;
            String secondPlaceId = secondResults.results[0].placeId;

            /* Querying details about the first place found in the list of the previous query */
            try {
                PlaceDetails firstDetailsQuery = PlacesApi.placeDetails(context, firstPlaceId).await();
                firstResponderPhone = firstDetailsQuery.formattedPhoneNumber;
                firstResponderAddress = firstDetailsQuery.vicinity;

                PlaceDetails secondDetailsQuery = PlacesApi.placeDetails(context, secondPlaceId).await();
                secondResponderPhone = secondDetailsQuery.formattedPhoneNumber;
                secondResponderAddress = secondDetailsQuery.vicinity;


                for(int i = 0; i<firstDetailsQuery.addressComponents.length; i++) {

                    if(Arrays.asList(firstDetailsQuery.addressComponents[i].types)
                            .contains(AddressComponentType.LOCALITY)) {
                        firstResponderCity = firstDetailsQuery.addressComponents[i].longName;
                    } else if(Arrays.asList(firstDetailsQuery.addressComponents[i].types)
                            .contains(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1)) {
                        firstResponderState = firstDetailsQuery.addressComponents[i].longName;
                    }
                }

                for(int i = 0; i<secondDetailsQuery.addressComponents.length; i++) {

                    if(Arrays.asList(secondDetailsQuery.addressComponents[i].types)
                            .contains(AddressComponentType.LOCALITY)) {
                        secondResponderCity = secondDetailsQuery.addressComponents[i].longName;
                    } else if(Arrays.asList(secondDetailsQuery.addressComponents[i].types)
                            .contains(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1)) {
                        secondResponderState = secondDetailsQuery.addressComponents[i].longName;
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            throw new ObjectNotFoundException("No results available for this place request");
        } catch(Exception e) {
            throw new ObjectNotFoundException("Server unreachable at the moment");
        }

        /* Creating new Responders and returning an array of them */
        Responder firstResponder = new Responder(firstResponderPhone, firstResponderAddress, firstResponderState, firstResponderCity);
        Responder secondResponder = new Responder(secondResponderPhone, secondResponderAddress, secondResponderState, secondResponderCity);

        return new Responder[] {firstResponder, secondResponder};
    }

    /**
     *  Returns a string containing the query to run for a responder, according to its type
     * @param emergencyCategory the category of the emergency
     * @return the search query to query for the type of responder
     */
    private static String determineRespondersType(Category emergencyCategory) {
        switch(emergencyCategory) {
            case CAR_CRASH:
                return "Hospital";
            case HOAX:
                return "Police Department";
            case FIRE:
                return "Fire Department";
            case MEDICAL:
                return "Hospital";
            case CRIME:
                return "Hospital";
            default:
                return "Police Department";

        }
    }

    /**
     * \brief Calculates the shortest route from the Responder to the emergency address
     *
     */
    private void calculateRoute(Boolean alternateRoute) {
        String key;
        String htmlRequest;

        /* Retrieving our Javascript API key */
        key = retrieveKey("JavascriptKey");
        if(key == null)
            return;

        /* Creating the URL for request */
        htmlRequest = "https://maps.googleapis.com/maps/api/js?key="+key+"&callback=initMap";
        /* Route is the fastest, used with the previous URL */
        if(!alternateRoute)
            this.route = createHTMLRoute(htmlRequest, "./temp/mainRoute.html");
        else
            this.route = createHTMLRoute(htmlRequest, "./temp/alternateRoute.html");

    }


    /**
     * \brief Retrieves the directions information between emergencyResponderAddress and emergencyLocationAddress
      */
    private void calculateDirections() throws ObjectNotFoundException {

        String key;
        GeoApiContext context = new GeoApiContext();
        DirectionsRoute[] results;
        String directionsString = "";
        String distance = "";
        String duration = "";

        key = retrieveKey("DirectionsKey");
        if(key == null)
            return;

        /* Setting key for the request's context */
        context.setApiKey(key);

        /* Creating request and setting parameters */
        DirectionsApiRequest myRequest = DirectionsApi.newRequest(context);
        myRequest.origin(this.emergencyResponderAddress);
        myRequest.destination(this.emergencyLocationAddress);
        myRequest.mode(TravelMode.DRIVING);

        /* Requesting directions information using request previously set */
        try {
            results = myRequest.await();
            if(results.length <= 0 || results[0].legs.length <= 0)
                throw new ObjectNotFoundException("No results available for this direction request");

            for(int i = 0; i<results[0].legs[0].steps.length; i++)
             directionsString += "\n" + results[0].legs[0].steps[i].htmlInstructions + ", " + results[0].legs[0].steps[i].distance;

        } catch(ObjectNotFoundException e) {
            throw new ObjectNotFoundException("No results available for this direction request");
        } catch(Exception e) {
            throw new ObjectNotFoundException("No network available at the moment");
        }

        /* Formatting directions */
        directionsString = directionsString.replace("<b>", "");
        directionsString = directionsString.replace("</b>", "");
        directionsString = directionsString.replace("<div>", "");
        directionsString = directionsString.replace("</div>", "");
        directionsString = directionsString.replace("<div style=\"font-size:0.9em\">", ", ");

        // Assign to the route
        routeDirections = directionsString;
        routeDistance = distance;
        routeDuration = duration;

    }


    /**
     * \brief Creates an HTML file containing the components required to display the map retrieved by the request in HTMHRequest. Saves it in "filename"
     *  @param HTMLRequest the URL of the request to include in the HTML file
     *  @param filename the name of the file to create
     *  @return a File object containing the HTML file created using the request
     */
    private File createHTMLRoute(String HTMLRequest, String filename) {
        File htmlTemplateFile;

        /* Opening file from template */
        try {
            htmlTemplateFile = new File("./templates/Maps.html");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }

        File directionFile = null;

        try {
            String htmlString = FileUtils.readFileToString(htmlTemplateFile);
            htmlString = htmlString.replace("$source", HTMLRequest);
            htmlString = htmlString.replace("$origin", this.emergencyResponderAddress);
            htmlString = htmlString.replace("$destination", this.emergencyLocationAddress);
            htmlString = htmlString.replace("$avoidHighways", "false");
            htmlString = htmlString.replace("$avoidTolls", "false");
            directionFile = new File("./"+filename);
            FileUtils.writeStringToFile(directionFile, htmlString);

        } catch(Exception e){
            e.printStackTrace();
        }

        return directionFile;
    }



    /**
     * \brief Retrieves a key from the maps.private.properties
     * @param keyToRetrieve the label of the key to retrieve from the file
     * @return a String containing the key retrieved */
    private String retrieveKey(String keyToRetrieve) {
        Properties properties = new Properties();
        InputStream input = null;
        String key = null;

        /* Retrieving Key */
        try {
            input = new FileInputStream("maps.private.properties");
            properties.load(input);
            key = properties.getProperty(keyToRetrieve); // Our Embed API key
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return key;
    }

}

