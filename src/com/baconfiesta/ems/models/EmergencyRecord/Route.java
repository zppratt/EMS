package com.baconfiesta.ems.models.EmergencyRecord;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * A route in the system from the responder to the emergency
 */
public class Route {

    /*
     * TODO: Handle the fact that the answer query can be empty
     * TODO: If no category, what should I do instead of return?
     * TODO: if cannot retrieve data, what happens?
     * */

    /**
     * \brief Determines the nearest emergency responder according to the type of the emergency
     *
     * @param record the emergency record currently being created
     *               <p>
     *               Determines an emergency responder according to the type of the emergency. Queries places from google, selects the nearest place,
     *               queries information about this place, and formats information according to a Responder. Creates a new Responder and appends it to the emergency record
     */
    public void determineNearestResponder(EmergencyRecord record) {

        String searchQuery;
        /* Determine emergency responder according to the type of the emergency:*/
        switch (record.getCategory()) {
            /* Fire Department*/
            case FIRE:
                searchQuery = "Fire Department";
                break;
            /* Police Department */
            case CRIME:
                searchQuery = "Police Department";
                break;
            case CAR_CRASH:
                searchQuery = "PoliceDepartment";
                break;
            /* Health Department */
            case MEDICAL:
                searchQuery = "Hospital";
                break;
            default:
                return;
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

        /* Creating a new Responder and appending it to the emergency record */
        Responder emergencyResponder = new Responder(responderPhone, responderAddress, responderState, responderZip);
        record.setResponder(emergencyResponder);

    }
}

