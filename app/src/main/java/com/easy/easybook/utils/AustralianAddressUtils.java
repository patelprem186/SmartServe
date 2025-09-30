package com.easy.easybook.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AustralianAddressUtils {
    
    private static final Map<String, List<String>> STATE_CITIES = new HashMap<>();
    
    static {
        // Australian Capital Territory
        List<String> actCities = new ArrayList<>();
        actCities.add("Canberra");
        actCities.add("Gungahlin");
        actCities.add("Tuggeranong");
        actCities.add("Weston Creek");
        actCities.add("Woden Valley");
        STATE_CITIES.put("ACT", actCities);
        
        // New South Wales
        List<String> nswCities = new ArrayList<>();
        nswCities.add("Sydney");
        nswCities.add("Newcastle");
        nswCities.add("Wollongong");
        nswCities.add("Wagga Wagga");
        nswCities.add("Albury");
        nswCities.add("Maitland");
        nswCities.add("Tamworth");
        nswCities.add("Orange");
        nswCities.add("Dubbo");
        nswCities.add("Nowra");
        nswCities.add("Bathurst");
        nswCities.add("Lismore");
        nswCities.add("Port Macquarie");
        nswCities.add("Coffs Harbour");
        nswCities.add("Broken Hill");
        STATE_CITIES.put("NSW", nswCities);
        
        // Northern Territory
        List<String> ntCities = new ArrayList<>();
        ntCities.add("Darwin");
        ntCities.add("Alice Springs");
        ntCities.add("Katherine");
        ntCities.add("Nhulunbuy");
        ntCities.add("Tennant Creek");
        ntCities.add("Palmerston");
        STATE_CITIES.put("NT", ntCities);
        
        // Queensland
        List<String> qldCities = new ArrayList<>();
        qldCities.add("Brisbane");
        qldCities.add("Gold Coast");
        qldCities.add("Townsville");
        qldCities.add("Cairns");
        qldCities.add("Toowoomba");
        qldCities.add("Rockhampton");
        qldCities.add("Mackay");
        qldCities.add("Bundaberg");
        qldCities.add("Hervey Bay");
        qldCities.add("Gladstone");
        qldCities.add("Mount Isa");
        qldCities.add("Maryborough");
        qldCities.add("Gympie");
        qldCities.add("Warwick");
        qldCities.add("Emerald");
        STATE_CITIES.put("QLD", qldCities);
        
        // South Australia
        List<String> saCities = new ArrayList<>();
        saCities.add("Adelaide");
        saCities.add("Mount Gambier");
        saCities.add("Whyalla");
        saCities.add("Murray Bridge");
        saCities.add("Port Augusta");
        saCities.add("Port Pirie");
        saCities.add("Port Lincoln");
        saCities.add("Gawler");
        saCities.add("Millicent");
        saCities.add("Kadina");
        saCities.add("Berri");
        saCities.add("Naracoorte");
        saCities.add("Roxby Downs");
        saCities.add("Victor Harbor");
        saCities.add("Wallaroo");
        STATE_CITIES.put("SA", saCities);
        
        // Tasmania
        List<String> tasCities = new ArrayList<>();
        tasCities.add("Hobart");
        tasCities.add("Launceston");
        tasCities.add("Devonport");
        tasCities.add("Burnie");
        tasCities.add("Ulverstone");
        tasCities.add("George Town");
        tasCities.add("Queenstown");
        tasCities.add("Scottsdale");
        tasCities.add("Smithton");
        tasCities.add("New Norfolk");
        tasCities.add("Sorell");
        tasCities.add("Kingston");
        tasCities.add("Currie");
        tasCities.add("Strahan");
        tasCities.add("Zeehan");
        STATE_CITIES.put("TAS", tasCities);
        
        // Victoria
        List<String> vicCities = new ArrayList<>();
        vicCities.add("Melbourne");
        vicCities.add("Geelong");
        vicCities.add("Ballarat");
        vicCities.add("Bendigo");
        vicCities.add("Shepparton");
        vicCities.add("Warrnambool");
        vicCities.add("Mildura");
        vicCities.add("Sale");
        vicCities.add("Traralgon");
        vicCities.add("Hamilton");
        vicCities.add("Colac");
        vicCities.add("Echuca");
        vicCities.add("Swan Hill");
        vicCities.add("Wodonga");
        vicCities.add("Frankston");
        STATE_CITIES.put("VIC", vicCities);
        
        // Western Australia
        List<String> waCities = new ArrayList<>();
        waCities.add("Perth");
        waCities.add("Fremantle");
        waCities.add("Rockingham");
        waCities.add("Mandurah");
        waCities.add("Bunbury");
        waCities.add("Geraldton");
        waCities.add("Albany");
        waCities.add("Broome");
        waCities.add("Kalgoorlie");
        waCities.add("Port Hedland");
        waCities.add("Busselton");
        waCities.add("Karratha");
        waCities.add("Esperance");
        waCities.add("Carnarvon");
        waCities.add("Kununurra");
        STATE_CITIES.put("WA", waCities);
    }
    
    /**
     * Gets list of cities for a given state
     */
    public static List<String> getCitiesForState(String stateCode) {
        if (stateCode == null || stateCode.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String cleanStateCode = stateCode.trim().toUpperCase();
        List<String> cities = STATE_CITIES.get(cleanStateCode);
        
        if (cities == null) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(cities);
    }
    
    /**
     * Gets all available states
     */
    public static List<String> getAllStates() {
        List<String> states = new ArrayList<>();
        for (String state : AustralianValidationUtils.AUSTRALIAN_STATES) {
            states.add(state);
        }
        return states;
    }
    
    /**
     * Gets all available state codes
     */
    public static List<String> getAllStateCodes() {
        List<String> stateCodes = new ArrayList<>();
        for (String code : AustralianValidationUtils.AUSTRALIAN_STATE_CODES) {
            stateCodes.add(code);
        }
        return stateCodes;
    }
    
    /**
     * Validates if a city exists in the given state
     */
    public static boolean isValidCityForState(String city, String stateCode) {
        if (city == null || city.trim().isEmpty() || stateCode == null || stateCode.trim().isEmpty()) {
            return false;
        }
        
        String cleanCity = city.trim();
        String cleanStateCode = stateCode.trim().toUpperCase();
        
        List<String> cities = STATE_CITIES.get(cleanStateCode);
        if (cities == null) {
            return false;
        }
        
        for (String validCity : cities) {
            if (validCity.equalsIgnoreCase(cleanCity)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets state code from city name (if unique)
     */
    public static String getStateCodeFromCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return "";
        }
        
        String cleanCity = city.trim();
        
        for (Map.Entry<String, List<String>> entry : STATE_CITIES.entrySet()) {
            for (String cityName : entry.getValue()) {
                if (cityName.equalsIgnoreCase(cleanCity)) {
                    return entry.getKey();
                }
            }
        }
        
        return "";
    }
}
