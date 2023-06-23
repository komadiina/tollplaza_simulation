package tollbooth.exceptions;

public final class VehicleStoppedException extends Exception {
    public VehicleStoppedException() {
        super("Vehicle has been stopped during examining!");
    }

    public VehicleStoppedException(String message) {
        super(message);
    }
}
