package smartshare.administrationservice.models;

public class Status {

    String message;

    String reasonForFailure = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReasonForFailure() {
        return reasonForFailure;
    }

    public void setReasonForFailure(String reasonForFailure) {
        this.reasonForFailure = reasonForFailure;
    }

    @Override
    public String toString() {
        return "Status{" +
                "message='" + message + '\'' +
                ", reasonForFailure='" + reasonForFailure + '\'' +
                '}';
    }
}
