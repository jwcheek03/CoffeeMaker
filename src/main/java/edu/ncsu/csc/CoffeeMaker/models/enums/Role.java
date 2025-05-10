package edu.ncsu.csc.CoffeeMaker.models.enums;

public enum Role {

    /**
     * Customer
     */
    ROLE_CUSTOMER ( 1, "customer/index" ),
    /**
     * Barista
     */
    ROLE_BARISTA ( 1, "barista/index" ),
    /**
     * manager
     */
    ROLE_MANAGER ( 1, "manager/index" );

    /** Code of the role as an int */
    private final int    code;

    /** Main index page for each role */
    private final String landingPage;

    /**
     * Role constructor
     *
     * @param code
     *            int code
     * @param landingPage
     *            Main index page for each role
     */
    private Role ( final int code, final String landingPage ) {
        this.code = code;
        this.landingPage = landingPage;
    }

    /**
     * Getter method for the code
     *
     * @return the numeric code
     */
    public int getCode () {
        return this.code;
    }

    /**
     * Getter method for the landing page
     *
     * @return The landing page
     */
    public String getLanding () {
        return this.landingPage;
    }

}
