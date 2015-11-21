package com.baconfiesta.ems.models.EmergencyRecord;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Properties;

/**
 * A route in the system from the responder to the emergency
 */
public class Route implements Serializable {

    private String emergencyResponderAddress;
    private String emergencyLocationAddress;
    private File mainRoute;
    private File alternateRoute;
    private String mainRouteDirections;
    private String alternateRouteDirections;
    private String mainRouteDistance;
    private String alternateRouteDistance;
    private String mainRouteDuration;
    private String alternateRouteDuration;
    private Boolean alternateRouteSelected;


    /**
     * \brief Constructor for a Route object: constructs and determines routes (main and alternate) and calculates directions
     * @param completeAddressFrom the complete address of the end point beginning of the route
     * @param completeAddressTo the complete address of the end point of the route
     * */
    public Route(String completeAddressFrom, String completeAddressTo) {
        this.emergencyLocationAddress = completeAddressFrom;
        this.emergencyResponderAddress = completeAddressTo;

        this.calculateRoute();
        this.calculateDirections(true);
        this.calculateDirections(false);
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
     * \brief Retrieve the HTML file containing the main route
     * @return the HTML file containing the main route
     * */
    public File getMainRoute() {
        return mainRoute;
    }

    /**
     * \brief Retrieve the HTML file containing the alternate route
     * @return the HTML file containing the alternate route
     * */
    public File getAlternateRoute() {
        return alternateRoute;
    }

    /**
     * \brief Retrieve the directions of the main route
     * @return a String containing the directions of the main route
     * */
    public String getMainRouteDirections() {
        return mainRouteDirections;
    }

    /**
     * \brief Retrieve the directions of the alternate route
     * @return a String containing the directions of the alternate route
     * */
    public String getAlternateRouteDirections() {
        return alternateRouteDirections;
    }

    /**
     * \brief Retrieve the distance of the main route
     * @return a String containing the distance of the main route
     * */
    public String getMainRouteDistance() {
        return mainRouteDistance;
    }

    /**
     * \brief Retrieve the distance of the alternate route
     * @return a String containing the distance of the alternate route
     * */
    public String getAlternateRouteDistance() {
        return alternateRouteDistance;
    }

    /**
     * \brief Retrieve the duration of the main route
     * @return a String containing the duration of the main route
     * */
    public String getMainRouteDuration() {
        return mainRouteDuration;
    }

    /**
     * \brief Retrieve the duration of the alternate route
     * @return a string containing the duration of the alternate route
     * */
    public String getAlternateRouteDuration() {
        return alternateRouteDuration;
    }

    /**
     * \brief Retrieve a string corresponding to which route has been selected
     * @return a string corresponding to which route has been selected (alternate or main)
     * */
    public String getAlternateRouteSelectedString() {
        return alternateRouteSelected?"Alternate Route":"Main Route";
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
    public static Responder determineNearestResponder(EmergencyRecord record) {

         /*
     * TODO: Handle the fact that the answer query can be empty
     * TODO: if cannot retrieve data, what happens?
     * TODO: set timer for each retrieval, if no answer after a given time, returns error or "Network Unavailable"
     * */

        String searchQuery = "Police Department"; // We always look for a police department

        /* Getting location of emergency in order to format the query string */
        Location emergencyLocation = record.getLocation();
        searchQuery += " near " + emergencyLocation.getAddress() + ", " + emergencyLocation.getZip() + ", " + emergencyLocation.getState();

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
        String responderPhone = "";
        String responderAddress = "";
        String responderState = "";
        int responderZip = 0;

        /* Querying places according to the type of the emergency */
        try {
            PlacesSearchResponse results = PlacesApi.textSearchQuery(context, searchQuery).await();

            /* Some print to make sure it will work*/
            System.out.println("Responder's name: " + results.results[0].name);
            System.out.println("Responder's address: " + results.results[0].formattedAddress);
            System.out.println("Responder's Place Id: " + results.results[0].placeId);
            String placeId = results.results[0].placeId;

            /* Querying details about the first place found in the list of the previous query */
            try {
                PlaceDetails detailsQuery = PlacesApi.placeDetails(context, placeId).await();
                responderPhone = detailsQuery.formattedPhoneNumber;
                responderAddress = detailsQuery.vicinity;

                for(int i = 0; i<detailsQuery.addressComponents.length; i++) {

                    if(Arrays.asList(detailsQuery.addressComponents[i].types)
                            .contains(AddressComponentType.POSTAL_CODE)) {
                        responderZip = Integer.parseInt(detailsQuery.addressComponents[i].longName);
                    } else if(Arrays.asList(detailsQuery.addressComponents[i].types)
                            .contains(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1)) {
                        responderState = detailsQuery.addressComponents[i].longName;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Creating a new Responder and returning it */
        return new Responder(responderPhone, responderAddress, responderState, responderZip);
    }

    /**
     * \brief Calculates 1 main route and 1 alternative route from the Responder to the emergency address
     *
     */
    private void calculateRoute() {
        String key;
        String htmlRequest;

        /* Retrieving our Javascript API key */
        key = retrieveKey("JavascriptKey");
        if(key == null)
            return;

        /* Creating the URL for request */
        htmlRequest = "https://maps.googleapis.com/maps/api/js?key="+key+"&callback=initMap";
        /* Main route is the fastest, used with the previous URL */
        this.mainRoute = createHTMLRoute(htmlRequest, "mainRoute.html", false, false);

        /* Adding restrictions to route */
        /* Second route is longer */
        this.alternateRoute = createHTMLRoute(htmlRequest, "alternateRoute.html", true, true);
    }


    /**
     * \brief Retrieves the directions information between emergencyResponderAddress and emergencyLocationAddress, depending on the main or alternate route
     * @param alternateRoute defines if the direction calculated concerns the main route or the alternate one */
    private void calculateDirections(Boolean alternateRoute) {

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

        /* Avoiding tolls and highways if we want to calculate the directions of the alternate route */
        if(alternateRoute)
            myRequest.avoid(DirectionsApi.RouteRestriction.HIGHWAYS, DirectionsApi.RouteRestriction.TOLLS);

        /* Requesting directions information using request previously set */
        try {
            results = myRequest.await();
            for(int i = 0; i<results[0].legs[0].steps.length; i++)
                directionsString += "\n" + results[0].legs[0].steps[i].htmlInstructions + ", " + results[0].legs[0].steps[i].distance;

        } catch(Exception e) {
            e.printStackTrace();
        }

        /* Formatting directions */
        directionsString = directionsString.replace("<b>", "");
        directionsString = directionsString.replace("</b>", "");
        directionsString = directionsString.replace("<div>", "");
        directionsString = directionsString.replace("</div>", "");
        directionsString = directionsString.replace("<div style=\"font-size:0.9em\">", ", ");

        if(alternateRoute) {    // If alternate route defined, assign values to alternate route values
            alternateRouteDirections = directionsString;
            alternateRouteDistance = distance;
            alternateRouteDuration = duration;
        } else {                // Assign to main route otherwise
            mainRouteDirections = directionsString;
            mainRouteDistance = distance;
            mainRouteDuration = duration;
        }

    }


    /**
     * \brief Creates an HTML file containing the components required to display the map retrieved by the request in HTMHRequest. Saves it in "filename"
     *  @param HTMLRequest the URL of the request to include in the HTML file
     *  @param filename the name of the file to create
     *  @param avoidHighways if set to true, searches for routes avoiding highways if possible
     *  @param avoidTolls if set to true, searches for routes avoiding tolls if possible
     *  @return a File object containing the HTML file created using the request
     */
    private File createHTMLRoute(String HTMLRequest, String filename, Boolean avoidHighways, Boolean avoidTolls) {
        File htmlTemplateFile = null;

        /* Opening file from template */
        try {
            htmlTemplateFile = new File("templates/Maps.html");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }

        File directionFile = null;

        try {
            String htmlString = FileUtils.readFileToString(htmlTemplateFile);
            htmlString = htmlString.replace("$source", HTMLRequest);
            htmlString = htmlString.replace("$origin", this.emergencyLocationAddress);
            htmlString = htmlString.replace("$destination", this.emergencyResponderAddress);
            htmlString = htmlString.replace("$avoidHighways", avoidHighways?"true":"false");
            htmlString = htmlString.replace("$avoidTolls", avoidTolls?"true":"false");
            directionFile = new File("./"+filename);
            FileUtils.writeStringToFile(directionFile, htmlString);

        } catch(Exception e){
            e.printStackTrace();
        }

        return directionFile;
    }


    /**
     * \brief Retrieve the static route (as an image file) according to the selected route: if alternateRouteSelected is set to false, the route retrieved is
     * the main one, otherwise retrieves the alternate route
     * @param alternateRouteSelected if set to true, retrieves alternate route static map, retrieves main route maps otherwise
     */
    public void retrieveStaticMap(Boolean alternateRouteSelected) {
        this.alternateRouteSelected = alternateRouteSelected;


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

