package beerly.ansteph.beerlybiz.api;

/**
 * Created by loicstephan on 2017/10/19.
 */

public interface Routes {

    /****************************************************---Domain---*************************************************************/

    //String DOMAIN="http://10.0.0.6/proimakapi/v1/";
    String DOMAIN="http://ec2-13-58-20-88.us-east-2.compute.amazonaws.com/";
    // String DOMAIN="https://inqb8.co.za/courses/";
    String API_VERSION="api/";
    String COU_VERSION="course_v1/";

    /****************************************************---Route---*************************************************************/

    String URL_REGISTER_BEERLOVERS = DOMAIN+API_VERSION+"store_beer_lover";
    String URL_RETRIEVE_EST=DOMAIN+API_VERSION+ "get_establishments"    ;//     DOMAIN+CA_VERSION+"register_candidate";

    String URL_EST_LOGIN=DOMAIN+API_VERSION+ "establishment_login";

    //savings
    String URL_EST_STORE_PROMO=DOMAIN+API_VERSION+ "save_promotion";
    String URL_EST_PROMO_UPDATE=DOMAIN+API_VERSION+ "update_promotion/%s";

    String URL_EST_PROMO_DELETE=DOMAIN+API_VERSION+ "delete_promotion/%s";



    //retrieve
    String URL_RETRIEVE_LOVER_PROFILE=DOMAIN+API_VERSION+ "get_beer_lover/%s" ;

    String URL_UPDATE_LOVER_PROFILE=DOMAIN+API_VERSION+ "edit_beer_lover/%s" ;


    String URL_RETRIEVE_PROMO_EST=DOMAIN+API_VERSION+ "get_promotion/%s"    ;

    String URL_RETRIEVE_PROMOTIONS=DOMAIN+API_VERSION+ "get_promotions"    ;

    String URL_RETRIEVE_BEERS=DOMAIN+API_VERSION+ "get_beers"    ;
    String URL_RETRIEVE_A_BEER=DOMAIN+API_VERSION+ "get_beer/%s"    ;

    ///Preferences

    String URL_STORE_PREFERENCES = DOMAIN+API_VERSION+"store_preference";
    String URL_UPDATE_PREFERENCES = DOMAIN+API_VERSION+"edit_preferences/%s";

    String URL_RETRIEVE_BEER_PREFERENCES=DOMAIN+API_VERSION+ "get_preferences/%s" ;

    ///Discount

    String URL_STORE_DISCOUNT= DOMAIN+API_VERSION+ "store_discount";
    String URL_RETRIEVE_DISCOUNT= DOMAIN+API_VERSION+ "get_discount/%s";


    ///Events
    String URL_RETRIEVE_EVENTS=DOMAIN+API_VERSION+ "get_events"      ;


    /****************************************************---COMMON MESSAGE---*************************************************************/

    public static final String ERROR_RESPONSE= "error";
    public static final String MSG_RESPONSE= "message";
    public static final String KEY_OTP = "otp";


    /****************************************************---TCs---*************************************************************/

    String URL_PRIVACY_POLICY ="http://ec2-13-58-20-88.us-east-2.compute.amazonaws.com/privacy_policy";
    String URL_TERM_CONDITIONS ="http://ec2-13-58-20-88.us-east-2.compute.amazonaws.com/terms_and_conditions";

}
