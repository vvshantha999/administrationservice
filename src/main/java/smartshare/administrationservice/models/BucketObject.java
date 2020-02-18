package smartshare.administrationservice.models;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "OBJECT")
@NoArgsConstructor
public @Data
class BucketObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "bucket_id")
    private Bucket bucket;

    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "Id")
    private User owner;


    @OneToMany(mappedBy = "bucketObject", cascade = CascadeType.ALL)
    private List<AccessingUser> accessingUsers = new ArrayList<>();


    public BucketObject(String name, Bucket bucket, User owner) {
        this.name = name;
        this.bucket = bucket;
        this.owner = owner;
    }

    public BucketObject addAccessingUser(AccessingUser accessingUser) {
        ArrayList<AccessingUser> newAccessingUsers = new ArrayList<>( getAccessingUsers() );
        newAccessingUsers.add( accessingUser );
        this.accessingUsers = newAccessingUsers;
        return this;
    }


}
