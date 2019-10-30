package smartshare.administrationservice.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class AccessingUsers implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "object_id")
    private BucketObject bucketObject;

    @OneToOne
    @JoinColumn(name = "access_id", referencedColumnName = "ID")
    private ObjectAccess access;

    public User getUser() {
        return user;
    }

    public BucketObject getBucketObject() {
        return bucketObject;
    }

    public ObjectAccess getAccess() {
        return access;
    }

    @Override
    public String toString() {
        return "AccessingUsers{" +
                "user=" + user +
                ", bucketObject=" + bucketObject +
                ", access=" + access +
                '}';
    }
}
