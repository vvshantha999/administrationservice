package smartshare.administrationservice.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "accessing_users")
public class AccessingUser implements Serializable {


    public AccessingUser(User user, BucketObject bucketObject, ObjectAccess objectAccess) {
        this.user = user;
        this.bucketObject = bucketObject;
        this.access = objectAccess;
    }

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
