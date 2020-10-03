// IRecepiHandler.aidl
package se.torsteneriksson.breadbake;

// Declare any non-default types here with import statements

interface IRecepiHandler {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int getRecepiState();
    int getRecepi();
    void setRecepiState(int state);
}
