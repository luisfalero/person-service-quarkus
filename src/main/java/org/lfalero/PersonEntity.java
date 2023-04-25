package org.lfalero;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "person")
@Getter
@Setter
@ToString
public class PersonEntity extends PanacheEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;
    private String salutation;
}
