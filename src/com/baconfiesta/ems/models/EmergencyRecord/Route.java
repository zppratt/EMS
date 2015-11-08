package com.baconfiesta.ems.models.EmergencyRecord;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResponse;

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


    /**
     * \brief Determines the nearest emergency responder according to the type of the emergency
     *
     * @param record the emergency record currently being created
     *               <p>
     *               Determines an emergency responder according to the type of the emergency. Queries places from google, selects the nearest place,
     *               queries information about this place, and formats information according to a Responder. Creates a new Responder and returns it.
     * @return       an emergency Responder
     */
    public Responder determineNearestResponder(EmergencyRecord record) {

         /*
     * TODO: Handle the fact that the answer query can be empty
     * TODO: If no category, what should I do instead of return?
     * TODO: if cannot retrieve data, what happens?
     * */

        String searchQuery;
        /* Determine emergency responder according to the type of the emergency:*/
        switch (record.getCategory()) {
            /* Fire Department*/
            case FIRE:
                searchQuery = "Police Department";
                break;
            /* Police Department */
            case CRIME:
                searchQuery = "Police Department";
                break;
            case CAR_CRASH:
                searchQuery = "Police Department";
                break;
            /* Health Department */
            case MEDICAL:
                searchQuery = "Police Department";
                break;
            default:
                return null;
        }

        /* Getting location of emergency in order to format the query string */
        Location emergencyLocation = record.getLocation();
        searchQuery += " near " + emergencyLocation.getAddress() + ", " + emergencyLocation.getZip() + ", " + emergencyLocation.getState();

        /* Creating a context for the places API query using our team key */
        GeoApiContext context = new GeoApiContext();
        Properties properties = new Properties();
        InputStream input = null;

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
                    /* @Zach, you have to explain me what's going on here. I think I get the point, but... Yeah */
                    if(Arrays.asList(detailsQuery.addressComponents[i].types)
                            .contains(AddressComponentType.POSTAL_CODE)) {
                        responderZip = Integer.parseInt(detailsQuery.addressComponents[i].longName);
                    } else if(Arrays.asList(detailsQuery.addressComponents[i].types)
                            .contains(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1)) {
                        responderState = detailsQuery.addressComponents[i].longName;
                    }
                }

            } catch (Exception e) {
                System.err.println(e);
            }

        } catch (Exception e) {
            System.err.println(e);
        }

        /* Creating a new Responder and returning it */
        return new Responder(responderPhone, responderAddress, responderState, responderZip);
    }

    /**
     * \brief Calculates 1 main route and 1 alternative route (if existing) from the Responder to the emergency address
     *
     */
    private void calculateRoute() {

    }
}

