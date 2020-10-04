// IRecepiHandler.aidl
package se.torsteneriksson.recepihandler;
// Declare any non-default types here with import statements
import se.torsteneriksson.recepihandler.Recepi;

interface IRecepiHandlerService {

    void addRecepi(in Recepi recepi);
    Recepi getRecepi();
    void nextStep();
    void prevStep();
}