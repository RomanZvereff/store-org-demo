package org.store.customerservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class Address implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    private String id;

    @NotNull
    private String streetName;

    @NotNull
    private String streetNumber;

    private String additionalInfo;

    @NotNull
    private String zipCode;

    @NotNull
    private String city;

    private String state;

    @NotNull
    private String country;

}
