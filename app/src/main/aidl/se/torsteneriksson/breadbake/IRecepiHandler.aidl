// IRecepiHandler.aidl
package se.torsteneriksson.breadbake;

// Declare any non-default types here with import statements

interface IRecepiHandler {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int getState();
    int getId();
    void setState(int state);
    void setId(int id);
}