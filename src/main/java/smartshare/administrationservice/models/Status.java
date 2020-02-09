package smartshare.administrationservice.models;

public class Status {

    private Boolean value;

    private String reasonForFailure = null;

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
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
                "message='" + value + '\'' +
                ", reasonForFailure='" + reasonForFailure + '\'' +
                '}';
    }
}
