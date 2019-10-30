package smartshare.administrationservice.models;

import javax.persistence.*;

@Entity
public class ObjectAccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "object_id", referencedColumnName = "id")
    private BucketObject bucketObject;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "access_id", referencedColumnName = "id")
    private ObjectAccess access;

    private String status;

    public BucketObject getBucketObject() {
        return bucketObject;
    }

    public void setBucketObject(BucketObject bucketObject) {
        this.bucketObject = bucketObject;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ObjectAccess getAccess() {
        return access;
    }

    public void setAccess(ObjectAccess access) {
        this.access = access;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public ObjectAccessRequest approve() {
        this.status = "approved";
        return this;
    }

    public ObjectAccessRequest reject() {
        this.status = "rejected";
        return this;
    }

    @Override
    public String toString() {
        return "ObjectAccessRequest{" +
                "id=" + id +
                ", bucketObject=" + bucketObject +
                ", owner=" + owner +
                ", user=" + user +
                ", access=" + access +
                ", status='" + status + '\'' +
                '}';
    }



}
