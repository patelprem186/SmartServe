package com.easy.easybook.utils;

import com.easy.easybook.models.CountryCode;
import java.util.ArrayList;
import java.util.List;

public class CountryCodeUtils {
    
    public static List<CountryCode> getCountryCodes() {
        List<CountryCode> countryCodes = new ArrayList<>();
        
        // Popular countries with their codes
        countryCodes.add(new CountryCode("India", "IN", "+91", "🇮🇳"));
        countryCodes.add(new CountryCode("United States", "US", "+1", "🇺🇸"));
        countryCodes.add(new CountryCode("United Kingdom", "GB", "+44", "🇬🇧"));
        countryCodes.add(new CountryCode("Canada", "CA", "+1", "🇨🇦"));
        countryCodes.add(new CountryCode("Australia", "AU", "+61", "🇦🇺"));
        countryCodes.add(new CountryCode("Germany", "DE", "+49", "🇩🇪"));
        countryCodes.add(new CountryCode("France", "FR", "+33", "🇫🇷"));
        countryCodes.add(new CountryCode("Japan", "JP", "+81", "🇯🇵"));
        countryCodes.add(new CountryCode("China", "CN", "+86", "🇨🇳"));
        countryCodes.add(new CountryCode("Brazil", "BR", "+55", "🇧🇷"));
        countryCodes.add(new CountryCode("Russia", "RU", "+7", "🇷🇺"));
        countryCodes.add(new CountryCode("South Korea", "KR", "+82", "🇰🇷"));
        countryCodes.add(new CountryCode("Italy", "IT", "+39", "🇮🇹"));
        countryCodes.add(new CountryCode("Spain", "ES", "+34", "🇪🇸"));
        countryCodes.add(new CountryCode("Netherlands", "NL", "+31", "🇳🇱"));
        countryCodes.add(new CountryCode("Sweden", "SE", "+46", "🇸🇪"));
        countryCodes.add(new CountryCode("Norway", "NO", "+47", "🇳🇴"));
        countryCodes.add(new CountryCode("Denmark", "DK", "+45", "🇩🇰"));
        countryCodes.add(new CountryCode("Finland", "FI", "+358", "🇫🇮"));
        countryCodes.add(new CountryCode("Switzerland", "CH", "+41", "🇨🇭"));
        countryCodes.add(new CountryCode("Austria", "AT", "+43", "🇦🇹"));
        countryCodes.add(new CountryCode("Belgium", "BE", "+32", "🇧🇪"));
        countryCodes.add(new CountryCode("Poland", "PL", "+48", "🇵🇱"));
        countryCodes.add(new CountryCode("Czech Republic", "CZ", "+420", "🇨🇿"));
        countryCodes.add(new CountryCode("Hungary", "HU", "+36", "🇭🇺"));
        countryCodes.add(new CountryCode("Portugal", "PT", "+351", "🇵🇹"));
        countryCodes.add(new CountryCode("Greece", "GR", "+30", "🇬🇷"));
        countryCodes.add(new CountryCode("Turkey", "TR", "+90", "🇹🇷"));
        countryCodes.add(new CountryCode("Israel", "IL", "+972", "🇮🇱"));
        countryCodes.add(new CountryCode("South Africa", "ZA", "+27", "🇿🇦"));
        countryCodes.add(new CountryCode("Egypt", "EG", "+20", "🇪🇬"));
        countryCodes.add(new CountryCode("Nigeria", "NG", "+234", "🇳🇬"));
        countryCodes.add(new CountryCode("Kenya", "KE", "+254", "🇰🇪"));
        countryCodes.add(new CountryCode("Morocco", "MA", "+212", "🇲🇦"));
        countryCodes.add(new CountryCode("Tunisia", "TN", "+216", "🇹🇳"));
        countryCodes.add(new CountryCode("Algeria", "DZ", "+213", "🇩🇿"));
        countryCodes.add(new CountryCode("Saudi Arabia", "SA", "+966", "🇸🇦"));
        countryCodes.add(new CountryCode("United Arab Emirates", "AE", "+971", "🇦🇪"));
        countryCodes.add(new CountryCode("Qatar", "QA", "+974", "🇶🇦"));
        countryCodes.add(new CountryCode("Kuwait", "KW", "+965", "🇰🇼"));
        countryCodes.add(new CountryCode("Bahrain", "BH", "+973", "🇧🇭"));
        countryCodes.add(new CountryCode("Oman", "OM", "+968", "🇴🇲"));
        countryCodes.add(new CountryCode("Jordan", "JO", "+962", "🇯🇴"));
        countryCodes.add(new CountryCode("Lebanon", "LB", "+961", "🇱🇧"));
        countryCodes.add(new CountryCode("Syria", "SY", "+963", "🇸🇾"));
        countryCodes.add(new CountryCode("Iraq", "IQ", "+964", "🇮🇶"));
        countryCodes.add(new CountryCode("Iran", "IR", "+98", "🇮🇷"));
        countryCodes.add(new CountryCode("Pakistan", "PK", "+92", "🇵🇰"));
        countryCodes.add(new CountryCode("Bangladesh", "BD", "+880", "🇧🇩"));
        countryCodes.add(new CountryCode("Sri Lanka", "LK", "+94", "🇱🇰"));
        countryCodes.add(new CountryCode("Nepal", "NP", "+977", "🇳🇵"));
        countryCodes.add(new CountryCode("Bhutan", "BT", "+975", "🇧🇹"));
        countryCodes.add(new CountryCode("Maldives", "MV", "+960", "🇲🇻"));
        countryCodes.add(new CountryCode("Afghanistan", "AF", "+93", "🇦🇫"));
        countryCodes.add(new CountryCode("Thailand", "TH", "+66", "🇹🇭"));
        countryCodes.add(new CountryCode("Vietnam", "VN", "+84", "🇻🇳"));
        countryCodes.add(new CountryCode("Malaysia", "MY", "+60", "🇲🇾"));
        countryCodes.add(new CountryCode("Singapore", "SG", "+65", "🇸🇬"));
        countryCodes.add(new CountryCode("Indonesia", "ID", "+62", "🇮🇩"));
        countryCodes.add(new CountryCode("Philippines", "PH", "+63", "🇵🇭"));
        countryCodes.add(new CountryCode("Taiwan", "TW", "+886", "🇹🇼"));
        countryCodes.add(new CountryCode("Hong Kong", "HK", "+852", "🇭🇰"));
        countryCodes.add(new CountryCode("Macau", "MO", "+853", "🇲🇴"));
        countryCodes.add(new CountryCode("New Zealand", "NZ", "+64", "🇳🇿"));
        countryCodes.add(new CountryCode("Mexico", "MX", "+52", "🇲🇽"));
        countryCodes.add(new CountryCode("Argentina", "AR", "+54", "🇦🇷"));
        countryCodes.add(new CountryCode("Chile", "CL", "+56", "🇨🇱"));
        countryCodes.add(new CountryCode("Colombia", "CO", "+57", "🇨🇴"));
        countryCodes.add(new CountryCode("Peru", "PE", "+51", "🇵🇪"));
        countryCodes.add(new CountryCode("Venezuela", "VE", "+58", "🇻🇪"));
        countryCodes.add(new CountryCode("Ecuador", "EC", "+593", "🇪🇨"));
        countryCodes.add(new CountryCode("Bolivia", "BO", "+591", "🇧🇴"));
        countryCodes.add(new CountryCode("Paraguay", "PY", "+595", "🇵🇾"));
        countryCodes.add(new CountryCode("Uruguay", "UY", "+598", "🇺🇾"));
        
        return countryCodes;
    }
    
    public static CountryCode getDefaultCountry() {
        return new CountryCode("Australia", "AU", "+61",  "🇦🇺");
    }
    
    public static CountryCode findCountryByCode(String dialCode) {
        for (CountryCode country : getCountryCodes()) {
            if (country.getDialCode().equals(dialCode)) {
                return country;
            }
        }
        return getDefaultCountry();
    }
}
