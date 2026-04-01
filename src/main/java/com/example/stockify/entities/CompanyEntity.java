package com.example.stockify.entities;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "company",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "companyName")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEntity {

    @Id
    @Column(name="nseCode" , length = 50)
    @NotBlank(message = "NSE code is required")
    private String nseCode ;

    @Column(name="companyName" , length = 100)
    @NotBlank(message = "Company name should not be null")
    private String companyName;

    @Column(name = "stockPrice")
    @NotNull(message = "Stock price should not be null ")
    @Positive(message = "Stock price should be always positive ")
    private Float stockPrice ;


    @Column(name = "yearLow")
    @PositiveOrZero(message = "year low cannot be negative")
    private Integer yearLow;

    @Column(name = "yearHigh")
    @PositiveOrZero(message = "year High cannot be negative")
    private Integer yearHigh;

    @Column(name = "description", columnDefinition = "TEXT" , length = 500)
    private String description;

    @Column(name = "bseCode", length = 50)
    private String bseCode;


}
