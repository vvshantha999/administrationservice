package smartshare.administrationservice.constant;

public enum StatusConstants {
    SUCCESS( "success" ), FAILED( "failed" ),
    INPROGRESS( "In Progress" ), APPROVED( "Approved" ), REJECTED( "Rejected" );

    private String usedFormat;

    StatusConstants(String usedFormat) {
        this.usedFormat = usedFormat;
    }

    @Override
    public String toString() {
        return this.usedFormat;
    }
}
